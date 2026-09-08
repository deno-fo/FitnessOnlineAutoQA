package tests.ios;

import flows.ios.IosLogoutFlow;
import flows.ios.IosPostLoginFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import annotations.IosDeviceTest;
import pages.ios.EmailAuthPage;
import pages.ios.LoginPage;
import pages.ios.MainPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IosRegisteredUserAuthenticationTest
        extends BaseIosTest {

    private LoginPage loginPage;
    private EmailAuthPage emailAuthPage;
    private IosPostLoginFlow postLoginFlow;
    private MainPage mainPage;
    private IosLogoutFlow logoutFlow;

    private boolean registeredLoginAttempted;

    @BeforeEach
    public void createPages() {
        loginPage = new LoginPage(driver);
        emailAuthPage = new EmailAuthPage(driver);
        postLoginFlow = new IosPostLoginFlow(driver);
        mainPage = new MainPage(driver);
        logoutFlow = new IosLogoutFlow(driver);

        registeredLoginAttempted = false;
    }

    @IosDeviceTest
    public void shouldSignInRegisteredUser() {
        loginPage.openEmailAuthentication();

        registeredLoginAttempted = true;
        emailAuthPage.signIn(
                TestData.REGISTERED_USER_EMAIL,
                TestData.REGISTERED_USER_PASSWORD
        );

        postLoginFlow.complete();

        assertTrue(
                mainPage.isOpened(),
                "Registered user login failed: "
                        + "main screen was not opened."
        );

    }

    @IosDeviceTest
    public void shouldShowErrorForInvalidCredentials() {
        loginPage.openEmailAuthentication();

        emailAuthPage.signIn(
                TestData.INVALID_EMAIL,
                TestData.INVALID_PASSWORD
        );

        assertTrue(
                emailAuthPage
                        .isInvalidCredentialsMessageDisplayed(),
                "Invalid credentials verification failed: "
                        + "expected login error was not displayed."
        );
    }

    @AfterEach
    public void restoreAuthenticationState() {
        if (emailAuthPage == null) {
            return;
        }
        if (registeredLoginAttempted) {
            emailAuthPage.dismissInvalidCredentialsMessageIfPresent();
            logoutFlow.logOutAfterInterruptedScenario();

            assertTrue(
                    loginPage
                            .isEmailAuthenticationOptionDisplayed(),
                    "Registered user logout failed: "
                            + "authentication options screen was not opened."
            );

            return;
        }

        emailAuthPage
                .dismissInvalidCredentialsMessageIfPresent();

        loginPage
                .returnToAuthenticationOptionsIfNeeded();
    }
}
