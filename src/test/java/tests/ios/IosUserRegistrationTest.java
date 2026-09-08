package tests.ios;

import annotations.IosDeviceTest;
import flows.ios.IosAccountDeletionFlow;
import flows.ios.IosPostLoginFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pages.ios.EmailRegistrationPage;
import pages.ios.LoginPage;
import pages.ios.MainPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IosUserRegistrationTest extends BaseIosTest {

    private LoginPage loginPage;
    private EmailRegistrationPage registrationPage;
    private IosPostLoginFlow postLoginFlow;
    private MainPage mainPage;
    private IosAccountDeletionFlow accountDeletionFlow;
    private boolean cleanupArmed;

    @BeforeEach
    public void createPagesAndFlows() {
        loginPage = new LoginPage(driver);
        registrationPage = new EmailRegistrationPage(driver);
        postLoginFlow = new IosPostLoginFlow(driver);
        mainPage = new MainPage(driver);
        accountDeletionFlow = new IosAccountDeletionFlow(driver);
        cleanupArmed = false;
    }

    @IosDeviceTest
    public void shouldRegisterNewUser() {
        loginPage.openEmailAuthentication();
        accountDeletionFlow.beforeCreatingTestAccount();
        cleanupArmed = true;

        registrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );

        postLoginFlow.completeUntilMainScreen();

        assertTrue(
                mainPage.isOpened(),
                "User registration failed: the main screen was not opened."
        );
    }

    @AfterEach
    public void deleteCreatedAccount() {
        if (!cleanupArmed) {
            return;
        }

        assertTrue(
                accountDeletionFlow.deleteAccountIfPossible(),
                "User registration cleanup failed: the account was not deleted."
        );

        assertTrue(
                loginPage.isEmailAuthenticationOptionDisplayed(),
                "User registration cleanup failed: authentication options "
                        + "screen was not opened."
        );
    }
}
