public class FinancialHealthScore {


    public static double calculate(
            CompanyData company) {
    
    
        double score = 50;
    
    
        if(company.getFreeCashFlow() > 0)
            score += 20;
    
    
        if(company.getDebtToEquity() < 1)
            score += 20;
    
    
        return score;
    
    }
    
    }