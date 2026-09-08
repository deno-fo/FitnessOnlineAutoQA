package flows.ios;

import components.ios.IosPasswordManagerPrompt;
import components.ios.IosTutorialOverlay;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ios.BodyParametersPage;
import pages.ios.GenderSelectionPage;
import pages.ios.HealthAccessPage;
import pages.ios.LoginPage;
import pages.ios.MainPage;
import pages.ios.NotificationAccessPage;
import pages.ios.WorkoutReportPage;
import utils.IosConfig;

import java.time.Duration;

/** Restores access to account actions after an interrupted scenario. */
public final class IosCleanupRecovery {
    private final IOSDriver driver;

    public IosCleanupRecovery(IOSDriver driver) {
        this.driver = driver;
    }

    public boolean openAccountMenu() {
        LoginPage login = new LoginPage(driver);
        MainPage main = new MainPage(driver);
        HealthAccessPage health = new HealthAccessPage(driver);
        NotificationAccessPage notifications = new NotificationAccessPage(driver);
        GenderSelectionPage gender = new GenderSelectionPage(driver);
        BodyParametersPage body = new BodyParametersPage(driver);
        IosPasswordManagerPrompt password = new IosPasswordManagerPrompt(driver);
        IosTutorialOverlay tutorial = new IosTutorialOverlay(driver);
        WorkoutReportPage report = new WorkoutReportPage(driver);
        RecoveryState state = new RecoveryState();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(35));
        wait.ignoring(StaleElementReferenceException.class);
        wait.withMessage("iOS cleanup could not reach More or a signed-out screen; "
                + "account cleanup was not completed.");
        wait.until(ignored -> {
            if (password.dismissIfPresent()) {
                return false;
            }
            // Cancel outstanding confirmations, never accept an unknown destructive action.
            try {
                driver.switchTo().alert().dismiss();
                return false;
            } catch (NoAlertPresentException ignoredAlert) {
                // No system/application alert is blocking navigation.
            }
            if (tutorial.isDisplayed()) {
                tutorial.dismissIfPresent();
                return false;
            }
            if (login.isSignedOutNow()) {
                state.signedOut = true;
                return true;
            }
            if (login.isEmailFormDisplayedNow()) {
                login.returnToAuthenticationOptionsIfNeeded();
                return false;
            }
            // Report has no usable More tab and cannot be dismissed by a back swipe.
            if (report.isOpenedNow() && !main.isOpenedNow()) {
                report.close();
                return false;
            }
            try {
                if (main.isOpenedNow() && main.openMoreIfAvailable()) {
                    return true;
                }
            } catch (ElementNotInteractableException blockedTab) {
                // A modal editor can leave the underlying tab in the accessibility tree.
            }
            if (health.isTurnOnAllButtonDisplayed()) {
                health.turnOnAll();
                return false;
            }
            if (health.isAllowButtonDisplayed()) {
                health.allow();
                return false;
            }
            if (health.isHealthScreenDisplayed()) {
                health.grantAccess();
                return false;
            }
            if (notifications.isNotificationScreenDisplayed()) {
                notifications.grantAccess();
                return false;
            }
            if (gender.isDisplayedNow()) {
                gender.selectMale();
                return false;
            }
            if (body.isDisplayedNow()) {
                body.continueWithDefaultValues();
                return false;
            }
            if (state.shouldRestart()) {
                // Drop transient editors without clearing the authenticated session.
                driver.terminateApp(IosConfig.BUNDLE_ID);
                driver.activateApp(IosConfig.BUNDLE_ID);
            }
            return false;
        });
        return !state.signedOut;
    }

    private static final class RecoveryState {
        private boolean signedOut;
        private int unknownScreens;
        private boolean restarted;

        boolean shouldRestart() {
            if (++unknownScreens < 2 || restarted) {
                return false;
            }
            restarted = true;
            return true;
        }
    }
}
