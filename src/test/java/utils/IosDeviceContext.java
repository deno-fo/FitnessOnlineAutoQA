package utils;

public final class IosDeviceContext {

    private static final ThreadLocal<IosDevice> CURRENT_DEVICE =
            new ThreadLocal<>();

    private static final ThreadLocal<String> CLEANUP_FAILURE = new ThreadLocal<>();

    public static void markCleanupFailed(String reason) {
        CLEANUP_FAILURE.set(reason);
    }

    public static String cleanupFailure() {
        return CLEANUP_FAILURE.get();
    }

    private IosDeviceContext() {
    }

    public static void set(IosDevice device) {
        CURRENT_DEVICE.set(device);
    }

    public static IosDevice get() {
        return CURRENT_DEVICE.get();
    }

    public static IosDevice getRequired() {
        IosDevice device = CURRENT_DEVICE.get();

        if (device == null) {
            throw new IllegalStateException(
                    "iOS device is not assigned "
                            + "to the current test thread."
            );
        }

        return device;
    }

    public static void clear() {
        CURRENT_DEVICE.remove();
        CLEANUP_FAILURE.remove();
    }
}
