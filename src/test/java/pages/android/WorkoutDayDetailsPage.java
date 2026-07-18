package pages.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class WorkoutDayDetailsPage extends AndroidBasePage {

    private final By exerciseRecommendations =
            id("tv_recom");

    private final By beginWorkoutButton =
            id("begin_button");

    private final By editWorkoutDayButton =
            id("action_edit");

    private final By workoutDayTitle =
            id("day_title");

    public WorkoutDayDetailsPage(AndroidDriver driver) {
        super(driver);
    }

    public void beginWorkout() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        beginWorkoutButton
                )
        ).click();
    }

    public void openWorkoutDayEditor() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        editWorkoutDayButton
                )
        ).click();
    }

    public String getWorkoutDayTitle() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        workoutDayTitle
                )
        ).getText();
    }

    public String getExerciseRecommendations() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        exerciseRecommendations
                )
        ).getText();
    }
}