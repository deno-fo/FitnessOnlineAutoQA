package tests.ios;

import flows.ios.IosAccountDeletionFlow;
import flows.ios.IosFeedPostFlow;
import flows.ios.IosPostLoginFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.ios.EmailRegistrationPage;
import pages.ios.FeedPage;
import pages.ios.LoginPage;
import utils.StepTimer;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IosFeedPostLifecycleTest
        extends BaseIosTest {

    private LoginPage loginPage;
    private EmailRegistrationPage registrationPage;
    private IosPostLoginFlow postLoginFlow;

    private IosFeedPostFlow feedPostFlow;
    private FeedPage feedPage;

    private IosAccountDeletionFlow accountDeletionFlow;

    @BeforeEach
    public void initializePagesAndFlows() {
        loginPage = new LoginPage(driver);
        registrationPage =
                new EmailRegistrationPage(driver);
        postLoginFlow =
                new IosPostLoginFlow(driver);

        feedPage = new FeedPage(driver);
        feedPostFlow =
                new IosFeedPostFlow(
                        driver,
                        feedPage
                );

        accountDeletionFlow =
                new IosAccountDeletionFlow(driver);
    }

    @Test
    public void shouldCreateInteractWithAndDeleteFeedPost() {
        registerNewUser();

        String postText =
                TestData.uniqueFeedPostText();

        String commentText =
                TestData.uniqueFeedCommentText();

        feedPostFlow.createTextPost(postText);

        boolean postDisplayed = StepTimer.get(
                "Feed | Verify created post",
                () -> feedPage.isPostDisplayed(postText)
        );

        assertTrue(
                postDisplayed,
                "Created feed post is not displayed."
        );

        feedPostFlow.likePost(postText);

        FeedPage.ReactionCounts likedCounts =
                StepTimer.get(
                        "Feed | Wait for like counts",
                        () -> feedPage.waitUntilReactionCounts(
                                postText,
                                "1",
                                "0"
                        )
                );

        assertEquals(
                "1",
                likedCounts.likes(),
                "Feed post likes count is wrong after liking."
        );

        assertEquals(
                "0",
                likedCounts.dislikes(),
                "Feed post dislikes count changed after liking."
        );

        feedPostFlow.dislikePost(postText);

        FeedPage.ReactionCounts dislikedCounts =
                StepTimer.get(
                        "Feed | Wait for dislike counts",
                        () -> feedPage.waitUntilReactionCounts(
                                postText,
                                "0",
                                "1"
                        )
                );

        assertEquals(
                "0",
                dislikedCounts.likes(),
                "Feed post like was not removed after disliking."
        );

        assertEquals(
                "1",
                dislikedCounts.dislikes(),
                "Feed post dislikes count is wrong after disliking."
        );

        feedPostFlow.openPostComments(postText);
        feedPostFlow.addComment(commentText);
        feedPostFlow.deleteComment(commentText);
        feedPostFlow.deletePost(postText);
    }

    private void registerNewUser() {
        StepTimer.run(
                "Registration | Open email authentication",
                loginPage::openEmailAuthentication
        );

        StepTimer.run(
                "Registration | Submit registration",
                () -> registrationPage.registerMaleUser(
                        TestData.uniqueEmail(),
                        TestData.PASSWORD,
                        TestData.NAME,
                        TestData.SURNAME
                )
        );

        StepTimer.run(
                "Registration | Complete onboarding",
                postLoginFlow::completeUntilMainScreen
        );
    }

    @AfterEach
    public void deleteCreatedAccount() {
        if (accountDeletionFlow == null) {
            return;
        }

        boolean accountDeleted =
                accountDeletionFlow
                        .deleteAccountIfPossible();

        if (accountDeleted) {
            boolean loginDisplayed = StepTimer.get(
                    "Cleanup | Verify login screen",
                    loginPage
                            ::isEmailAuthenticationOptionDisplayed
            );

            assertTrue(
                    loginDisplayed,
                    "Account cleanup failed: "
                            + "the login screen was not opened."
            );
        }
    }
}
