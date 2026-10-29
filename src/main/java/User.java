
import java.util.LinkedList;

public class User {

    private int userId;
    private String username;
    private String password;

    private LinkedList<String> watchlist;

    public User(
            int userId,
            String username,
            String password) {

        this.userId = userId;
        this.username = username;
        this.password = password;

        watchlist = new LinkedList<>();
    }

    // =========================
    // GETTERS
    // =========================

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LinkedList<String> getWatchlist() {
        return watchlist;
    }

    // =========================
    // WATCHLIST
    // =========================

    public void addStock(String symbol) {

        if (!watchlist.contains(symbol)) {

            watchlist.add(symbol);
        }
    }

    public void removeStock(String symbol) {

        watchlist.remove(symbol);
    }

    public boolean hasStock(String symbol) {

        return watchlist.contains(symbol);
    }
}
