package flows;

import components.ios.IosTutorialOverlay;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ios.HealthAccessPage;
import pages.ios.MainPage;
import pages.ios.NotificationAccessPage;

import java.time.Duration;

public class IosPostLoginFlow {

    private final IOSDriver driver;
    private final WebDriverWait wait;

    private final HealthAccessPage healthAccessPage;
    private final NotificationAccessPage
            notificationAccessPage;
    private final IosTutorialOverlay tutorialOverlay;
    private final MainPage mainPage;

    private boolean healthGrantRequested;
    private boolean notificationGrantRequested;

    public IosPostLoginFlow(IOSDriver driver) {
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
        healthGrantRequested = false;
        notificationGrantRequested = false;

        wait.until(currentDriver -> {
            if (notificationGrantRequested
                    && acceptSystemAlertIfPresent()) {
                notificationGrantRequested = false;
                return false;
            }

            if (mainPage.isOpenedNow()) {
                tutorialOverlay.dismissIfPresent();
                return true;
            }

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

            if (!healthGrantRequested
                    && healthAccessPage
                    .isHealthScreenDisplayed()) {
                healthAccessPage.grantAccess();
                healthGrantRequested = true;
                return false;
            }

            if (!notificationGrantRequested
                    && notificationAccessPage
                    .isNotificationScreenDisplayed()) {
                notificationAccessPage.grantAccess();
                notificationGrantRequested = true;
                return false;
            }

            return false;
        });
    }

    private boolean acceptSystemAlertIfPresent() {
        try {
            Alert alert =
                    driver.switchTo().alert();

            alert.accept();
            return true;
        } catch (NoAlertPresentException ignored) {
            return false;
        }
    }
}