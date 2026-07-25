package tests.ios;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriverException;
import utils.AppiumConfig;
import utils.IosConfig;
import utils.IosDevice;
import utils.IosDeviceContext;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public abstract class BaseIosTest {

    protected IOSDriver driver;

    @BeforeEach
    public void setUp()
            throws IOException,
            InterruptedException {

        AppiumConfig
                .ensureServerIsAvailable();

        IosDevice device =
                IosDeviceContext.getRequired();

        XCUITestOptions options =
                new XCUITestOptions();

        options.setDeviceName(
                device.name()
        );

        options.setUdid(
                device.udid()
        );

        options.setBundleId(
                IosConfig.BUNDLE_ID
        );

        options.setCapability(
                "appium:xcodeOrgId",
                IosConfig.TEAM_ID
        );

        options.setCapability(
                "appium:xcodeSigningId",
                "Apple Development"
        );

        options.setCapability(
                "appium:"
                        + "allowProvisioningDeviceRegistration",
                true
        );

        options.setUpdatedWdaBundleId(
                IosConfig.WDA_BUNDLE_ID
        );

        options.setCapability(
                "appium:wdaLocalPort",
                device.wdaLocalPort()
        );

        options.setCapability(
                "appium:derivedDataPath",
                device.derivedDataPath()
        );

        options.setCapability(
                "appium:showXcodeLog",
                true
        );

        options.setNoReset(true);

        driver = new IOSDriver(
                new URL(
                        AppiumConfig.SERVER_URL
                ),
                options
        );

        driver.setSettings(
                Map.of(
                        "animationCoolOffTimeout",
                        0.5
                )
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver == null) {
            return;
        }

        try {
            driver.quit();
        } catch (WebDriverException ignored) {
            // Сессия могла уже завершиться
            // из-за ошибки WDA/Appium.
        } finally {
            driver = null;
        }
    }
}
