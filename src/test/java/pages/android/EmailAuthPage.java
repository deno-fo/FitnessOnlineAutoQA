package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class EmailAuthPage extends AndroidBasePage {
    private final By signInTab =
            AppiumBy.accessibilityId("Sign in");
    private final By emailField =
            id("login_email");
    private final By passwordField =
            id("login_password");
    private final By signInButton =
            id("login_button");
    private final By errorSnackbar =
            id("snackbar_text");

    public EmailAuthPage(AndroidDriver driver) {
        super(driver);
    }

    public void selectSignInTab() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        signInTab
                )
        ).click();
    }

    public void enterEmail(String email) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        emailField
                )
        ).sendKeys(email);
    }

    public void enterPassword(String password) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        passwordField
                )
        ).sendKeys(password);
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
        submitSignIn();
    }

    public boolean isErrorSnackbarDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        errorSnackbar
                )
        ).isDisplayed();
    }
}