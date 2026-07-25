package pages.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.StaleElementReferenceException;
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

    protected boolean isDisplayedWithoutWait(
            By locator
    ) {
        return driver.findElements(locator)
                .stream()
                .anyMatch(element -> {
                    try {
                        return element.isDisplayed();
                    } catch (
                            StaleElementReferenceException
                                    exception
                    ) {
                        return false;
                    }
                });
    }

    protected boolean isReadyWithoutWait(
            By locator
    ) {
        return driver.findElements(locator)
                .stream()
                .anyMatch(element -> {
                    try {
                        return element.isDisplayed()
                                && element.isEnabled();
                    } catch (
                            StaleElementReferenceException
                                    exception
                    ) {
                        return false;
                    }
                });
    }
}