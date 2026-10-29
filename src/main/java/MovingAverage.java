import java.util.LinkedList;

public class MovingAverage {


    public static double calculate(
            LinkedList<StockPrice> prices,
            int days) {


        int start =
                Math.max(
                    0,
                    prices.size() - days
                );


        double total = 0;


        int count = 0;


        for(int i = start; i < prices.size(); i++) {


            total += prices.get(i).getClose();

            count++;

        }


        return total / count;

    }

}