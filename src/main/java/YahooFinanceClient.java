import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class YahooFinanceClient {

    public String getStockData(String symbol) {

        try {

            String url =
                    "https://query1.finance.yahoo.com/v8/finance/chart/"
                    + symbol
                    + "?range=5y&interval=1d";

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header(
                                    "User-Agent",
                                    "Mozilla/5.0"
                            )
                            .GET()
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println(
                    "Yahoo Status: "
                    + response.statusCode()
            );

            if (response.statusCode() != 200) {

                System.out.println(
                        response.body()
                );

                return null;
            }

            return response.body();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;
    }
}