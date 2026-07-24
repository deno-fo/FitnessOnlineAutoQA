package flows.ios;

import components.ios.IosTutorialOverlay;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriverException;
import pages.ios.FeedPage;
import pages.ios.NewPostPage;
import pages.ios.PostDetailsPage;
import utils.StepTimer;

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
        StepTimer.run(
                "Create post | Tap Feed tab",
                feedPage::tapFeedTab
        );

        StepTimer.run(
                "Create post | Dismiss notification banner",
                this::dismissPossibleSystemNotificationBanner
        );

        StepTimer.run(
                "Create post | Wait Feed ready",
                feedPage::waitUntilReady
        );

        StepTimer.run(
                "Create post | Open create form",
                feedPage::openCreatePost
        );

        StepTimer.run(
                "Create post | Fill and submit form",
                () -> newPostPage.publishTextPost(postText)
        );

        StepTimer.run(
                "Create post | Wait created post ready",
                () -> feedPage.waitUntilPostReady(postText)
        );
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
        StepTimer.run(
                "Comments | Tap comments action",
                () -> feedPage.openPostComments(postText)
        );

        StepTimer.run(
                "Comments | Dismiss tutorial overlay",
                () -> tutorialOverlay
                        .waitAndDismissIfPresent(
                                Duration.ofMillis(300)
                        )
        );

        StepTimer.run(
                "Comments | Wait post details opened",
                postDetailsPage::waitUntilOpened
        );
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
        StepTimer.run(
                "Delete post | Open menu and confirm",
                postDetailsPage::deletePost
        );

        StepTimer.run(
                "Delete post | Wait Feed ready",
                feedPage::waitUntilReady
        );

        StepTimer.run(
                "Delete post | Wait post disappeared",
                () -> feedPage.waitUntilPostDisappears(
                        postText
                )
        );
    }

    private void dismissPossibleSystemNotificationBanner() {
        StepTimer.run(
                "Notification banner | Pre-swipe pause",
                () -> pause(Duration.ofMillis(250))
        );

        Dimension screenSize = StepTimer.get(
                "Notification banner | Get screen size",
                () -> driver.manage()
                        .window()
                        .getSize()
        );

        StepTimer.run(
                "Notification banner | Execute swipe",
                () -> swipeSystemNotificationBannerUp(
                        screenSize
                )
        );

        StepTimer.run(
                "Notification banner | Post-swipe pause",
                () -> pause(Duration.ofMillis(100))
        );
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
