public class ValuationScore {


    public static double calculate(
            CompanyData company) {
    
    
        double pe =
                company.getPeRatio();
    
    
        if(pe < 20)
            return 80;
    
    
        else if(pe < 35)
            return 60;
    
    
        else
            return 40;
    
    
    }
    
    }