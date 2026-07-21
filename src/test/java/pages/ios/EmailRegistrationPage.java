package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Map;
import java.util.List;

public class EmailRegistrationPage extends IosBasePage {

    private final By emailField =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeTextField' "
                            + "AND value == 'Enter your email address'"
            );

    private final By passwordField =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeSecureTextField' "
                            + "AND value == 'Enter your password'"
            );

    private final By firstNameField =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeTextField' "
                            + "AND value == 'Enter your first name'"
            );

    private final By lastNameField =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeTextField' "
                            + "AND value == 'Enter your last name'"
            );

    private final By maleButton =
            AppiumBy.accessibilityId("Male");

    private final By signUpButton =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' "
                            + "AND name == 'SIGN UP'"
            );

    public EmailRegistrationPage(IOSDriver driver) {
        super(driver);
    }

    public void registerMaleUser(
            String email,
            String password,
            String firstName,
            String lastName
    ) {
        enterText(emailField, email);
        enterPassword(password);
        enterText(firstNameField, firstName);

        hideKeyboardIfCovering(lastNameField);
        enterText(lastNameField, lastName);

        hideKeyboardIfCovering(maleButton);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        maleButton
                )
        ).click();

        hideKeyboardIfCovering(signUpButton);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        signUpButton
                )
        ).click();
    }

    private void enterText(By locator, String value) {
        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        locator
                )
        );

        field.click();
        field.clear();
        field.sendKeys(value);
    }

    private void enterPassword(String password) {
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

    private void hideKeyboardIfCovering(
            By targetLocator
    ) {
        List<WebElement> keyboards =
                driver.findElements(
                        AppiumBy.className(
                                "XCUIElementTypeKeyboard"
                        )
                );

        if (keyboards.isEmpty()) {
            return;
        }

        WebElement keyboard = keyboards.get(0);
        WebElement target = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        targetLocator
                )
        );

        Rectangle keyboardBounds =
                keyboard.getRect();

        Rectangle targetBounds =
                target.getRect();

        int targetBottom =
                targetBounds.getY()
                        + targetBounds.getHeight();

        if (targetBottom
                < keyboardBounds.getY()) {
            return;
        }

        try {
            driver.hideKeyboard();
        } catch (WebDriverException ignored) {
            // Элемент не перекрыт или клавиатура уже закрыта.
        }
    }
}