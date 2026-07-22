package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HealthAccessPage
        extends IosBasePage {

    private final By healthTitle =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'Health'"
            );

    private final By grantAccessButton =
            AppiumBy.accessibilityId(
                    "GRANT ACCESS"
            );

    private final By turnOnAllButton =
            AppiumBy.accessibilityId(
                    "Turn On All"
            );

    private final By allowButton =
            AppiumBy.accessibilityId(
                    "UIA.Health.Allow.Button"
            );

    public HealthAccessPage(
            IOSDriver driver
    ) {
        super(driver);
    }

    public boolean isHealthScreenDisplayed() {
        return isDisplayedNow(healthTitle);
    }

    public boolean isTurnOnAllButtonDisplayed() {
        return isDisplayedNow(turnOnAllButton);
    }

    public boolean isAllowButtonDisplayed() {
        return isDisplayedNow(allowButton);
    }

    public void grantAccess() {
        click(grantAccessButton);
    }

    public void turnOnAll() {
        click(turnOnAllButton);
    }

    public void allow() {
        click(allowButton);
    }

    private void click(By locator) {
        wait.until(
                ExpectedConditions
                        .elementToBeClickable(locator)
        ).click();
    }
}