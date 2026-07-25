package tests.android;

import annotations.AndroidDeviceTest;
import annotations.RequiresGoogleHealth;
import flows.android.AndroidOnboardingFlow;
import flows.android.LogoutFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    private AndroidOnboardingFlow onboardingFlow;
    private MainPage mainPage;
    private LogoutFlow logoutFlow;

    private boolean userLoggedIn;

    @BeforeEach
    public void createPages() {
        loginPage = new LoginPage(driver);
        emailAuthPage = new EmailAuthPage(driver);
        googleHealthPage = new GoogleHealthPage(driver);
        healthPermissionsPage = new HealthPermissionsPage(driver);
        onboardingFlow = new AndroidOnboardingFlow(driver);
        mainPage = new MainPage(driver);
        logoutFlow = new LogoutFlow(driver);

        userLoggedIn = false;
    }

    @AndroidDeviceTest
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

    @AndroidDeviceTest
    public void shouldSignInRegisteredUserAfterSkippingGoogleHealthInApp() {
        signInAfterSkippingGoogleHealth();

        assertRegisteredUserLoginSucceeded();
        userLoggedIn = true;
    }

    @AndroidDeviceTest
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

    @AndroidDeviceTest
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
        onboardingFlow.completeRegisteredUserLogin();
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
