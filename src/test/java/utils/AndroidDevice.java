package utils;

public record AndroidDevice(
        String udid,
        String name,
        int systemPort
) {

    public String displayName() {
        return name + " | " + udid;
    }
}