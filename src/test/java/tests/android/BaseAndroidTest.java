package tests.android;

import annotations.RequiresGoogleHealth;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import utils.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class BaseAndroidTest {

    private static final int SYSTEM_PORT_BASE = 8200;
    private static final int SYSTEM_PORT_MAX = 8299;

    protected AndroidDriver driver;
    protected boolean healthConnectAvailable;

    protected String deviceUdid;
    protected int systemPort;

    @BeforeEach
    public void setUp(TestInfo testInfo)
            throws IOException, InterruptedException {

        AppiumConfig.ensureServerIsAvailable();

        AndroidDevice assignedDevice =
                AndroidDeviceContext.get();

        if (assignedDevice != null) {
            deviceUdid =
                    assignedDevice.udid();

            systemPort =
                    assignedDevice.systemPort();
        } else {
            deviceUdid =
                    DeviceUtils.getSingleConnectedDeviceUdid();

            systemPort =
                    resolveSystemPort(deviceUdid);
        }

        ensureFitnessOnlineIsInstalled(deviceUdid);

        healthConnectAvailable =
                DeviceUtils.isHealthConnectAvailable(
                        deviceUdid
                );

        ensureGoogleHealthRequirementIsMet(testInfo);

        DeviceUtils.clearAppData(
                deviceUdid,
                AndroidConfig.APP_PACKAGE
        );

        UiAutomator2Options options =
                createDriverOptions();

        logSessionConfiguration();

        driver = new AndroidDriver(
                new URL(AppiumConfig.SERVER_URL),
                options
        );
    }

    private void ensureGoogleHealthRequirementIsMet(
            TestInfo testInfo
    ) {
        boolean googleHealthRequired =
                testInfo.getTestMethod()
                        .map(method -> method.isAnnotationPresent(
                                RequiresGoogleHealth.class
                        ))
                        .orElse(false);

        if (!googleHealthRequired) {
            return;
        }

        assumeTrue(
                healthConnectAvailable,
                "Google Health is required for this test, "
                        + "but it is not available on device "
                        + deviceUdid
                        + ". Android API level is below 34 and "
                        + "Health Connect app is not installed."
        );
    }

    private UiAutomator2Options createDriverOptions() {
        UiAutomator2Options options =
                new UiAutomator2Options();

        options.setUdid(
                deviceUdid
        );

        options.setAppPackage(
                AndroidConfig.APP_PACKAGE
        );

        options.setAppActivity(
                AndroidConfig.APP_ACTIVITY
        );

        options.setNoReset(true);

        options.setCapability(
                "appium:systemPort",
                systemPort
        );

        options.setCapability(
                "appium:forceAppLaunch",
                true
        );

        return options;
    }

    private void logSessionConfiguration() {
        System.out.println(
                "[Android test session] device="
                        + deviceUdid
                        + ", systemPort="
                        + systemPort
        );
    }

    private static int resolveSystemPort(
            String deviceUdid
    ) throws IOException, InterruptedException {

        String configuredPort =
                System.getProperty("android.systemPort");

        if (configuredPort != null
                && !configuredPort.isBlank()) {

            return parseSystemPort(configuredPort);
        }

        return resolveAutomaticSystemPort(deviceUdid);
    }

    private static int resolveAutomaticSystemPort(
            String deviceUdid
    ) throws IOException, InterruptedException {

        List<String> connectedDevices =
                DeviceUtils.getConnectedDeviceUdids();

        int deviceIndex =
                connectedDevices.indexOf(deviceUdid);

        ensureDeviceIsConnected(
                deviceUdid,
                deviceIndex
        );

        return calculateAutomaticSystemPort(
                deviceIndex
        );
    }

    private static void ensureDeviceIsConnected(
            String deviceUdid,
            int deviceIndex
    ) {
        if (deviceIndex >= 0) {
            return;
        }

        throw new IllegalStateException(
                "Android device disappeared before "
                        + "Appium session creation: "
                        + deviceUdid
        );
    }

    private static int calculateAutomaticSystemPort(
            int deviceIndex
    ) {
        int systemPort =
                SYSTEM_PORT_BASE + deviceIndex;

        if (systemPort <= SYSTEM_PORT_MAX) {
            return systemPort;
        }

        throw new IllegalStateException(
                "Too many Android devices are connected. "
                        + "Automatic systemPort allocation supports "
                        + "up to "
                        + (
                        SYSTEM_PORT_MAX
                                - SYSTEM_PORT_BASE
                                + 1
                )
                        + " devices. You may select a port manually "
                        + "using -Dandroid.systemPort=<PORT>."
        );
    }

    private static int parseSystemPort(
            String rawValue
    ) {
        try {
            int port =
                    Integer.parseInt(rawValue);

            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException(
                        "android.systemPort must be between "
                                + "1 and 65535, but was "
                                + port
                );
            }

            return port;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "android.systemPort must be a valid integer, "
                            + "but was: "
                            + rawValue,
                    exception
            );
        }
    }

    private static void ensureFitnessOnlineIsInstalled(
            String deviceUdid
    ) throws IOException, InterruptedException {

        if (DeviceUtils.isPackageInstalled(
                deviceUdid,
                AndroidConfig.APP_PACKAGE
        )) {
            return;
        }

        throw new IllegalStateException(
                "FitnessOnline is not installed on Android device "
                        + deviceUdid
                        + ". Expected package: "
                        + AndroidConfig.APP_PACKAGE
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}