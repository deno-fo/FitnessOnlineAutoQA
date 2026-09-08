package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

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

    /*
     * Recent Android builds no longer expose the report value with the
     * stats_reps_made_text resource id. The value itself is still present
     * and has a stable, user-visible phrase, so keep a text fallback.
     */
    private final By completedRepeatsTextFallback =
            AppiumBy.xpath(
                    "//*[contains(@text, 'reps') "
                            + "and contains(@text, 'expected')]"
            );

    private final By liftedWeightText =
            id("stats_lifted_weight_text");

    private final By liftedWeightTextFallback =
            AppiumBy.xpath(
                    "//*[contains(@text, 'kg') "
                            + "and contains(@text, 'expected')]"
            );

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
        return getMetricText(
                completedRepeatsText,
                completedRepeatsTextFallback
        );
    }

    public String getLiftedWeightText() {
        return getMetricText(
                liftedWeightText,
                liftedWeightTextFallback
        );
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

    private String getMetricText(By primaryLocator, By fallbackLocator) {
        List<By> locators = List.of(
                primaryLocator,
                fallbackLocator
        );

        wait.until(
                currentDriver -> hasTextElement(
                        currentDriver,
                        locators
                )
        );

        return findTextElement(
                locators
        ).getText();
    }

    private boolean hasTextElement(
            WebDriver currentDriver,
            List<By> locators
    ) {
        return locators.stream()
                .anyMatch(locator -> currentDriver
                        .findElements(locator)
                        .stream()
                        .anyMatch(this::hasText));
    }

    private WebElement findTextElement(List<By> locators) {
        return locators.stream()
                .flatMap(locator -> driver.findElements(locator).stream())
                .filter(this::hasText)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "No workout report metric was found."
                ));
    }

    private boolean hasText(WebElement element) {
        try {
            String text = element.getText();
            return text != null && !text.isBlank();
        } catch (StaleElementReferenceException exception) {
            return false;
        }
    }
}
