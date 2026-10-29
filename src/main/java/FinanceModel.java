public class FinanceModel {
    
    public static double[] getWeights(CompanyData company) {

        double growthWeight = 0.20;
        double valuationWeight = 0.25;
        double healthWeight = 0.25;
        double technicalWeight = 0.10;
        double momentumWeight = 0.05;
        double marketWeight = 0.15;


        return new double[] {

                growthWeight,

                valuationWeight,

                healthWeight,

                technicalWeight,

                momentumWeight,

                marketWeight

        };

    }

}