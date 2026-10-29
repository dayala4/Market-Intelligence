
public enum PredictionPeriod {

    ONE_MONTH("1 Month", 21),
    THREE_MONTHS("3 Months", 63),
    ONE_YEAR("1 Year", 252),
    THREE_YEARS("3 Years", 756);

    private final String label;
    private final int tradingDays;

    PredictionPeriod(String label, int tradingDays) {
        this.label = label;
        this.tradingDays = tradingDays;
    }

    public String getLabel() {
        return label;
    }

    public int getTradingDays() {
        return tradingDays;
    }

    @Override
    public String toString() {
        return label;
    }
}
