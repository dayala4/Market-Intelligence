import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PoliticianTransactionParser {

private static final Pattern NAME_PATTERN =
        Pattern.compile("Name:\\s*(.+)");

private static final Pattern STATE_PATTERN =
        Pattern.compile("State/District:\\s*([A-Z]{2}\\d+)");

private static final Pattern FILING_ID_PATTERN =
        Pattern.compile("Filing ID #([0-9]+)");

private static final Pattern TRANSACTION_PATTERN =
        Pattern.compile(
                "SP\\s+(.+?)\\s+\\[([A-Z]+)\\]\\s+"
                        + "(P|S(?: \\(partial\\))?|E)\\s+"
                        + "(\\d{2}/\\d{2}/\\d{4})\\s+"
                        + "(\\d{2}/\\d{2}/\\d{4})\\s+"
                        + "(\\$[0-9,]+(?:\\s*-\\s*\\$[0-9,]+)?)",
                Pattern.DOTALL
        );

public static List<PoliticianTransaction> parse(String text) {

    List<PoliticianTransaction> transactions =
            new ArrayList<>();

    if (text == null || text.trim().isEmpty()) {
        return transactions;
    }

    String normalizedText = normalizeText(text);

    String name = extractName(normalizedText);
    String chamber = "House";
    String state = extractState(normalizedText);

    Matcher matcher =
            TRANSACTION_PATTERN.matcher(normalizedText);

    while (matcher.find()) {

        String assetDescription = matcher.group(1).trim();
        String assetType = matcher.group(2).trim();

        String actionCode = matcher.group(3).trim();
        String transactionDate = matcher.group(4).trim();
        String filingDate = matcher.group(5).trim();
        String amountRange = matcher.group(6).trim();

        String ticker = extractTicker(assetDescription);

        String company = extractCompany(assetDescription);

        String action = convertAction(actionCode);

        transactions.add(
                new PoliticianTransaction(
                        name,
                        chamber,
                        state,
                        ticker,
                        company,
                        action,
                        transactionDate,
                        filingDate,
                        amountRange
                )
        );
    }

    return transactions;
}

private static String normalizeText(String text) {

    String normalized = text.replace("\r", "\n");

    normalized = normalized.replaceAll("[ \\t]+", " ");

    normalized = normalized.replaceAll(
            "\\n+",
            "\n"
    );

    return normalized;
}

private static String extractName(String text) {

    Matcher matcher =
            NAME_PATTERN.matcher(text);

    if (matcher.find()) {
        return matcher.group(1).trim();
    }

    return "Unknown";
}

private static String extractState(String text) {

    Matcher matcher =
            STATE_PATTERN.matcher(text);

    if (matcher.find()) {
        return matcher.group(1).trim();
    }

    return "Unknown";
}

private static String extractTicker(String assetDescription) {

    Pattern tickerPattern =
            Pattern.compile("\\(([A-Z]{1,5})\\)");

    Matcher matcher =
            tickerPattern.matcher(assetDescription);

    String ticker = null;

    while (matcher.find()) {
        ticker = matcher.group(1);
    }

    if (ticker == null) {
        return "N/A";
    }

    return ticker;
}

private static String extractCompany(String assetDescription) {

    String company = assetDescription;

    int firstParen =
            company.indexOf("(");

    if (firstParen > 0) {
        company = company.substring(0, firstParen);
    }

    return company
            .replaceAll("\\s+", " ")
            .trim();
}

private static String convertAction(String actionCode) {

    if (actionCode.startsWith("P")) {
        return "BUY";
    }

    if (actionCode.startsWith("S")) {
        return "SELL";
    }

    if (actionCode.startsWith("E")) {
        return "EXCHANGE";
    }

    return "OTHER";
}

public static void printTransactions(
        List<PoliticianTransaction> transactions) {

    System.out.println();
    System.out.println(
            "========== PARSED TRANSACTIONS =========="
    );
    System.out.println();

    for (PoliticianTransaction transaction : transactions) {

        System.out.println(
                "Politician: "
                        + transaction.getName()
        );

        System.out.println(
                "State: "
                        + transaction.getState()
        );

        System.out.println(
                "Ticker: "
                        + transaction.getTicker()
        );

        System.out.println(
                "Company: "
                        + transaction.getCompany()
        );

        System.out.println(
                "Action: "
                        + transaction.getAction()
        );

        System.out.println(
                "Transaction Date: "
                        + transaction.getTransactionDate()
        );

        System.out.println(
                "Filing Date: "
                        + transaction.getFilingDate()
        );

        System.out.println(
                "Amount Range: "
                        + transaction.getAmountRange()
        );

        System.out.println(
                "----------------------------------------"
        );
    }

    System.out.println();
    System.out.println(
            "Total parsed transactions: "
                    + transactions.size()
    );
}

}
