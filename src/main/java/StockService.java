import java.util.LinkedList;
import java.util.List;

public class StockService {


    // =====================================================
    // SEARCH STOCKS
    // =====================================================

    public List<StockSearchResult> searchStocks(
            String query) {

        MarketDataService marketData =
                new MarketDataService();

        return marketData.searchStocks(query);
    }


    // =====================================================
    // GET STOCK ANALYSIS
    // =====================================================

    public StockReport getStockAnalysis(
            String symbol) {

        MarketDataService marketData =
                new MarketDataService();

        LinkedList<StockPrice> prices =
                marketData.getHistoricalPrices(symbol);

        CompanyData company =
                marketData.getCompanyData(symbol);

        StockAnalyzer analyzer =
                new StockAnalyzer();

        return analyzer.analyze(
                symbol,
                prices,
                company
        );
    }


    // =====================================================
    // GET HISTORICAL PRICES
    // =====================================================

    public LinkedList<StockPrice> getHistoricalPrices(
            String symbol) {

        MarketDataService marketData =
                new MarketDataService();

        return marketData.getHistoricalPrices(symbol);
    }
}