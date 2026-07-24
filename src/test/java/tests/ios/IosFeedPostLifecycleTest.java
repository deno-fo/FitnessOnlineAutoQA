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
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        feedPostFlow =
                new IosFeedPostFlow(driver);
        feedPage = new FeedPage(driver);

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

        assertTrue(
                feedPage.isPostDisplayed(postText),
                "Created feed post is not displayed."
        );

        feedPostFlow.likePost(postText);

        FeedPage.ReactionCounts likedCounts =
                feedPage.waitUntilReactionCounts(
                        postText,
                        "1",
                        "0"
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
                feedPage.waitUntilReactionCounts(
                        postText,
                        "0",
                        "1"
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

        assertFalse(
                feedPage.isPostDisplayed(postText),
                "Deleted feed post is still displayed."
        );
    }

    private void registerNewUser() {
        loginPage.openEmailAuthentication();

        registrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );

        postLoginFlow.completeUntilMainScreen();
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
            assertTrue(
                    loginPage
                            .isEmailAuthenticationOptionDisplayed(),
                    "Account cleanup failed: "
                            + "the login screen was not opened."
            );
        }
    }
}
