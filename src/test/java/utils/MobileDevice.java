package utils;

public interface MobileDevice {

    String udid();

    String name();

    default String displayName() {
        return name() + " | " + udid();
    }
}
