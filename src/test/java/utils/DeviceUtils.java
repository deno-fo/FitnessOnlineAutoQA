package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceUtils {

    private static final String ADB_PATH =
            resolveAdbPath();

    private static String resolveAdbPath() {
        String executableName =
                isWindows() ? "adb.exe" : "adb";

        for (String environmentVariable :
                List.of("ANDROID_HOME", "ANDROID_SDK_ROOT")) {

            String sdkRoot =
                    System.getenv(environmentVariable);

            if (sdkRoot == null || sdkRoot.isBlank()) {
                continue;
            }

            Path adbPath = Path.of(
                    sdkRoot,
                    "platform-tools",
                    executableName
            );

            if (Files.isRegularFile(adbPath)) {
                return adbPath.toString();
            }
        }

        /*
         * If neither SDK variable points to adb,
         * let the operating system search for it in PATH.
         */
        return executableName;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name")
                .startsWith("Windows");
    }

    private static Process startAdb(
            String... arguments
    ) {
        List<String> command = new ArrayList<>();

        command.add(ADB_PATH);
        command.addAll(List.of(arguments));

        try {
            return new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not start adb. Resolved command: "
                            + ADB_PATH
                            + ". Configure ANDROID_HOME or "
                            + "ANDROID_SDK_ROOT, or add adb to PATH.",
                    exception
            );
        }
    }

    private DeviceUtils() {
    }

    public static List<String> getConnectedDeviceUdids()
            throws IOException, InterruptedException {

        Process process =
                startAdb("devices");

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
                        .sorted()
                        .toList();

        if (connectedDevices.isEmpty()) {
            throw new IllegalStateException(
                    "No authorized Android device is connected. "
                            + "ADB output: "
                            + output
            );
        }

        return connectedDevices;
    }

    public static String getSingleConnectedDeviceUdid()
            throws IOException, InterruptedException {

        String configuredUdid =
                System.getProperty("android.udid");

        List<String> connectedDevices =
                getConnectedDeviceUdids();

        if (configuredUdid != null
                && !configuredUdid.isBlank()) {

            if (!connectedDevices.contains(configuredUdid)) {
                throw new IllegalStateException(
                        "Configured Android device is not connected: "
                                + configuredUdid
                                + ". Connected devices: "
                                + connectedDevices
                );
            }

            return configuredUdid;
        }

        if (connectedDevices.size() > 1) {
            throw new IllegalStateException(
                    "More than one Android device is connected: "
                            + connectedDevices
                            + ". Select one using -Dandroid.udid=<UDID> "
                            + "or use the Android matrix launcher."
            );
        }

        return connectedDevices.get(0);
    }

    public static String getDeviceDisplayName(
            String deviceUdid
    ) throws IOException, InterruptedException {

        String configuredDeviceName =
                readAdbOutput(
                        "-s",
                        deviceUdid,
                        "shell",
                        "settings",
                        "get",
                        "global",
                        "device_name"
                );

        if (isUsableDeviceName(configuredDeviceName)) {
            return configuredDeviceName;
        }

        String model =
                readAdbOutput(
                        "-s",
                        deviceUdid,
                        "shell",
                        "getprop",
                        "ro.product.model"
                );

        if (isUsableDeviceName(model)) {
            return model;
        }

        return "Android";
    }

    public static void clearAppData(
            String deviceUdid,
            String appPackage
    ) throws IOException, InterruptedException {

        Process process = startAdb(
                "-s",
                deviceUdid,
                "shell",
                "pm",
                "clear",
                appPackage
        );

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

        Process process = startAdb(
                "-s",
                deviceUdid,
                "shell",
                "getprop",
                "ro.build.version.sdk"
        );

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

        Process process = startAdb(
                "-s",
                deviceUdid,
                "shell",
                "pm",
                "path",
                packageName
        );

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

    private static String readAdbOutput(
            String... arguments
    ) throws IOException, InterruptedException {

        Process process = startAdb(arguments);

        String output = new String(
                process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        ).trim();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IllegalStateException(
                    "ADB command failed. Output: "
                            + output
            );
        }

        return output;
    }

    private static boolean isUsableDeviceName(
            String value
    ) {
        return value != null
                && !value.isBlank()
                && !"null".equalsIgnoreCase(value);
    }
}