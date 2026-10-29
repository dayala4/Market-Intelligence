
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Retrieves company facts from the SEC EDGAR company-facts API.
 * Public-facing methods accept ticker symbols where possible; CIKs are handled internally.
 */
public class SECDataClient {

    private static final String COMPANY_TICKERS_URL =
            "https://www.sec.gov/files/company_tickers.json";

    private static final String COMPANY_FACTS_URL =
            "https://data.sec.gov/api/xbrl/companyfacts/CIK%s.json";

    private static final String USER_AGENT =
            "MarketIntelligence contact traveljunkie679@gmail.com";

    private final HttpClient client;
    private Map<String, String> tickerToCik;

    public SECDataClient() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public JsonObject getCompanyFacts(String cik) {
        if (cik == null || cik.trim().isEmpty()) {
            System.out.println("SEC request skipped: CIK is empty.");
            return null;
        }

        String normalizedCik = cik.trim().replaceAll("[^0-9]", "");

        if (normalizedCik.isEmpty()) {
            System.out.println("SEC request skipped: CIK is invalid.");
            return null;
        }

        try {
            normalizedCik = String.format(
                    Locale.ROOT,
                    "%010d",
                    Long.parseLong(normalizedCik)
            );

            String url = String.format(COMPANY_FACTS_URL, normalizedCik);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("SEC Company Facts Status: " + response.statusCode());

            if (response.statusCode() != 200) {
                System.out.println(
                        "SEC Company Facts request failed for CIK " + normalizedCik
                );
                return null;
            }

            return JsonParser.parseString(response.body()).getAsJsonObject();

        } catch (Exception e) {
            System.out.println("SEC Company Facts error: " + e.getMessage());
            return null;
        }
    }

    public JsonObject getCompanyTickers() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(COMPANY_TICKERS_URL))
                    .timeout(Duration.ofSeconds(30))
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                System.out.println(
                        "SEC company tickers status: " + response.statusCode()
                );
                return null;
            }

            return JsonParser.parseString(response.body()).getAsJsonObject();

        } catch (Exception e) {
            System.out.println("SEC company tickers error: " + e.getMessage());
            return null;
        }
    }

    /** Returns the SEC CIK for a ticker, such as NVDA or AAPL. */
    public String getCikByTicker(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return null;
        }

        String ticker = symbol.trim().toUpperCase(Locale.ROOT);

        if (tickerToCik == null) {
            tickerToCik = loadTickerMap();
        }

        return tickerToCik.get(ticker);
    }

    private Map<String, String> loadTickerMap() {
        Map<String, String> map = new HashMap<>();
        JsonObject tickers = getCompanyTickers();

        if (tickers == null) {
            return map;
        }

        for (Map.Entry<String, JsonElement> entry : tickers.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }

            JsonObject company = entry.getValue().getAsJsonObject();

            if (!company.has("ticker") || !company.has("cik_str")) {
                continue;
            }

            String ticker = company.get("ticker")
                    .getAsString()
                    .trim()
                    .toUpperCase(Locale.ROOT);

            String cik = company.get("cik_str").getAsString();

            if (!ticker.isEmpty() && !cik.isEmpty()) {
                map.put(ticker, cik);
            }
        }

        System.out.println("SEC ticker map loaded: " + map.size() + " tickers.");
        return map;
    }

    /** Retrieves company financial data using a stock ticker instead of a CIK. */
    public CompanyData getCompanyDataBySymbol(String symbol) {
        String cik = getCikByTicker(symbol);

        if (cik == null) {
            System.out.println("SEC CIK lookup failed for ticker: " + symbol);
            return null;
        }

        System.out.println(
                "SEC ticker lookup: "
                        + symbol.trim().toUpperCase(Locale.ROOT)
                        + " -> CIK " + cik
        );

        return getCompanyDataByCik(cik);
    }

    public CompanyData getCompanyDataByCik(String cik) {
        JsonObject companyFacts = getCompanyFacts(cik);

        if (companyFacts == null || !companyFacts.has("facts")) {
            return null;
        }

        JsonObject facts = companyFacts.getAsJsonObject("facts");

        if (!facts.has("us-gaap")) {
            System.out.println("SEC facts do not contain us-gaap data for CIK " + cik);
            return null;
        }

        JsonObject usGaap = facts.getAsJsonObject("us-gaap");
        CompanyData data = new CompanyData();

        String companyName = companyFacts.has("entityName")
                ? companyFacts.get("entityName").getAsString()
                : "Unknown Company";

        data.setCompanyName(companyName);

        System.out.println("SEC data parsed for: " + companyName);

        double revenue = getLatestAnnualFact(
                usGaap,
                new String[]{
                        "RevenueFromContractWithCustomerExcludingAssessedTax",
                        "SalesRevenueNet",
                        "Revenues"
                }
        );

        double netIncome = getLatestAnnualFact(
                usGaap,
                new String[]{"NetIncomeLoss", "ProfitLoss"}
        );

        double eps = getLatestAnnualFact(
                usGaap,
                new String[]{"EarningsPerShareDiluted", "EarningsPerShareBasic"}
        );

        double operatingIncome = getLatestAnnualFact(
                usGaap,
                new String[]{"OperatingIncomeLoss"}
        );

        double grossProfit = getLatestAnnualFact(
                usGaap,
                new String[]{"GrossProfit"}
        );

        double operatingCashFlow = getLatestAnnualFact(
                usGaap,
                new String[]{"NetCashProvidedByUsedInOperatingActivities"}
        );

        double capex = getLatestAnnualFact(
                usGaap,
                new String[]{
                        "PaymentsToAcquirePropertyPlantAndEquipment",
                        "PaymentsToAcquireProductiveAssets"
                }
        );

        double assets = getLatestFact(
                usGaap,
                new String[]{"Assets"},
                true
        );

        double liabilities = getLatestFact(
                usGaap,
                new String[]{"Liabilities"},
                true
        );

        double equity = getLatestFact(
                usGaap,
                new String[]{
                        "StockholdersEquity",
                        "StockholdersEquityIncludingPortionAttributableToNoncontrollingInterest"
                },
                true
        );

        double cash = getLatestFact(
                usGaap,
                new String[]{
                        "CashAndCashEquivalentsAtCarryingValue",
                        "CashCashEquivalentsRestrictedCashAndRestrictedCashEquivalents"
                },
                true
        );

        double totalDebt = getLatestFact(
                usGaap,
                new String[]{
                        "LongTermDebtAndFinanceLeaseObligationsCurrent",
                        "LongTermDebtCurrent",
                        "ShortTermBorrowings"
                },
                true
        );

        double longTermDebt = getLatestFact(
                usGaap,
                new String[]{
                        "LongTermDebtAndFinanceLeaseObligationsNoncurrent",
                        "LongTermDebtNoncurrent",
                        "LongTermDebt"
                },
                true
        );

        totalDebt += longTermDebt;

        System.out.println("Annual revenue: " + revenue);
        System.out.println("Annual net income: " + netIncome);
        System.out.println("Annual EPS: " + eps);
        System.out.println("Annual operating cash flow: " + operatingCashFlow);
        System.out.println("Annual capital expenditures: " + capex);
        System.out.println("Latest cash: " + cash);
        System.out.println("Latest total debt estimate: " + totalDebt);

        if (eps != 0) {
            data.setEps(eps);
        }

        if (revenue > 0) {
            if (netIncome != 0) {
                data.setProfitMargin((netIncome / revenue) * 100.0);

                System.out.printf(
                        Locale.ROOT,
                        "Calculated profit margin: %.2f%%%n",
                        (netIncome / revenue) * 100.0
                );
            }

            if (grossProfit != 0) {
                data.setGrossMargin((grossProfit / revenue) * 100.0);
            }

            if (operatingIncome != 0) {
                data.setOperatingMargin((operatingIncome / revenue) * 100.0);
            }
        }

        if (liabilities != 0 && equity != 0) {
            // Total liabilities / equity. This is not strictly debt / equity.
            data.setDebtToEquity(liabilities / equity);
        }

        if (netIncome != 0 && assets > 0) {
            data.setReturnOnAssets((netIncome / assets) * 100.0);
        }

        if (netIncome != 0 && equity != 0) {
            data.setReturnOnEquity((netIncome / equity) * 100.0);
        }

        if (operatingCashFlow != 0) {
            data.setOperatingCashFlow(operatingCashFlow);
        }

        if (operatingCashFlow != 0 && capex != 0) {
    // Only calculate free cash flow when both inputs are available.
    data.setFreeCashFlow(operatingCashFlow - Math.abs(capex));
}

        if (cash != 0) {
            data.setTotalCash(cash);
        }

        if (totalDebt != 0) {
            data.setTotalDebt(totalDebt);
        }

        return data;
    }

    private double getLatestAnnualFact(JsonObject usGaap, String[] possibleTags) {
        double bestValue = 0;
        LocalDate bestEndDate = null;

        for (String tag : possibleTags) {
            if (!usGaap.has(tag)) {
                continue;
            }

            JsonObject fact = usGaap.getAsJsonObject(tag);

            if (!fact.has("units")) {
                continue;
            }

            JsonObject units = fact.getAsJsonObject("units");

            for (Map.Entry<String, JsonElement> unitEntry : units.entrySet()) {
                JsonArray observations = unitEntry.getValue().getAsJsonArray();

                for (JsonElement observationElement : observations) {
                    if (!observationElement.isJsonObject()) {
                        continue;
                    }

                    JsonObject observation = observationElement.getAsJsonObject();

                    if (!observation.has("end")
                            || !observation.has("val")
                            || !observation.has("form")
                            || !observation.has("start")) {
                        continue;
                    }

                    String form = observation.get("form").getAsString();

                    if (!(form.equals("10-K")
                            || form.equals("20-F")
                            || form.equals("40-F"))) {
                        continue;
                    }

                    try {
                        LocalDate start = LocalDate.parse(
                                observation.get("start").getAsString()
                        );

                        LocalDate end = LocalDate.parse(
                                observation.get("end").getAsString()
                        );

                        long days = ChronoUnit.DAYS.between(start, end);

                        if (days < 330 || days > 380) {
                            continue;
                        }

                        if (bestEndDate == null || end.isAfter(bestEndDate)) {
                            bestEndDate = end;
                            bestValue = observation.get("val").getAsDouble();
                        }

                    } catch (Exception ignored) {
                        // Skip malformed SEC observations.
                    }
                }
            }
        }

        return bestValue;
    }

    private double getLatestFact(
            JsonObject usGaap,
            String[] possibleTags,
            boolean instantFact) {

        double bestValue = 0;
        LocalDate bestEndDate = null;

        for (String tag : possibleTags) {
            if (!usGaap.has(tag)) {
                continue;
            }

            JsonObject fact = usGaap.getAsJsonObject(tag);

            if (!fact.has("units")) {
                continue;
            }

            JsonObject units = fact.getAsJsonObject("units");

            for (Map.Entry<String, JsonElement> unitEntry : units.entrySet()) {
                JsonArray observations = unitEntry.getValue().getAsJsonArray();

                for (JsonElement observationElement : observations) {
                    if (!observationElement.isJsonObject()) {
                        continue;
                    }

                    JsonObject observation = observationElement.getAsJsonObject();

                    if (!observation.has("end") || !observation.has("val")) {
                        continue;
                    }

                    if (observation.has("form")) {
                        String form = observation.get("form").getAsString();

                        if (!(form.equals("10-K")
                                || form.equals("10-Q")
                                || form.equals("20-F")
                                || form.equals("40-F"))) {
                            continue;
                        }
                    }

                    if (instantFact && observation.has("start")) {
                        continue;
                    }

                    try {
                        LocalDate end = LocalDate.parse(
                                observation.get("end").getAsString()
                        );

                        if (bestEndDate == null || end.isAfter(bestEndDate)) {
                            bestEndDate = end;
                            bestValue = observation.get("val").getAsDouble();
                        }

                    } catch (Exception ignored) {
                        // Skip malformed SEC observations.
                    }
                }
            }
        }

        return bestValue;
    }

    public boolean testConnection() {
        JsonObject tickers = getCompanyTickers();
        return tickers != null && !tickers.entrySet().isEmpty();
    }
}
