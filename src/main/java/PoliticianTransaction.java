public class PoliticianTransaction {

    private final String name;
    private final String chamber;
    private final String state;
    private final String ticker;
    private final String company;
    private final String action;
    private final String transactionDate;
    private final String filingDate;
    private final String amountRange;

    public PoliticianTransaction(
            String name,
            String chamber,
            String state,
            String ticker,
            String company,
            String action,
            String transactionDate,
            String filingDate,
            String amountRange) {

        this.name = name;
        this.chamber = chamber;
        this.state = state;
        this.ticker = ticker;
        this.company = company;
        this.action = action;
        this.transactionDate = transactionDate;
        this.filingDate = filingDate;
        this.amountRange = amountRange;
    }

    public String getName() {
        return name;
    }

    public String getChamber() {
        return chamber;
    }

    public String getState() {
        return state;
    }

    public String getTicker() {
        return ticker;
    }

    public String getCompany() {
        return company;
    }

    public String getAction() {
        return action;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getFilingDate() {
        return filingDate;
    }

    public String getAmountRange() {
        return amountRange;
    }
}