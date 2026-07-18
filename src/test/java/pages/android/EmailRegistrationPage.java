package pages.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class EmailRegistrationPage extends AndroidBasePage {
    private final By emailField =
            id("register_email");
    private final By passwordField =
            id("register_password");
    private final By nameField =
            id("register_name");
    private final By surnameField =
            id("register_surname");
    private final By maleOption =
            id("register_men");
    private final By femaleOption =
            id("register_women");
    private final By signUpButton =
            id("btn_register");

    public EmailRegistrationPage(AndroidDriver driver) {
        super(driver);
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

    public void enterName(String name) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        nameField
                )
        ).sendKeys(name);
    }

    public void enterSurname(String surname) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        surnameField
                )
        ).sendKeys(surname);
    }

    public void selectMale() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        maleOption
                )
        ).click();
    }

    public void selectFemale() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        femaleOption
                )
        ).click();
    }

    public void submitRegistration() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        signUpButton
                )
        ).click();
    }

    public void registerMaleUser(
            String email,
            String password,
            String name,
            String surname
    ) {
        enterEmail(email);
        enterPassword(password);
        enterName(name);
        enterSurname(surname);
        selectMale();
        submitRegistration();
    }

    public void registerFemaleUser(
            String email,
            String password,
            String name,
            String surname
    ) {
        enterEmail(email);
        enterPassword(password);
        enterName(name);
        enterSurname(surname);
        selectFemale();
        submitRegistration();
    }
}