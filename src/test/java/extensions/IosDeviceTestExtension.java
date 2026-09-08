package extensions;

import annotations.IosDeviceTest;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.api.extension.TestWatcher;
import utils.IosDevice;
import utils.IosDeviceContext;
import utils.IosDeviceUtils;
import utils.IosTestResults;
import utils.IosDeviceQuarantine;
import org.opentest4j.TestAbortedException;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public final class IosDeviceTestExtension
        implements TestTemplateInvocationContextProvider {

    private static final ExtensionContext.Namespace RESULTS_NAMESPACE =
            ExtensionContext.Namespace.create(
                    IosDeviceTestExtension.class,
                    "ios-results"
            );

    @Override
    public boolean supportsTestTemplate(
            ExtensionContext context
    ) {
        return context.getTestMethod()
                .map(method ->
                        method.isAnnotationPresent(
                                IosDeviceTest.class
                        )
                )
                .orElse(false);
    }

    @Override
    public Stream<TestTemplateInvocationContext>
    provideTestTemplateInvocationContexts(
            ExtensionContext context
    ) {
        List<IosDevice> devices = loadDevices();
        IosTestResults results = getResults(context);
        IosDeviceQuarantine quarantine = context.getRoot()
                .getStore(RESULTS_NAMESPACE)
                .getOrComputeIfAbsent(
                        IosDeviceQuarantine.class,
                        ignored -> new IosDeviceQuarantine(),
                        IosDeviceQuarantine.class
                );

        devices.forEach(results::registerDevice);

        return devices.stream()
                .map(device ->
                        createInvocationContext(
                                device,
                                results,
                                quarantine
                        )
                );
    }

    private TestTemplateInvocationContext
    createInvocationContext(
            IosDevice device,
            IosTestResults results,
            IosDeviceQuarantine quarantine
    ) {
        return new TestTemplateInvocationContext() {

            @Override
            public String getDisplayName(
                    int invocationIndex
            ) {
                return device.displayName();
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return List.of(
                        new IosDeviceInvocationExtension(
                                device,
                                results,
                                quarantine
                        )
                );
            }
        };
    }

    private List<IosDevice> loadDevices() {
        try {
            List<IosDevice> devices =
                    IosDeviceUtils.getConnectedIphones();

            String configuredUdid =
                    System.getProperty("ios.udid");

            if (configuredUdid == null
                    || configuredUdid.isBlank()) {
                return devices;
            }

            return devices.stream()
                    .filter(device ->
                            device.udid().equals(configuredUdid)
                    )
                    .findFirst()
                    .map(List::of)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Configured iOS device "
                                            + "is not connected: "
                                            + configuredUdid
                                            + ". Connected devices: "
                                            + devices.stream()
                                            .map(IosDevice::udid)
                                            .toList()
                            )
                    );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "iOS device discovery was interrupted.",
                    exception
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to discover iOS devices.",
                    exception
            );
        }
    }

    private IosTestResults getResults(
            ExtensionContext context
    ) {
        return context.getRoot()
                .getStore(RESULTS_NAMESPACE)
                .getOrComputeIfAbsent(
                        IosTestResults.class,
                        ignored -> new IosTestResults(),
                        IosTestResults.class
                );
    }

    static final class
    IosDeviceInvocationExtension
            implements BeforeEachCallback,
            AfterEachCallback,
            TestWatcher {

        private static final ConcurrentMap<
                String,
                ReentrantLock
                > DEVICE_LOCKS =
                new ConcurrentHashMap<>();

        private final IosDevice device;
        private final IosTestResults results;
        private final ReentrantLock deviceLock;
        private final IosDeviceQuarantine quarantine;

        private long startedAtNanos;
        private boolean lockAcquired;

        IosDeviceInvocationExtension(
                IosDevice device,
                IosTestResults results,
                IosDeviceQuarantine quarantine
        ) {
            this.device = device;
            this.results = results;
            this.quarantine = quarantine;
            this.deviceLock = DEVICE_LOCKS.computeIfAbsent(
                    device.udid(),
                    ignored -> new ReentrantLock(true)
            );
        }

        @Override
        public void beforeEach(
                ExtensionContext context
        ) throws InterruptedException {
            deviceLock.lockInterruptibly();
            lockAcquired = true;

            // Check under the device lock: the preceding test may just have failed cleanup.
            String reason = quarantine.reason(device.udid());
            if (reason != null) {
                lockAcquired = false;
                deviceLock.unlock();
                throw new TestAbortedException(
                        "iOS device " + device.displayName()
                                + " is excluded from this run: " + reason
                                + ". Restore the signed-out state manually before a new run."
                );
            }

            IosDeviceContext.set(device);
            startedAtNanos = System.nanoTime();
        }

        @Override
        public void afterEach(
                ExtensionContext context
        ) {
            try {
                String failure = IosDeviceContext.cleanupFailure();
                if (lockAcquired && failure != null) {
                    quarantine.block(device.udid(), getTestName(context) + ": " + failure);
                }
                IosDeviceContext.clear();
            } finally {
                if (lockAcquired) {
                    lockAcquired = false;
                    deviceLock.unlock();
                }
            }
        }

        @Override
        public void testSuccessful(
                ExtensionContext context
        ) {
            results.passed(
                    device,
                    getTestName(context),
                    getDuration()
            );
        }

        @Override
        public void testFailed(
                ExtensionContext context,
                Throwable cause
        ) {
            results.failed(
                    device,
                    getTestName(context),
                    getDuration(),
                    cause
            );
        }

        @Override
        public void testAborted(
                ExtensionContext context,
                Throwable cause
        ) {
            results.skipped(
                    device,
                    getTestName(context),
                    getDuration(),
                    formatSkippedReason(cause)
            );
        }

        @Override
        public void testDisabled(
                ExtensionContext context,
                Optional<String> reason
        ) {
            results.skipped(
                    device,
                    getTestName(context),
                    Duration.ZERO,
                    reason.orElse("Test was disabled.")
            );
        }

        private String getTestName(
                ExtensionContext context
        ) {
            return context.getRequiredTestClass()
                    .getSimpleName()
                    + "#"
                    + context.getRequiredTestMethod()
                    .getName();
        }

        private Duration getDuration() {
            if (startedAtNanos == 0L) {
                return Duration.ZERO;
            }

            return Duration.ofNanos(
                    System.nanoTime() - startedAtNanos
            );
        }

        private String formatSkippedReason(
                Throwable cause
        ) {
            if (cause == null
                    || cause.getMessage() == null
                    || cause.getMessage().isBlank()) {
                return "Test was aborted.";
            }

            return cause.getMessage()
                    .split("\\R", 2)[0];
        }
    }
}
