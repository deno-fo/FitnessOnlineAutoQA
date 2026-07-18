package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.AndroidConfig;

public class WorkoutDayEditorPage extends AndroidBasePage {

    private final By workoutDayNameField =
            id("et_name");

    private final By addNewExerciseButton =
            id("name");

    private final By saveButton =
            id("btn_save");

    private final String exerciseTitleId =
            AndroidConfig.APP_PACKAGE + ":id/tv_title";

    private final By deleteWorkoutDayButton =
            id("btn_delete");

    private final By confirmDeleteButton =
            AppiumBy.id("android:id/button1");

    public WorkoutDayEditorPage(AndroidDriver driver) {
        super(driver);
    }

    public void enterWorkoutDayName(String workoutDayName) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        workoutDayNameField
                )
        ).sendKeys(workoutDayName);
    }

    public void openExerciseSelection() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        addNewExerciseButton
                )
        ).click();
    }

    public void openExerciseSettings(String exerciseName) {
        By exerciseTitle = AppiumBy.androidUIAutomator(
                "new UiSelector()" +
                        ".resourceId(\"" + exerciseTitleId + "\")" +
                        ".text(\"" + exerciseName + "\")"
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(exerciseTitle)
        ).click();
    }

    public void saveWorkoutDay() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        saveButton
                )
        ).click();
    }

    public void deleteWorkoutDay() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        deleteWorkoutDayButton
                )
        ).click();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        confirmDeleteButton
                )
        ).click();
    }
}