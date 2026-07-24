package flows.ios;

import components.ios.IosTutorialOverlay;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriverException;
import pages.ios.FeedPage;
import pages.ios.NewPostPage;
import pages.ios.PostDetailsPage;

import java.time.Duration;
import java.util.Map;

public class IosFeedPostFlow {

    private final IOSDriver driver;
    private final FeedPage feedPage;
    private final NewPostPage newPostPage;
    private final PostDetailsPage postDetailsPage;
    private final IosTutorialOverlay tutorialOverlay;

    public IosFeedPostFlow(IOSDriver driver) {
        this(
                driver,
                new FeedPage(driver)
        );
    }

    public IosFeedPostFlow(
            IOSDriver driver,
            FeedPage feedPage
    ) {
        this.driver = driver;
        this.feedPage = feedPage;
        newPostPage = new NewPostPage(driver);
        postDetailsPage = new PostDetailsPage(driver);
        tutorialOverlay = new IosTutorialOverlay(driver);
    }

    public void createTextPost(
            String postText
    ) {
        feedPage.tapFeedTab();
        dismissPossibleSystemNotificationBanner();
        feedPage.waitUntilReady();
        feedPage.openCreatePost();
        newPostPage.publishTextPost(postText);
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

    private void dismissPossibleSystemNotificationBanner() {
        pause(Duration.ofMillis(250));

        Dimension screenSize =
                driver.manage()
                        .window()
                        .getSize();

        swipeSystemNotificationBannerUp(
                screenSize
        );

        pause(Duration.ofMillis(100));
    }

    private void swipeSystemNotificationBannerUp(
            Dimension screenSize
    ) {
        int x =
                screenSize.getWidth() / 2;

        int fromY =
                (int) (
                        screenSize.getHeight()
                                * 0.15
                );

        int toY =
                (int) (
                        screenSize.getHeight()
                                * 0.02
                );

        try {
            driver.executeScript(
                    "mobile: dragFromToForDuration",
                    Map.of(
                            "duration", 0.20,
                            "fromX", x,
                            "fromY", fromY,
                            "toX", x,
                            "toY", toY
                    )
            );
        } catch (WebDriverException ignored) {
            /*
             * Системного баннера могло не быть.
             * В таком случае продолжаем создание
             * поста штатно.
             */
        }
    }

    private void pause(
            Duration duration
    ) {
        try {
            Thread.sleep(
                    duration.toMillis()
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Interrupted while waiting for "
                            + "the iOS notification banner.",
                    exception
            );
        }
    }
}
