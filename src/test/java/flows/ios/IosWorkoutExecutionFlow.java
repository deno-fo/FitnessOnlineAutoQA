package flows.ios;

import components.ios.IosTutorialOverlay;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ios.WorkoutReportPage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IosWorkoutExecutionFlow {

    private enum FinishTapResult {
        DESTINATION_OPENED,
        DIALOG_CLOSED,
        DIALOG_STILL_OPEN
    }

    private final IOSDriver driver;
    private final WebDriverWait wait;
    private final WorkoutReportPage reportPage;
    private final IosTutorialOverlay tutorialOverlay;

    private final By beginWorkoutButton =
            AppiumBy.accessibilityId(
                    "BEGIN WORKOUT"
            );

    private final By appleWatchWarningAlert =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeAlert' "
                            + "AND name == 'Warning'"
            );

    private final By appleWatchOkElement =
            AppiumBy.iOSNsPredicateString(
                    "name ==[c] 'OK' "
                            + "OR label ==[c] 'OK' "
                            + "OR value ==[c] 'OK'"
            );

    private final By tutorialNextButton =
            AppiumBy.accessibilityId(
                    "NEXT"
            );

    private final By tutorialGotItButton =
            AppiumBy.accessibilityId(
                    "GOT IT"
            );

    private final By weightField =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeTextField' "
                            + "AND value == 'Kilograms'"
            );

    private final By repeatsField =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeTextField' "
                            + "AND value == 'Repeats'"
            );

    private final By addSetButton =
            AppiumBy.accessibilityId(
                    "exercise add"
            );

    private final By finishWorkoutQuestion =
            AppiumBy.iOSNsPredicateString(
                    "name == 'Finish workout?' "
                            + "OR label == 'Finish workout?'"
            );

    /*
     * Не ограничиваем тип элемента.
     * Кастомный iOS-alert может отдавать Yes
     * как Button, StaticText или Other.
     */
    private final By finishWorkoutYesElement =
            AppiumBy.iOSNsPredicateString(
                    "name ==[c] 'Yes' "
                            + "OR label ==[c] 'Yes' "
                            + "OR value ==[c] 'Yes'"
            );

    /*
     * Один общий локатор для обнаружения диалога.
     * Наличие конкретного текста
     * This was the last exercise. больше не обязательно.
     */
    private final By finishDialogMarkers =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeAlert' "
                            + "OR name == 'Finish workout?' "
                            + "OR label == 'Finish workout?' "
                            + "OR name == "
                            + "'This was the last exercise.' "
                            + "OR label == "
                            + "'This was the last exercise.' "
                            + "OR name ==[c] 'Yes' "
                            + "OR label ==[c] 'Yes'"
            );

    public IosWorkoutExecutionFlow(
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

        this.reportPage =
                new WorkoutReportPage(driver);

        this.tutorialOverlay =
                new IosTutorialOverlay(driver);
    }

    public void completeWorkout(
            int numberOfSets,
            String weight,
            String repeats
    ) {
        click(beginWorkoutButton);

        dismissAppleWatchWarningIfPresent();
        dismissWorkoutTutorialIfPresent();

        makeSetFieldsVisible();

        for (int set = 0;
             set < numberOfSets;
             set++) {

            enterText(
                    weightField,
                    weight
            );

            enterText(
                    repeatsField,
                    repeats
            );

            tapAddSetFast();
        }

        /*
         * Раньше здесь было обязательное ожидание:
         *
         * visibilityOfElementLocated(lastExerciseMessage)
         *
         * Оно и ломало текущий запуск.
         * Теперь достаточно любого маркера
         * финального диалога.
         */
        waitForFinishDialog();

        confirmWorkoutFinish();

        tutorialOverlay
                .waitAndDismissIfPresent(
                        Duration.ofMillis(300)
                );

        wait.until(currentDriver ->
                reportPage.isOpenedNow()
        );
    }

    private void tapAddSetFast() {

        WebElement button =
                findVisibleElement(
                        addSetButton
                );

        if (button != null) {
            try {
                button.click();
                return;

            } catch (
                    WebDriverException ignored
            ) {
                /*
                 * Кнопка найдена, но клавиатура
                 * могла перекрыть фактический тап.
                 */
            }
        }

        /*
         * Медленное скрытие клавиатуры выполняем
         * только как fallback, а не перед каждым
         * подходом безусловно.
         */
        hideKeyboardIfPresent();

        wait.until(
                ExpectedConditions
                        .elementToBeClickable(
                                addSetButton
                        )
        ).click();
    }

    private void waitForFinishDialog() {
        wait.until(currentDriver ->
                isPresent(finishDialogMarkers)
        );

        System.out.println(
                "[iOS] Finish workout dialog detected."
        );
    }

    private void confirmWorkoutFinish() {
        /*
         * Способ №1:
         * стандартный Alert API, если WDA
         * распознало кастомное окно как alert.
         */
        if (tryPressYesThroughAlertApi()) {
            FinishTapResult result =
                    waitAfterFinishTap(
                            Duration.ofSeconds(3)
                    );

            if (handleFinishTapResult(result)) {
                return;
            }
        }

        /*
         * Перед следующей попыткой убеждаемся,
         * что диалог действительно ещё открыт.
         * Не тыкаем повторно уже по отчёту.
         */
        if (isFinishDialogAbsent()) {
            waitForDestinationAfterDialogClosed();
            return;
        }

        /*
         * Способ №2:
         * тап по реальному accessibility-элементу Yes,
         * если он существует и не является
         * гигантским контейнером на весь экран.
         */
        if (tapYesElementIfPossible()) {
            FinishTapResult result =
                    waitAfterFinishTap(
                            Duration.ofSeconds(3)
                    );

            if (handleFinishTapResult(result)) {
                return;
            }
        }

        if (isFinishDialogAbsent()) {
            waitForDestinationAfterDialogClosed();
            return;
        }

        /*
         * Способ №3:
         * координата относительно текста
         * Finish workout?.
         */
        if (tapYesRelativeToQuestion()) {
            FinishTapResult result =
                    waitAfterFinishTap(
                            Duration.ofSeconds(3)
                    );

            if (handleFinishTapResult(result)) {
                return;
            }
        }

        if (isFinishDialogAbsent()) {
            waitForDestinationAfterDialogClosed();
            return;
        }

        /*
         * Способ №4:
         * экранная координата по Inspector.
         *
         * Yes примерно:
         * 67% ширины;
         * 46.3% высоты окна приложения.
         */
        tapByScreenRatio(
                0.67,
                0.463
        );

        FinishTapResult finalResult =
                waitAfterFinishTap(
                        Duration.ofSeconds(5)
                );

        if (handleFinishTapResult(finalResult)) {
            return;
        }

        throw new TimeoutException(
                "Finish workout dialog is still open "
                        + "after all Yes tap strategies."
        );
    }

    private boolean tryPressYesThroughAlertApi() {
        List<String> buttons =
                getAlertButtons();

        String yesButton = null;

        for (String button : buttons) {
            if ("Yes".equalsIgnoreCase(button)) {
                yesButton = button;
                break;
            }
        }

        if (yesButton == null) {
            return false;
        }

        try {
            driver.executeScript(
                    "mobile: alert",
                    Map.of(
                            "action", "accept",
                            "buttonLabel", yesButton
                    )
            );

            System.out.println(
                    "[iOS] Pressed Yes through alert API."
            );

            return true;
        } catch (WebDriverException ignored) {
            return false;
        }
    }

    private List<String> getAlertButtons() {
        try {
            Object result =
                    driver.executeScript(
                            "mobile: alert",
                            Map.of(
                                    "action", "getButtons"
                            )
                    );

            if (!(result
                    instanceof Collection<?> rawButtons)) {
                return List.of();
            }

            List<String> buttons =
                    new ArrayList<>();

            for (Object rawButton : rawButtons) {
                if (rawButton != null) {
                    buttons.add(
                            String.valueOf(rawButton)
                    );
                }
            }

            return buttons;
        } catch (WebDriverException ignored) {
            return List.of();
        }
    }

    private boolean tapYesElementIfPossible() {
        WebElement yesElement =
                findFirstElement(
                        finishWorkoutYesElement
                );

        if (yesElement == null) {
            return false;
        }

        Rectangle bounds;

        try {
            bounds = yesElement.getRect();
        } catch (
                StaleElementReferenceException ignored
        ) {
            return false;
        }

        Dimension screenSize =
                driver.manage()
                        .window()
                        .getSize();

        /*
         * Inspector раньше возвращал огромный Other,
         * занимавший почти весь экран.
         * Его центр не является кнопкой Yes.
         */
        boolean plausibleElement =
                bounds.getWidth() > 0
                        && bounds.getHeight() > 0
                        && bounds.getWidth()
                        < screenSize.getWidth() * 0.60
                        && bounds.getHeight()
                        < screenSize.getHeight() * 0.20;

        if (!plausibleElement) {
            System.out.println(
                    "[iOS] Ignoring oversized Yes element: "
                            + bounds
            );

            return false;
        }

        tapElementCenter(yesElement);

        System.out.println(
                "[iOS] Tapped Yes element center: "
                        + bounds
        );

        return true;
    }

    private boolean tapYesRelativeToQuestion() {
        WebElement question =
                findFirstElement(
                        finishWorkoutQuestion
                );

        if (question == null) {
            return false;
        }

        Rectangle bounds;

        try {
            bounds = question.getRect();
        } catch (
                StaleElementReferenceException ignored
        ) {
            return false;
        }

        Dimension screenSize =
                driver.manage()
                        .window()
                        .getSize();

        int x =
                (int) (
                        screenSize.getWidth()
                                * 0.67
                );

        int verticalOffset =
                Math.max(
                        28,
                        (int) (
                                screenSize.getHeight()
                                        * 0.035
                        )
                );

        int y =
                bounds.getY()
                        + bounds.getHeight()
                        + verticalOffset;

        y = Math.min(
                y,
                screenSize.getHeight() - 20
        );

        System.out.println(
                "[iOS] Tapping Yes relative to question: "
                        + "x=" + x
                        + ", y=" + y
                        + ", question=" + bounds
        );

        tapByCoordinates(x, y);
        return true;
    }

    private FinishTapResult waitAfterFinishTap(
            Duration timeout
    ) {
        long deadline =
                System.nanoTime()
                        + timeout.toNanos();

        int consecutiveMissingDialogSamples = 0;

        while (System.nanoTime() < deadline) {

            if (isFinishDialogAbsent()) {
                consecutiveMissingDialogSamples++;

                if (consecutiveMissingDialogSamples >= 2) {
                    return FinishTapResult.DIALOG_CLOSED;
                }
            } else {
                consecutiveMissingDialogSamples = 0;

                if (isFinishDestinationOpened()) {
                    return FinishTapResult
                            .DESTINATION_OPENED;
                }
            }

            pause(Duration.ofMillis(100));
        }

        return FinishTapResult.DIALOG_STILL_OPEN;
    }

    private boolean handleFinishTapResult(
            FinishTapResult result
    ) {
        if (result
                == FinishTapResult.DESTINATION_OPENED) {
            return true;
        }

        if (result
                == FinishTapResult.DIALOG_CLOSED) {

            waitForDestinationAfterDialogClosed();
            return true;
        }

        return false;
    }

    private void waitForDestinationAfterDialogClosed() {
        WebDriverWait destinationWait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(10)
                );

        destinationWait.pollingEvery(
                Duration.ofMillis(250)
        );

        try {
            destinationWait.until(
                    currentDriver ->
                            isFinishDestinationOpened()
            );
        } catch (TimeoutException exception) {
            throw new TimeoutException(
                    "Finish dialog disappeared, "
                            + "but neither the report "
                            + "nor tutorial appeared.",
                    exception
            );
        }
    }

    private boolean isFinishDestinationOpened() {
        try {
            return tutorialOverlay.isDisplayed()
                    || reportPage.isOpenedNow();
        } catch (
                StaleElementReferenceException ignored
        ) {
            return false;
        }
    }

    private boolean isFinishDialogAbsent() {
        return !isPresent(
                finishDialogMarkers
        );
    }

    private void dismissAppleWatchWarningIfPresent() {
        WebDriverWait warningWait =
                new WebDriverWait(
                        driver,
                        Duration.ofMillis(1200)
                );

        warningWait.pollingEvery(
                Duration.ofMillis(200)
        );

        try {
            Alert alert =
                    warningWait.until(
                            ExpectedConditions
                                    .alertIsPresent()
                    );

            alert.accept();

            waitUntilElementDisappears(
                    appleWatchWarningAlert,
                    Duration.ofSeconds(2)
            );

            return;
        } catch (WebDriverException ignored) {
            // Это может быть кастомный alert.
        }

        WebElement okElement =
                findFirstElement(
                        appleWatchOkElement
                );

        if (isPlausibleSmallElement(okElement)) {
            tapElementCenter(okElement);

            if (waitUntilElementDisappears(
                    appleWatchWarningAlert,
                    Duration.ofSeconds(2)
            )) {
                return;
            }
        }

        if (!isPresent(
                appleWatchWarningAlert
        )) {
            return;
        }

        /*
         * У Apple Watch warning одна кнопка OK.
         */
        tapByScreenRatio(
                0.50,
                0.475
        );

        if (!waitUntilElementDisappears(
                appleWatchWarningAlert,
                Duration.ofSeconds(2)
        )) {
            throw new TimeoutException(
                    "Apple Watch warning remained visible "
                            + "after attempting to press OK."
            );
        }
    }

    private boolean isPlausibleSmallElement(
            WebElement element
    ) {
        if (element == null) {
            return false;
        }

        try {
            Rectangle bounds =
                    element.getRect();

            Dimension screenSize =
                    driver.manage()
                            .window()
                            .getSize();

            return bounds.getWidth() > 0
                    && bounds.getHeight() > 0
                    && bounds.getWidth()
                    < screenSize.getWidth() * 0.60
                    && bounds.getHeight()
                    < screenSize.getHeight() * 0.20;
        } catch (
                StaleElementReferenceException ignored
        ) {
            return false;
        }
    }

    private void dismissWorkoutTutorialIfPresent() {
        WebDriverWait tutorialWait =
                new WebDriverWait(
                        driver,
                        Duration.ofMillis(900)
                );

        tutorialWait.pollingEvery(
                Duration.ofMillis(200)
        );

        try {
            tutorialWait.until(currentDriver ->
                    isDisplayed(
                            tutorialNextButton
                    )
                            || isDisplayed(
                            tutorialGotItButton
                    )
            );
        } catch (TimeoutException ignored) {
            return;
        }

        for (int step = 0;
             step < 5;
             step++) {

            WebElement nextButton =
                    findVisibleElement(
                            tutorialNextButton
                    );

            if (nextButton != null) {
                nextButton.click();
                pause(Duration.ofMillis(200));
                continue;
            }

            WebElement gotItButton =
                    findVisibleElement(
                            tutorialGotItButton
                    );

            if (gotItButton != null) {
                gotItButton.click();
            }

            return;
        }
    }

    private void makeSetFieldsVisible() {
        if (isDisplayed(weightField)
                && isDisplayed(repeatsField)) {
            return;
        }

        driver.executeScript(
                "mobile: swipe",
                Map.of(
                        "direction", "up"
                )
        );

        wait.until(currentDriver ->
                isDisplayed(weightField)
                        && isDisplayed(
                        repeatsField
                )
        );
    }

    private void enterText(
            By locator,
            String text
    ) {
        WebElement field = findVisibleElement(locator);

        if (field == null) {
            field =
                    wait.until(
                            ExpectedConditions
                                    .visibilityOfElementLocated(locator)
                    );
        }

        field.click();

        try {
            field.clear();
            field.sendKeys(text);

        } catch (StaleElementReferenceException ignored) {

            WebElement refreshedField =
                    wait.until(
                            ExpectedConditions
                                    .visibilityOfElementLocated(locator)
                    );

            refreshedField.sendKeys(text);
        }
    }

    private void click(By locator) {
        WebElement element = findVisibleElement(locator);

        if (element != null) {
            element.click();
            return;
        }

        wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(locator)
        ).click();
    }

    private boolean waitUntilElementDisappears(
            By locator,
            Duration timeout
    ) {
        WebDriverWait disappearanceWait =
                new WebDriverWait(
                        driver,
                        timeout
                );

        disappearanceWait.pollingEvery(
                Duration.ofMillis(200)
        );

        try {
            return disappearanceWait.until(
                    currentDriver ->
                            !isPresent(locator)
            );
        } catch (TimeoutException ignored) {
            return false;
        }
    }

    private void tapByScreenRatio(
            double xRatio,
            double yRatio
    ) {
        Dimension screenSize =
                driver.manage()
                        .window()
                        .getSize();

        int x =
                (int) (
                        screenSize.getWidth()
                                * xRatio
                );

        int y =
                (int) (
                        screenSize.getHeight()
                                * yRatio
                );

        System.out.println(
                "[iOS] Screen tap: "
                        + "x=" + x
                        + ", y=" + y
                        + ", screen=" + screenSize
        );

        tapByCoordinates(x, y);
    }

    private void tapElementCenter(
            WebElement element
    ) {
        Rectangle bounds =
                element.getRect();

        int x =
                bounds.getX()
                        + bounds.getWidth() / 2;

        int y =
                bounds.getY()
                        + bounds.getHeight() / 2;

        tapByCoordinates(x, y);
    }

    private void tapByCoordinates(
            int x,
            int y
    ) {
        driver.executeScript(
                "mobile: tap",
                Map.of(
                        "x", x,
                        "y", y
                )
        );
    }

    private WebElement findFirstElement(
            By locator
    ) {
        try {
            List<WebElement> elements =
                    driver.findElements(locator);

            if (elements.isEmpty()) {
                return null;
            }

            return elements.get(0);
        } catch (
                StaleElementReferenceException ignored
        ) {
            return null;
        }
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
        return findVisibleElement(
                locator
        ) != null;
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
                    // Элемент пересоздался.
                }
            }
        } catch (
                StaleElementReferenceException ignored
        ) {
            // Accessibility tree обновилось.
        }

        return null;
    }

    private void hideKeyboardIfPresent() {
        try {
            driver.hideKeyboard();
        } catch (WebDriverException ignored) {
            // Клавиатура уже скрыта.
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
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Workout execution was interrupted.",
                    exception
            );
        }
    }
}