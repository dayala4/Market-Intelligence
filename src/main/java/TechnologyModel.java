public class TechnologyModel {

    public static double[] getWeights(CompanyData company) {

        double growthWeight = 0.30;
        double valuationWeight = 0.16;
        double healthWeight = 0.21;
        double technicalWeight = 0.11;
        double momentumWeight = 0.11;
        double marketWeight = 0.11;

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