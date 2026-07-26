package tests.ios;

import flows.ios.IosAccountDeletionFlow;
import flows.ios.IosPostLoginFlow;
import flows.ios.IosPreMadeWorkoutSelectionFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import annotations.IosDeviceTest;
import pages.ios.EmailRegistrationPage;
import pages.ios.LoginPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IosPreMadeWorkoutSelectionTest
        extends BaseIosTest {

    private LoginPage loginPage;
    private EmailRegistrationPage
            emailRegistrationPage;
    private IosPreMadeWorkoutSelectionFlow
            preMadeWorkoutSelectionFlow;
    private IosPostLoginFlow postLoginFlow;
    private IosAccountDeletionFlow
            accountDeletionFlow;

    @BeforeEach
    public void createPages() {
        loginPage = new LoginPage(driver);

        emailRegistrationPage =
                new EmailRegistrationPage(driver);

        preMadeWorkoutSelectionFlow =
                new IosPreMadeWorkoutSelectionFlow(
                        driver
                );

        postLoginFlow =
                new IosPostLoginFlow(driver);

        accountDeletionFlow =
                new IosAccountDeletionFlow(driver);
    }

    @IosDeviceTest
    public void shouldSelectPreMadeWorkoutForNewUser() {
        loginPage.openEmailAuthentication();

        emailRegistrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );

        preMadeWorkoutSelectionFlow
                .selectGeneralMuscleBuildingForGymAtExpertLevel();

        postLoginFlow.complete();
    }

    @AfterEach
    public void cleanUpCreatedAccount() {
        if (accountDeletionFlow != null
                && accountDeletionFlow
                .deleteAccountIfPossible()) {

            assertTrue(
                    loginPage
                            .isEmailAuthenticationOptionDisplayed(),
                    "Account deletion failed: "
                            + "authentication options screen "
                            + "was not opened."
            );
        }
    }
}