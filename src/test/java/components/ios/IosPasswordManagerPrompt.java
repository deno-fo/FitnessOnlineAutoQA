package components.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/** Handles the optional iOS Password AutoFill sheet. */
public final class IosPasswordManagerPrompt {

    static final String NOT_NOW_BUTTON_XPATH =
            "//XCUIElementTypeSheet[@name='Save Password?']"
                    + "//*[@name='Not Now' "
                    + "or @label='Not Now' "
                    + "or @value='Not Now']";

    private final IOSDriver driver;

    private final List<By> notNowButtons = List.of(
            AppiumBy.xpath(NOT_NOW_BUTTON_XPATH),
            AppiumBy.iOSNsPredicateString(
                    "name == 'Not Now' "
                            + "OR label == 'Not Now' "
                            + "OR value == 'Not Now'"
            )
    );

    public IosPasswordManagerPrompt(IOSDriver driver) {
        this.driver = driver;
    }

    public boolean dismissIfPresent() {
        for (By locator : notNowButtons) {
            try {
                for (WebElement button : driver.findElements(locator)) {
                    try {
                        if (button.isDisplayed()) {
                            button.click();
                            return true;
                        }
                    } catch (StaleElementReferenceException ignored) {
                        // The sheet was recreated; try the next match.
                    }
                }
            } catch (StaleElementReferenceException ignored) {
                // The accessibility tree changed; try the fallback locator.
            }
        }

        return false;
    }

    public void waitAndDismissIfPresent(Duration timeout) {
        WebDriverWait promptWait =
                new WebDriverWait(driver, timeout);

        promptWait.pollingEvery(Duration.ofMillis(200));

        try {
            promptWait.until(currentDriver -> dismissIfPresent());
        } catch (TimeoutException ignored) {
            // The password manager prompt is optional.
        }
    }
}
