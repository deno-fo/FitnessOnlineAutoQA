package tests.android;

import annotations.AndroidDeviceTest;
import components.android.TutorialOverlay;
import flows.android.AccountDeletionFlow;
import flows.android.AndroidOnboardingFlow;
import flows.android.PreMadeWorkoutSelectionFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pages.android.EmailRegistrationPage;
import pages.android.GoogleHealthPage;
import pages.android.LoginPage;
import pages.android.MainPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AndroidPreMadeWorkoutSelectionTest
        extends BaseAndroidTest {

    private LoginPage loginPage;
    private EmailRegistrationPage emailRegistrationPage;
    private GoogleHealthPage googleHealthPage;
    private AndroidOnboardingFlow onboardingFlow;
    private PreMadeWorkoutSelectionFlow preMadeWorkoutSelectionFlow;
    private TutorialOverlay tutorialOverlay;
    private MainPage mainPage;
    private AccountDeletionFlow accountDeletionFlow;

    @BeforeEach
    public void createPages() {
        loginPage =
                new LoginPage(driver);

        emailRegistrationPage =
                new EmailRegistrationPage(driver);

        googleHealthPage =
                new GoogleHealthPage(driver);

        onboardingFlow =
                new AndroidOnboardingFlow(driver);

        preMadeWorkoutSelectionFlow =
                new PreMadeWorkoutSelectionFlow(driver);

        tutorialOverlay =
                new TutorialOverlay(driver);

        mainPage =
                new MainPage(driver);

        accountDeletionFlow =
                new AccountDeletionFlow(driver);
    }

    @AndroidDeviceTest
    public void shouldSelectPreMadeWorkoutForNewUser() {
        loginPage.skipWelcomeScreen();
        loginPage.openEmailAuthentication();

        emailRegistrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );

        googleHealthPage.skipAccess();
        googleHealthPage.declinePersuasion();
        googleHealthPage.continueWithoutAccess();

        onboardingFlow.completeRegistrationWithDefaultUserData(
                preMadeWorkoutSelectionFlow::isReady
        );

        preMadeWorkoutSelectionFlow
                .selectGeneralMuscleBuildingForGymAtExpertLevel();

        tutorialOverlay.dismiss();

        assertTrue(
                mainPage.isPreMadeWorkoutSelected(
                        "General muscle building",
                        "For gym",
                        "Expert"
                ),
                "Wrong PreMade workout was selected."
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
