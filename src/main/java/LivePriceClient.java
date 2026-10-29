
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LivePriceClient {

    private String loadApiKey() {
        Properties properties = new Properties();

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("api.properties")) {

            if (input == null) {
                System.out.println("api.properties not found.");
                return null;
            }

            properties.load(input);

            String key = properties.getProperty("FMP_API_KEY_1");

            if (key == null || key.trim().isEmpty()) {
                System.out.println("FMP API key is not configured.");
                return null;
            }

            return key.trim();

        } catch (Exception e) {
            System.out.println("Could not load FMP API key.");
            return null;
        }
    }

    public double getCurrentPrice(String symbol) {
        String apiKey = loadApiKey();

        if (apiKey == null) {
            return 0;
        }

        try {
            String encodedSymbol = URLEncoder.encode(
                    symbol,
                    StandardCharsets.UTF_8
            );

            String url =
                    "https://financialmodelingprep.com/stable/quote"
                    + "?symbol=" + encodedSymbol
                    + "&apikey=" + URLEncoder.encode(
                            apiKey,
                            StandardCharsets.UTF_8
                    );

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                System.out.println(
                        "Live price request failed. HTTP status: "
                        + response.statusCode()
                );
                return 0;
            }

            JsonElement element = JsonParser.parseString(
                    response.body()
            );

            if (!element.isJsonArray()) {
                System.out.println(
                        "Live price response was not in the expected format."
                );
                return 0;
            }

            JsonArray array = element.getAsJsonArray();

            if (array.isEmpty() || !array.get(0).isJsonObject()) {
                System.out.println("No live price returned for " + symbol + ".");
                return 0;
            }

            JsonObject stock = array.get(0).getAsJsonObject();

            if (!stock.has("price")
                    || stock.get("price").isJsonNull()) {
                System.out.println("Price is missing for " + symbol + ".");
                return 0;
            }

            return stock.get("price").getAsDouble();

        } catch (Exception e) {
            System.out.println("Could not retrieve live price for " + symbol + ".");
            return 0;
        }
    }
}
