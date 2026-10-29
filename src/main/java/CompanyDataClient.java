import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.util.Properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CompanyDataClient {

    // =====================================================
    // API KEYS
    // =====================================================

    private final List<String> apiKeys =
            new ArrayList<>();

    private int currentKeyIndex = 0;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public CompanyDataClient() {

        try {

            Properties properties =
                    new Properties();

            InputStream input =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream(
                                    "api.properties"
                            );

            System.out.println(
                    "API PROPERTIES LOCATION: "
                            + getClass()
                                    .getClassLoader()
                                    .getResource(
                                            "api.properties"
                                    )
            );

            if (input == null) {

                System.out.println(
                        "api.properties not found!"
                );

                return;
            }

            properties.load(input);
            input.close();

            // =================================================
            // LOAD ALL FMP KEYS
            // =================================================

            for (int i = 1; i <= 5; i++) {

                String key =
                        properties.getProperty(
                                "FMP_API_KEY_" + i
                        );

                if (key != null &&
                        !key.trim().isEmpty()) {

                    apiKeys.add(
                            key.trim()
                    );

                    System.out.println(
                            "FMP API KEY "
                                    + i
                                    + " loaded."
                    );
                }
            }

            System.out.println(
                    "================================"
            );

            System.out.println(
                    "FMP API KEYS LOADED: "
                            + apiKeys.size()
            );

            System.out.println(
                    "================================"
            );

            if (apiKeys.isEmpty()) {

                System.out.println(
                        "WARNING: No FMP API keys loaded!"
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Could not load FMP API keys!"
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // SEARCH STOCKS
    // =====================================================

    public List<StockSearchResult> searchStocks(
            String query) {

        List<StockSearchResult> results =
                new ArrayList<>();

        if (query == null ||
                query.trim().isEmpty()) {

            return results;
        }

        if (apiKeys.isEmpty()) {

            System.out.println(
                    "No FMP API keys available."
            );

            return results;
        }

        try {

            HttpClient client =
                    HttpClient.newHttpClient();

            String encodedQuery =
                    java.net.URLEncoder.encode(
                            query.trim(),
                            java.nio.charset.StandardCharsets.UTF_8
                    );

            String searchURL =
                    "https://financialmodelingprep.com/stable/search-symbol"
                    + "?query="
                    + encodedQuery;

            String responseBody =
                    sendRequest(
                            client,
                            searchURL
                    );

            if (responseBody == null) {

                System.out.println(
                        "Stock search failed with all FMP keys."
                );

                return results;
            }

            System.out.println(
                    "SEARCH API RESPONSE:"
            );

            System.out.println(
                    responseBody
            );

            if (!responseBody
                    .trim()
                    .startsWith("[")) {

                return results;
            }

            JsonArray array =
                    JsonParser.parseString(
                            responseBody
                    ).getAsJsonArray();

            for (int i = 0;
                    i < array.size();
                    i++) {

                JsonObject stock =
                        array.get(i)
                                .getAsJsonObject();

                String symbol =
                        getString(
                                stock,
                                "symbol"
                        );

                String name =
                        getString(
                                stock,
                                "name"
                        );

                String exchange =
                        getString(
                                stock,
                                "exchange"
                        );

                if (!symbol.isEmpty() &&
                        !name.isEmpty()) {

                    results.add(
                            new StockSearchResult(
                                    symbol,
                                    name,
                                    exchange
                            )
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Stock search failed!"
            );

            e.printStackTrace();
        }

        return results;
    }

    // =====================================================
    // GET COMPANY DATA
    //
    // IMPORTANT:
    //
    // The CACHE WARMER checks the SQL cache BEFORE calling
    // this method.
    //
    // Therefore this method does NOT check the cache again.
    //
    // This prevents:
    //
    // StockCacheWarmer
    //      ↓
    // cache check
    //      ↓
    // CompanyDataClient
    //      ↓
    // cache check AGAIN
    //
    // =====================================================

    public CompanyData getCompanyData(
            String symbol) {

        if (symbol == null ||
                symbol.trim().isEmpty()) {

            return null;
        }

        symbol =
                symbol.trim()
                        .toUpperCase();

        // =================================================
        // NOT CACHED
        //
        // The caller has already verified this.
        // =================================================

        System.out.println(
                "Getting available company data from FMP..."
        );

        if (apiKeys.isEmpty()) {

            System.out.println(
                    "Cannot get FMP data because "
                            + "no API keys are available."
            );

            return null;
        }

        try {

            HttpClient client =
                    HttpClient.newHttpClient();

            // =================================================
            // DEFAULT VALUES
            // =================================================

            double peRatio = 0.0;
            double pegRatio = 0.0;
            double priceToSales = 0.0;
            double priceToBook = 0.0;
            double evToEbitda = 0.0;
            double evToRevenue = 0.0;
            double eps = 0.0;

            double profitMargin = 0.0;
            double grossMargin = 0.0;
            double operatingMargin = 0.0;
            double debtToEquity = 0.0;
            double currentRatio = 0.0;
            double returnOnEquity = 0.0;
            double returnOnAssets = 0.0;

            double dividendYield = 0.0;
            double dividendPerShare = 0.0;
            double payoutRatio = 0.0;

            double earningsGrowth = 0.0;
            double revenueGrowth = 0.0;
            double epsGrowth = 0.0;
            double grossProfitGrowth = 0.0;
            double operatingIncomeGrowth = 0.0;

            double freeCashFlow = 0.0;

            String companyName = "";
            String description = "";
            String sector = "";
            String industry = "";
            String country = "";
            String exchange = "";
            String currency = "";
            String website = "";
            String ceo = "";
            String ipoDate = "";

            double week52High = 0.0;
            double week52Low = 0.0;
            double beta = 0.0;
            double averageVolume = 0.0;
            double sharesOutstanding = 0.0;
            double marketCap = 0.0;

            boolean receivedData = false;

            // =================================================
            // RATIOS TTM
            // =================================================

            System.out.println(
                    "REQUESTING FMP RATIOS: "
                            + symbol
            );

            String ratiosURL =
                    "https://financialmodelingprep.com/stable/ratios-ttm"
                    + "?symbol="
                    + symbol;

            String ratiosJSON =
                    sendRequest(
                            client,
                            ratiosURL
                    );

            if (ratiosJSON != null &&
                    ratiosJSON
                            .trim()
                            .startsWith("[")) {

                try {

                    JsonArray ratiosArray =
                            JsonParser.parseString(
                                    ratiosJSON
                            ).getAsJsonArray();

                    if (ratiosArray.size() > 0) {

                        JsonObject ratios =
                                ratiosArray
                                        .get(0)
                                        .getAsJsonObject();

                        peRatio =
                                getDouble(
                                        ratios,
                                        "priceToEarningsRatioTTM"
                                );

                        pegRatio =
                                getDouble(
                                        ratios,
                                        "priceToEarningsGrowthRatioTTM"
                                );

                        priceToSales =
                                getDouble(
                                        ratios,
                                        "priceToSalesRatioTTM"
                                );

                        priceToBook =
                                getDouble(
                                        ratios,
                                        "priceToBookRatioTTM"
                                );

                        evToEbitda =
                                getDouble(
                                        ratios,
                                        "enterpriseValueMultipleTTM"
                                );

                        evToRevenue =
                                getDouble(
                                        ratios,
                                        "enterpriseValueOverRevenueTTM"
                                );

                        eps =
                                getDouble(
                                        ratios,
                                        "netIncomePerShareTTM"
                                );

                        profitMargin =
                                getDouble(
                                        ratios,
                                        "netProfitMarginTTM"
                                ) * 100;

                        grossMargin =
                                getDouble(
                                        ratios,
                                        "grossProfitMarginTTM"
                                ) * 100;

                        operatingMargin =
                                getDouble(
                                        ratios,
                                        "operatingProfitMarginTTM"
                                ) * 100;

                        debtToEquity =
                                getDouble(
                                        ratios,
                                        "debtToEquityRatioTTM"
                                );

                        currentRatio =
                                getDouble(
                                        ratios,
                                        "currentRatioTTM"
                                );

                        returnOnEquity =
                                getDouble(
                                        ratios,
                                        "returnOnEquityTTM"
                                ) * 100;

                        returnOnAssets =
                                getDouble(
                                        ratios,
                                        "returnOnAssetsTTM"
                                ) * 100;

                        dividendYield =
                                getDouble(
                                        ratios,
                                        "dividendYieldTTM"
                                );

                        dividendPerShare =
                                getDouble(
                                        ratios,
                                        "dividendPerShareTTM"
                                );

                        payoutRatio =
                                getDouble(
                                        ratios,
                                        "payoutRatioTTM"
                                );

                        freeCashFlow =
                                getDouble(
                                        ratios,
                                        "freeCashFlowPerShareTTM"
                                );

                        receivedData = true;

                        System.out.println(
                                "FMP RATIOS RECEIVED FOR "
                                        + symbol
                        );
                    }

                } catch (Exception e) {

                    System.out.println(
                            "Could not parse FMP ratios for "
                                    + symbol
                    );
                }

            } else {

                System.out.println(
                        "FMP ratios unavailable for "
                                + symbol
                                + " - continuing."
                );
            }

            // =================================================
            // INCOME STATEMENT
            // =================================================

            System.out.println(
                    "REQUESTING FMP INCOME STATEMENT: "
                            + symbol
            );

            String incomeURL =
                    "https://financialmodelingprep.com/stable/income-statement"
                    + "?symbol="
                    + symbol
                    + "&limit=2";

            String incomeJSON =
                    sendRequest(
                            client,
                            incomeURL
                    );

            if (incomeJSON != null &&
                    incomeJSON
                            .trim()
                            .startsWith("[")) {

                try {

                    JsonArray incomeArray =
                            JsonParser.parseString(
                                    incomeJSON
                            ).getAsJsonArray();

                    if (incomeArray.size() >= 2) {

                        JsonObject currentYear =
                                incomeArray
                                        .get(0)
                                        .getAsJsonObject();

                        JsonObject previousYear =
                                incomeArray
                                        .get(1)
                                        .getAsJsonObject();

                        double currentRevenue =
                                getDouble(
                                        currentYear,
                                        "revenue"
                                );

                        double previousRevenue =
                                getDouble(
                                        previousYear,
                                        "revenue"
                                );

                        if (previousRevenue != 0) {

                            revenueGrowth =
                                    (
                                            (
                                                    currentRevenue
                                                            - previousRevenue
                                            )
                                                    / previousRevenue
                                    ) * 100;
                        }

                        double currentIncome =
                                getDouble(
                                        currentYear,
                                        "netIncome"
                                );

                        double previousIncome =
                                getDouble(
                                        previousYear,
                                        "netIncome"
                                );

                        if (previousIncome != 0) {

                            earningsGrowth =
                                    (
                                            (
                                                    currentIncome
                                                            - previousIncome
                                            )
                                                    / previousIncome
                                    ) * 100;
                        }

                        double currentEPS =
                                getDouble(
                                        currentYear,
                                        "eps"
                                );

                        double previousEPS =
                                getDouble(
                                        previousYear,
                                        "eps"
                                );

                        if (previousEPS != 0) {

                            epsGrowth =
                                    (
                                            (
                                                    currentEPS
                                                            - previousEPS
                                            )
                                                    / previousEPS
                                    ) * 100;
                        }

                        double currentGrossProfit =
                                getDouble(
                                        currentYear,
                                        "grossProfit"
                                );

                        double previousGrossProfit =
                                getDouble(
                                        previousYear,
                                        "grossProfit"
                                );

                        if (previousGrossProfit != 0) {

                            grossProfitGrowth =
                                    (
                                            (
                                                    currentGrossProfit
                                                            - previousGrossProfit
                                            )
                                                    / previousGrossProfit
                                    ) * 100;
                        }

                        double currentOperatingIncome =
                                getDouble(
                                        currentYear,
                                        "operatingIncome"
                                );

                        double previousOperatingIncome =
                                getDouble(
                                        previousYear,
                                        "operatingIncome"
                                );

                        if (previousOperatingIncome != 0) {

                            operatingIncomeGrowth =
                                    (
                                            (
                                                    currentOperatingIncome
                                                            - previousOperatingIncome
                                            )
                                                    / previousOperatingIncome
                                    ) * 100;
                        }

                        receivedData = true;

                        System.out.println(
                                "FMP INCOME DATA RECEIVED FOR "
                                        + symbol
                        );
                    }

                } catch (Exception e) {

                    System.out.println(
                            "Could not parse FMP income data for "
                                    + symbol
                    );
                }

            } else {

                System.out.println(
                        "FMP income statement unavailable for "
                                + symbol
                                + " - continuing."
                );
            }

            // =================================================
            // COMPANY PROFILE
            // =================================================

            System.out.println(
                    "REQUESTING FMP PROFILE: "
                            + symbol
            );

            String profileURL =
                    "https://financialmodelingprep.com/stable/profile"
                    + "?symbol="
                    + symbol;

            String profileJSON =
                    sendRequest(
                            client,
                            profileURL
                    );

            if (profileJSON != null &&
                    profileJSON
                            .trim()
                            .startsWith("[")) {

                try {

                    JsonArray profileArray =
                            JsonParser.parseString(
                                    profileJSON
                            ).getAsJsonArray();

                    if (profileArray.size() > 0) {

                        JsonObject profile =
                                profileArray
                                        .get(0)
                                        .getAsJsonObject();

                        companyName =
                                getString(
                                        profile,
                                        "companyName"
                                );

                        description =
                                getString(
                                        profile,
                                        "description"
                                );

                        sector =
                                getString(
                                        profile,
                                        "sector"
                                );

                        industry =
                                getString(
                                        profile,
                                        "industry"
                                );

                        country =
                                getString(
                                        profile,
                                        "country"
                                );

                        exchange =
                                getString(
                                        profile,
                                        "exchange"
                                );

                        currency =
                                getString(
                                        profile,
                                        "currency"
                                );

                        website =
                                getString(
                                        profile,
                                        "website"
                                );

                        ceo =
                                getString(
                                        profile,
                                        "ceo"
                                );

                        ipoDate =
                                getString(
                                        profile,
                                        "ipoDate"
                                );

                        week52High =
                                getDouble(
                                        profile,
                                        "yearHigh"
                                );

                        week52Low =
                                getDouble(
                                        profile,
                                        "yearLow"
                                );

                        beta =
                                getDouble(
                                        profile,
                                        "beta"
                                );

                        averageVolume =
                                getDouble(
                                        profile,
                                        "averageVolume"
                                );

                        sharesOutstanding =
                                getDouble(
                                        profile,
                                        "sharesOutstanding"
                                );

                        marketCap =
                                getDouble(
                                        profile,
                                        "marketCap"
                                );

                        receivedData = true;

                        System.out.println(
                                "FMP PROFILE RECEIVED FOR "
                                        + symbol
                        );
                    }

                } catch (Exception e) {

                    System.out.println(
                            "Could not parse FMP profile for "
                                    + symbol
                    );
                }

            } else {

                System.out.println(
                        "FMP profile unavailable for "
                                + symbol
                                + " - continuing."
                );
            }

            // =================================================
            // NOTHING AVAILABLE
            // =================================================

            if (!receivedData) {

                System.out.println(
                        "NO FMP DATA AVAILABLE FOR "
                                + symbol
                );

                return null;
            }

            // =================================================
            // CREATE COMPANY DATA
            // =================================================

            CompanyData company =
                    new CompanyData(

                            // VALUATION
                            peRatio,
                            0.0,
                            pegRatio,
                            priceToSales,
                            priceToBook,
                            evToEbitda,
                            0.0,
                            evToRevenue,
                            eps,
                            0.0,

                            // GROWTH
                            earningsGrowth,
                            revenueGrowth,
                            epsGrowth,
                            grossProfitGrowth,
                            operatingIncomeGrowth,
                            0.0,
                            0.0,
                            0.0,

                            // FINANCIAL HEALTH
                            profitMargin,
                            grossMargin,
                            operatingMargin,
                            debtToEquity,
                            returnOnEquity,
                            returnOnAssets,
                            currentRatio,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            freeCashFlow,

                            // DIVIDENDS
                            dividendYield,
                            dividendPerShare,
                            payoutRatio,
                            0.0,
                            "",
                            "",

                            // STOCK STATISTICS
                            marketCap,
                            week52High,
                            week52Low,
                            beta,
                            averageVolume,
                            sharesOutstanding,
                            0.0,
                            0.0,
                            0.0,

                            // COMPANY
                            companyName,
                            description,
                            sector,
                            industry,
                            country,
                            exchange,
                            currency,
                            website,
                            ceo,
                            "",
                            ipoDate,
                            ""
                    );

            // =================================================
            // SAVE TO SQL CACHE
            // =================================================

            System.out.println(
                    "Saving "
                            + symbol
                            + " to company cache..."
            );

            

            System.out.println(
                    symbol
                            + " successfully cached!"
            );

            return company;

        } catch (Exception e) {

            System.out.println(
                    "Company data request failed!"
            );

            e.printStackTrace();
        }

        return null;
    }

    // =====================================================
    // SEND REQUEST
    // =====================================================

    private String sendRequest(
            HttpClient client,
            String baseURL)
            throws Exception {

        if (apiKeys.isEmpty()) {

            System.out.println(
                    "No FMP API keys available."
            );

            return null;
        }

        int keysTried = 0;

        while (keysTried < apiKeys.size()) {

            String apiKey =
                    apiKeys.get(
                            currentKeyIndex
                    );

            int keyNumber =
                    currentKeyIndex + 1;

            System.out.println(
                    "================================"
            );

            System.out.println(
                    "USING FMP API KEY "
                            + keyNumber
                            + " OF "
                            + apiKeys.size()
            );

            System.out.println(
                    "================================"
            );

            String separator =
                    baseURL.contains("?")
                            ? "&"
                            : "?";

            String url =
                    baseURL
                            + separator
                            + "apikey="
                            + apiKey;

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(url)
                            )
                            .GET()
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            int status =
                    response.statusCode();

            System.out.println(
                    "FMP STATUS: "
                            + status
            );

            // =================================================
            // SUCCESS
            // =================================================

            if (status == 200) {

                System.out.println(
                        "FMP REQUEST SUCCESSFUL."
                );

                rotateToNextKey();

                return response.body();
            }

            // =================================================
            // 402 = PLAN RESTRICTION
            // =================================================

            if (status == 402) {

                System.out.println(
                        "FMP ENDPOINT NOT AVAILABLE "
                                + "UNDER CURRENT PLAN."
                );

                System.out.println(
                        "SKIPPING THIS ENDPOINT AND "
                                + "CONTINUING WITH AVAILABLE DATA."
                );

                System.out.println(
                        "FMP ERROR RESPONSE:"
                );

                System.out.println(
                        response.body()
                );

                return null;
            }

            // =================================================
            // 429 = RATE LIMIT
            // =================================================

            if (status == 429) {

                System.out.println(
                        "FMP KEY "
                                + keyNumber
                                + " HIT RATE LIMIT."
                );

                System.out.println(
                        "ROTATING TO NEXT FMP API KEY..."
                );

                rotateToNextKey();

                keysTried++;

                continue;
            }

            // =================================================
            // 401 = UNAUTHORIZED
            // =================================================

            if (status == 401) {

                System.out.println(
                        "FMP KEY "
                                + keyNumber
                                + " IS UNAUTHORIZED."
                );

                System.out.println(
                        "ROTATING TO NEXT FMP API KEY..."
                );

                rotateToNextKey();

                keysTried++;

                continue;
            }

            // =================================================
            // OTHER ERROR
            // =================================================

            System.out.println(
                    "FMP ERROR RESPONSE:"
            );

            System.out.println(
                    response.body()
            );

            return null;
        }

        // =====================================================
        // ALL KEYS FAILED
        // =====================================================

        System.out.println(
                "================================"
        );

        System.out.println(
                "ALL FMP API KEYS FAILED."
        );

        System.out.println(
                "================================"
        );

        return null;
    }

    // =====================================================
    // ROTATE API KEY
    // =====================================================

    private void rotateToNextKey() {

        currentKeyIndex++;

        if (currentKeyIndex >= apiKeys.size()) {

            currentKeyIndex = 0;
        }
    }

    // =====================================================
    // SAFE DOUBLE
    // =====================================================

    private double getDouble(
            JsonObject object,
            String key) {

        try {

            if (object == null ||
                    !object.has(key) ||
                    object.get(key).isJsonNull()) {

                return 0.0;
            }

            JsonElement value =
                    object.get(key);

            if (value.isJsonPrimitive()) {

                return value.getAsDouble();
            }

        } catch (Exception ignored) {
        }

        return 0.0;
    }

    // =====================================================
    // SAFE STRING
    // =====================================================

    private String getString(
            JsonObject object,
            String key) {

        try {

            if (object == null ||
                    !object.has(key) ||
                    object.get(key).isJsonNull()) {

                return "";
            }

            return object
                    .get(key)
                    .getAsString();

        } catch (Exception ignored) {
        }

        return "";
    }
}