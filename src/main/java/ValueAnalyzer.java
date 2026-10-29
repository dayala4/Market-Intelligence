public class ValueAnalyzer {


    public String analyzeValue(
            double currentPrice,
            double movingAverage) {


        /*
         * Value Difference Formula:
         *
         * Difference =
         * (Current Price - Reference Price)
         * /
         * Reference Price
         *
         * Example:
         *
         * Current:
         * $90
         *
         * MA:
         * $100
         *
         * Difference:
         * -10%
         *
         */

        double difference =
                (currentPrice - movingAverage)
                / movingAverage * 100;



        if(difference <= -5) {

            return "Cheap (" 
                    + difference 
                    + "%)";

        }


        else if(difference >= 5) {

            return "Expensive ("
                    + difference
                    + "%)";

        }


        else {

            return "Fair Value ("
                    + difference
                    + "%)";

        }

    }

}