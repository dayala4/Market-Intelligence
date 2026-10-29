
import java.util.LinkedList;
import java.util.List;

public class MarketDataService {

    private final YahooFinanceClient yahoo;
    private final CompanyDataClient fmp;
    private final SECDataClient sec;

    public MarketDataService() {
        yahoo = new YahooFinanceClient();
        fmp = new CompanyDataClient();
        sec = new SECDataClient();
    }

    public LinkedList<StockPrice> getHistoricalPrices(String symbol) {
        String json = yahoo.getStockData(symbol);
        return YahooFinanceParser.parse(json);
    }

    public CompanyData getCompanyData(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return null;
        }

        String normalizedSymbol = symbol.trim().toUpperCase();

        // SEC is the primary source for company financials.
        CompanyData secData =
                sec.getCompanyDataBySymbol(normalizedSymbol);

        // FMP supplies metrics or profile information SEC does not provide.
        CompanyData fmpData =
                fmp.getCompanyData(normalizedSymbol);

        if (secData == null) {
            System.out.println(
                    "SEC data unavailable for " + normalizedSymbol
                            + ". Using FMP data."
            );
            return fmpData;
        }

        if (fmpData != null) {
            fillMissingData(fmpData, secData);
        }

        System.out.println(
                "Company sector for " + normalizedSymbol + ": "
                        + secData.getSector()
        );

        return secData;
    }

    private void fillMissingData(
            CompanyData source,
            CompanyData target) {

        // VALUATION
        if (target.getPeRatio() == 0) {
            copyIfNonZero(source.getPeRatio(), target::setPeRatio);
        }
        if (target.getForwardPe() == 0) {
            copyIfNonZero(source.getForwardPe(), target::setForwardPe);
        }
        if (target.getPegRatio() == 0) {
            copyIfNonZero(source.getPegRatio(), target::setPegRatio);
        }
        if (target.getPriceToSales() == 0) {
            copyIfNonZero(source.getPriceToSales(), target::setPriceToSales);
        }
        if (target.getPriceToBook() == 0) {
            copyIfNonZero(source.getPriceToBook(), target::setPriceToBook);
        }
        if (target.getEvToEbitda() == 0) {
            copyIfNonZero(source.getEvToEbitda(), target::setEvToEbitda);
        }
        if (target.getEnterpriseValue() == 0) {
            copyIfNonZero(source.getEnterpriseValue(), target::setEnterpriseValue);
        }
        if (target.getEvToRevenue() == 0) {
            copyIfNonZero(source.getEvToRevenue(), target::setEvToRevenue);
        }
        if (target.getEps() == 0) {
            copyIfNonZero(source.getEps(), target::setEps);
        }
        if (target.getForwardEps() == 0) {
            copyIfNonZero(source.getForwardEps(), target::setForwardEps);
        }

        // GROWTH
        if (target.getEarningsGrowth() == 0) {
            copyIfNonZero(source.getEarningsGrowth(), target::setEarningsGrowth);
        }
        if (target.getRevenueGrowth() == 0) {
            copyIfNonZero(source.getRevenueGrowth(), target::setRevenueGrowth);
        }
        if (target.getEpsGrowth() == 0) {
            copyIfNonZero(source.getEpsGrowth(), target::setEpsGrowth);
        }
        if (target.getGrossProfitGrowth() == 0) {
            copyIfNonZero(source.getGrossProfitGrowth(), target::setGrossProfitGrowth);
        }
        if (target.getOperatingIncomeGrowth() == 0) {
            copyIfNonZero(source.getOperatingIncomeGrowth(), target::setOperatingIncomeGrowth);
        }
        if (target.getFreeCashFlowGrowth() == 0) {
            copyIfNonZero(source.getFreeCashFlowGrowth(), target::setFreeCashFlowGrowth);
        }
        if (target.getQuarterlyRevenueGrowth() == 0) {
            copyIfNonZero(source.getQuarterlyRevenueGrowth(), target::setQuarterlyRevenueGrowth);
        }
        if (target.getQuarterlyEarningsGrowth() == 0) {
            copyIfNonZero(source.getQuarterlyEarningsGrowth(), target::setQuarterlyEarningsGrowth);
        }

        // FINANCIAL HEALTH
        if (target.getProfitMargin() == 0) {
            copyIfNonZero(source.getProfitMargin(), target::setProfitMargin);
        }
        if (target.getGrossMargin() == 0) {
            copyIfNonZero(source.getGrossMargin(), target::setGrossMargin);
        }
        if (target.getOperatingMargin() == 0) {
            copyIfNonZero(source.getOperatingMargin(), target::setOperatingMargin);
        }
        if (target.getDebtToEquity() == 0) {
            copyIfNonZero(source.getDebtToEquity(), target::setDebtToEquity);
        }
        if (target.getReturnOnEquity() == 0) {
            copyIfNonZero(source.getReturnOnEquity(), target::setReturnOnEquity);
        }
        if (target.getReturnOnAssets() == 0) {
            copyIfNonZero(source.getReturnOnAssets(), target::setReturnOnAssets);
        }
        if (target.getCurrentRatio() == 0) {
            copyIfNonZero(source.getCurrentRatio(), target::setCurrentRatio);
        }
        if (target.getQuickRatio() == 0) {
            copyIfNonZero(source.getQuickRatio(), target::setQuickRatio);
        }
        if (target.getTotalDebt() == 0) {
            copyIfNonZero(source.getTotalDebt(), target::setTotalDebt);
        }
        if (target.getTotalCash() == 0) {
            copyIfNonZero(source.getTotalCash(), target::setTotalCash);
        }
        if (target.getOperatingCashFlow() == 0) {
            copyIfNonZero(source.getOperatingCashFlow(), target::setOperatingCashFlow);
        }
        if (target.getFreeCashFlow() == 0) {
            copyIfNonZero(source.getFreeCashFlow(), target::setFreeCashFlow);
        }

        // DIVIDENDS
        if (target.getDividendYield() == 0) {
            copyIfNonZero(source.getDividendYield(), target::setDividendYield);
        }
        if (target.getDividendPerShare() == 0) {
            copyIfNonZero(source.getDividendPerShare(), target::setDividendPerShare);
        }
        if (target.getPayoutRatio() == 0) {
            copyIfNonZero(source.getPayoutRatio(), target::setPayoutRatio);
        }
        if (target.getDividendGrowth() == 0) {
            copyIfNonZero(source.getDividendGrowth(), target::setDividendGrowth);
        }

        if (!hasText(target.getExDividendDate())) {
            target.setExDividendDate(source.getExDividendDate());
        }
        if (!hasText(target.getDividendFrequency())) {
            target.setDividendFrequency(source.getDividendFrequency());
        }

        // STOCK STATISTICS
        if (target.getMarketCap() == 0) {
            copyIfNonZero(source.getMarketCap(), target::setMarketCap);
        }
        if (target.get52WeekHigh() == 0) {
            copyIfNonZero(source.get52WeekHigh(), target::set52WeekHigh);
        }
        if (target.get52WeekLow() == 0) {
            copyIfNonZero(source.get52WeekLow(), target::set52WeekLow);
        }
        if (target.getBeta() == 0) {
            copyIfNonZero(source.getBeta(), target::setBeta);
        }
        if (target.getAverageVolume() == 0) {
            copyIfNonZero(source.getAverageVolume(), target::setAverageVolume);
        }
        if (target.getSharesOutstanding() == 0) {
            copyIfNonZero(source.getSharesOutstanding(), target::setSharesOutstanding);
        }
        if (target.getFloatShares() == 0) {
            copyIfNonZero(source.getFloatShares(), target::setFloatShares);
        }
        if (target.getShortInterest() == 0) {
            copyIfNonZero(source.getShortInterest(), target::setShortInterest);
        }
        if (target.getShortRatio() == 0) {
            copyIfNonZero(source.getShortRatio(), target::setShortRatio);
        }

        // COMPANY PROFILE
        copyTextIfMissing(source.getCompanyName(), target.getCompanyName(),
                target::setCompanyName);
        copyTextIfMissing(source.getDescription(), target.getDescription(),
                target::setDescription);
        copyTextIfMissing(source.getSector(), target.getSector(),
                target::setSector);
        copyTextIfMissing(source.getIndustry(), target.getIndustry(),
                target::setIndustry);
        copyTextIfMissing(source.getCountry(), target.getCountry(),
                target::setCountry);
        copyTextIfMissing(source.getExchange(), target.getExchange(),
                target::setExchange);
        copyTextIfMissing(source.getCurrency(), target.getCurrency(),
                target::setCurrency);
        copyTextIfMissing(source.getWebsite(), target.getWebsite(),
                target::setWebsite);
        copyTextIfMissing(source.getCeo(), target.getCeo(),
                target::setCeo);
        copyTextIfMissing(source.getEmployees(), target.getEmployees(),
                target::setEmployees);
        copyTextIfMissing(source.getIpoDate(), target.getIpoDate(),
                target::setIpoDate);
        copyTextIfMissing(source.getHeadquarters(), target.getHeadquarters(),
                target::setHeadquarters);
    }

    private void copyIfNonZero(
            double value,
            java.util.function.DoubleConsumer setter) {

        if (value != 0 && Double.isFinite(value)) {
            setter.accept(value);
        }
    }

    private void copyTextIfMissing(
            String sourceValue,
            String targetValue,
            java.util.function.Consumer<String> setter) {

        if (!hasText(targetValue) && hasText(sourceValue)) {
            setter.accept(sourceValue);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public List<StockSearchResult> searchStocks(String query) {
        return fmp.searchStocks(query);
    }
}
