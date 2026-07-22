package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class WorkoutReportPage
        extends IosBasePage {

    /*
     * Не ограничиваем тип XCUIElementTypeStaticText.
     *
     * На экране значение визуально равно 100%,
     * но приложение может отдавать его через
     * name, label или value другого XCUI-типа.
     */
    private final By completionPercentage =
            AppiumBy.iOSNsPredicateString(
                    "name == '100%' "
                            + "OR label == '100%' "
                            + "OR value == '100%'"
            );

    private final By caloriesLabel =
            AppiumBy.accessibilityId(
                    "Calories"
            );

    private final By stepsLabel =
            AppiumBy.accessibilityId(
                    "Steps"
            );

    private final By pulseLabel =
            AppiumBy.accessibilityId(
                    "Pulse"
            );

    public WorkoutReportPage(
            IOSDriver driver
    ) {
        super(driver);
    }

    public boolean isOpened() {
        return wait.until(
                currentDriver ->
                        isOpenedNow()
        );
    }

    public boolean isOpenedNow() {
        /*
         * Для определения открытия отчёта
         * достаточно двух стабильных признаков.
         */
        return isDisplayedNow(
                caloriesLabel
        )
                && findPercentageElementNow()
                != null;
    }

    public String getCompletionPercentage() {
        WebDriverWait percentageWait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(5)
                );

        percentageWait.pollingEvery(
                Duration.ofMillis(250)
        );

        WebElement percentageElement =
                percentageWait.until(
                        currentDriver ->
                                findPercentageElementNow()
                );

        String text =
                readFirstNonBlankAttribute(
                        percentageElement,
                        "name",
                        "label",
                        "value"
                );

        if (text == null) {
            throw new IllegalStateException(
                    "Completion percentage element "
                            + "was found, but name, label "
                            + "and value were empty."
            );
        }

        return text;
    }

    public boolean hasExpectedRepetitions(
            int expectedRepetitions
    ) {
        By repetitionsResult =
                AppiumBy.accessibilityId(
                        expectedRepetitions
                                + " reps of the expected "
                                + expectedRepetitions
                                + " reps"
                );

        return wait.until(
                currentDriver ->
                        isDisplayedNow(
                                repetitionsResult
                        )
        );
    }

    public boolean hasExpectedLiftedWeight(
            int expectedWeight
    ) {
        By liftedWeightResult =
                AppiumBy.accessibilityId(
                        expectedWeight
                                + " kg of the expected "
                                + expectedWeight
                                + " kg"
                );

        return wait.until(
                currentDriver ->
                        isDisplayedNow(
                                liftedWeightResult
                        )
        );
    }

    public boolean hasActivityMetrics() {
        return wait.until(
                currentDriver ->
                        isDisplayedNow(caloriesLabel)
                                && isDisplayedNow(
                                stepsLabel
                        )
                                && isDisplayedNow(
                                pulseLabel
                        )
        );
    }

    public void closeIfPresent() {
        if (!isDisplayedNow(
                caloriesLabel
        )) {
            return;
        }

        Dimension screenSize =
                driver.manage()
                        .window()
                        .getSize();

        driver.executeScript(
                "mobile: tap",
                Map.of(
                        "x",
                        (int) (
                                screenSize.getWidth()
                                        * 0.11
                        ),
                        "y",
                        (int) (
                                screenSize.getHeight()
                                        * 0.10
                        )
                )
        );

        wait.until(
                currentDriver ->
                        !isDisplayedNow(
                                caloriesLabel
                        )
        );
    }

    private WebElement findPercentageElementNow() {
        try {
            List<WebElement> elements =
                    driver.findElements(
                            completionPercentage
                    );

            if (elements.isEmpty()) {
                return null;
            }

            /*
             * Не вызываем обязательный isDisplayed().
             * Для кастомной SwiftUI-разметки этот
             * атрибут иногда определяется неверно,
             * хотя элемент присутствует на экране.
             */
            return elements.get(0);
        } catch (
                StaleElementReferenceException ignored
        ) {
            return null;
        }
    }

    private String readFirstNonBlankAttribute(
            WebElement element,
            String... attributes
    ) {
        for (String attribute : attributes) {
            try {
                String value =
                        element.getAttribute(
                                attribute
                        );

                if (value != null
                        && !value.isBlank()) {
                    return value;
                }
            } catch (
                    StaleElementReferenceException ignored
            ) {
                return null;
            }
        }

        return null;
    }
}