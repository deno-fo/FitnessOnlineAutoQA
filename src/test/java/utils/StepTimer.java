package utils;

import java.util.Locale;
import java.util.function.Supplier;

public final class StepTimer {

    private static final String PREFIX = "[STEP TIMER]";

    private StepTimer() {
    }

    public static long start(String stepName) {
        long startedAt = System.nanoTime();

        System.out.printf(
                Locale.US,
                "%s START | %s%n",
                PREFIX,
                stepName
        );

        return startedAt;
    }

    public static void finish(
            String stepName,
            long startedAt
    ) {
        logResult(
                "DONE ",
                stepName,
                startedAt,
                null
        );
    }

    public static void run(
            String stepName,
            Runnable action
    ) {
        long startedAt = start(stepName);

        try {
            action.run();
            finish(stepName, startedAt);

        } catch (RuntimeException | Error exception) {
            logResult(
                    "FAIL ",
                    stepName,
                    startedAt,
                    exception
            );

            throw exception;
        }
    }

    public static <T> T get(
            String stepName,
            Supplier<T> action
    ) {
        long startedAt = start(stepName);

        try {
            T result = action.get();
            finish(stepName, startedAt);
            return result;

        } catch (RuntimeException | Error exception) {
            logResult(
                    "FAIL ",
                    stepName,
                    startedAt,
                    exception
            );

            throw exception;
        }
    }

    private static void logResult(
            String status,
            String stepName,
            long startedAt,
            Throwable failure
    ) {
        double elapsedSeconds =
                (System.nanoTime() - startedAt)
                        / 1_000_000_000.0;

        if (failure == null) {
            System.out.printf(
                    Locale.US,
                    "%s %s | %s | %.3f s%n",
                    PREFIX,
                    status,
                    stepName,
                    elapsedSeconds
            );
            return;
        }

        System.out.printf(
                Locale.US,
                "%s %s | %s | %.3f s | %s%n",
                PREFIX,
                status,
                stepName,
                elapsedSeconds,
                failure.getClass().getSimpleName()
        );
    }
}
