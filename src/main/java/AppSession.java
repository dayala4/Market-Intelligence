
public class AppSession {

    private static UserManager userManager =
            new UserManager();

    private static boolean onlineSession = false;

    public static UserManager getUserManager() {
        return userManager;
    }

    public static User getCurrentUser() {
        return userManager.getCurrentUser();
    }

    public static void login(User user) {
        userManager.setCurrentUser(user);
        onlineSession = false;
    }

    public static void loginOnline() {
        onlineSession = true;
    }

    public static boolean isOnlineSession() {
        return onlineSession;
    }

    public static void logout() {
        userManager.logout();
        onlineSession = false;
        SupabaseSession.clear();
    }
}
