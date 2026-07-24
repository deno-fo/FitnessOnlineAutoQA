package flows.ios;

import components.ios.IosTutorialOverlay;
import io.appium.java_client.ios.IOSDriver;
import pages.ios.FeedPage;
import pages.ios.NewPostPage;
import pages.ios.PostDetailsPage;

import java.time.Duration;

public class IosFeedPostFlow {

    private final FeedPage feedPage;
    private final NewPostPage newPostPage;
    private final PostDetailsPage postDetailsPage;
    private final IosTutorialOverlay tutorialOverlay;

    public IosFeedPostFlow(IOSDriver driver) {
        feedPage = new FeedPage(driver);
        newPostPage = new NewPostPage(driver);
        postDetailsPage = new PostDetailsPage(driver);
        tutorialOverlay = new IosTutorialOverlay(driver);
    }

    public void createTextPost(
            String postText
    ) {
        feedPage.openFeed();
        feedPage.openCreatePost();
        newPostPage.publishTextPost(postText);
        feedPage.waitUntilReady();
        feedPage.waitUntilPostReady(postText);
    }

    public void likePost(
            String postText
    ) {
        feedPage.likePost(postText);
    }

    public void dislikePost(
            String postText
    ) {
        feedPage.dislikePost(postText);
    }

    public void openPostComments(
            String postText
    ) {
        feedPage.openPostComments(postText);

        tutorialOverlay
                .waitAndDismissIfPresent(
                        Duration.ofMillis(300)
                );

        postDetailsPage.waitUntilOpened();
    }

    public void addComment(
            String commentText
    ) {
        postDetailsPage.addComment(commentText);
    }

    public void deleteComment(
            String commentText
    ) {
        postDetailsPage.deleteComment(commentText);
    }

    public void deletePost(
            String postText
    ) {
        postDetailsPage.deletePost();
        feedPage.waitUntilReady();
        feedPage.waitUntilPostDisappears(postText);
    }
}
