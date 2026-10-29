
import java.util.LinkedList;

public class PredictionEngine {

    public PredictionResult predict(
            String symbol,
            LinkedList<StockPrice> prices,
            CompanyData company,
            PredictionPeriod period) {

        /*
         * Validate inputs
         */

        if (prices == null || prices.isEmpty()) {
            throw new IllegalArgumentException(
                    "Historical prices cannot be empty."
            );
        }

        if (company == null) {
            throw new IllegalArgumentException(
                    "Company data cannot be null."
            );
        }

        if (period == null) {
            throw new IllegalArgumentException(
                    "Prediction period cannot be null."
            );
        }

        /*
         * Current price
         */

        double currentPrice =
                prices.getLast().getClose();

        if (currentPrice <= 0) {
            throw new IllegalArgumentException(
                    "Current stock price must be greater than zero."
            );
        }

        /*
         * One-year historical growth score
         *
         * Approximately 252 trading days = one year.
         */

        int oneYearDays = 252;

        double growthScore = 0;

        if (prices.size() > oneYearDays) {

            double oldPrice =
                    prices.get(
                            prices.size() - oneYearDays
                    ).getClose();

            if (oldPrice > 0) {

                growthScore =
                        ((currentPrice - oldPrice)
                                / oldPrice) * 100;

            }

        } else {

            System.out.println(
                    "Not enough historical data for 1-year growth."
            );

        }

        /*
         * Technical score
         */

        double ma200 =
                MovingAverage.calculate(
                        prices,
                        200
                );

        double technicalScore = 0;

        if (ma200 > 0) {

            technicalScore =
                    ((currentPrice - ma200)
                            / ma200) * 20;

        }

        /*
         * Valuation score
         */

        double peRatio =
                company.getPeRatio();

        double valuationScore;

        if (peRatio > 0 && peRatio < 20) {

            valuationScore = 10;

        } else if (peRatio > 0 && peRatio < 30) {

            valuationScore = 5;

        } else if (peRatio <= 0) {

            valuationScore = 0;

        } else {

            valuationScore = -5;

        }

        /*
         * Financial health score
         */

        double healthScore =

                (company.getRevenueGrowth() * 0.30)

                        +

                (company.getEarningsGrowth() * 0.30)

                        +

                (company.getProfitMargin() * 0.15)

                        +

                (company.getReturnOnEquity() * 0.15)

                        -

                (company.getDebtToEquity() * 5)

                        +

                Math.min(
                        company.getFreeCashFlow()
                                / 10000000000.0,
                        10
                );

        /*
         * Momentum score
         */

        double rsi =
                RSI.calculate(
                        prices,
                        14
                );

        double momentumScore;

        if (rsi < 30) {

            momentumScore = 10;

        } else if (rsi > 70) {

            momentumScore = -10;

        } else {

            momentumScore = 0;

        }

        /*
         * Market score
         */

        double marketScore = 0;

        /*
         * Default weights
         */

        double growthWeight = 0.30;
        double valuationWeight = 0.20;
        double healthWeight = 0.15;
        double technicalWeight = 0.15;
        double momentumWeight = 0.10;
        double marketWeight = 0.10;

        /*
         * Sector classification
         */

        MarketSector sector =
                MarketClassifier.classify(
                        company.getSector()
                );

        System.out.println("Sector Used: " + sector);

        /*
         * Sector-specific weights
         */

        double[] weights =
                SectorModelSelector.getWeights(
                        sector,
                        company
                );

        growthWeight = weights[0];
        valuationWeight = weights[1];
        healthWeight = weights[2];
        technicalWeight = weights[3];
        momentumWeight = weights[4];
        marketWeight = weights[5];

        /*
         * Final model score
         */

        double finalScore =

                growthScore * growthWeight

                        +

                valuationScore * valuationWeight

                        +

                healthScore * healthWeight

                        +

                technicalScore * technicalWeight

                        +

                momentumScore * momentumWeight

                        +

                marketScore * marketWeight;

        /*
         * Bound the model's annualized estimate.
         *
         * These are model assumptions, not validated
         * limits on actual stock returns.
         */

        if (finalScore > 60) {
            finalScore = 60;
        }

        if (finalScore < -30) {
            finalScore = -30;
        }

        /*
         * Time-based prediction
         *
         * Interpret the final score as an annualized
         * return estimate, then compound it over the
         * selected number of trading days.
         *
         * PredictionPeriod now directly affects price.
         */

        int tradingDays =
                period.getTradingDays();

        double years =
                tradingDays / 252.0;

        double annualReturn =
                finalScore / 100.0;

        double predictedPrice =
                currentPrice
                        * Math.pow(
                                1 + annualReturn,
                                years
                        );

        /*
         * Prediction direction
         */

        String direction;

        if (predictedPrice > currentPrice) {

            direction = "Upward";

        } else if (predictedPrice < currentPrice) {

            direction = "Downward";

        } else {

            direction = "Neutral";

        }

        /*
         * Debug output
         */

        System.out.println(
                "Prediction Period: " + period.getLabel()
        );

        System.out.println(
                "Trading Days: " + tradingDays
        );

        System.out.printf(
                "Final Score: %.2f%%%n",
                finalScore
        );

        System.out.printf(
                "Current Price: $%.2f%n",
                currentPrice
        );

        System.out.printf(
                "Predicted Price: $%.2f%n",
                predictedPrice
        );

        /*
         * Return prediction
         */

        return new PredictionResult(
                predictedPrice,
                direction,
                period
        );
    }
}
