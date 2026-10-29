
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PoliticianDataService {

    private static final String HOUSE_DISCLOSURE_URL =
            "https://disclosures-clerk.house.gov/FinancialDisclosure";

    private static final String HOUSE_SEARCH_URL =
            "https://disclosures-clerk.house.gov/FinancialDisclosure/ViewSearch";

    private static final String HOUSE_MEMBER_SEARCH_URL =
            "https://disclosures-clerk.house.gov/FinancialDisclosure/ViewMemberSearchResult";

    private final HttpClient client;

    public PoliticianDataService() {
        client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public boolean isHouseDisclosureAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HOUSE_DISCLOSURE_URL))
                    .header("User-Agent", "MarketIntelligence/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(
                    "House disclosure status: " + response.statusCode()
            );

            return response.statusCode() == 200;

        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Unable to reach House disclosure website: "
                            + e.getMessage()
            );

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            return false;
        }
    }

    public String downloadDisclosurePage() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HOUSE_DISCLOSURE_URL))
                    .header("User-Agent", "MarketIntelligence/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(
                    "Disclosure page status: " + response.statusCode()
            );

            if (response.statusCode() != 200) {
                return null;
            }

            return response.body();

        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Error downloading disclosure page: " + e.getMessage()
            );

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            return null;
        }
    }

    // =========================================================
    // SEARCH FOR A HOUSE MEMBER
    // =========================================================

    public List<PoliticianTransaction> getRecentTransactions(
            String lastName,
            int filingYear) {

        List<PoliticianTransaction> transactions = new ArrayList<>();

        try {
            System.out.println();
            System.out.println(
                    "Searching House disclosures for: " + lastName
            );

            // 1. Open the search page.
            HttpRequest searchPageRequest = HttpRequest.newBuilder()
                    .uri(URI.create(HOUSE_SEARCH_URL))
                    .header("User-Agent", "MarketIntelligence/1.0")
                    .GET()
                    .build();

            HttpResponse<String> searchPageResponse = client.send(
                    searchPageRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(
                    "Search page status: "
                            + searchPageResponse.statusCode()
            );

            if (searchPageResponse.statusCode() != 200) {
                return transactions;
            }

            String searchPage = searchPageResponse.body();

            // 2. Extract the verification token.
            String token = extractVerificationToken(searchPage);

            if (token == null) {
                System.out.println(
                        "Could not find House search verification token."
                );
                return transactions;
            }

            // 3. Submit the member search.
            String formData =
                    "LastName=" + encode(lastName)
                            + "&FilingYear=" + filingYear
                            + "&State="
                            + "&District="
                            + "&__RequestVerificationToken="
                            + encode(token);

            HttpRequest memberSearchRequest = HttpRequest.newBuilder()
                    .uri(URI.create(HOUSE_MEMBER_SEARCH_URL))
                    .header("User-Agent", "MarketIntelligence/1.0")
                    .header(
                            "Content-Type",
                            "application/x-www-form-urlencoded"
                    )
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> memberSearchResponse = client.send(
                    memberSearchRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(
                    "Member search status: "
                            + memberSearchResponse.statusCode()
            );

            if (memberSearchResponse.statusCode() != 200) {
                return transactions;
            }

            String resultsPage = memberSearchResponse.body();

            // 4. Find a transaction disclosure PDF.
            String pdfUrl = extractFirstPdfUrl(resultsPage);

            if (pdfUrl == null) {
    System.out.println(
            "No transaction disclosure PDF found."
    );

    System.out.println("Searching response for PDF links...");

    Pattern debugPattern = Pattern.compile(
            "href\\s*=\\s*[\"']([^\"']+)[\"']",
            Pattern.CASE_INSENSITIVE
    );

    Matcher debugMatcher = debugPattern.matcher(resultsPage);

    int count = 0;

    while (debugMatcher.find() && count < 20) {
        String link = debugMatcher.group(1);

        if (link.toLowerCase().contains(".pdf")
                || link.toLowerCase().contains("ptr")
                || link.toLowerCase().contains("disclosure")) {
            System.out.println("Possible disclosure link: " + link);
            count++;
        }
    }

    System.out.println("Possible links printed: " + count);

    return transactions;
}

            System.out.println("Disclosure PDF found:");
            System.out.println(pdfUrl);

            // 5. Download the PDF.
            Path temporaryPdf = Files.createTempFile(
                    "politician-disclosure-",
                    ".pdf"
            );

            try {
                HttpRequest pdfRequest = HttpRequest.newBuilder()
                        .uri(URI.create(pdfUrl))
                        .header("User-Agent", "MarketIntelligence/1.0")
                        .GET()
                        .build();

                HttpResponse<byte[]> pdfResponse = client.send(
                        pdfRequest,
                        HttpResponse.BodyHandlers.ofByteArray()
                );

                System.out.println(
                        "PDF download status: "
                                + pdfResponse.statusCode()
                );

                if (pdfResponse.statusCode() != 200) {
                    return transactions;
                }

                Files.write(temporaryPdf, pdfResponse.body());

                // 6. Extract PDF text.
                System.out.println("Extracting PDF text...");

                String text;

                try (PDDocument document =
                             Loader.loadPDF(temporaryPdf.toFile())) {

                    PDFTextStripper stripper = new PDFTextStripper();
                    text = stripper.getText(document);
                }

                System.out.println("PDF text extracted.");

                // 7. Parse transactions.
                transactions = PoliticianTransactionParser.parse(text);

                System.out.println(
                        "Transactions parsed: " + transactions.size()
                );

                return transactions;

            } finally {
                // 8. Delete the temporary PDF.
                try {
                    Files.deleteIfExists(temporaryPdf);
                    System.out.println("Temporary PDF deleted.");

                } catch (IOException e) {
                    System.out.println(
                            "Could not delete temporary PDF: "
                                    + e.getMessage()
                    );
                }
            }

        } catch (IOException | InterruptedException e) {
            System.out.println(
                    "Error retrieving politician transactions: "
                            + e.getMessage()
            );

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            return transactions;
        }
    }

    // =========================================================
    // EXTRACT HOUSE VERIFICATION TOKEN
    // =========================================================

    private String extractVerificationToken(String html) {
        Pattern pattern = Pattern.compile(
                "name=\"__RequestVerificationToken\"[^>]*value=\"([^\"]+)\""
        );

        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    // =========================================================
    // FIND AND NORMALIZE PDF LINK
    // =========================================================

    private String extractFirstPdfUrl(String html) {
        Pattern pattern = Pattern.compile(
                "href\\s*=\\s*[\"']([^\"']*ptr-pdfs/[^\"']+\\.pdf)[\"']",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(html);

        if (!matcher.find()) {
            return null;
        }

        String path = matcher.group(1).trim()
                .replace("&amp;", "&");

        // Already an absolute URL.
        if (path.startsWith("https://") || path.startsWith("http://")) {
            return path;
        }

        // Root-relative URL.
        if (path.startsWith("/")) {
            return "https://disclosures-clerk.house.gov" + path;
        }

        // Relative URL without a leading slash.
        return "https://disclosures-clerk.house.gov/" + path;
    }

    // =========================================================
    // SIMPLE URL FORM ENCODING
    // =========================================================

    private String encode(String value) {
        return value
                .replace("%", "%25")
                .replace(" ", "%20")
                .replace("&", "%26")
                .replace("+", "%2B");
    }
}
