public class StockSearchResult {

    private String symbol;
    private String name;
    private String exchange;

    public StockSearchResult(
            String symbol,
            String name,
            String exchange) {

        this.symbol = symbol;
        this.name = name;
        this.exchange = exchange;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getExchange() {
        return exchange;
    }

    @Override
    public String toString() {
        return symbol + " — " + name;
    }
}