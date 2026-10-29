
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public final class SupabaseAuthService {

    private SupabaseAuthService() {
    }

    public static SignupResult signUp(
            String email,
            String password,
            String username) {

        if (!SupabaseClient.isConfigured()) {
            return new SignupResult(
                    false, "Supabase is not configured.", false);
        }

        String cleanEmail = email == null ? "" : email.trim();
        String cleanUsername = username == null ? "" : username.trim();

        if (cleanEmail.isEmpty()
                || cleanUsername.isEmpty()
                || password == null
                || password.isEmpty()) {
            return new SignupResult(
                    false,
                    "Enter an email, username, and password.",
                    false);
        }

        if (!cleanEmail.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return new SignupResult(
                    false, "Enter a valid email address.", false);
        }

        if (!cleanUsername.matches(
                "^[A-Za-z0-9_]{3,30}$")) {
            return new SignupResult(
                    false,
                    "Username must be 3–30 characters using only letters, numbers, or underscores.",
                    false);
        }

        if (password.length() < 6) {
            return new SignupResult(
                    false, "Password must contain at least 6 characters.", false);
        }

        String json = "{"
                + "\"email\":\"" + escapeJson(cleanEmail) + "\","
                + "\"password\":\"" + escapeJson(password) + "\","
                + "\"data\":{\"username\":\""
                + escapeJson(cleanUsername) + "\"}"
                + "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(SupabaseClient.getAuthUri("signup"))
                    .header("apikey", SupabaseClient.getPublishableKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response =
                    SupabaseClient.getHttpClient().send(
                            request,
                            HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            String body = response.body();

            if (status >= 200 && status < 300) {
                return new SignupResult(
                        true,
                        "Account request accepted. Check your email for a confirmation link before signing in.",
                        true);
            }

            return new SignupResult(
                    false, extractError(body, status), false);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new SignupResult(
                    false, "Signup was interrupted. Please try again.", false);

        } catch (Exception e) {
            return new SignupResult(
                    false,
                    "Could not connect to Supabase. Check your internet connection and try again.",
                    false);
        }
    }

    public static SignupResult signIn(
            String email,
            String password) {

        if (email == null || email.isBlank()
                || password == null || password.isEmpty()) {
            return new SignupResult(
                    false, "Enter your email and password.", false);
        }

        String json = "{"
                + "\"email\":\"" + escapeJson(email.trim()) + "\","
                + "\"password\":\"" + escapeJson(password) + "\""
                + "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(SupabaseClient.getAuthUri(
                            "token?grant_type=password"))
                    .header("apikey", SupabaseClient.getPublishableKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response =
                    SupabaseClient.getHttpClient().send(
                            request,
                            HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            String body = response.body();

            if (status >= 200 && status < 300) {
                String accessToken =
                        extractJsonString(body, "access_token");
                String refreshToken =
                        extractJsonString(body, "refresh_token");
                String userId = extractJsonString(body, "id");
                String returnedEmail =
                        extractJsonString(body, "email");

                if (userId == null) {
                    userId = extractNestedUserId(body);
                }

                if (returnedEmail == null) {
                    returnedEmail = email.trim();
                }

                if (accessToken == null || userId == null) {
                    return new SignupResult(
                            false,
                            "Login response was incomplete. Please try again.",
                            false);
                }

                SupabaseSession.start(
                        accessToken, refreshToken, userId, returnedEmail);

                return new SignupResult(
                        true, "Signed in successfully.", false);
            }

            String lower = body == null ? "" : body.toLowerCase();

            if (lower.contains("email not confirmed")) {
                return new SignupResult(
                        false,
                        "Please confirm your email before signing in.",
                        true);
            }

            return new SignupResult(
                    false,
                    "Login failed. Check your email and password.",
                    false);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new SignupResult(
                    false, "Login was interrupted. Please try again.", false);

        } catch (Exception e) {
            return new SignupResult(
                    false,
                    "Could not connect to Supabase. Check your connection and try again.",
                    false);
        }
    }

    private static String extractNestedUserId(String json) {
        int userKey = json.indexOf("\"user\"");
        if (userKey < 0) return null;

        int idKey = json.indexOf("\"id\"", userKey);
        if (idKey < 0) return null;

        int colon = json.indexOf(':', idKey);
        if (colon < 0) return null;

        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;

        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (secondQuote < 0) return null;

        return json.substring(firstQuote + 1, secondQuote);
    }

    private static String extractJsonString(
            String json, String key) {

        if (json == null) return null;

        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex < 0) return null;

        int colon = json.indexOf(':', keyIndex + search.length());
        if (colon < 0) return null;

        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;

        StringBuilder result = new StringBuilder();
        boolean escaped = false;

        for (int i = firstQuote + 1; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escaped) {
                switch (c) {
                    case 'n': result.append('\n'); break;
                    case 'r': result.append('\r'); break;
                    case 't': result.append('\t'); break;
                    default: result.append(c);
                }
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                return result.toString();
            } else {
                result.append(c);
            }
        }

        return null;
    }

    private static String escapeJson(String value) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            switch (c) {
                case '"': result.append("\\\""); break;
                case '\\': result.append("\\\\"); break;
                case '\b': result.append("\\b"); break;
                case '\f': result.append("\\f"); break;
                case '\n': result.append("\\n"); break;
                case '\r': result.append("\\r"); break;
                case '\t': result.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        result.append(String.format("\\u%04x", (int) c));
                    } else {
                        result.append(c);
                    }
            }
        }

        return result.toString();
    }

    private static String extractError(String body, int status) {
        String lower = body == null ? "" : body.toLowerCase();

        if (lower.contains("already registered")
                || lower.contains("already been registered")
                || lower.contains("user already exists")) {
            return "An account with that email may already exist. Try signing in instead.";
        }

        if (lower.contains("invalid email")) {
            return "Enter a valid email address.";
        }

        if (lower.contains("rate limit")) {
            return "Too many attempts. Please wait before trying again.";
        }

        if (lower.contains("signup is disabled")) {
            return "Online signup is disabled in Supabase settings.";
        }

        return "Online signup failed (HTTP " + status
                + "). Please check your details and try again.";
    }

    public static final class SignupResult {

        private final boolean successful;
        private final String message;
        private final boolean confirmationRequired;

        public SignupResult(
                boolean successful,
                String message,
                boolean confirmationRequired) {
            this.successful = successful;
            this.message = message;
            this.confirmationRequired = confirmationRequired;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public String getMessage() {
            return message;
        }

        public boolean isConfirmationRequired() {
            return confirmationRequired;
        }
    }
}
