
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

public final class SupabaseClient {

    private static final String PROJECT_URL =
        "https://gxakmtzvptbpyxsytpfg.supabase.co";

    private static final String PUBLISHABLE_KEY =
            "sb_publishable_3xk7CTvUn8JqBCx-FxQH6A_NWpctOw5";

    private static final HttpClient HTTP_CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .build();

    private SupabaseClient() {
    }

    public static HttpClient getHttpClient() {
        return HTTP_CLIENT;
    }

    public static String getProjectUrl() {
        return PROJECT_URL;
    }

    public static String getPublishableKey() {
        return PUBLISHABLE_KEY;
    }

    public static boolean isConfigured() {
        return PROJECT_URL.startsWith("https://")
                && !PROJECT_URL.contains("PASTE_YOUR")
                && !PUBLISHABLE_KEY.contains("PASTE_YOUR")
                && !PUBLISHABLE_KEY.isBlank();
    }

    public static URI getAuthUri(String endpoint) {
        return URI.create(
                PROJECT_URL.replaceAll("/+$", "")
                        + "/auth/v1/" + endpoint
        );
    }

    public static URI getRestUri(String endpoint) {
        return URI.create(
                PROJECT_URL.replaceAll("/+$", "")
                        + "/rest/v1/" + endpoint
        );
    }
}




