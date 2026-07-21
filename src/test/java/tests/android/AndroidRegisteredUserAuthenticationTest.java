package tests.android;

import annotations.RequiresGoogleHealth;
import components.android.AndroidNotificationPermissionDialog;
import components.android.TutorialOverlay;
import flows.android.LogoutFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.android.EmailAuthPage;
import pages.android.GoogleHealthPage;
import pages.android.HealthPermissionsPage;
import pages.android.LoginPage;
import pages.android.MainPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AndroidRegisteredUserAuthenticationTest
        extends BaseAndroidTest {

    private LoginPage loginPage;
    private EmailAuthPage emailAuthPage;
    private GoogleHealthPage googleHealthPage;
    private HealthPermissionsPage healthPermissionsPage;
    private AndroidNotificationPermissionDialog notificationPermissionDialog;
    private TutorialOverlay tutorialOverlay;
    private MainPage mainPage;
    private LogoutFlow logoutFlow;

    private boolean userLoggedIn;

    @BeforeEach
    public void createPages() {
        loginPage = new LoginPage(driver);
        emailAuthPage = new EmailAuthPage(driver);
        googleHealthPage = new GoogleHealthPage(driver);
        healthPermissionsPage = new HealthPermissionsPage(driver);

        notificationPermissionDialog =
                new AndroidNotificationPermissionDialog(driver);

        tutorialOverlay = new TutorialOverlay(driver);
        mainPage = new MainPage(driver);
        logoutFlow = new LogoutFlow(driver);

        userLoggedIn = false;
    }

    @Test
    @RequiresGoogleHealth
    public void shouldSignInRegisteredUserWithGoogleHealth() {
        openEmailLoginForm();

        emailAuthPage.signIn(
                TestData.REGISTERED_USER_EMAIL,
                TestData.REGISTERED_USER_PASSWORD
        );

        googleHealthPage.grantAccess();
        healthPermissionsPage.enableAllowAll();
        healthPermissionsPage.confirmHealthPermissions();

        completeLoginOnboarding();

        assertRegisteredUserLoginSucceeded();
        userLoggedIn = true;
    }

    @Test
    public void shouldSignInRegisteredUserAfterSkippingGoogleHealthInApp() {
        signInAfterSkippingGoogleHealth();

        assertRegisteredUserLoginSucceeded();
        userLoggedIn = true;
    }

    @Test
    @RequiresGoogleHealth
    public void shouldSignInRegisteredUserAfterDenyingGoogleHealthInSystemDialog() {
        openEmailLoginForm();

        emailAuthPage.signIn(
                TestData.REGISTERED_USER_EMAIL,
                TestData.REGISTERED_USER_PASSWORD
        );

        googleHealthPage.grantAccess();
        healthPermissionsPage.denyAccess();
        googleHealthPage.continueWithoutAccess();

        completeLoginOnboarding();

        assertRegisteredUserLoginSucceeded();
        userLoggedIn = true;
    }

    @Test
    public void shouldShowErrorForInvalidCredentials() {
        openEmailLoginForm();

        emailAuthPage.signIn(
                TestData.INVALID_EMAIL,
                TestData.INVALID_PASSWORD
        );

        assertTrue(
                emailAuthPage.isErrorSnackbarDisplayed(),
                "Invalid credentials verification failed: "
                        + "expected login error snackbar was not displayed."
        );
    }

    private void signInAfterSkippingGoogleHealth() {
        openEmailLoginForm();

        emailAuthPage.signIn(
                TestData.REGISTERED_USER_EMAIL,
                TestData.REGISTERED_USER_PASSWORD
        );

        googleHealthPage.skipAccess();
        googleHealthPage.declinePersuasion();
        googleHealthPage.continueWithoutAccess();

        completeLoginOnboarding();
    }

    private void completeLoginOnboarding() {
        notificationPermissionDialog.allowNotifications();
        tutorialOverlay.dismissIfPresent();
    }

    private void openEmailLoginForm() {
        loginPage.skipWelcomeScreen();
        loginPage.openEmailAuthentication();
    }

    private void assertRegisteredUserLoginSucceeded() {
        assertTrue(
                mainPage.isBottomNavigationDisplayed(),
                "Registered user login failed: "
                        + "main screen was not opened."
        );
    }

    private void assertRegisteredUserLogoutSucceeded() {
        assertTrue(
                loginPage.isEmailAuthenticationOptionDisplayed(),
                "Registered user logout failed during test cleanup: "
                        + "authentication options screen was not opened."
        );
    }

    @AfterEach
    public void logOutRegisteredUser()
            throws InterruptedException {
        if (!userLoggedIn) {
            return;
        }
        logoutFlow.logOut();
        assertRegisteredUserLogoutSucceeded();
    }
}