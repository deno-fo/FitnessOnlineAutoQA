package extensions;

import annotations.AndroidDeviceTest;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.api.extension.TestWatcher;
import utils.AndroidDevice;
import utils.AndroidDeviceContext;
import utils.AndroidTestResults;
import utils.DeviceUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class AndroidDeviceTestExtension
        implements TestTemplateInvocationContextProvider {

    private static final int DEFAULT_SYSTEM_PORT_BASE =
            8200;

    private static final ExtensionContext.Namespace
            RESULTS_NAMESPACE =
            ExtensionContext.Namespace.create(
                    AndroidDeviceTestExtension.class,
                    "android-results"
            );

    @Override
    public boolean supportsTestTemplate(
            ExtensionContext context
    ) {
        return context.getTestMethod()
                .map(
                        method ->
                                method.isAnnotationPresent(AndroidDeviceTest.class
                                )
                )
                .orElse(false);
    }

    @Override
    public Stream<TestTemplateInvocationContext>
    provideTestTemplateInvocationContexts(
            ExtensionContext context
    ) {
        List<AndroidDevice> devices =
                loadDevices();

        AndroidTestResults results =
                getResults(context);

        devices.forEach(results::registerDevice);

        return devices.stream()
                .map(
                        device ->
                                createInvocationContext(
                                        device,
                                        results
                                )
                );
    }

    private TestTemplateInvocationContext
    createInvocationContext(
            AndroidDevice device,
            AndroidTestResults results
    ) {
        return new TestTemplateInvocationContext() {

            @Override
            public String getDisplayName(
                    int invocationIndex
            ) {
                return device.displayName();
            }

            @Override
            public List<Extension>
            getAdditionalExtensions() {
                return List.of(
                        new AndroidDeviceInvocationExtension(
                                device,
                                results
                        )
                );
            }
        };
    }

    private List<AndroidDevice> loadDevices() {
        try {
            List<String> connectedDevices =
                    DeviceUtils.getConnectedDeviceUdids();

            String configuredUdid =
                    System.getProperty("android.udid");

            if (configuredUdid != null
                    && !configuredUdid.isBlank()) {

                if (!connectedDevices.contains(
                        configuredUdid
                )) {
                    throw new IllegalStateException(
                            "Configured Android device "
                                    + "is not connected: "
                                    + configuredUdid
                                    + ". Connected devices: "
                                    + connectedDevices
                    );
                }

                connectedDevices =
                        List.of(configuredUdid);
            }

            int systemPortBase =
                    getSystemPortBase();

            ensurePortRangeIsValid(
                    systemPortBase,
                    connectedDevices.size()
            );

            List<String> finalConnectedDevices =
                    connectedDevices;

            return IntStream.range(
                            0,
                            finalConnectedDevices.size()
                    )
                    .mapToObj(
                            index ->
                                    createDevice(
                                            finalConnectedDevices
                                                    .get(index),
                                            resolveSystemPort(
                                                    systemPortBase,
                                                    index,
                                                    finalConnectedDevices
                                                            .size()
                                            )
                                    )
                    )
                    .toList();

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Android device discovery "
                            + "was interrupted.",
                    exception
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to discover " + "Android devices.",
                    exception
            );
        }
    }

    private AndroidDevice createDevice(
            String udid,
            int systemPort
    ) {
        try {
            return new AndroidDevice(
                    udid,
                    DeviceUtils.getDeviceDisplayName(
                            udid
                    ),
                    systemPort
            );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Device name lookup "
                            + "was interrupted for "
                            + udid,
                    exception
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to get device name for "
                            + udid,
                    exception
            );
        }
    }

    private int getSystemPortBase() {
        String configuredPortBase =
                System.getProperty(
                        "android.systemPortBase"
                );

        if (configuredPortBase == null
                || configuredPortBase.isBlank()) {
            return DEFAULT_SYSTEM_PORT_BASE;
        }

        return parsePort(
                configuredPortBase,
                "android.systemPortBase"
        );
    }

    private int resolveSystemPort(
            int systemPortBase,
            int deviceIndex,
            int deviceCount
    ) {
        String configuredPort =
                System.getProperty(
                        "android.systemPort"
                );

        if (deviceCount == 1
                && configuredPort != null
                && !configuredPort.isBlank()) {
            return parsePort(
                    configuredPort,
                    "android.systemPort"
            );
        }

        return systemPortBase + deviceIndex;
    }

    private int parsePort(
            String value,
            String propertyName
    ) {
        try {
            int port = Integer.parseInt(value);

            if (port < 1024 || port > 65535) {
                throw new IllegalArgumentException(
                        propertyName
                                + " must be between "
                                + "1024 and 65535: "
                                + port
                );
            }

            return port;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    propertyName
                            + " must be a number: "
                            + value,
                    exception
            );
        }
    }

    private void ensurePortRangeIsValid(
            int systemPortBase,
            int deviceCount
    ) {
        if (systemPortBase < 1024
                || systemPortBase > 65535) {
            throw new IllegalArgumentException(
                    "Android system port base "
                            + "must be between "
                            + "1024 and 65535: "
                            + systemPortBase
            );
        }

        long lastPort =
                (long) systemPortBase
                        + deviceCount
                        - 1L;

        if (lastPort > 65535) {
            throw new IllegalArgumentException(
                    "Not enough Android system ports. "
                            + "Base port: "
                            + systemPortBase
                            + ", device count: "
                            + deviceCount
            );
        }
    }

    private AndroidTestResults getResults(
            ExtensionContext context
    ) {
        return context.getRoot()
                .getStore(RESULTS_NAMESPACE)
                .getOrComputeIfAbsent(
                        AndroidTestResults.class,
                        ignored ->
                                new AndroidTestResults(), AndroidTestResults.class
                );
    }

    private static final class
    AndroidDeviceInvocationExtension
            implements BeforeEachCallback,
            AfterEachCallback,
            TestWatcher {

        private static final ConcurrentMap<
                String,
                ReentrantLock
                > DEVICE_LOCKS =
                new ConcurrentHashMap<>();

        private final AndroidDevice device;
        private final AndroidTestResults results;
        private final ReentrantLock deviceLock;

        private long startedAtNanos;
        private boolean lockAcquired;

        private AndroidDeviceInvocationExtension(
                AndroidDevice device,
                AndroidTestResults results
        ) {
            this.device = device;
            this.results = results;
            this.deviceLock =
                    DEVICE_LOCKS.computeIfAbsent(
                            device.udid(),
                            ignored ->
                                    new ReentrantLock(
                                            true
                                    )
                    );
        }

        @Override
        public void beforeEach(
                ExtensionContext context
        ) throws InterruptedException {

            deviceLock.lockInterruptibly();
            lockAcquired = true;

            AndroidDeviceContext.set(device);
            startedAtNanos = System.nanoTime();
        }

        @Override
        public void afterEach(
                ExtensionContext context
        ) {
            try {
                AndroidDeviceContext.clear();
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
                    reason.orElse(
                            "Test was disabled."
                    )
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
                    System.nanoTime()
                            - startedAtNanos
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

            return cause.
                    getMessage()
                    .split("\\R", 2)[0];
        }
    }
}