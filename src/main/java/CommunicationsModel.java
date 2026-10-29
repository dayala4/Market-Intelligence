public class CommunicationsModel {

    public static double[] getWeights(CompanyData company) {

        return new double[] {
            0.30, // Growth
            0.20, // Valuation
            0.20, // Financial Health
            0.10, // Technical
            0.10, // Momentum
            0.10  // Market
        };
    }
}