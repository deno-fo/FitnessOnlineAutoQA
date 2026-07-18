package pages.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CustomWorkoutCreationPage extends AndroidBasePage {

    private final By workoutNameField =
            id("et_name");

    private final By saveButton =
            id("btn_create");

    public CustomWorkoutCreationPage(AndroidDriver driver) {
        super(driver);
    }

    public void enterWorkoutName(String workoutName) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        workoutNameField
                )
        ).clear();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        workoutNameField
                )
        ).sendKeys(workoutName);
    }

    public void saveWorkout() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        saveButton
                )
        ).click();
    }

    public void createWorkout(String workoutName) {
        enterWorkoutName(workoutName);
        saveWorkout();
    }
}