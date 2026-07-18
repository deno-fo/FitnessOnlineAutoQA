package tests.ios;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import utils.IosConfig;
import utils.AppiumConfig;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class BaseIosTest {

    protected IOSDriver driver;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        XCUITestOptions options = new XCUITestOptions();

        options.setDeviceName(
                IosConfig.DEVICE_NAME
        );

        options.setUdid(
                IosConfig.UDID
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

        options.setUpdatedWdaBundleId(
                IosConfig.WDA_BUNDLE_ID
        );

        options.setCapability(
                "appium:showXcodeLog",
                true
        );

        options.setNoReset(true);

        driver = new IOSDriver(
                new URL(AppiumConfig.SERVER_URL),
                options
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}