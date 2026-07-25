package utils;

public record IosDevice(
        String udid,
        String name,
        int wdaLocalPort,
        String derivedDataPath
) implements MobileDevice {
}
