public class IndustrialsModel {
    
    public static double[] getWeights(CompanyData company) {

        double growthWeight = 0.25;
        double valuationWeight = 0.20;
        double healthWeight = 0.25;
        double technicalWeight = 0.10;
        double momentumWeight = 0.10;
        double marketWeight = 0.10;


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
