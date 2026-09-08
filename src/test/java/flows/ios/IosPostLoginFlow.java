package flows.ios;

import components.ios.IosTutorialOverlay;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ios.HealthAccessPage;
import pages.ios.MainPage;
import pages.ios.NotificationAccessPage;

import java.time.Duration;
import java.util.function.BooleanSupplier;

public class IosPostLoginFlow {

    private final IOSDriver driver;
    private final WebDriverWait wait;

    private final HealthAccessPage
            healthAccessPage;

    private final NotificationAccessPage
            notificationAccessPage;

    private final IosTutorialOverlay
            tutorialOverlay;

    private final MainPage mainPage;

    private boolean healthGrantRequested;
    private boolean notificationGrantRequested;

    public IosPostLoginFlow(
            IOSDriver driver
    ) {
        this.driver = driver;

        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(25)
        );

        this.wait.pollingEvery(
                Duration.ofMillis(250)
        );

        this.healthAccessPage =
                new HealthAccessPage(driver);

        this.notificationAccessPage =
                new NotificationAccessPage(driver);

        this.tutorialOverlay =
                new IosTutorialOverlay(driver);

        this.mainPage =
                new MainPage(driver);
    }

    public void complete() {
        completeUntil(
                mainPage::isDashboardOpenedNow
        );
    }

    public void completeUntilMainScreen() {
        completeUntil(
                mainPage::isOpenedNow
        );
    }

    private void completeUntil(
            BooleanSupplier completionCondition
    ) {
        healthGrantRequested = false;
        notificationGrantRequested = false;

        wait.until(currentDriver -> {
            try {
                /*
                 * Системный alert уведомлений
                 * проверяем только после того,
                 * как реально нажали GRANT ACCESS
                 * на экране Notifications.
                 */
                if (notificationGrantRequested
                        && acceptSystemAlertIfPresent()) {

                    notificationGrantRequested = false;
                    return false;
                }

                /*
                 * Сначала обрабатываем системный
                 * экран Apple Health.
                 */
                if (healthAccessPage
                        .isTurnOnAllButtonDisplayed()) {

                    healthAccessPage.turnOnAll();
                    return false;
                }

                if (healthAccessPage
                        .isAllowButtonDisplayed()) {

                    healthAccessPage.allow();
                    return false;
                }

                /*
                 * Затем экран приложения
                 * с запросом Apple Health.
                 */
                if (!healthGrantRequested
                        && healthAccessPage
                        .isHealthScreenDisplayed()) {

                    healthAccessPage.grantAccess();
                    healthGrantRequested = true;

                    return false;
                }

                /*
                 * Затем экран приложения
                 * с запросом уведомлений.
                 */
                if (!notificationGrantRequested
                        && notificationAccessPage
                        .isNotificationScreenDisplayed()) {

                    notificationAccessPage
                            .grantAccess();

                    notificationGrantRequested = true;
                    return false;
                }

                /*
                 * The first post-login run may show a blocking
                 * tutorial popover. Dismiss it before checking the
                 * destination screen; otherwise the popover can hide
                 * the very locator used as the completion condition.
                 */
                if (tutorialOverlay.isDisplayed()) {
                    tutorialOverlay.dismissIfPresent();
                    return false;
                }

                /*
                 * Главный экран проверяем только
                 * после обработки разрешений.
                 */
                if (completionCondition
                        .getAsBoolean()) {
                    return true;
                }

                return false;
            } catch (
                    StaleElementReferenceException ignored
            ) {
                /*
                 * Экран переключился прямо во время
                 * проверки. Следующий polling
                 * продолжит с актуальным деревом.
                 */
                return false;
            }
        });
    }

    private boolean acceptSystemAlertIfPresent() {
        try {
            Alert alert =
                    driver.switchTo().alert();

            alert.accept();
            return true;
        } catch (
                NoAlertPresentException ignored
        ) {
            return false;
        }
    }
}
