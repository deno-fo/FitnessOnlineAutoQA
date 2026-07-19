package tests.ios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.ios.EmailAuthPage;
import pages.ios.LoginPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IosRegisteredUserAuthenticationTest
        extends BaseIosTest {

    private LoginPage loginPage;
    private EmailAuthPage emailAuthPage;

    @BeforeEach
    public void createPages() {
        loginPage = new LoginPage(driver);
        emailAuthPage = new EmailAuthPage(driver);
    }

    @Test
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
}