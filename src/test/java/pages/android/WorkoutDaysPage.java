package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.TimeoutException;
import utils.AndroidConfig;

public class WorkoutDaysPage extends AndroidBasePage {

    private final By addWorkoutDayButton =
            id("fab");

    private final By workoutDayTitle =
            id("tv_title");

    private final By emptyWorkoutDaysImage =
            id("image");

    public WorkoutDaysPage(AndroidDriver driver) {
        super(driver);
    }

    public void addWorkoutDay() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        addWorkoutDayButton
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

    public void openWorkoutDay(String workoutDayName) {
        By workoutDayTitle = AppiumBy.androidUIAutomator(
                "new UiSelector()" +
                        ".resourceId(\"" +
                        AndroidConfig.APP_PACKAGE +
                        ":id/tv_title\")" +
                        ".text(\"" + workoutDayName + "\")"
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        workoutDayTitle
                )
        ).click();
    }
    public boolean isWorkoutDayDisplayed(String workoutDayName) {
        By workoutDayTitle =
                AppiumBy.androidUIAutomator(
                        "new UiSelector().text(\""
                                + workoutDayName
                                + "\")"
                );
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            workoutDayTitle
                    )
            ).isDisplayed();
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public boolean isWorkoutDayAbsent(String workoutDayName) {
        By workoutDayTitle = AppiumBy.androidUIAutomator(
                "new UiSelector()" +
                        ".resourceId(\"" +
                        AndroidConfig.APP_PACKAGE +
                        ":id/tv_title\")" +
                        ".text(\"" + workoutDayName + "\")"
        );
        return wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        workoutDayTitle
                )
        );
    }

    public boolean isEmptyWorkoutDaysPlaceholderDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        emptyWorkoutDaysImage
                )
        ).isDisplayed();
    }
}