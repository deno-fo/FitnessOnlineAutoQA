package utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class AppiumConfig {

    public static final String SERVER_URL =
            "http://127.0.0.1:4723";

    private static final Duration SERVER_CHECK_TIMEOUT =
            Duration.ofSeconds(3);

    private static final HttpClient HTTP_CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(SERVER_CHECK_TIMEOUT)
                    .build();

    public static void ensureServerIsAvailable() {
        URI statusUri =
                URI.create(SERVER_URL + "/status");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(statusUri)
                .timeout(SERVER_CHECK_TIMEOUT)
                .GET()
                .build();

        try {
            HttpResponse<Void> response =
                    HTTP_CLIENT.send(
                            request,
                            HttpResponse.BodyHandlers.discarding()
                    );

            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode < 300) {
                return;
            }

            throw new IllegalStateException(
                    "Appium server returned HTTP "
                            + statusCode
                            + " for "
                            + statusUri
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Appium server is not available at "
                            + statusUri
                            + ". Start Appium and retry.",
                    exception
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Appium server availability check was interrupted.",
                    exception
            );
        }
    }

    private AppiumConfig() {
    }
}