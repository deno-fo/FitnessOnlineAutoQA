package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends IosBasePage {

    private final By welcomeSkipButton =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'Skip'"
            );

    private final By emailAuthenticationButton =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'Sign in/Sign up with email'"
            );

    public LoginPage(IOSDriver driver) {
        super(driver);
    }

    public void openEmailAuthentication() {
        if (!driver.findElements(welcomeSkipButton).isEmpty()) {
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            welcomeSkipButton
                    )
            ).click();

            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            emailAuthenticationButton
                    )
            ).click();

            return;
        }

        if (!driver.findElements(emailAuthenticationButton).isEmpty()) {
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            emailAuthenticationButton
                    )
            ).click();
        }
    }
}