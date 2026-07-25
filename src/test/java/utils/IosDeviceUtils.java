package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public final class IosDeviceUtils {

    private static final int DEFAULT_WDA_PORT_BASE = 8100;

    private static final Pattern DEVICE_LINE_PATTERN =
            Pattern.compile(
                    "^(.+?) \\(([^()]*)\\) \\(([0-9A-Fa-f-]+)\\)$"
            );

    private static List<IosDevice> cachedDevices;

    private IosDeviceUtils() {
    }

    public static synchronized List<IosDevice> getConnectedIphones()
            throws IOException, InterruptedException {

        if (cachedDevices != null) {
            return cachedDevices;
        }

        String output = getXcodeDeviceList();
        List<DiscoveredIosDevice> discoveredDevices =
                parseConnectedIphones(output);

        if (discoveredDevices.isEmpty()) {
            throw new IllegalStateException(
                    "No connected iPhone was found. "
                            + "Unlock the iPhone, connect it to the Mac "
                            + "and confirm that the computer is trusted. "
                            + "Xcode output: "
                            + output
            );
        }

        int wdaPortBase = getWdaPortBase();
        ensurePortRangeIsValid(
                wdaPortBase,
                discoveredDevices.size()
        );

        cachedDevices = IntStream.range(
                        0,
                        discoveredDevices.size()
                )
                .mapToObj(index -> createDevice(
                        discoveredDevices.get(index),
                        wdaPortBase + index
                ))
                .toList();

        return cachedDevices;
    }

    public static synchronized void clearCache() {
        cachedDevices = null;
    }

    private static String getXcodeDeviceList()
            throws IOException, InterruptedException {

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

        return output;
    }

    private static List<DiscoveredIosDevice> parseConnectedIphones(
            String output
    ) {
        List<DiscoveredIosDevice> devices = new ArrayList<>();
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
                    new DiscoveredIosDevice(
                            deviceUdid,
                            deviceName
                    )
            );
        }

        return devices;
    }

    private static IosDevice createDevice(
            DiscoveredIosDevice discoveredDevice,
            int wdaLocalPort
    ) {
        String safeUdid = discoveredDevice.udid()
                .replaceAll("[^A-Za-z0-9._-]", "_");

        String derivedDataPath =
                System.getProperty("java.io.tmpdir")
                        + "appium-wda-"
                        + safeUdid;

        return new IosDevice(
                discoveredDevice.udid(),
                discoveredDevice.name(),
                wdaLocalPort,
                derivedDataPath
        );
    }

    private static int getWdaPortBase() {
        String configuredPortBase =
                System.getProperty("ios.wdaLocalPortBase");

        if (configuredPortBase == null
                || configuredPortBase.isBlank()) {
            return DEFAULT_WDA_PORT_BASE;
        }

        try {
            return Integer.parseInt(configuredPortBase);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "ios.wdaLocalPortBase must be a number: "
                            + configuredPortBase,
                    exception
            );
        }
    }

    private static void ensurePortRangeIsValid(
            int portBase,
            int deviceCount
    ) {
        if (portBase < 1024 || portBase > 65535) {
            throw new IllegalArgumentException(
                    "iOS WDA port base must be between "
                            + "1024 and 65535: "
                            + portBase
            );
        }

        long lastPort =
                (long) portBase + deviceCount - 1L;

        if (lastPort > 65535) {
            throw new IllegalArgumentException(
                    "Not enough iOS WDA ports. Base port: "
                            + portBase
                            + ", device count: "
                            + deviceCount
            );
        }
    }

    private record DiscoveredIosDevice(
            String udid,
            String name
    ) {
    }
}
