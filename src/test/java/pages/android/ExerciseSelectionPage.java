package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.AndroidConfig;

public class ExerciseSelectionPage extends AndroidBasePage {

    private final By chestMuscleGroup =
            AppiumBy.androidUIAutomator(
                    "new UiSelector().text(\"Chest\")"
            );

    private final By firstExerciseCheckbox =
            AppiumBy.androidUIAutomator(
                    "new UiSelector()" +
                            ".resourceId(\"" +
                            AndroidConfig.APP_PACKAGE +
                            ":id/checkBoxSelected\")" +
                            ".instance(0)"
            );

    private final By addButton =
            id("buttonAdd");

    public ExerciseSelectionPage(AndroidDriver driver) {
        super(driver);
    }

    public void openChestExercises() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        chestMuscleGroup
                )
        ).click();
    }

    public void selectFirstExercise() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        firstExerciseCheckbox
                )
        ).click();
    }

    public void addSelectedExercises() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        addButton
                )
        ).click();
    }

    public void addFirstChestExercise() {
        openChestExercises();
        selectFirstExercise();
        addSelectedExercises();
    }
}