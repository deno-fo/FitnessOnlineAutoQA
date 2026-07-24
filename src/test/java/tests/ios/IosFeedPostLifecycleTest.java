package tests.ios;

import org.junit.jupiter.api.Test;
import flows.ios.IosFeedPostFlow;

public class IosFeedPostLifecycleTest extends BaseIosTest {

    @Test
    public void createLikeCommentDeletePost() {
        String postText = "iOS feed lifecycle test";

        IosFeedPostFlow flow =
                new IosFeedPostFlow(driver);

        flow.createTextPost(postText);
        flow.likeTopPost();
        flow.openComments();
        flow.deletePost();
    }
}
