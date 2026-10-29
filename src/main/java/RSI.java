import java.util.LinkedList;

public class RSI {

    public static double calculate(
            LinkedList<StockPrice> prices,
            int period) {

        if (prices.size() <= period) {
            return 50;
        }

        double gains = 0;
        double losses = 0;

        int start = prices.size() - period;

        for (int i = start; i < prices.size(); i++) {

            double change =
                    prices.get(i).getClose()
                    - prices.get(i - 1).getClose();

            if (change > 0) {
                gains += change;
            } else {
                losses += Math.abs(change);
            }
        }

        double averageGain = gains / period;
        double averageLoss = losses / period;

        if (averageLoss == 0) {
            return 100;
        }

        double rs = averageGain / averageLoss;

        return 100 - (100 / (1 + rs));
    }
}