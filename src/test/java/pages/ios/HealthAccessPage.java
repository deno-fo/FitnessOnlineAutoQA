package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HealthAccessPage extends IosBasePage {

    private final By healthTitle =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'Health'"
            );

    private final By grantAccessButton =
            AppiumBy.accessibilityId("GRANT ACCESS");

    private final By turnOnAllButton =
            AppiumBy.accessibilityId("Turn On All");

    private final By allowButton =
            AppiumBy.accessibilityId(
                    "UIA.Health.Allow.Button"
            );

    public HealthAccessPage(IOSDriver driver) {
        super(driver);
    }

    public boolean isHealthScreenDisplayed() {
        return isDisplayed(healthTitle);
    }

    public boolean isTurnOnAllButtonDisplayed() {
        return isDisplayed(turnOnAllButton);
    }

    public boolean isAllowButtonDisplayed() {
        return isDisplayed(allowButton);
    }

    public void grantAccess() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        grantAccessButton
                )
        ).click();
    }

    public void turnOnAll() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        turnOnAllButton
                )
        ).click();
    }

    public void allow() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        allowButton
                )
        ).click();
    }

    private boolean isDisplayed(By locator) {
        return driver.findElements(locator)
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }
}