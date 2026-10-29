import com.google.gson.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;

public class FinancialDataParser {

    public static LinkedList<StockPrice> parse(String json) {

        LinkedList<StockPrice> prices =
                new LinkedList<>();

        JsonObject root =
                JsonParser.parseString(json)
                        .getAsJsonObject();

        System.out.println(json);

        JsonObject series =
                root.getAsJsonObject(
                        "Time Series (Daily)"
                );

        for (Map.Entry<String, JsonElement> entry
                : series.entrySet()) {

            String dateString =
                    entry.getKey();

            JsonObject day =
                    entry.getValue()
                            .getAsJsonObject();

            double close =
                    day.get("4. close")
                            .getAsDouble();

            LocalDateTime date =
                    LocalDate.parse(dateString)
                            .atStartOfDay();

            prices.addFirst(
                    new StockPrice(
                            date,
                            close
                    )
            );
        }

        return prices;
    }
}