public class CloudModel {

    public static double[] getWeights(CompanyData company) {

        return new double[] {

            0.45, // growth
            0.10, // valuation
            0.20, // health
            0.10, // technical
            0.05, // momentum
            0.10  // market

        };
    }
}