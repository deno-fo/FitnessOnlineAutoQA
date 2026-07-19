package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.Rectangle;
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

    public EmailAuthPage(IOSDriver driver) {
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
                        bounds.getX() + bounds.getWidth() / 2,
                        "y",
                        bounds.getY() + bounds.getHeight() / 2
                )
        );

        field.sendKeys(password);

        wait.until(currentDriver -> {
            String value = field.getAttribute("value");

            return value != null
                    && !value.isBlank()
                    && !value.equals("Enter your password");
        });
    }

    public void submitSignIn() {
        hideKeyboardIfPresent();

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

    public boolean isInvalidCredentialsMessageDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        invalidCredentialsMessage
                )
        ).isDisplayed();
    }

    private void hideKeyboardIfPresent() {
        try {
            driver.hideKeyboard();
        } catch (WebDriverException ignored) {
            // Keyboard is already hidden.
        }
    }
}