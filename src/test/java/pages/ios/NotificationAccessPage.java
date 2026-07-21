package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class NotificationAccessPage extends IosBasePage {

    private final By notificationsTitle =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'Notifications'"
            );

    private final By grantAccessButton =
            AppiumBy.accessibilityId("GRANT ACCESS");

    public NotificationAccessPage(IOSDriver driver) {
        super(driver);
    }

    public boolean isNotificationScreenDisplayed() {
        return driver.findElements(notificationsTitle)
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public void grantAccess() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        grantAccessButton
                )
        ).click();
    }
}