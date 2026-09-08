package flows.android;

import components.android.TutorialOverlay;
import io.appium.java_client.android.AndroidDriver;
import pages.android.FeedPage;
import pages.android.NewPostPage;
import pages.android.PostDetailsPage;

public class FeedPostFlow {

    private final FeedPage feedPage;
    private final NewPostPage newPostPage;
    private final PostDetailsPage postDetailsPage;
    private final TutorialOverlay tutorialOverlay;

    public FeedPostFlow(AndroidDriver driver) {
        feedPage = new FeedPage(driver);
        newPostPage = new NewPostPage(driver);
        postDetailsPage = new PostDetailsPage(driver);
        tutorialOverlay = new TutorialOverlay(driver);
    }

    public void createTextPost(String postText) {
        feedPage.openFeed();
        feedPage.openCreatePost();
        newPostPage.publishTextPost(postText);
        feedPage.waitUntilReady();
        feedPage.waitUntilPostReady(postText);
    }

    public void likePost(String postText) {
        feedPage.likePost(postText);
    }

    public void dislikePost(String postText) {
        feedPage.dislikePost(postText);
    }

    public void openPostComments(String postText) {
        feedPage.openPostComments(postText);
        tutorialOverlay.dismissIfPresent();
        postDetailsPage.waitUntilOpened();
    }

    public void addComment(String commentText) {
        postDetailsPage.addComment(commentText);
    }

    public void deleteComment(String commentText) {
        postDetailsPage.deleteComment(commentText);
    }

    public void deletePost(String postText) {
        postDetailsPage.deletePost();
        feedPage.waitUntilReady();
        feedPage.waitUntilPostDisappears(postText);
    }
}
