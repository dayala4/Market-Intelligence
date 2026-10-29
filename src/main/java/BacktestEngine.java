import java.util.LinkedList;


public class BacktestEngine {


    public BacktestResult runTest(
            String symbol,
            LinkedList<StockPrice> prices,
            CompanyData company,
            int startIndex) {



        LinkedList<StockPrice> oldData =
                new LinkedList<>();



        for(int i = 0; i < startIndex; i++) {

            oldData.add(
                    prices.get(i)
            );

        }



        double startPrice =
                oldData.getLast()
                .getClose();



        PredictionEngine engine =
                new PredictionEngine();



        PredictionResult prediction =
                engine.predict(
                        symbol,
                        oldData,
                        company,
                        PredictionPeriod.ONE_YEAR
                );



        double predictedPrice =
                prediction.getPredictedPrice();



        double actualPrice =
                prices.get(
                    startIndex + 252
                )
                .getClose();



        return new BacktestResult(
                symbol,
                startPrice,
                predictedPrice,
                actualPrice
        );

    }

}