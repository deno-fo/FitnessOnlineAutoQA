package tests;

import annotations.RequiresGoogleHealth;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import utils.AndroidConfig;
import utils.AppiumConfig;
import utils.DeviceUtils;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.fail;

public class BaseAndroidTest {

    protected AndroidDriver driver;
    protected boolean healthConnectAvailable;

    @BeforeEach
    public void setUp(TestInfo testInfo)
            throws IOException, InterruptedException {

        AppiumConfig.ensureServerIsAvailable();

        String deviceUdid =
                DeviceUtils.getSingleConnectedDeviceUdid();
        ensureFitnessOnlineIsInstalled(deviceUdid);

        healthConnectAvailable =
                DeviceUtils.isHealthConnectAvailable(
                        deviceUdid
                );

        boolean googleHealthRequired =
                testInfo.getTestMethod()
                        .map(method -> method.isAnnotationPresent(
                                RequiresGoogleHealth.class
                        ))
                        .orElse(false);

        if (googleHealthRequired && !healthConnectAvailable) {
            fail(
                    "Google Health is required for this test, "
                            + "but it is not available on this device. "
                            + "Android API level is below 34 and "
                            + "Health Connect app is not installed."
            );
        }

        DeviceUtils.clearAppData(
                deviceUdid,
                AndroidConfig.APP_PACKAGE
        );

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
                "appium:forceAppLaunch",
                true
        );

        driver = new AndroidDriver(
                new URL(AppiumConfig.SERVER_URL),
                options
        );
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