package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class WorkoutReportPage extends AndroidBasePage {

    private final By reportTitle =
            id("title");

    private final By workoutDayName =
            id("text_view_date_title");

    private final By totalCompletionPercentage =
            AppiumBy.xpath(
                    "//*[@resource-id=" +
                            "'fitness.online.app:id/percentage_bar_total']" +
                            "//*[@text='100%']"
            );

    private final By completedRepeatsText =
            id("stats_reps_made_text");

    private final By liftedWeightText =
            id("stats_lifted_weight_text");

    private final By closeReportButton =
            AppiumBy.accessibilityId("Navigate up");

    private final By shareButton =
            id("item_share");

    private final By activitiesBlock =
            id("layout_activities");

    public WorkoutReportPage(AndroidDriver driver) {
        super(driver);
    }

    public String getWorkoutDayName() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        workoutDayName
                )
        ).getText();
    }

    public String getCompletionPercentage() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        totalCompletionPercentage
                )
        ).getText();
    }

    public String getCompletedRepeatsText() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        completedRepeatsText
                )
        ).getText();
    }

    public String getLiftedWeightText() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        liftedWeightText
                )
        ).getText();
    }

    public boolean isActivitiesBlockDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        activitiesBlock
                )
        ).isDisplayed();
    }

    public boolean isShareButtonDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        shareButton
                )
        ).isDisplayed();
    }

    public void closeReport() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        reportTitle
                )
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        closeReportButton
                )
        ).click();

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        reportTitle
                )
        );
    }
}