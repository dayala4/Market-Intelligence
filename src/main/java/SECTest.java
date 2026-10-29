public class SECTest {
    public static void main(String[] args) {
        SECDataClient sec = new SECDataClient();

        testTicker(sec, "AAPL");
        System.out.println();
        testTicker(sec, "NVDA");
    }

    private static void testTicker(SECDataClient sec, String symbol) {
        System.out.println("Testing SEC lookup for " + symbol + "...");

        String cik = sec.getCikByTicker(symbol);
        if (cik == null) {
            System.out.println("Failed to find a CIK for " + symbol + ".");
            return;
        }

        System.out.println("Ticker: " + symbol);
        System.out.println("CIK: " + cik);

        CompanyData data = sec.getCompanyDataBySymbol(symbol);
        if (data == null) {
            System.out.println("Failed to retrieve company data for " + symbol + ".");
            return;
        }

        System.out.println("Company: " + data.getCompanyName());
        System.out.println("EPS: " + data.getEps());
        System.out.println("Profit Margin: " + (data.getProfitMargin() * 100) + "%");
        System.out.println("Debt to Equity: " + data.getDebtToEquity());
        System.out.println("Return on Assets: " + data.getReturnOnAssets());
        System.out.println("Return on Equity: " + data.getReturnOnEquity());
        System.out.println("Total Cash: " + data.getTotalCash());
        System.out.println("Operating Cash Flow: " + data.getOperatingCashFlow());
        System.out.println("Free Cash Flow: " + data.getFreeCashFlow());
    }
}
