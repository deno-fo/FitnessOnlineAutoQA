package flows.ios;

import io.appium.java_client.ios.IOSDriver;
import pages.ios.IosFeedPage;
import pages.ios.IosNewPostPage;
import pages.ios.IosPostDetailsPage;

public class IosFeedPostFlow {

    private final IosFeedPage feedPage;
    private final IosNewPostPage newPostPage;
    private final IosPostDetailsPage postDetailsPage;

    public IosFeedPostFlow(IOSDriver driver) {
        feedPage = new IosFeedPage(driver);
        newPostPage = new IosNewPostPage(driver);
        postDetailsPage = new IosPostDetailsPage(driver);
    }

    public void createTextPost(String text) {
        feedPage.openCreatePost();
        newPostPage.publishTextPost(text);
        feedPage.waitUntilTopPostReady(text);
    }

    public void likeTopPost() {
        feedPage.likeTopPost();
        feedPage.waitUntilReactionCounts("1", "0");
    }

    public void openComments() {
        feedPage.openTopPostComments();
        postDetailsPage.waitUntilOpened();
    }

    public void deletePost() {
        postDetailsPage.deletePost();
    }
}
