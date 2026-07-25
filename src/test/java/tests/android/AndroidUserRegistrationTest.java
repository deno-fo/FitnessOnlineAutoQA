package tests.android;

import annotations.AndroidDeviceTest;
import annotations.RequiresGoogleHealth;
import components.android.AndroidNotificationPermissionDialog;
import flows.android.AccountDeletionFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pages.android.*;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AndroidUserRegistrationTest extends BaseAndroidTest {

    private LoginPage loginPage;
    private EmailRegistrationPage emailRegistrationPage;
    private GoogleHealthPage googleHealthPage;
    private HealthPermissionsPage healthPermissionsPage;
    private BodyParametersPage bodyParametersPage;
    private AndroidNotificationPermissionDialog notificationPermissionDialog;
    private MainPage mainPage;
    private AccountDeletionFlow accountDeletionFlow;

    @BeforeEach
    public void createPages() {
        loginPage = new LoginPage(driver);
        emailRegistrationPage = new EmailRegistrationPage(driver);
        googleHealthPage = new GoogleHealthPage(driver);
        healthPermissionsPage = new HealthPermissionsPage(driver);
        bodyParametersPage = new BodyParametersPage(driver);
        notificationPermissionDialog =
                new AndroidNotificationPermissionDialog(driver);
        mainPage = new MainPage(driver);
        accountDeletionFlow = new AccountDeletionFlow(driver);
    }

    @AndroidDeviceTest
    @RequiresGoogleHealth
    public void shouldRegisterUserWithGoogleHealth() {
        openEmailRegistrationForm();

        emailRegistrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );

        googleHealthPage.grantAccess();
        healthPermissionsPage.enableAllowAll();
        healthPermissionsPage.confirmHealthPermissions();

        completeOnboarding();

        assertRegisteredUserHomeOpened();
    }

    @AndroidDeviceTest
    public void shouldRegisterUserAfterSkippingGoogleHealthInApp() {
        openEmailRegistrationForm();

        emailRegistrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );

        googleHealthPage.skipAccess();
        googleHealthPage.declinePersuasion();
        googleHealthPage.continueWithoutAccess();

        completeOnboarding();

        assertRegisteredUserHomeOpened();
    }

    @AndroidDeviceTest
    @RequiresGoogleHealth
    public void shouldRegisterUserAfterDenyingGoogleHealthInSystemDialog() {
        openEmailRegistrationForm();

        emailRegistrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );

        googleHealthPage.grantAccess();
        healthPermissionsPage.denyAccess();
        googleHealthPage.continueWithoutAccess();

        completeOnboarding();

        assertRegisteredUserHomeOpened();
    }

    private void openEmailRegistrationForm() {
        loginPage.skipWelcomeScreen();
        loginPage.openEmailAuthentication();
    }

    private void completeOnboarding() {
        bodyParametersPage.continueWithDefaultValues();
        notificationPermissionDialog.allowNotifications();
    }

    private void assertRegisteredUserHomeOpened() {
        assertTrue(
                mainPage.isBottomNavigationDisplayed(),
                "Registered user creation failed: main screen was not opened."
        );
    }

    @AfterEach
    public void cleanUpCreatedAccount()
            throws InterruptedException {

        if (accountDeletionFlow != null
                && accountDeletionFlow.canDeleteAccount()) {

            accountDeletionFlow.deleteAccount();
        }
    }
}