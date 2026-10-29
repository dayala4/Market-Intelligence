
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserManager {

    private static final String USER_FILE = "users.dat";
    private static final String MIGRATION_KEY = "users_dat_to_sqlite_v1";

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, User> usersByUsername = new HashMap<>();

    private int nextUserId = 1;
    private User currentUser;

    public UserManager() {
    try {
        loadUsersFromDatabase();
    } catch (SQLException e) {
        System.err.println("Could not initialize account storage.");
        e.printStackTrace();
    }
}

    // =========================
    // CREATE USER
    // =========================

    public User createUser(String username, String password) {

        if (username == null || password == null) {
            return null;
        }

        username = username.trim();

        if (username.isEmpty() || password.isEmpty()) {
            return null;
        }

        if (usersByUsername.containsKey(username)) {
            return null;
        }

        String hashedPassword = hashPassword(password);

        try (Connection connection = DatabaseManager.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (username, password) VALUES (?, ?)")) {

                statement.setString(1, username);
                statement.setString(2, hashedPassword);
                statement.executeUpdate();
            }

            int userId;

            try (Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "SELECT last_insert_rowid()")) {

                if (!result.next()) {
                    throw new SQLException("Could not retrieve the new user ID.");
                }

                userId = result.getInt(1);
            }

            User user = new User(userId, username, hashedPassword);
            addUser(user);

            System.out.println("User created in SQLite.");
            return user;

        } catch (SQLException e) {
            System.err.println("Could not create user in SQLite.");
            e.printStackTrace();
            return null;
        }
    }

    // =========================
    // LOGIN
    // =========================

    public User login(String username, String password) {

        if (username == null || password == null) {
            return null;
        }

        User user = usersByUsername.get(username.trim());

        if (user == null) {
            return null;
        }

        String hashedPassword = hashPassword(password);

        if (!user.getPassword().equals(hashedPassword)) {
            return null;
        }

        setCurrentUser(user);
        return user;
    }

    // =========================
    // ADD USER TO MEMORY
    // =========================

    public void addUser(User user) {

        if (user == null) {
            return;
        }

        users.put(user.getUserId(), user);
        usersByUsername.put(user.getUsername(), user);

        if (user.getUserId() >= nextUserId) {
            nextUserId = user.getUserId() + 1;
        }
    }

    // =========================
    // FIND USER
    // =========================

    public User getUser(int userId) {
        return users.get(userId);
    }

    public User getUser(String username) {

        if (username == null) {
            return null;
        }

        return usersByUsername.get(username.trim());
    }

    // =========================
    // REMOVE USER
    // =========================

    public void removeUser(int userId) {

        User user = users.get(userId);

        if (user == null) {
            return;
        }

        try (Connection connection = DatabaseManager.getConnection()) {

            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM watchlist WHERE user_id = ?")) {

                    statement.setInt(1, userId);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM users WHERE id = ?")) {

                    statement.setInt(1, userId);
                    statement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

            users.remove(userId);
            usersByUsername.remove(user.getUsername());

            if (currentUser != null &&
                    currentUser.getUserId() == userId) {
                currentUser = null;
            }

            System.out.println("User removed from SQLite.");

        } catch (SQLException e) {
            System.err.println("Could not remove user from SQLite.");
            e.printStackTrace();
        }
    }

    // =========================
    // CHECK USER
    // =========================

    public boolean containsUser(int userId) {
        return users.containsKey(userId);
    }

    public boolean containsUsername(String username) {

        if (username == null) {
            return false;
        }

        return usersByUsername.containsKey(username.trim());
    }

    // =========================
    // USER COUNT
    // =========================

    public int getUserCount() {
        return users.size();
    }

    // =========================
    // CURRENT USER
    // =========================

    public void setCurrentUser(User user) {

        currentUser = user;

        if (user != null) {
            addUser(user);
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    // =========================
    // SAVE USERS TO SQLITE
    // =========================

    public void saveUsers() {

        try (Connection connection = DatabaseManager.getConnection()) {

            connection.setAutoCommit(false);

            try {
                for (User user : users.values()) {

                    int updated;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         "UPDATE users SET username = ?, password = ? " +
                                         "WHERE id = ?")) {

                        statement.setString(1, user.getUsername());
                        statement.setString(2, user.getPassword());
                        statement.setInt(3, user.getUserId());

                        updated = statement.executeUpdate();
                    }

                    if (updated == 0) {
                        try (PreparedStatement statement =
                                     connection.prepareStatement(
                                             "INSERT INTO users (id, username, password) " +
                                             "VALUES (?, ?, ?)")) {

                            statement.setInt(1, user.getUserId());
                            statement.setString(2, user.getUsername());
                            statement.setString(3, user.getPassword());
                            statement.executeUpdate();
                        }
                    }

                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         "DELETE FROM watchlist WHERE user_id = ?")) {

                        statement.setInt(1, user.getUserId());
                        statement.executeUpdate();
                    }

                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         "INSERT OR IGNORE INTO watchlist " +
                                         "(user_id, symbol) VALUES (?, ?)")) {

                        for (String symbol : user.getWatchlist()) {
                            statement.setInt(1, user.getUserId());
                            statement.setString(2, symbol);
                            statement.addBatch();
                        }

                        statement.executeBatch();
                    }
                }

                connection.commit();
                System.out.println("Users and watchlists saved to SQLite.");

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Could not save users to SQLite.");
            e.printStackTrace();
        }
    }

    // =========================
    // LOAD USERS FROM SQLITE
    // =========================

    
private void loadUsersFromDatabase() throws SQLException {

 try (Connection connection = DatabaseManager.getConnection();
     PreparedStatement statement = connection.prepareStatement(
             "SELECT id, username, password " +
             "FROM users ORDER BY id");
     ResultSet result = statement.executeQuery()) {

    while (result.next()) {

            int userId = result.getInt("id");
            String username = result.getString("username");
            String password = result.getString("password");

            User user = new User(userId, username, password);

            try (PreparedStatement watchlistStatement =
                         connection.prepareStatement(
                                 "SELECT symbol FROM watchlist " +
                                 "WHERE user_id = ? ORDER BY symbol")) {

                watchlistStatement.setInt(1, userId);

                try (ResultSet watchlistResult =
                             watchlistStatement.executeQuery()) {

                    while (watchlistResult.next()) {
                        user.addStock(watchlistResult.getString("symbol"));
                    }
                }
            }

            addUser(user);
        }
    }

    System.out.println(
            "Loaded " + users.size() + " user(s) from SQLite.");
}

    // =========================
    // PASSWORD HASHING
    // =========================

    private String hashPassword(String password) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    "SHA-256 is not available.", e);
        }
    }
}
