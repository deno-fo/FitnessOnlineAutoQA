package pages.ios;

import components.ios.IosPasswordManagerPrompt;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Map;

public class EmailAuthPage extends IosBasePage {

    private final By signInTab =
            AppiumBy.accessibilityId("Sign in");

    private final By emailField =
            AppiumBy.className(
                    "XCUIElementTypeTextField"
            );

    private final By passwordField =
            AppiumBy.className(
                    "XCUIElementTypeSecureTextField"
            );

    private final By signInButton =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' "
                            + "AND name == 'SIGN IN'"
            );

    private final By invalidCredentialsMessage =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'Incorrect email or password.'"
            );

    private final By errorConfirmationButton =
            AppiumBy.accessibilityId("OK");

    private final IosPasswordManagerPrompt
            passwordManagerPrompt;

    private final By forgotPasswordLink =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'Forgot your password?'"
            );

    public EmailAuthPage(IOSDriver driver) {
        super(driver);
        passwordManagerPrompt =
                new IosPasswordManagerPrompt(driver);
    }

    public void selectSignInTab() {
        if (!driver.findElements(forgotPasswordLink).isEmpty()) {
            return;
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        signInTab
                )
        ).click();
    }

    public void enterEmail(String email) {
        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        emailField
                )
        );

        field.click();
        field.clear();
        field.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        passwordField
                )
        );

        Rectangle bounds = field.getRect();

        driver.executeScript(
                "mobile: tap",
                Map.of(
                        "x",
                        bounds.getX()
                                + bounds.getWidth() / 2,
                        "y",
                        bounds.getY()
                                + bounds.getHeight() / 2
                )
        );

        field.clear();
        field.sendKeys(password);
    }

    public void submitSignIn() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        signInButton
                )
        ).click();
    }

    public void signIn(String email, String password) {
        selectSignInTab();
        enterEmail(email);
        enterPassword(password);
        dismissSavePasswordPromptIfPresent();
        submitSignIn();
        dismissSavePasswordPromptIfPresent();
    }

    public boolean isInvalidCredentialsMessageDisplayed() {
        return wait.until(
                currentDriver -> {
                    dismissSavePasswordPromptIfPresent();
                    return isDisplayedNow(invalidCredentialsMessage);
                }
        );
    }

    public void dismissInvalidCredentialsMessageIfPresent() {
        dismissSavePasswordPromptIfPresent();

        if (driver.findElements(
                invalidCredentialsMessage
        ).isEmpty()) {
            return;
        }

        try {
            driver.switchTo()
                    .alert()
                    .accept();
        } catch (NoAlertPresentException ignored) {
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            errorConfirmationButton
                    )
            ).click();
        }

        dismissSavePasswordPromptIfPresent();
    }

    public void dismissSavePasswordPromptIfPresent() {
        passwordManagerPrompt.dismissIfPresent();
    }
}
