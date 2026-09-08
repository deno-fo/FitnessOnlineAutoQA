package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Map;

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

    private final By forgotPasswordLink =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'Forgot your password?'"
            );

    private final By savePasswordNotNowButton =
            AppiumBy.iOSNsPredicateString(
                    "name == 'Not Now' "
                            + "OR label == 'Not Now' "
                            + "OR value == 'Not Now'"
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
                        || isDisplayedNow(welcomeSkipButton))
        );
    }

    /**
     * Returns from the email authentication form to the initial
     * authentication options screen. iOS exposes the header/back
     * control as one unnamed container, so the native back gesture
     * is the stable interaction here.
     */
    public void returnToAuthenticationOptionsIfNeeded() {
        if (!isDisplayedNow(emailAuthenticationForm)
                && !isDisplayedNow(forgotPasswordLink)) {
            return;
        }

        swipeBack();

        wait.until(currentDriver ->
                !isDisplayedNow(forgotPasswordLink)
        );

        swipeBack();

        wait.until(currentDriver ->
                isDisplayedNow(emailAuthenticationButton)
                        || isDisplayedNow(welcomeSkipButton)
                        || isDisplayedNow(guestModeButton)
        );

        dismissSavePasswordPromptIfPresent(
                Duration.ofSeconds(5)
        );
    }

    public void dismissSavePasswordPromptIfPresent(
            Duration timeout
    ) {
        WebDriverWait promptWait =
                new WebDriverWait(driver, timeout);

        promptWait.pollingEvery(
                Duration.ofMillis(200)
        );

        try {
            promptWait.until(currentDriver -> {
                try {
                    WebElement button =
                            findVisibleElementNow(
                                    savePasswordNotNowButton
                            );

                    if (button == null) {
                        return false;
                    }

                    button.click();
                    return true;
                } catch (StaleElementReferenceException ignored) {
                    return false;
                }
            });
        } catch (TimeoutException ignored) {
            // The password manager prompt is optional.
        }
    }

    private void swipeBack() {
        Dimension screenSize =
                driver.manage()
                        .window()
                        .getSize();

        int y =
                (int) (screenSize.getHeight() * 0.20);

        driver.executeScript(
                "mobile: dragFromToForDuration",
                Map.of(
                        "duration", 0.12,
                        "fromX", 5,
                        "fromY", y,
                        "toX", screenSize.getWidth() - 5,
                        "toY", y
                )
        );
    }

    private boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }
}
