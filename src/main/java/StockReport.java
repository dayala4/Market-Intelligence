
public class StockReport {

    // =====================================================
    // STOCK DATA
    // =====================================================

    private double currentPrice;
    private String sector;
    private double ma20;
    private double ma50;
    private double ma200;
    private double rsi;

    // =====================================================
    // COMPANY DATA
    // =====================================================

    private double peRatio;
    private double earningsGrowth;
    private double dividendYield;
    private String companyName;
    private String companyDescription;

    // =====================================================
    // PREDICTION DATA
    // =====================================================

    private double predictedPrice;
    private String predictionDirection;
    private String trend;

    // =====================================================
    // CONSTRUCTOR — WITH SECTOR
    // =====================================================

    public StockReport(
            double currentPrice,
            String sector,
            double ma20,
            double ma50,
            double ma200,
            double rsi,
            double peRatio,
            double earningsGrowth,
            double dividendYield,
            String companyName,
            String companyDescription,
            double predictedPrice,
            String predictionDirection,
            String trend) {

        this.currentPrice = currentPrice;
        this.sector = sector;

        this.ma20 = ma20;
        this.ma50 = ma50;
        this.ma200 = ma200;
        this.rsi = rsi;

        this.peRatio = peRatio;
        this.earningsGrowth = earningsGrowth;
        this.dividendYield = dividendYield;

        this.companyName = companyName;
        this.companyDescription = companyDescription;

        this.predictedPrice = predictedPrice;
        this.predictionDirection = predictionDirection;
        this.trend = trend;
    }

    // =====================================================
    // ORIGINAL CONSTRUCTOR — BACKWARD COMPATIBILITY
    // =====================================================

    public StockReport(
            double currentPrice,
            double ma20,
            double ma50,
            double ma200,
            double rsi,
            double peRatio,
            double earningsGrowth,
            double dividendYield,
            String companyName,
            String companyDescription,
            double predictedPrice,
            String predictionDirection,
            String trend) {

        this(
                currentPrice,
                "Unknown",
                ma20,
                ma50,
                ma200,
                rsi,
                peRatio,
                earningsGrowth,
                dividendYield,
                companyName,
                companyDescription,
                predictedPrice,
                predictionDirection,
                trend
        );
    }

    // =====================================================
    // STOCK
    // =====================================================

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getSector() {
        return sector;
    }

    // =====================================================
    // MOVING AVERAGES
    // =====================================================

    public double getMa20() {
        return ma20;
    }

    public double getMa50() {
        return ma50;
    }

    public double getMa200() {
        return ma200;
    }

    // =====================================================
    // RSI
    // =====================================================

    public double getRsi() {
        return rsi;
    }

    // =====================================================
    // FUNDAMENTALS
    // =====================================================

    public double getPeRatio() {
        return peRatio;
    }

    public double getEarningsGrowth() {
        return earningsGrowth;
    }

    public double getDividendYield() {
        return dividendYield;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    // =====================================================
    // PREDICTION
    // =====================================================

    public double getPredictedPrice() {
        return predictedPrice;
    }

    public String getPredictionDirection() {
        return predictionDirection;
    }

    // =====================================================
    // TREND
    // =====================================================

    public String getTrend() {
        return trend;
    }
}
