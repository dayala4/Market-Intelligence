public class RealEstateModel {
    
    public static double[] getWeights(CompanyData company) {

        double growthWeight = 0.15;
        double valuationWeight = 0.25;
        double healthWeight = 0.20;
        double technicalWeight = 0.10;
        double momentumWeight = 0.05;
        double marketWeight = 0.25;


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