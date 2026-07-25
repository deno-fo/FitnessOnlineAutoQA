package utils;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class AndroidTestResults
        implements ExtensionContext.Store.CloseableResource {

    private static final Object PRINT_LOCK =
            new Object();

    private final Map<String, DeviceSummary> summaries =
            new ConcurrentHashMap<>();

    private final Queue<TestResult> testResults =
            new ConcurrentLinkedQueue<>();

    public void registerDevice(AndroidDevice device) {
        summaries.computeIfAbsent(
                device.udid(),
                ignored -> new DeviceSummary(device)
        );
    }

    public void passed(
            AndroidDevice device,
            String testName,
            Duration duration
    ) {
        getSummary(device)
                .passed
                .incrementAndGet();

        testResults.add(
                new TestResult(
                        TestStatus.PASS,
                        device,
                        testName,
                        duration,
                        null
                )
        );
    }

    public void failed(
            AndroidDevice device,
            String testName,
            Duration duration,
            Throwable cause
    ) {
        getSummary(device)
                .failed
                .incrementAndGet();

        testResults.add(
                new TestResult(
                        TestStatus.FAIL,
                        device,
                        testName,
                        duration,
                        formatReason(cause)
                )
        );
    }

    public void skipped(
            AndroidDevice device,
            String testName,
            Duration duration,
            String reason
    ) {
        getSummary(device)
                .skipped
                .incrementAndGet();

        testResults.add(
                new TestResult(
                        TestStatus.SKIP,
                        device,
                        testName,
                        duration,
                        reason
                )
        );
    }

    private DeviceSummary getSummary(
            AndroidDevice device
    ) {
        registerDevice(device);

        return summaries.get(device.udid());
    }

    private String formatReason(
            Throwable throwable
    ) {
        if (throwable == null) {
            return "Unknown failure";
        }

        Throwable rootCause =
                throwable;

        while (rootCause.getCause() != null
                && rootCause.getCause() != rootCause) {

            rootCause =
                    rootCause.getCause();
        }

        String message =
                rootCause.getMessage();

        if (message == null
                || message.isBlank()) {

            return rootCause
                    .getClass()
                    .getSimpleName();
        }

        String firstLine =
                message.split("\\R", 2)[0];

        return rootCause
                .getClass()
                .getSimpleName()
                + ": "
                + firstLine;
    }

    @Override
    public void close() {
        synchronized (PRINT_LOCK) {
            List<DeviceSummary> orderedSummaries =
                    summaries.values()
                            .stream()
                            .sorted(
                                    Comparator
                                            .comparing(
                                                    (DeviceSummary summary) ->
                                                            summary.device.name()
                                            )
                                            .thenComparing(
                                                    summary ->
                                                            summary.device.udid()
                                            )
                            )
                            .toList();

            System.out.println();
            System.out.println(
                    "====================== ANDROID TEST REPORT ======================"
            );
            System.out.println();

            printDeviceSummaries(
                    orderedSummaries
            );

            System.out.println();
            System.out.println(
                    "-----------------------------------------------------------------"
            );
            System.out.println("TEST RESULTS");
            System.out.println();

            testResults.forEach(
                    this::printTestResult
            );

            System.out.println();
            System.out.println(
                    "================================================================="
            );
        }
    }

    private void printDeviceSummaries(
            List<DeviceSummary> orderedSummaries
    ) {
        System.out.println("DEVICE SUMMARY");

        int deviceLabelWidth =
                orderedSummaries.stream()
                        .map(summary ->
                                formatDeviceLabel(
                                        summary.device
                                )
                        )
                        .mapToInt(String::length)
                        .max()
                        .orElse(0);

        for (DeviceSummary summary :
                orderedSummaries) {

            int passed =
                    summary.passed.get();

            int failed =
                    summary.failed.get();

            int skipped =
                    summary.skipped.get();

            int total =
                    passed
                            + failed
                            + skipped;

            String deviceLabel =
                    formatDeviceLabel(
                            summary.device
                    );

            String format =
                    "  %-"
                            + deviceLabelWidth
                            + "s | PASS: %d | FAIL: %d"
                            + " | SKIP: %d | TOTAL: %d%n";

            System.out.printf(
                    format,
                    deviceLabel,
                    passed,
                    failed,
                    skipped,
                    total
            );
        }
    }

    private void printTestResult(
            TestResult result
    ) {
        String durationPart =
                result.duration().isZero()
                        ? ""
                        : " | "
                        + formatDuration(
                        result.duration()
                );

        System.out.printf(
                "[%s] [%s] %s%s%n",
                result.status(),
                result.device().displayName(),
                result.testName(),
                durationPart
        );

        if (result.reason() != null
                && !result.reason().isBlank()) {

            System.out.println(
                    "       Reason: "
                            + result.reason()
            );
        }
    }

    private String formatDeviceLabel(
            AndroidDevice device
    ) {
        return device.name()
                + " ["
                + device.udid()
                + "]";
    }

    private String formatDuration(
            Duration duration
    ) {
        return String.format(
                Locale.US,
                "%.1f s",
                duration.toMillis() / 1000.0
        );
    }

    private enum TestStatus {
        PASS,
        FAIL,
        SKIP
    }

    private record TestResult(
            TestStatus status,
            AndroidDevice device,
            String testName,
            Duration duration,
            String reason
    ) {
    }

    private static final class DeviceSummary {

        private final AndroidDevice device;

        private final AtomicInteger passed =
                new AtomicInteger();

        private final AtomicInteger failed =
                new AtomicInteger();

        private final AtomicInteger skipped =
                new AtomicInteger();

        private DeviceSummary(
                AndroidDevice device
        ) {
            this.device = device;
        }
    }
}