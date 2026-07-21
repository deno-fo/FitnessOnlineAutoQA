package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MainPage extends IosBasePage {

    private final By moreTab =
            AppiumBy.accessibilityId("More");

    private final By nextTrainingLabel =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'Next training'"
            );

    public MainPage(IOSDriver driver) {
        super(driver);
    }

    public boolean isOpened() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        moreTab
                )
        ).isDisplayed();
    }

    public boolean isOpenedNow() {
        try {
            List<WebElement> elements =
                    driver.findElements(moreTab);

            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    return true;
                }
            }

            return false;
        } catch (StaleElementReferenceException ignored) {
            return false;
        }
    }

    public boolean isDashboardOpenedNow() {
        try {
            List<WebElement> elements =
                    driver.findElements(
                            nextTrainingLabel
                    );

            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    return true;
                }
            }

            return false;
        } catch (StaleElementReferenceException ignored) {
            return false;
        }
    }

    public boolean openMoreIfAvailable() {
        WebDriverWait shortWait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(3)
                );

        try {
            WebElement more =
                    shortWait.until(
                            ExpectedConditions.elementToBeClickable(
                                    moreTab
                            )
                    );

            more.click();
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public void openMore() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        moreTab
                )
        ).click();
    }
}