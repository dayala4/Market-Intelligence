import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedList;

public class YahooFinanceParser {

    public static LinkedList<StockPrice> parse(String json) {

        LinkedList<StockPrice> prices =
                new LinkedList<>();

        if (json == null || json.isEmpty()) {

            System.out.println(
                    "Yahoo returned no data."
            );

            return prices;
        }

        try {

            JsonObject root =
                    JsonParser.parseString(json)
                            .getAsJsonObject();

            JsonObject chart =
                    root.getAsJsonObject("chart");

            JsonArray results =
                    chart.getAsJsonArray("result");

            if (results == null ||
                    results.size() == 0) {

                System.out.println(
                        "No Yahoo results found."
                );

                return prices;
            }

            JsonObject result =
                    results.get(0)
                            .getAsJsonObject();

            // =================================================
            // TIMESTAMPS
            // =================================================

            JsonArray timestamps =
                    result.getAsJsonArray(
                            "timestamp"
                    );

            // =================================================
            // INDICATORS
            // =================================================

            JsonObject indicators =
                    result.getAsJsonObject(
                            "indicators"
                    );

            JsonObject quote =
                    indicators
                            .getAsJsonArray("quote")
                            .get(0)
                            .getAsJsonObject();

            JsonArray opens =
                    quote.getAsJsonArray("open");

            JsonArray highs =
                    quote.getAsJsonArray("high");

            JsonArray lows =
                    quote.getAsJsonArray("low");

            JsonArray closes =
                    quote.getAsJsonArray("close");

            // =================================================
            // CREATE STOCK PRICES
            // =================================================

            for (int i = 0;
                 i < timestamps.size();
                 i++) {

                if (opens.get(i).isJsonNull() ||
                    highs.get(i).isJsonNull() ||
                    lows.get(i).isJsonNull() ||
                    closes.get(i).isJsonNull()) {

                    continue;
                }

                long timestamp =
                        timestamps
                                .get(i)
                                .getAsLong();

                LocalDate date =
                        Instant
                                .ofEpochSecond(timestamp)
                                .atZone(
                                        ZoneId.systemDefault()
                                )
                                .toLocalDate();

                double open =
                        opens
                                .get(i)
                                .getAsDouble();

                double high =
                        highs
                                .get(i)
                                .getAsDouble();

                double low =
                        lows
                                .get(i)
                                .getAsDouble();

                double close =
                        closes
                                .get(i)
                                .getAsDouble();

       if (high < Math.max(open, close)
        || low > Math.min(open, close)
        || high < low) {

    System.out.println(
            "Skipping invalid Yahoo OHLC candle: "
            + date
            + " | O: " + open
            + " | H: " + high
            + " | L: " + low
            + " | C: " + close
    );

    continue;
}


                prices.add(
                        new StockPrice(
                                date,
                                open,
                                high,
                                low,
                                close
                        )
                );
            }

            // =================================================
            // DEBUG
            // =================================================

            System.out.println(
                    "Yahoo historical records: "
                    + prices.size()
            );

            if (!prices.isEmpty()) {

                System.out.println(
                        "Oldest date: "
                        + prices
                                .getFirst()
                                .getDate()
                );

                System.out.println(
                        "Newest date: "
                        + prices
                                .getLast()
                                .getDate()
                );
            }

            System.out.println(
                    "Number of Yahoo records: "
                    + prices.size()
            );

            // =================================================
            // FIRST 5
            // =================================================

            for (int i = 0;
                 i < Math.min(5, prices.size());
                 i++) {

                StockPrice price =
                        prices.get(i);

                System.out.println(
                        price.getDate()
                        + " | O: "
                        + price.getOpen()
                        + " | H: "
                        + price.getHigh()
                        + " | L: "
                        + price.getLow()
                        + " | C: "
                        + price.getClose()
                );
            }

            System.out.println("...");

            // =================================================
            // LAST 5
            // =================================================

            for (int i =
                    Math.max(
                            0,
                            prices.size() - 5
                    );

                 i < prices.size();

                 i++) {

                StockPrice price =
                        prices.get(i);

                System.out.println(
                        price.getDate()
                        + " | O: "
                        + price.getOpen()
                        + " | H: "
                        + price.getHigh()
                        + " | L: "
                        + price.getLow()
                        + " | C: "
                        + price.getClose()
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Yahoo parser failed!"
            );

            e.printStackTrace();
        }

        return prices;
    }
}