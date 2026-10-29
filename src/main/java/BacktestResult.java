public class BacktestResult {


    private String symbol;

    private double startPrice;

    private double predictedPrice;

    private double actualPrice;

    private double errorPercent;

    private String result;



    public BacktestResult(
            String symbol,
            double startPrice,
            double predictedPrice,
            double actualPrice) {


        this.symbol = symbol;

        this.startPrice = startPrice;

        this.predictedPrice = predictedPrice;

        this.actualPrice = actualPrice;



        this.errorPercent =
                ((actualPrice - predictedPrice)
                / actualPrice) * 100;



        if(predictedPrice > actualPrice) {

            result = "OVERPREDICTED";

        }

        else {

            result = "UNDERPREDICTED";

        }

    }



    public void printReport() {


        System.out.println(
                "========== BACKTEST =========="
        );


        System.out.println(
                "Stock: "
                + symbol
        );


        System.out.println(
                "Starting Price: $"
                + startPrice
        );


        System.out.println(
                "Predicted Price: $"
                + predictedPrice
        );


        System.out.println(
                "Actual Price: $"
                + actualPrice
        );


        System.out.println(
                "Error: "
                + String.format("%.2f", errorPercent)
                + "%"
        );


        System.out.println(
                "Result: "
                + result
        );

    }

}