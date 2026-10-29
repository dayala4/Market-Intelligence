
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SupabaseConnectionTest {

    public static void main(String[] args) throws Exception {
        testTable("profiles");
        testTable("watchlist");
    }

    private static void testTable(String table) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SupabaseClient.getRestUri(
                        table + "?select=*&limit=1"))
                .header("apikey", SupabaseClient.getPublishableKey())
                .header("Authorization",
                        "Bearer " + SupabaseClient.getPublishableKey())
                .GET()
                .build();

        HttpResponse<String> response =
                SupabaseClient.getHttpClient().send(
                        request,
                        HttpResponse.BodyHandlers.ofString());

        System.out.println(table + " HTTP status: "
                + response.statusCode());
        System.out.println(table + " response: "
                + response.body());
    }
}
