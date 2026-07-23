package tests.android;

import components.android.AndroidNotificationPermissionDialog;
import flows.android.AccountDeletionFlow;
import flows.android.FeedPostFlow;
import flows.android.GoogleHealthAccessFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.android.BodyParametersPage;
import pages.android.EmailRegistrationPage;
import pages.android.FeedPage;
import pages.android.LoginPage;
import pages.android.PostDetailsPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AndroidFeedPostLifecycleTest extends BaseAndroidTest {

    private LoginPage loginPage;
    private EmailRegistrationPage emailRegistrationPage;
    private GoogleHealthAccessFlow googleHealthAccessFlow;
    private BodyParametersPage bodyParametersPage;
    private AndroidNotificationPermissionDialog notificationPermissionDialog;

    private FeedPostFlow feedPostFlow;
    private FeedPage feedPage;
    private PostDetailsPage postDetailsPage;

    private AccountDeletionFlow accountDeletionFlow;

    @BeforeEach
    public void createPages() {
        loginPage =
                new LoginPage(driver);

        emailRegistrationPage =
                new EmailRegistrationPage(driver);

        googleHealthAccessFlow =
                new GoogleHealthAccessFlow(driver);

        bodyParametersPage =
                new BodyParametersPage(driver);

        notificationPermissionDialog =
                new AndroidNotificationPermissionDialog(driver);

        feedPostFlow =
                new FeedPostFlow(driver);

        feedPage =
                new FeedPage(driver);

        postDetailsPage =
                new PostDetailsPage(driver);

        accountDeletionFlow =
                new AccountDeletionFlow(driver);
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

        feedPostFlow.likeTopPost();
        feedPage.waitUntilReactionCounts("1", "0");

        assertEquals(
                "1",
                feedPage.getTopPostLikesCount(),
                "Feed post likes count is wrong after liking."
        );

        assertEquals(
                "0",
                feedPage.getTopPostDislikesCount(),
                "Feed post dislikes count changed after liking."
        );

        feedPostFlow.dislikeTopPost();
        feedPage.waitUntilReactionCounts("0", "1");

        assertEquals(
                "0",
                feedPage.getTopPostLikesCount(),
                "Feed post like was not removed after disliking."
        );

        assertEquals(
                "1",
                feedPage.getTopPostDislikesCount(),
                "Feed post dislikes count is wrong after disliking."
        );

        feedPostFlow.openTopPostComments();
        feedPostFlow.addComment(commentText);

        assertTrue(
                postDetailsPage.isCommentDisplayed(commentText),
                "Created feed comment is not displayed."
        );

        feedPostFlow.deleteComment(commentText);

        assertFalse(
                postDetailsPage.isCommentDisplayed(commentText),
                "Deleted feed comment is still displayed."
        );

        feedPostFlow.deletePost(postText);

        assertFalse(
                feedPage.isPostDisplayed(postText),
                "Deleted feed post is still displayed."
        );
    }

    private void registerNewUser() {
        loginPage.skipWelcomeScreen();
        loginPage.openEmailAuthentication();

        emailRegistrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );

        googleHealthAccessFlow.continueDependingOnAvailability(
                healthConnectAvailable
        );

        bodyParametersPage.continueWithDefaultValues();
        notificationPermissionDialog.allowNotifications();
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
