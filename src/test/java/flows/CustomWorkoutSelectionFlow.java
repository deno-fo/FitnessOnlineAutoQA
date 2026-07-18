package flows;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.android.AndroidBasePage;

public class CustomWorkoutSelectionFlow
        extends AndroidBasePage {

    private final By workoutBuilderButton =
            AppiumBy.androidUIAutomator(
                    "new UiSelector().text(\"USE WORKOUT BUILDER\")"
            );

    private final By workoutBuilderButtonAfterScroll =
            AppiumBy.androidUIAutomator(
                    "new UiScrollable(" +
                            "new UiSelector().scrollable(true)" +
                            ")" +
                            ".scrollIntoView(" +
                            "new UiSelector().text(\"USE WORKOUT BUILDER\")" +
                            ")"
            );

    public CustomWorkoutSelectionFlow(AndroidDriver driver) {
        super(driver);
    }

    public void openWorkoutBuilder() {
        wait.until(currentDriver -> {
            try {
                if (!currentDriver
                        .findElements(workoutBuilderButton)
                        .isEmpty()) {
                    return true;
                }

                currentDriver.findElement(
                        workoutBuilderButtonAfterScroll
                );

                return !currentDriver
                        .findElements(workoutBuilderButton)
                        .isEmpty();

            } catch (WebDriverException exception) {
                return false;
            }
        });

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        workoutBuilderButton
                )
        ).click();
    }
}