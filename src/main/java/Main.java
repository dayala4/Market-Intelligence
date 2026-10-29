public class Main {

    public static void main(String[] args) {

        System.out.println("PROGRAM STARTED");

        // Create the database tables before opening the login screen.
        if (!DatabaseManager.initializeDatabase()) {
            System.err.println("Database initialization failed.");
            return;
        }

        System.out.println("OPENING LOGIN");

        new LoginScreen().setVisible(true);
    }
}