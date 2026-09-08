package tests.ios;

import flows.ios.IosAccountDeletionFlow;
import flows.ios.IosGuestOnboardingFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import annotations.IosDeviceTest;
import pages.ios.LoginPage;
import pages.ios.MainPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IosGuestAccountCreationTest
        extends BaseIosTest {

    private LoginPage loginPage;
    private MainPage mainPage;

    private IosGuestOnboardingFlow
            guestOnboardingFlow;

    private IosAccountDeletionFlow
            accountDeletionFlow;

    private boolean guestSessionStarted;
    private boolean guestAccountCreated;

    @BeforeEach
    public void initializePagesAndFlows() {
        loginPage = new LoginPage(driver);
        mainPage = new MainPage(driver);

        guestOnboardingFlow =
                new IosGuestOnboardingFlow(driver);

        accountDeletionFlow =
                new IosAccountDeletionFlow(driver);

        guestSessionStarted = false;
        guestAccountCreated = false;
    }

    @IosDeviceTest
    public void shouldCreateGuestAccount() {
        accountDeletionFlow.beforeCreatingTestAccount();
        guestSessionStarted = true;
        loginPage.enterGuestMode();

        guestOnboardingFlow.complete();

        assertTrue(
                mainPage.isOpened(),
                "Guest account creation failed: "
                        + "the main screen was not opened."
        );

        guestAccountCreated = true;
    }

    @AfterEach
    public void deleteGuestAccount() {
        if (!guestSessionStarted) {
            return;
        }

        boolean accountDeleted =
                accountDeletionFlow
                        .deleteAccountIfPossible();

        if (guestAccountCreated) {
            assertTrue(
                    accountDeleted,
                    "Guest account cleanup failed: "
                            + "the account was not deleted."
            );
        }

        if (accountDeleted) {
            assertTrue(
                    loginPage
                            .isEmailAuthenticationOptionDisplayed(),
                    "Guest account cleanup failed: "
                            + "the login screen was not opened."
            );
        }
    }
}
