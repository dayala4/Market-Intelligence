public class GrowthScore {


    public static double calculate(
            CompanyData company) {


        double score = 0;


        score += company.getRevenueGrowth()
                * 0.5;


        score += company.getEarningsGrowth()
                * 0.5;


        return score;

    }

}