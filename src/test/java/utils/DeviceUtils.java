package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class DeviceUtils {

    private static final String ADB_PATH =
            "/Users/admin/Library/Android/sdk/platform-tools/adb";

    private DeviceUtils() {
    }

    public static String getSingleConnectedDeviceUdid()
            throws IOException, InterruptedException {

        Process process = new ProcessBuilder(
                ADB_PATH,
                "devices"
        )
                .redirectErrorStream(true)
                .start();

        String output = new String(
                process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IllegalStateException(
                    "Failed to get connected Android devices. "
                            + "ADB output: "
                            + output
            );
        }

        List<String> connectedDevices =
                Arrays.stream(output.split("\\R"))
                        .skip(1)
                        .map(String::trim)
                        .filter(line -> line.endsWith("\tdevice"))
                        .map(line -> line.split("\\s+")[0])
                        .toList();

        if (connectedDevices.isEmpty()) {
            throw new IllegalStateException(
                    "No authorized Android device is connected. "
                            + "ADB output: "
                            + output
            );
        }

        if (connectedDevices.size() > 1) {
            throw new IllegalStateException(
                    "More than one Android device is connected: "
                            + connectedDevices
                            + ". Connect only one device for this test run."
            );
        }

        return connectedDevices.get(0);
    }

    public static void clearAppData(
            String deviceUdid,
            String appPackage
    ) throws IOException, InterruptedException {

        Process process = new ProcessBuilder(
                ADB_PATH,
                "-s",
                deviceUdid,
                "shell",
                "pm",
                "clear",
                appPackage
        )
                .redirectErrorStream(true)
                .start();

        String output = new String(
                process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        int exitCode = process.waitFor();

        if (exitCode != 0 || !output.contains("Success")) {
            throw new IllegalStateException(
                    "Failed to clear app data. ADB output: "
                            + output
            );
        }
    }

    public static int getAndroidApiLevel(
            String deviceUdid
    ) throws IOException, InterruptedException {

        Process process = new ProcessBuilder(
                ADB_PATH,
                "-s",
                deviceUdid,
                "shell",
                "getprop",
                "ro.build.version.sdk"
        )
                .redirectErrorStream(true)
                .start();

        String output = new String(
                process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        ).trim();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IllegalStateException(
                    "Failed to get Android API level. ADB output: "
                            + output
            );
        }
        return Integer.parseInt(output);
    }

    public static boolean isPackageInstalled(
            String deviceUdid,
            String packageName
    ) throws IOException, InterruptedException {

        Process process = new ProcessBuilder(
                ADB_PATH,
                "-s",
                deviceUdid,
                "shell",
                "pm",
                "path",
                packageName
        )
                .redirectErrorStream(true)
                .start();

        String output = new String(
                process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        ).trim();

        int exitCode = process.waitFor();

        return exitCode == 0
                && output.startsWith("package:");
    }

    public static boolean isHealthConnectAvailable(
            String deviceUdid
    ) throws IOException, InterruptedException {

        int apiLevel =
                getAndroidApiLevel(deviceUdid);

        if (apiLevel >= 34) {
            return true;
        }

        return isPackageInstalled(
                deviceUdid,
                "com.google.android.apps.healthdata"
        );
    }
}