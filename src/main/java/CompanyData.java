public class CompanyData {

    // =====================================================
    // VALUATION
    // =====================================================

    private double peRatio;
    private double forwardPe;
    private double pegRatio;
    private double priceToSales;
    private double priceToBook;
    private double evToEbitda;
    private double enterpriseValue;
    private double evToRevenue;
    private double eps;
    private double forwardEps;

    // =====================================================
    // GROWTH
    // =====================================================

    private double earningsGrowth;
    private double revenueGrowth;
    private double epsGrowth;
    private double grossProfitGrowth;
    private double operatingIncomeGrowth;
    private double freeCashFlowGrowth;
    private double quarterlyRevenueGrowth;
    private double quarterlyEarningsGrowth;

    // =====================================================
    // FINANCIAL HEALTH
    // =====================================================

    private double profitMargin;
    private double grossMargin;
    private double operatingMargin;
    private double debtToEquity;
    private double returnOnEquity;
    private double returnOnAssets;
    private double currentRatio;
    private double quickRatio;
    private double totalDebt;
    private double totalCash;
    private double operatingCashFlow;
    private double freeCashFlow;

    // =====================================================
    // DIVIDEND
    // =====================================================

    private double dividendYield;
    private double dividendPerShare;
    private double payoutRatio;
    private double dividendGrowth;

    private String exDividendDate;
    private String dividendFrequency;

    // =====================================================
    // STOCK STATISTICS
    // =====================================================

    private double marketCap;
    private double week52High;
    private double week52Low;
    private double beta;
    private double averageVolume;
    private double sharesOutstanding;
    private double floatShares;
    private double shortInterest;
    private double shortRatio;

    // =====================================================
    // COMPANY INFORMATION
    // =====================================================

    private String companyName;
    private String description;
    private String sector;
    private String industry;
    private String country;
    private String exchange;
    private String currency;
    private String website;
    private String ceo;
    private String employees;
    private String ipoDate;
    private String headquarters;


    // =====================================================
    // EMPTY CONSTRUCTOR
    // =====================================================

    public CompanyData() {
    }


    // =====================================================
    // FULL CONSTRUCTOR
    // =====================================================

    public CompanyData(
            double peRatio,
            double forwardPe,
            double pegRatio,
            double priceToSales,
            double priceToBook,
            double evToEbitda,
            double enterpriseValue,
            double evToRevenue,
            double eps,
            double forwardEps,

            double earningsGrowth,
            double revenueGrowth,
            double epsGrowth,
            double grossProfitGrowth,
            double operatingIncomeGrowth,
            double freeCashFlowGrowth,
            double quarterlyRevenueGrowth,
            double quarterlyEarningsGrowth,

            double profitMargin,
            double grossMargin,
            double operatingMargin,
            double debtToEquity,
            double returnOnEquity,
            double returnOnAssets,
            double currentRatio,
            double quickRatio,
            double totalDebt,
            double totalCash,
            double operatingCashFlow,
            double freeCashFlow,

            double dividendYield,
            double dividendPerShare,
            double payoutRatio,
            double dividendGrowth,
            String exDividendDate,
            String dividendFrequency,

            double marketCap,
            double week52High,
            double week52Low,
            double beta,
            double averageVolume,
            double sharesOutstanding,
            double floatShares,
            double shortInterest,
            double shortRatio,

            String companyName,
            String description,
            String sector,
            String industry,
            String country,
            String exchange,
            String currency,
            String website,
            String ceo,
            String employees,
            String ipoDate,
            String headquarters) {

        this.peRatio = peRatio;
        this.forwardPe = forwardPe;
        this.pegRatio = pegRatio;
        this.priceToSales = priceToSales;
        this.priceToBook = priceToBook;
        this.evToEbitda = evToEbitda;
        this.enterpriseValue = enterpriseValue;
        this.evToRevenue = evToRevenue;
        this.eps = eps;
        this.forwardEps = forwardEps;

        this.earningsGrowth = earningsGrowth;
        this.revenueGrowth = revenueGrowth;
        this.epsGrowth = epsGrowth;
        this.grossProfitGrowth = grossProfitGrowth;
        this.operatingIncomeGrowth = operatingIncomeGrowth;
        this.freeCashFlowGrowth = freeCashFlowGrowth;
        this.quarterlyRevenueGrowth = quarterlyRevenueGrowth;
        this.quarterlyEarningsGrowth = quarterlyEarningsGrowth;

        this.profitMargin = profitMargin;
        this.grossMargin = grossMargin;
        this.operatingMargin = operatingMargin;
        this.debtToEquity = debtToEquity;
        this.returnOnEquity = returnOnEquity;
        this.returnOnAssets = returnOnAssets;
        this.currentRatio = currentRatio;
        this.quickRatio = quickRatio;
        this.totalDebt = totalDebt;
        this.totalCash = totalCash;
        this.operatingCashFlow = operatingCashFlow;
        this.freeCashFlow = freeCashFlow;

        this.dividendYield = dividendYield;
        this.dividendPerShare = dividendPerShare;
        this.payoutRatio = payoutRatio;
        this.dividendGrowth = dividendGrowth;
        this.exDividendDate = exDividendDate;
        this.dividendFrequency = dividendFrequency;

        this.marketCap = marketCap;
        this.week52High = week52High;
        this.week52Low = week52Low;
        this.beta = beta;
        this.averageVolume = averageVolume;
        this.sharesOutstanding = sharesOutstanding;
        this.floatShares = floatShares;
        this.shortInterest = shortInterest;
        this.shortRatio = shortRatio;

        this.companyName = companyName;
        this.description = description;
        this.sector = sector;
        this.industry = industry;
        this.country = country;
        this.exchange = exchange;
        this.currency = currency;
        this.website = website;
        this.ceo = ceo;
        this.employees = employees;
        this.ipoDate = ipoDate;
        this.headquarters = headquarters;
    }


    // =====================================================
    // GETTERS - VALUATION
    // =====================================================

    public double getPeRatio() {
        return peRatio;
    }

    public double getForwardPe() {
        return forwardPe;
    }

    public double getPegRatio() {
        return pegRatio;
    }

    public double getPriceToSales() {
        return priceToSales;
    }

    public double getPriceToBook() {
        return priceToBook;
    }

    public double getEvToEbitda() {
        return evToEbitda;
    }

    public double getEnterpriseValue() {
        return enterpriseValue;
    }

    public double getEvToRevenue() {
        return evToRevenue;
    }

    public double getEps() {
        return eps;
    }

    public double getForwardEps() {
        return forwardEps;
    }


    // =====================================================
    // SETTERS - VALUATION
    // =====================================================

    public void setPeRatio(double value) {
        this.peRatio = value;
    }

    public void setForwardPe(double value) {
        this.forwardPe = value;
    }

    public void setPegRatio(double value) {
        this.pegRatio = value;
    }

    public void setPriceToSales(double value) {
        this.priceToSales = value;
    }

    public void setPriceToBook(double value) {
        this.priceToBook = value;
    }

    public void setEvToEbitda(double value) {
        this.evToEbitda = value;
    }

    public void setEnterpriseValue(double value) {
        this.enterpriseValue = value;
    }

    public void setEvToRevenue(double value) {
        this.evToRevenue = value;
    }

    public void setEps(double value) {
        this.eps = value;
    }

    public void setForwardEps(double value) {
        this.forwardEps = value;
    }


    // =====================================================
    // GETTERS - GROWTH
    // =====================================================

    public double getEarningsGrowth() {
        return earningsGrowth;
    }

    public double getRevenueGrowth() {
        return revenueGrowth;
    }

    public double getEpsGrowth() {
        return epsGrowth;
    }

    public double getGrossProfitGrowth() {
        return grossProfitGrowth;
    }

    public double getOperatingIncomeGrowth() {
        return operatingIncomeGrowth;
    }

    public double getFreeCashFlowGrowth() {
        return freeCashFlowGrowth;
    }

    public double getQuarterlyRevenueGrowth() {
        return quarterlyRevenueGrowth;
    }

    public double getQuarterlyEarningsGrowth() {
        return quarterlyEarningsGrowth;
    }


    // =====================================================
    // SETTERS - GROWTH
    // =====================================================

    public void setEarningsGrowth(double value) {
        this.earningsGrowth = value;
    }

    public void setRevenueGrowth(double value) {
        this.revenueGrowth = value;
    }

    public void setEpsGrowth(double value) {
        this.epsGrowth = value;
    }

    public void setGrossProfitGrowth(double value) {
        this.grossProfitGrowth = value;
    }

    public void setOperatingIncomeGrowth(double value) {
        this.operatingIncomeGrowth = value;
    }

    public void setFreeCashFlowGrowth(double value) {
        this.freeCashFlowGrowth = value;
    }

    public void setQuarterlyRevenueGrowth(double value) {
        this.quarterlyRevenueGrowth = value;
    }

    public void setQuarterlyEarningsGrowth(double value) {
        this.quarterlyEarningsGrowth = value;
    }


    // =====================================================
    // GETTERS - FINANCIAL HEALTH
    // =====================================================

    public double getProfitMargin() {
        return profitMargin;
    }

    public double getGrossMargin() {
        return grossMargin;
    }

    public double getOperatingMargin() {
        return operatingMargin;
    }

    public double getDebtToEquity() {
        return debtToEquity;
    }

    public double getReturnOnEquity() {
        return returnOnEquity;
    }

    public double getReturnOnAssets() {
        return returnOnAssets;
    }

    public double getCurrentRatio() {
        return currentRatio;
    }

    public double getQuickRatio() {
        return quickRatio;
    }

    public double getTotalDebt() {
        return totalDebt;
    }

    public double getTotalCash() {
        return totalCash;
    }

    public double getOperatingCashFlow() {
        return operatingCashFlow;
    }

    public double getFreeCashFlow() {
        return freeCashFlow;
    }


    // =====================================================
    // SETTERS - FINANCIAL HEALTH
    // =====================================================

    public void setProfitMargin(double value) {
        this.profitMargin = value;
    }

    public void setGrossMargin(double value) {
        this.grossMargin = value;
    }

    public void setOperatingMargin(double value) {
        this.operatingMargin = value;
    }

    public void setDebtToEquity(double value) {
        this.debtToEquity = value;
    }

    public void setReturnOnEquity(double value) {
        this.returnOnEquity = value;
    }

    public void setReturnOnAssets(double value) {
        this.returnOnAssets = value;
    }

    public void setCurrentRatio(double value) {
        this.currentRatio = value;
    }

    public void setQuickRatio(double value) {
        this.quickRatio = value;
    }

    public void setTotalDebt(double value) {
        this.totalDebt = value;
    }

    public void setTotalCash(double value) {
        this.totalCash = value;
    }

    public void setOperatingCashFlow(double value) {
        this.operatingCashFlow = value;
    }

    public void setFreeCashFlow(double value) {
        this.freeCashFlow = value;
    }


    // =====================================================
    // GETTERS - DIVIDEND
    // =====================================================

    public double getDividendYield() {
        return dividendYield;
    }

    public double getDividendPerShare() {
        return dividendPerShare;
    }

    public double getPayoutRatio() {
        return payoutRatio;
    }

    public double getDividendGrowth() {
        return dividendGrowth;
    }

    public String getExDividendDate() {
        return exDividendDate;
    }

    public String getDividendFrequency() {
        return dividendFrequency;
    }


    // =====================================================
    // SETTERS - DIVIDEND
    // =====================================================

    public void setDividendYield(double value) {
        this.dividendYield = value;
    }

    public void setDividendPerShare(double value) {
        this.dividendPerShare = value;
    }

    public void setPayoutRatio(double value) {
        this.payoutRatio = value;
    }

    public void setDividendGrowth(double value) {
        this.dividendGrowth = value;
    }

    public void setExDividendDate(String value) {
        this.exDividendDate = value;
    }

    public void setDividendFrequency(String value) {
        this.dividendFrequency = value;
    }


    // =====================================================
    // GETTERS - STOCK STATISTICS
    // =====================================================

    public double getMarketCap() {
        return marketCap;
    }

    public double get52WeekHigh() {
        return week52High;
    }

    public double get52WeekLow() {
        return week52Low;
    }

    public double getBeta() {
        return beta;
    }

    public double getAverageVolume() {
        return averageVolume;
    }

    public double getSharesOutstanding() {
        return sharesOutstanding;
    }

    public double getFloatShares() {
        return floatShares;
    }

    public double getShortInterest() {
        return shortInterest;
    }

    public double getShortRatio() {
        return shortRatio;
    }


    // =====================================================
    // SETTERS - STOCK STATISTICS
    // =====================================================

    public void setMarketCap(double value) {
        this.marketCap = value;
    }

    public void set52WeekHigh(double value) {
        this.week52High = value;
    }

    public void set52WeekLow(double value) {
        this.week52Low = value;
    }

    public void setBeta(double value) {
        this.beta = value;
    }

    public void setAverageVolume(double value) {
        this.averageVolume = value;
    }

    public void setSharesOutstanding(double value) {
        this.sharesOutstanding = value;
    }

    public void setFloatShares(double value) {
        this.floatShares = value;
    }

    public void setShortInterest(double value) {
        this.shortInterest = value;
    }

    public void setShortRatio(double value) {
        this.shortRatio = value;
    }


    // =====================================================
    // GETTERS - COMPANY INFORMATION
    // =====================================================

    public String getCompanyName() {
        return companyName;
    }

    public String getDescription() {
        return description;
    }

    public String getSector() {
        return sector;
    }

    public String getIndustry() {
        return industry;
    }

    public String getCountry() {
        return country;
    }

    public String getExchange() {
        return exchange;
    }

    public String getCurrency() {
        return currency;
    }

    public String getWebsite() {
        return website;
    }

    public String getCeo() {
        return ceo;
    }

    public String getEmployees() {
        return employees;
    }

    public String getIpoDate() {
        return ipoDate;
    }

    public String getHeadquarters() {
        return headquarters;
    }


    // =====================================================
    // SETTERS - COMPANY INFORMATION
    // =====================================================

    public void setCompanyName(String value) {
        this.companyName = value;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public void setSector(String value) {
        this.sector = value;
    }

    public void setIndustry(String value) {
        this.industry = value;
    }

    public void setCountry(String value) {
        this.country = value;
    }

    public void setExchange(String value) {
        this.exchange = value;
    }

    public void setCurrency(String value) {
        this.currency = value;
    }

    public void setWebsite(String value) {
        this.website = value;
    }

    public void setCeo(String value) {
        this.ceo = value;
    }

    public void setEmployees(String value) {
        this.employees = value;
    }

    public void setIpoDate(String value) {
        this.ipoDate = value;
    }

    public void setHeadquarters(String value) {
        this.headquarters = value;
    }
}