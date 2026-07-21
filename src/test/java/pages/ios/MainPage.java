package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MainPage extends IosBasePage {

    private final By moreTab =
            AppiumBy.accessibilityId("More");

    public MainPage(IOSDriver driver) {
        super(driver);
    }

    public boolean isOpenedNow() {
        return driver.findElements(moreTab)
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public boolean isOpened() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        moreTab
                )
        ).isDisplayed();
    }

    public void openMore() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        moreTab
                )
        ).click();
    }
}