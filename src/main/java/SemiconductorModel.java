public class SemiconductorModel {

    public static double[] getWeights(CompanyData company) {

        return new double[] {

            0.30, // growth
            0.15, // valuation
            0.15, // health
            0.15, // technical
            0.15, // momentum
            0.10  // market

        };
    }
}