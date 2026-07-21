package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IosDeviceUtils {

    private static final Pattern DEVICE_LINE_PATTERN =
            Pattern.compile(
                    "^(.+?) \\(([^()]*)\\) \\(([0-9A-Fa-f-]+)\\)$"
            );

    private static IosDevice cachedDevice;

    private IosDeviceUtils() {
    }

    public static synchronized IosDevice getSingleConnectedIphone()
            throws IOException, InterruptedException {

        if (cachedDevice != null) {
            return cachedDevice;
        }

        Process process = new ProcessBuilder(
                "xcrun",
                "xctrace",
                "list",
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
                    "Failed to get connected iOS devices. "
                            + "Xcode output: "
                            + output
            );
        }

        List<IosDevice> connectedIphones =
                parseConnectedIphones(output);

        if (connectedIphones.isEmpty()) {
            throw new IllegalStateException(
                    "No connected iPhone was found. "
                            + "Unlock the iPhone, connect it to the Mac "
                            + "and confirm that the computer is trusted. "
                            + "Xcode output: "
                            + output
            );
        }

        if (connectedIphones.size() > 1) {
            throw new IllegalStateException(
                    "More than one iPhone is connected: "
                            + connectedIphones
                            + ". Connect only one iPhone "
                            + "for this test run."
            );
        }

        cachedDevice = connectedIphones.get(0);

        return cachedDevice;
    }

    private static List<IosDevice> parseConnectedIphones(
            String output
    ) {
        List<IosDevice> devices = new ArrayList<>();
        boolean connectedDevicesSection = false;

        for (String line : output.split("\\R")) {
            String trimmedLine = line.trim();

            if ("== Devices ==".equals(trimmedLine)) {
                connectedDevicesSection = true;
                continue;
            }

            if (connectedDevicesSection
                    && trimmedLine.startsWith("==")) {
                break;
            }

            if (!connectedDevicesSection) {
                continue;
            }

            Matcher matcher =
                    DEVICE_LINE_PATTERN.matcher(trimmedLine);

            if (!matcher.matches()) {
                continue;
            }

            String deviceName = matcher.group(1).trim();

            if (!deviceName
                    .toLowerCase(Locale.ROOT)
                    .contains("iphone")) {
                continue;
            }

            String deviceUdid = matcher.group(3).trim();

            devices.add(
                    new IosDevice(
                            deviceName,
                            deviceUdid
                    )
            );
        }

        return devices;
    }

    public record IosDevice(
            String name,
            String udid
    ) {
    }
}
