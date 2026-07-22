package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MainPage extends IosBasePage {

    private final By moreTab =
            AppiumBy.accessibilityId(
                    "More"
            );

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
                currentDriver ->
                        isOpenedNow()
        );
    }

    public boolean isOpenedNow() {
        return isDisplayedNow(moreTab);
    }

    public boolean isDashboardOpenedNow() {
        return isDisplayedNow(
                nextTrainingLabel
        );
    }

    public boolean openMoreIfAvailable() {
        WebDriverWait shortWait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(2)
                );

        shortWait.pollingEvery(
                Duration.ofMillis(250)
        );

        try {
            WebElement more =
                    shortWait.until(
                            ExpectedConditions
                                    .elementToBeClickable(
                                            moreTab
                                    )
                    );

            more.click();
            return true;
        } catch (TimeoutException ignored) {
            return false;
        }
    }

    public void openMore() {
        wait.until(
                ExpectedConditions
                        .elementToBeClickable(
                                moreTab
                        )
        ).click();
    }
}