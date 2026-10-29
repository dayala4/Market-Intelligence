
public final class SupabaseSession {

    private static String accessToken;
    private static String refreshToken;
    private static String userId;
    private static String email;

    private SupabaseSession() {
    }

    public static synchronized void start(
            String newAccessToken,
            String newRefreshToken,
            String newUserId,
            String newEmail) {

        accessToken = newAccessToken;
        refreshToken = newRefreshToken;
        userId = newUserId;
        email = newEmail;
    }

    public static synchronized boolean isLoggedIn() {
        return accessToken != null
                && !accessToken.isBlank()
                && userId != null
                && !userId.isBlank();
    }

    public static synchronized String getAccessToken() {
        return accessToken;
    }

    public static synchronized String getRefreshToken() {
        return refreshToken;
    }

    public static synchronized String getUserId() {
        return userId;
    }

    public static synchronized String getEmail() {
        return email;
    }

    public static synchronized void clear() {
        accessToken = null;
        refreshToken = null;
        userId = null;
        email = null;
    }
}
