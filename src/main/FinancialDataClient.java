
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class FinancialDataClient {

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
            String key = properties.getProperty("ALPHA_VANTAGE_API_KEY");

            if (key == null || key.trim().isEmpty()) {
                System.out.println("Alpha Vantage API key is not configured.");
                return null;
            }

            return key.trim();

        } catch (Exception e) {
            System.out.println("Could not load Alpha Vantage API key.");
            return null;
        }
    }

    public String getStockPrices(String symbol) {
        String apiKey = loadApiKey();

        if (apiKey == null) {
            return null;
        }

        try {
            String url =
                    "https://www.alphavantage.co/query"
                    + "?function=TIME_SERIES_DAILY"
                    + "&symbol=" + symbol
                    + "&outputsize=compact"
                    + "&apikey=" + apiKey;

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Status Code: " + response.statusCode());

            if (response.statusCode() != 200) {
                System.out.println("Stock price request failed.");
                return null;
            }

            return response.body();

        } catch (Exception e) {
            System.out.println("Could not retrieve stock prices.");
            return null;
        }
    }
}
