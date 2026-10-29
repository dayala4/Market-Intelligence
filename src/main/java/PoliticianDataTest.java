
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PoliticianDataTest {

    public static void main(String[] args) {

        System.out.println("POLITICIAN PDF TEST");
        System.out.println();

        String pdfUrl =
                "https://disclosures-clerk.house.gov/"
                        + "public_disc/ptr-pdfs/2026/20033725.pdf";

        HttpClient client =
                HttpClient.newBuilder()
                        .followRedirects(
                                HttpClient.Redirect.NORMAL
                        )
                        .build();

        try {

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(pdfUrl))
                            .header(
                                    "User-Agent",
                                    "StockPredictor/1.0"
                            )
                            .GET()
                            .build();

            HttpResponse<byte[]> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );

            System.out.println(
                    "PDF status: "
                            + response.statusCode()
            );

            System.out.println(
                    "PDF bytes: "
                            + response.body().length
            );

            byte[] data =
                    response.body();

            System.out.println();
            System.out.println(
                    "FIRST 20 BYTES:"
            );

            for (
                    int i = 0;
                    i < Math.min(20, data.length);
                    i++
            ) {

                System.out.printf(
                        "%02X ",
                        data[i]
                );
            }

            System.out.println();
            System.out.println();

            String header =
                    new String(
                            data,
                            0,
                            Math.min(
                                    20,
                                    data.length
                            )
                    );

            System.out.println(
                    "PDF HEADER:"
            );

            System.out.println(
                    header
            );

            if (
                    data.length >= 4
                    && data[0] == '%'
                    && data[1] == 'P'
                    && data[2] == 'D'
                    && data[3] == 'F'
            ) {

                System.out.println();
                System.out.println(
                        "SUCCESS: This is a real PDF."
                );

            } else {

                System.out.println();
                System.out.println(
                        "WARNING: Response is not a PDF."
                );
            }

        } catch (IOException | InterruptedException e) {

            System.out.println();
            System.out.println(
                    "ERROR:"
            );

            System.out.println(
                    e.getMessage()
            );

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}