public class InsiderTransaction {

    private final String name;
    private final String title;
    private final String company;
    private final String ticker;
    private final String action;
    private final String shares;
    private final String price;
    private final String transactionDate;

    public InsiderTransaction(
            String name,
            String title,
            String company,
            String ticker,
            String action,
            String shares,
            String price,
            String transactionDate) {

        this.name = name;
        this.title = title;
        this.company = company;
        this.ticker = ticker;
        this.action = action;
        this.shares = shares;
        this.price = price;
        this.transactionDate = transactionDate;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getTicker() {
        return ticker;
    }

    public String getAction() {
        return action;
    }

    public String getShares() {
        return shares;
    }

    public String getPrice() {
        return price;
    }

    public String getTransactionDate() {
        return transactionDate;
    }
}