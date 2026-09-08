package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

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
                    "name == '100%'"
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

    private final int closeButtonX;
    private final int closeButtonY;

    public WorkoutReportPage(
            IOSDriver driver
    ) {
        super(driver);

        Dimension screenSize =
                driver.manage()
                        .window()
                        .getSize();

        closeButtonX =
                (int) (
                        screenSize.getWidth()
                                * 0.11
                );

        closeButtonY =
                (int) (
                        screenSize.getHeight()
                                * 0.10
                );
    }

    public boolean isOpenedNow() {
        return isDisplayedNow(
                caloriesLabel
        );
    }

    public String getCompletionPercentage() {

        WebElement percentageElement =
                findPercentageElementNow();

        if (percentageElement == null) {
            throw new IllegalStateException(
                    "Completion percentage element not found."
            );
        }

        String text =
                readFirstNonBlankAttribute(
                        percentageElement
                );

        if (text == null) {
            throw new IllegalStateException(
                    "Completion percentage value is empty."
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
        return isDisplayedNow(caloriesLabel)
                && isDisplayedNow(stepsLabel)
                && isDisplayedNow(pulseLabel);
    }

    public void close() {
        tapCloseButton();
    }

    public void closeIfPresent() {
        if (!isDisplayedNow(caloriesLabel)) {
            return;
        }

        tapCloseButton();
    }

    private void tapCloseButton() {
        driver.executeScript(
                "mobile: tap",
                Map.of(
                        "x", closeButtonX,
                        "y", closeButtonY
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
            WebElement element
    ) {
        for (String attribute :
                List.of(
                        "name",
                        "label",
                        "value"
                )) {
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
