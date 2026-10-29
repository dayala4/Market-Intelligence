
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // Store application data in the current Windows user's local app-data folder.
    private static final String APP_FOLDER = "MarketIntelligence";

    private static String getDatabaseUrl() throws SQLException {
        String localAppData = System.getenv("LOCALAPPDATA");

        if (localAppData == null || localAppData.trim().isEmpty()) {
            throw new SQLException(
                    "Windows LOCALAPPDATA environment variable is unavailable."
            );
        }

        Path appDirectory = Paths.get(localAppData, APP_FOLDER);

        try {
            Files.createDirectories(appDirectory);
        } catch (Exception e) {
            throw new SQLException(
                    "Could not create the application data directory: "
                            + appDirectory,
                    e
            );
        }

        Path databasePath = appDirectory.resolve("stockpredictor.db");

        return "jdbc:sqlite:" + databasePath.toAbsolutePath();
    }

    // =========================
    // GET DATABASE CONNECTION
    // =========================

    public static Connection getConnection() throws SQLException {

        Connection connection =
                DriverManager.getConnection(getDatabaseUrl());

                try (Statement statement = connection.createStatement();
     var result = statement.executeQuery("PRAGMA database_list")) {
    while (result.next()) {
        System.out.println("DATABASE FILE: " + result.getString("file"));
    }
}


try (Statement statement = connection.createStatement();
     var tables = statement.executeQuery(
             "SELECT name FROM sqlite_master " +
             "WHERE type = 'table' ORDER BY name")) {

    while (tables.next()) {
        String tableName = tables.getString("name");

        System.out.println("TABLE: " + tableName);

        try (Statement columnStatement = connection.createStatement();
             var columns = columnStatement.executeQuery(
                     "PRAGMA table_info(\"" + tableName + "\")")) {

            while (columns.next()) {
                System.out.println(" - " + columns.getString("name"));
            }
        }
    }
}

        // Enable foreign-key enforcement for this connection.
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            connection.close();
            throw e;
        }

        return connection;
    }

    // =========================
    // INITIALIZE DATABASE
    // =========================

    public static boolean initializeDatabase() {

        String createUsersTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL" +
                ")";

        String createWatchlistTable =
                "CREATE TABLE IF NOT EXISTS watchlist (" +
                "user_id INTEGER NOT NULL, " +
                "symbol TEXT NOT NULL, " +
                "PRIMARY KEY (user_id, symbol), " +
                "FOREIGN KEY (user_id) " +
                "REFERENCES users(id) " +
                "ON DELETE CASCADE" +
                ")";

        String createCompanyCacheTable =
              "CREATE TABLE IF NOT EXISTS company_cache (" +
             "symbol TEXT PRIMARY KEY, " +
             "company_data TEXT NOT NULL, " +
             "last_updated INTEGER NOT NULL" +
             ")";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(createUsersTable);
            statement.executeUpdate(createWatchlistTable);
            statement.executeUpdate(createCompanyCacheTable);

            System.out.println("SQLite database initialized.");
            return true;

        } catch (SQLException e) {

            System.err.println("Failed to initialize SQLite database.");
            e.printStackTrace();
            return false;
        }
    }
}
