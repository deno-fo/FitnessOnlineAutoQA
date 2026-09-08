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

    private final By guestModeButton =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' "
                            + "AND name == 'SKIP'"
            );

    private final By emailAuthenticationButton =
            AppiumBy.iOSNsPredicateString(
                    "(type == 'XCUIElementTypeStaticText' "
                            + "OR type == 'XCUIElementTypeButton') "
                            + "AND (name == "
                            + "'Sign in/Sign up with email' "
                            + "OR label == "
                            + "'Sign in/Sign up with email')"
            );

    private final By emailAuthenticationForm =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeTextField' "
                            + "OR type == "
                            + "'XCUIElementTypeSecureTextField' "
                            + "OR name == 'Sign in'"
            );

    public LoginPage(IOSDriver driver) {
        super(driver);
    }

    public void enterGuestMode() {
        wait.until(currentDriver ->
                isPresent(welcomeSkipButton)
                        || isPresent(guestModeButton)
        );

        if (isPresent(welcomeSkipButton)) {
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            welcomeSkipButton
                    )
            ).click();
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        guestModeButton
                )
        ).click();
    }

    public void openEmailAuthentication() {
        wait.until(currentDriver ->
                isPresent(welcomeSkipButton)
                        || isPresent(emailAuthenticationButton)
                        || isPresent(emailAuthenticationForm)
        );

        if (isPresent(emailAuthenticationForm)) {
            return;
        }

        if (isPresent(welcomeSkipButton)) {
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            welcomeSkipButton
                    )
            ).click();
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        emailAuthenticationButton
                )
        ).click();
    }

    public boolean isEmailAuthenticationOptionDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        emailAuthenticationButton
                )
        ).isDisplayed();
    }

    public void waitUntilSignedOut() {
        By alert = AppiumBy.className("XCUIElementTypeAlert");
        wait.until(currentDriver ->
                !isDisplayedNow(alert)
                        && (isDisplayedNow(emailAuthenticationButton)
                        || isDisplayedNow(emailAuthenticationForm)
                        || isDisplayedNow(welcomeSkipButton))
        );
    }

    private boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }
}
