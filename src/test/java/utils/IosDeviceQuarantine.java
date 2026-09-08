package utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Shared by invocations of one JUnit run; a new run gets a fresh registry. */
public final class IosDeviceQuarantine {
    private final ConcurrentMap<String, String> reasons = new ConcurrentHashMap<>();

    public void block(String udid, String reason) {
        reasons.putIfAbsent(udid, reason);
    }

    public String reason(String udid) {
        return reasons.get(udid);
    }
}
