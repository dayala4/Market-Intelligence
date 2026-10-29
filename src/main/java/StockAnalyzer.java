
import java.util.LinkedList;

public class StockAnalyzer {

    public StockReport analyze(
            String symbol,
            LinkedList<StockPrice> prices,
            CompanyData company) {

        // CURRENT PRICE
        double currentPrice =
                prices.getLast().getClose();

        // MOVING AVERAGES
        double ma20 =
                MovingAverage.calculate(prices, 20);

        double ma50 =
                MovingAverage.calculate(prices, 50);

        double ma200 =
                MovingAverage.calculate(prices, 200);

        // RSI
        double rsi =
                RSI.calculate(prices, 14);

        // TREND
        String trend;

        if (currentPrice > ma20
                && ma20 > ma50
                && ma50 > ma200) {

            trend = "STRONG UPWARD";

        } else if (currentPrice > ma200) {

            trend = "POSITIVE";

        } else {

            trend = "WEAK";
        }

        // PREDICTION
        PredictionEngine engine =
                new PredictionEngine();

        PredictionResult prediction =
                engine.predict(
                        symbol,
                        prices,
                        company,
                        PredictionPeriod.ONE_YEAR
                );

        // COMPANY DESCRIPTION
        String companyDescription =
                company.getDescription();

        // STOCK REPORT
        return new StockReport(
                currentPrice,

                company.getSector(),

                ma20,
                ma50,
                ma200,

                rsi,

                company.getPeRatio(),
                company.getEarningsGrowth(),
                company.getDividendYield(),

                company.getCompanyName(),
                companyDescription,

                prediction.getPredictedPrice(),
                prediction.getDirection(),

                trend
        );
    }
}
