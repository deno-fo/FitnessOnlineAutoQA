package tests.android;

import annotations.RequiresGoogleHealth;
import components.android.AndroidNotificationPermissionDialog;
import flows.android.AccountDeletionFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.android.BodyParametersPage;
import pages.android.GenderSelectionPage;
import pages.android.GoogleHealthPage;
import pages.android.HealthPermissionsPage;
import pages.android.LoginPage;
import pages.android.MainPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AndroidGuestAccountCreationTest extends BaseAndroidTest {

    private LoginPage loginPage;
    private HealthPermissionsPage healthPermissionsPage;
    private GoogleHealthPage googleHealthPage;
    private GenderSelectionPage genderSelectionPage;
    private BodyParametersPage bodyParametersPage;
    private AndroidNotificationPermissionDialog notificationPermissionDialog;
    private MainPage mainPage;
    private AccountDeletionFlow accountDeletionFlow;

    @BeforeEach
    public void createPages() {
        loginPage = new LoginPage(driver);
        healthPermissionsPage = new HealthPermissionsPage(driver);
        googleHealthPage = new GoogleHealthPage(driver);
        genderSelectionPage = new GenderSelectionPage(driver);
        bodyParametersPage = new BodyParametersPage(driver);
        notificationPermissionDialog =
                new AndroidNotificationPermissionDialog(driver);
        mainPage = new MainPage(driver);
        accountDeletionFlow = new AccountDeletionFlow(driver);
    }

    @Test
    @RequiresGoogleHealth
    public void shouldCompleteGuestOnboardingWithGoogleHealth() {
        loginPage.enterGuestMode();

        googleHealthPage.grantAccess();
        healthPermissionsPage.enableAllowAll();
        healthPermissionsPage.confirmHealthPermissions();

        completeOnboardingWithDefaultUserData();

        assertGuestHomeScreenOpened();
    }

    @Test
    public void shouldCompleteGuestOnboardingAfterSkippingGoogleHealthInApp() {
        loginPage.enterGuestMode();

        googleHealthPage.skipAccess();
        googleHealthPage.declinePersuasion();
        googleHealthPage.continueWithoutAccess();

        completeOnboardingWithDefaultUserData();

        assertGuestHomeScreenOpened();
    }

    @Test
    @RequiresGoogleHealth
    public void shouldCompleteGuestOnboardingAfterDenyingGoogleHealthInSystemDialog() {
        loginPage.enterGuestMode();

        googleHealthPage.grantAccess();
        healthPermissionsPage.denyAccess();
        googleHealthPage.continueWithoutAccess();

        completeOnboardingWithDefaultUserData();

        assertGuestHomeScreenOpened();
    }

    private void completeOnboardingWithDefaultUserData() {
        genderSelectionPage.selectMale();
        bodyParametersPage.continueWithDefaultValues();
        notificationPermissionDialog.allowNotifications();
    }

    private void assertGuestHomeScreenOpened() {
        assertAll(
                "Guest onboarding verification",

                () -> assertTrue(
                        mainPage.isBottomNavigationDisplayed(),
                        "Bottom navigation is not displayed."
                ),

                () -> assertTrue(
                        mainPage.isWorkoutProgramsListDisplayed(),
                        "Workout programs list is not displayed."
                )
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