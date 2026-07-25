package flows.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.android.AndroidBasePage;

public class PreMadeWorkoutSelectionFlow extends AndroidBasePage {

    private final By generalMuscleBuildingProgram =
            AppiumBy.androidUIAutomator(
                    "new UiSelector().text(\"General muscle building\")"
            );

    private final By gymTrainingOption =
            AppiumBy.androidUIAutomator(
                    "new UiSelector().text(\"In the gym\")"
            );

    private final By expertDifficultyOption =
            AppiumBy.androidUIAutomator(
                    "new UiSelector().text(\"Expert\")"
            );

    private final By enrollButton =
            AppiumBy.id(
                    "fitness.online.app:id/btn_enroll"
            );

    public PreMadeWorkoutSelectionFlow(
            AndroidDriver driver
    ) {
        super(driver);
    }

    public void selectGeneralMuscleBuildingForGymAtExpertLevel() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        generalMuscleBuildingProgram
                )
        ).click();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        gymTrainingOption
                )
        ).click();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        expertDifficultyOption
                )
        ).click();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        enrollButton
                )
        ).click();
    }

    public boolean isReady() {
        return isReadyWithoutWait(
                generalMuscleBuildingProgram
        );
    }
}