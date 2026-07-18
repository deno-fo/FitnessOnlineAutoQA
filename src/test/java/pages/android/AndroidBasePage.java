package pages.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.AndroidConfig;

import java.time.Duration;

public class AndroidBasePage {

    protected static final Duration DEFAULT_TIMEOUT =
            Duration.ofSeconds(10);

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    public AndroidBasePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                DEFAULT_TIMEOUT
        );
    }

    protected By id(String resourceId) {
        return AppiumBy.id(
                AndroidConfig.APP_PACKAGE + ":id/" + resourceId
        );
    }
}