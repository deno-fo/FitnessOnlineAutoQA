package tests.android;

import annotations.AndroidDeviceTest;
import annotations.RequiresGoogleHealth;
import flows.android.AccountDeletionFlow;
import flows.android.AndroidOnboardingFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pages.android.GoogleHealthPage;
import pages.android.HealthPermissionsPage;
import pages.android.LoginPage;
import pages.android.MainPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AndroidGuestAccountCreationTest
        extends BaseAndroidTest {

    private LoginPage loginPage;
    private HealthPermissionsPage healthPermissionsPage;
    private GoogleHealthPage googleHealthPage;
    private AndroidOnboardingFlow onboardingFlow;
    private MainPage mainPage;
    private AccountDeletionFlow accountDeletionFlow;

    @BeforeEach
    public void createPages() {
        loginPage =
                new LoginPage(driver);

        healthPermissionsPage =
                new HealthPermissionsPage(driver);

        googleHealthPage =
                new GoogleHealthPage(driver);

        onboardingFlow =
                new AndroidOnboardingFlow(driver);

        mainPage =
                new MainPage(driver);

        accountDeletionFlow =
                new AccountDeletionFlow(driver);
    }

    @AndroidDeviceTest
    @RequiresGoogleHealth
    public void shouldCompleteGuestOnboardingWithGoogleHealth() {
        loginPage.enterGuestMode();

        googleHealthPage.grantAccess();
        healthPermissionsPage.enableAllowAll();
        healthPermissionsPage.confirmHealthPermissions();

        completeOnboardingWithDefaultUserData();

        assertGuestHomeScreenOpened();
    }

    @AndroidDeviceTest
    public void shouldCompleteGuestOnboardingAfterSkippingGoogleHealthInApp() {
        loginPage.enterGuestMode();

        googleHealthPage.skipAccess();
        googleHealthPage.declinePersuasion();
        googleHealthPage.continueWithoutAccess();

        completeOnboardingWithDefaultUserData();

        assertGuestHomeScreenOpened();
    }

    @AndroidDeviceTest
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
        onboardingFlow
                .completeGuestWithDefaultUserData();
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
