package pages.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends AndroidBasePage {

    private final By welcomeSkipButton =
            id("btnSkip");
    private final By loginSkipButton =
            id("skip");
    private final By emailAuthButton =
            id("byEmail");

    public LoginPage(AndroidDriver driver) {
        super(driver);
    }

    public void skipWelcomeScreen() {
        if (!driver.findElements(welcomeSkipButton).isEmpty()) {
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            welcomeSkipButton
                    )
            ).click();
        }
    }

    public void skipLoginScreen() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        loginSkipButton
                )
        ).click();
    }

    public void enterGuestMode() {
        skipWelcomeScreen();
        skipLoginScreen();
    }

    public void openEmailAuthentication() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        emailAuthButton
                )
        ).click();
    }

    public boolean isEmailAuthenticationOptionDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        emailAuthButton
                )
        ).isDisplayed();
    }
}