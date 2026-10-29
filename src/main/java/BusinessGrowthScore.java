public class BusinessGrowthScore {


    private double revenueGrowth;
    private double marginGrowth;
    private double riskAdjustment;


    public BusinessGrowthScore(
            double revenueGrowth,
            double marginGrowth,
            double riskAdjustment) {

        this.revenueGrowth = revenueGrowth;
        this.marginGrowth = marginGrowth;
        this.riskAdjustment = riskAdjustment;
    }


    public double getBusinessScore() {


        /*
         * Business Growth Formula:
         *
         * Revenue growth
         * +
         * Margin improvement
         * -
         * Risk factors
         *
         */
        return revenueGrowth
                + marginGrowth
                - riskAdjustment;

    }

}