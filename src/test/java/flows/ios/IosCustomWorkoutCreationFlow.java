package flows.ios;

import components.ios.IosTutorialOverlay;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Dimension;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class IosCustomWorkoutCreationFlow {

    private final IOSDriver driver;
    private final WebDriverWait wait;
    private final IosTutorialOverlay tutorialOverlay;

    private final By workoutBuilderButton =
            AppiumBy.accessibilityId(
                    "USE WORKOUT BUILDER"
            );

    private final By programNameField =
            AppiumBy.className(
                    "XCUIElementTypeTextField"
            );

    private final By confirmProgramButton =
            AppiumBy.accessibilityId(
                    "icon done"
            );

    private final By workoutsTab =
            AppiumBy.iOSClassChain(
                    "**/XCUIElementTypeStaticText"
                            + "[`name == \"Workouts\"`][2]"
            );

    private final By addWorkoutDayButton =
            AppiumBy.accessibilityId(
                    "floatingButtonAdd"
            );

    private final By workoutDayNameField =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeTextField' "
                            + "AND value == "
                            + "'Enter workout name...'"
            );

    private final By addNewExerciseButton =
            AppiumBy.accessibilityId(
                    "Add new exercise"
            );

    private final By chestCategory =
            AppiumBy.accessibilityId(
                    "Chest"
            );

    /*
     * Эти позиционные локаторы уже подтверждены
     * фактическим Inspector конкретного экрана.
     * Не заменяем их выдуманными accessibility id.
     */
    private final By firstExerciseCheckbox =
            AppiumBy.xpath(
                    "(//XCUIElementTypeButton"
                            + "[@name='checkbox na'])[1]"
            );

    private final By addSelectedExerciseButton =
            AppiumBy.accessibilityId(
                    "ADD (1)"
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

    private final By beginWorkoutButton =
            AppiumBy.accessibilityId(
                    "BEGIN WORKOUT"
            );

    public IosCustomWorkoutCreationFlow(
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

        this.tutorialOverlay =
                new IosTutorialOverlay(driver);
    }

    public void createProgram(
            String programName
    ) {
        scrollToWorkoutBuilder();
        click(workoutBuilderButton);

        enterProgramName(programName);

        dismissPossibleSystemNotificationBanner();

        click(confirmProgramButton);

        wait.until(currentDriver ->
                isPresent(workoutsTab)
        );

        tutorialOverlay
                .waitAndDismissIfPresent(
                        Duration.ofMillis(300)
                );
    }

    public void addWorkoutDay(
            String workoutDayName,
            String exerciseName,
            String sets,
            String repeats,
            String weight
    ) {
        click(workoutsTab);
        click(addWorkoutDayButton);

        enterText(
                workoutDayNameField,
                workoutDayName
        );

        click(addNewExerciseButton);

        click(chestCategory);

        selectFirstExercise();
        click(addSelectedExerciseButton);

        By exercise =
                AppiumBy.accessibilityId(
                        exerciseName
                );

        openExerciseAfterAdding(
                exercise
        );

        enterText(setsField, sets);
        enterText(repeatsField, repeats);
        enterText(weightField, weight);

        String workoutParameters =
                sets
                        + "x"
                        + repeats
                        + "x"
                        + weight
                        + " kg";

        By workoutParametersLabel =
                AppiumBy.accessibilityId(
                        workoutParameters
                );

        click(saveButton);

        wait.until(currentDriver ->
                isPresent(workoutParametersLabel)
                        || isPresent(beginWorkoutButton)
        );

        click(saveButton);

        wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(
                                beginWorkoutButton
                        )
        );
    }

    public boolean isWorkoutReady(
            String exerciseName,
            String sets,
            String repeats,
            String weight
    ) {
        By exercise =
                AppiumBy.accessibilityId(
                        exerciseName
                );

        By parameters =
                AppiumBy.accessibilityId(
                        sets
                                + "x"
                                + repeats
                                + "x"
                                + weight
                                + " kg"
                );

        try {
            return wait.until(currentDriver ->
                    isDisplayed(exercise)
                            && isDisplayed(parameters)
                            && isDisplayed(
                            beginWorkoutButton
                    )
            );
        } catch (TimeoutException ignored) {
            return false;
        }
    }

    private void openExerciseAfterAdding(
            By exercise
    ) {
        WebDriverWait transitionWait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(10)
                );

        transitionWait.pollingEvery(
                Duration.ofMillis(150)
        );

        try {
            transitionWait.until(
                    currentDriver -> {

                        /*
                         * Подсказка может появиться позднее,
                         * поэтому проверяем её на каждом цикле.
                         */
                        if (tutorialOverlay
                                .dismissVisiblePopoverFast()) {
                            return false;
                        }

                        /*
                         * Настройки упражнения считаем
                         * открытыми только тогда, когда
                         * появились второе и третье поля.
                         *
                         * Первое поле использовать нельзя:
                         * на предыдущем экране им является
                         * название тренировочного дня.
                         */
                        if (findVisibleElement(repeatsField)
                                != null
                                && findVisibleElement(weightField)
                                != null) {
                            return true;
                        }

                        /*
                         * Тап повторяем, потому что предыдущий
                         * мог попасть в появившуюся подсказку.
                         */
                        WebElement exerciseElement =
                                findVisibleElement(
                                        exercise
                                );

                        if (exerciseElement != null) {
                            try {
                                exerciseElement.click();
                            } catch (
                                    WebDriverException ignored
                            ) {
                                /*
                                 * Экран или подсказка могли
                                 * пересоздаться во время клика.
                                 * Следующий polling повторит
                                 * проверку и попытку.
                                 */
                            }
                        }

                        return false;
                    }
            );

        } catch (TimeoutException exception) {
            throw new TimeoutException(
                    "Exercise settings did not open "
                            + "after adding the exercise. "
                            + "The tutorial popover may still "
                            + "be blocking the screen.",
                    exception
            );
        }
    }

    private void selectFirstExercise() {
        WebElement checkbox =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        firstExerciseCheckbox
                                )
                );

        tapElementCenter(
                checkbox,
                firstExerciseCheckbox
        );

        /*
         * Проверяем результат тапа.
         * Повторно checkbox вслепую не нажимаем,
         * чтобы не снять уже сделанный выбор.
         */
        try {
            new WebDriverWait(
                    driver,
                    Duration.ofSeconds(5)
            ).until(
                    ExpectedConditions
                            .visibilityOfElementLocated(
                                    addSelectedExerciseButton
                            )
            );
        } catch (TimeoutException exception) {
            throw new TimeoutException(
                    "The first exercise checkbox was tapped, "
                            + "but ADD (1) did not appear.",
                    exception
            );
        }
    }

    private void scrollToWorkoutBuilder() {
        for (int attempt = 0;
             attempt < 6;
             attempt++) {

            if (isDisplayed(
                    workoutBuilderButton
            )) {
                return;
            }

            driver.executeScript(
                    "mobile: swipe",
                    Map.of(
                            "direction",
                            "up",
                            "velocity",
                            5000
                    )
            );
        }

        wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(
                                workoutBuilderButton
                        )
        );
    }

    private void enterProgramName(
            String programName
    ) {
        WebElement field =
                wait.until(
                        ExpectedConditions
                                .elementToBeClickable(
                                        programNameField
                                )
                );

        field.click();

        try {
            field.clear();
        } catch (
                StaleElementReferenceException ignored
        ) {
            /*
             * На этом экране clear действительно
             * может пересоздать TextField.
             */
        }

        WebElement refreshedField =
                wait.until(
                        ExpectedConditions
                                .elementToBeClickable(
                                        programNameField
                                )
                );

        refreshedField.sendKeys(programName);

        wait.until(currentDriver ->
                hasValue(
                        programNameField,
                        programName
                )
        );
    }

    private void enterText(
            By locator,
            String text
    ) {
        WebElement field =
                wait.until(
                        ExpectedConditions
                                .presenceOfElementLocated(
                                        locator
                                )
                );

        field.click();

        try {
            field.sendKeys(text);
        } catch (
                StaleElementReferenceException ignored
        ) {
            WebElement refreshedField =
                    wait.until(
                            ExpectedConditions
                                    .presenceOfElementLocated(
                                            locator
                                    )
                    );

            refreshedField.click();
            refreshedField.sendKeys(text);
        }
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
                        .visibilityOfElementLocated(locator)
        ).click();
    }

    private void tapElementCenter(
            WebElement element,
            By fallbackLocator
    ) {
        try {
            tapElementCenter(element);
        } catch (
                StaleElementReferenceException ignored
        ) {
            WebElement refreshedElement =
                    wait.until(
                            ExpectedConditions
                                    .visibilityOfElementLocated(
                                            fallbackLocator
                                    )
                    );

            tapElementCenter(
                    refreshedElement
            );
        }
    }

    private void tapElementCenter(
            WebElement element
    ) {
        Rectangle bounds =
                element.getRect();

        driver.executeScript(
                "mobile: tap",
                Map.of(
                        "x",
                        bounds.getX()
                                + bounds.getWidth() / 2,
                        "y",
                        bounds.getY()
                                + bounds.getHeight() / 2
                )
        );
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
                    // Элемент пересоздался
                }
            }
        } catch (
                StaleElementReferenceException ignored
        ) {
            // Accessibility tree обновилось
        }

        return null;
    }

    private boolean hasValue(
            By locator,
            String expectedValue
    ) {
        try {
            List<WebElement> elements =
                    driver.findElements(locator);

            for (WebElement element : elements) {
                try {
                    String actualValue =
                            element.getAttribute(
                                    "value"
                            );

                    if (expectedValue.equals(
                            actualValue
                    )) {
                        return true;
                    }
                } catch (
                        StaleElementReferenceException ignored
                ) {
                    // Поле пересоздалось.
                }
            }
        } catch (
                StaleElementReferenceException ignored
        ) {
            // Accessibility tree обновилось.
        }

        return false;
    }

    private boolean isPresent(
            By locator
    ) {
        try {
            return !driver
                    .findElements(locator)
                    .isEmpty();
        } catch (
                StaleElementReferenceException ignored
        ) {
            return false;
        }
    }

    private boolean isDisplayed(
            By locator
    ) {
        try {
            List<WebElement> elements =
                    driver.findElements(locator);

            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        return true;
                    }
                } catch (
                        StaleElementReferenceException ignored
                ) {
                    // Элемент пересоздался.
                }
            }
        } catch (
                StaleElementReferenceException ignored
        ) {
            // Accessibility tree обновилось.
        }

        return false;
    }

    private void dismissPossibleSystemNotificationBanner() {
        Dimension screenSize =
                driver.manage()
                        .window()
                        .getSize();

        /*
         * Даём уведомлению от бота появиться,
         * затем смахиваем его перед нажатием
         * кнопки подтверждения.
         */
        pause(Duration.ofMillis(700));

        swipeSystemNotificationBannerUp(
                screenSize
        );

        /*
         * Ждём окончания анимации исчезновения,
         * чтобы следующий тап попал по галочке.
         */
        pause(Duration.ofMillis(200));
    }

    private void swipeSystemNotificationBannerUp(
            Dimension screenSize
    ) {
        int x =
                screenSize.getWidth() / 2;

        int fromY =
                (int) (
                        screenSize.getHeight()
                                * 0.15
                );

        int toY =
                (int) (
                        screenSize.getHeight()
                                * 0.02
                );

        try {
            driver.executeScript(
                    "mobile: dragFromToForDuration",
                    Map.of(
                            "duration", 0.20,
                            "fromX", x,
                            "fromY", fromY,
                            "toX", x,
                            "toY", toY
                    )
            );
        } catch (WebDriverException ignored) {
            /*
             * Системного баннера могло не быть.
             * В таком случае продолжаем создание
             * программы штатно.
             */
        }
    }

    private void pause(
            Duration duration
    ) {
        try {
            Thread.sleep(
                    duration.toMillis()
            );
        } catch (InterruptedException exception) {
            Thread.currentThread()
                    .interrupt();

            throw new IllegalStateException(
                    "Program creation was interrupted.",
                    exception
            );
        }
    }
}