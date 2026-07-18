package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.remote.RemoteWebElement;
import java.util.Map;

public class WorkoutExecutionPage extends AndroidBasePage {

    private final By weightField =
            id("value");

    private final By repeatsField =
            id("repeats");

    private final By addSetButton =
            id("add");

    private final By finishWorkoutButton =
            AppiumBy.id("android:id/button1");

    private final By continueWorkoutButton =
            AppiumBy.id("android:id/button2");

    public WorkoutExecutionPage(AndroidDriver driver) {
        super(driver);
    }

    public void enterWeight(String weight) {
        replaceText(weightField, weight);
    }

    public void enterRepeats(String repeats) {
        replaceText(repeatsField, repeats);
    }

    public void addSet() {
        wait.until(
                ExpectedConditions.elementToBeClickable(addSetButton)
        ).click();
    }

    public void recordSet(String weight, String repeats) {
        enterWeight(weight);
        enterRepeats(repeats);
        addSet();
    }

    public void finishWorkout() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        finishWorkoutButton
                )
        ).click();
    }

    public void continueWorkout() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        continueWorkoutButton
                )
        ).click();
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