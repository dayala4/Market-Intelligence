public class DividendData {

    private double dividendPerShare;
    private double dividendYield;

    public DividendData(
            double dividendPerShare,
            double dividendYield) {

        this.dividendPerShare = dividendPerShare;
        this.dividendYield = dividendYield;
    }

    public double getDividendPerShare() {
        return dividendPerShare;
    }

    public double getDividendYield() {
        return dividendYield;
    }
}