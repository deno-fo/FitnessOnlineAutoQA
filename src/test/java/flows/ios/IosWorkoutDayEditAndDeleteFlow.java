package flows.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class IosWorkoutDayEditAndDeleteFlow {

    private final IOSDriver driver;
    private final WebDriverWait wait;

    private final By editWorkoutDayButton =
            AppiumBy.accessibilityId(
                    "tabEdit"
            );

    private final By workoutDayNameField =
            AppiumBy.className(
                    "XCUIElementTypeTextField"
            );

    private final By setsField =
            AppiumBy.xpath(
                    "(//XCUIElementTypeTextField)[1]"
            );

    private final By repeatsField =
            AppiumBy.xpath(
                    "(//XCUIElementTypeTextField)[2]"
            );

    private final By weightField =
            AppiumBy.xpath(
                    "(//XCUIElementTypeTextField)[3]"
            );

    private final By saveButton =
            AppiumBy.accessibilityId(
                    "SAVE"
            );

    private final By deleteButton =
            AppiumBy.accessibilityId(
                    "DELETE"
            );

    private final By beginWorkoutButton =
            AppiumBy.accessibilityId(
                    "BEGIN WORKOUT"
            );

    private final By emptyWorkoutDaysPlaceholder =
            AppiumBy.accessibilityId(
                    "Your list of exercises is empty"
            );

    public IosWorkoutDayEditAndDeleteFlow(
            IOSDriver driver
    ) {
        this.driver = driver;

        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        this.wait.pollingEvery(
                Duration.ofMillis(250)
        );
    }

    public void editWorkoutDay(
            String exerciseName,
            String updatedWorkoutDayName,
            String updatedSets,
            String updatedRepeats,
            String updatedWeight
    ) {
        openWorkoutDayEditor();

        replaceText(
                workoutDayNameField,
                updatedWorkoutDayName
        );

        click(
                editableExercise(exerciseName)
        );

        wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(
                                weightField
                        )
        );

        replaceText(
                setsField,
                updatedSets
        );

        replaceText(
                repeatsField,
                updatedRepeats
        );

        replaceText(
                weightField,
                updatedWeight
        );

        /*
         * Первый SAVE сохраняет настройки
         * конкретного упражнения.
         */
        click(saveButton);

        wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(
                                deleteButton
                        )
        );

        /*
         * Второй SAVE сохраняет тренировочный день.
         */
        click(saveButton);

        wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(
                                beginWorkoutButton
                        )
        );
    }

    public boolean hasUpdatedWorkoutData(
            String updatedWorkoutDayName,
            String updatedSets,
            String updatedRepeats,
            String updatedWeight
    ) {
        By updatedName =
                AppiumBy.accessibilityId(
                        updatedWorkoutDayName
                );

        By updatedParameters =
                AppiumBy.accessibilityId(
                        updatedSets
                                + "x"
                                + updatedRepeats
                                + "x"
                                + updatedWeight
                                + " kg"
                );

        try {
            return wait.until(currentDriver ->
                    isDisplayed(updatedName)
                            && isDisplayed(
                            updatedParameters
                    )
            );
        } catch (TimeoutException ignored) {
            return false;
        }
    }

    public void deleteWorkoutDay() {
        openWorkoutDayEditor();

        click(deleteButton);

        wait.until(
                ExpectedConditions
                        .alertIsPresent()
        );

        driver.executeScript(
                "mobile: alert",
                Map.of(
                        "action",
                        "accept",
                        "buttonLabel",
                        "Yes"
                )
        );

        wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(
                                emptyWorkoutDaysPlaceholder
                        )
        );
    }

    public boolean isWorkoutDayDeleted() {
        return isDisplayed(
                emptyWorkoutDaysPlaceholder
        );
    }

    private void openWorkoutDayEditor() {
        click(editWorkoutDayButton);

        wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(
                                deleteButton
                        )
        );
    }

    private By editableExercise(
            String exerciseName
    ) {
        return AppiumBy.accessibilityId(
                exerciseName
        );
    }

    private void replaceText(
            By locator,
            String text
    ) {
        WebElement field =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        locator
                                )
                );

        field.click();

        try {
            field.clear();
        } catch (
                StaleElementReferenceException ignored
        ) {
            /*
             * После clear SwiftUI может
             * пересоздать TextField.
             */
        }

        WebElement refreshedField =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        locator
                                )
                );

        refreshedField.click();
        refreshedField.sendKeys(text);
    }

    private void click(
            By locator
    ) {
        WebElement element =
                findVisibleElement(locator);

        if (element != null) {
            element.click();
            return;
        }

        wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(
                                locator
                        )
        ).click();
    }

    private boolean isDisplayed(
            By locator
    ) {
        return findVisibleElement(locator)
                != null;
    }

    private WebElement findVisibleElement(
            By locator
    ) {
        try {
            List<WebElement> elements =
                    driver.findElements(locator);

            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        return element;
                    }
                } catch (
                        StaleElementReferenceException ignored
                ) {
                    // Экран обновился во время проверки.
                }
            }
        } catch (
                StaleElementReferenceException ignored
        ) {
            // Обновилось accessibility tree.
        }

        return null;
    }
}