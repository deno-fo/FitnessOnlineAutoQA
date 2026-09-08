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
import pages.ios.LoginPage;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
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

        // iOS Password AutoFill may show a blocking sheet on app launch.
        new LoginPage(driver)
                .dismissSavePasswordPromptIfPresent(
                        Duration.ofSeconds(5)
                );
    }

    @AfterEach
    public void tearDown() {
        if (driver == null) {
            return;
        }

        try {
            // Subclass cleanup runs first. Confirm it actually restored a signed-out screen.
            new LoginPage(driver).waitUntilSignedOut();
        } catch (RuntimeException exception) {
            String reason = "Cleanup could not confirm the signed-out screen";
            IosDeviceContext.markCleanupFailed(reason);
            throw new IllegalStateException(
                    reason + "; remaining tests on this iPhone will be skipped.",
                    exception
            );
        } finally {
            try {
                driver.quit();
            } catch (WebDriverException ignored) {
                // The session may already have ended after a WDA/Appium failure.
            } finally {
                driver = null;
            }
        }
    }
}
