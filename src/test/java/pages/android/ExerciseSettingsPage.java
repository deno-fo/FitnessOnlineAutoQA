package pages.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.remote.RemoteWebElement;
import java.util.Map;

public class ExerciseSettingsPage extends AndroidBasePage {

    private final By setsField =
            id("etSet");

    private final By repeatsField =
            id("etRepeat");

    private final By weightField =
            id("etWeight");

    private final By saveButton =
            id("btnSave");

    public ExerciseSettingsPage(AndroidDriver driver) {
        super(driver);
    }

    public void setSets(String sets) {
        replaceText(setsField, sets);
    }

    public void setRepeats(String repeats) {
        replaceText(repeatsField, repeats);
    }

    public void setWeight(String weight) {
        replaceText(weightField, weight);
    }

    public void saveSettings() {
        wait.until(
                ExpectedConditions.elementToBeClickable(saveButton)
        ).click();
    }

    public void configureExercise(
            String sets,
            String repeats,
            String weight
    ) {
        setSets(sets);
        setRepeats(repeats);
        setWeight(weight);
        saveSettings();
    }

    private void replaceText(By locator, String value) {
        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        String elementId =
                ((RemoteWebElement) field).getId();

        driver.executeScript(
                "mobile: replaceElementValue",
                Map.of(
                        "elementId", elementId,
                        "text", value
                )
        );
    }
}