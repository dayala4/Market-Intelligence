import java.awt.Color;

public class ThemeManager {

    // =========================
    // LIGHT MODE
    // =========================

    public static final Color LIGHT_BACKGROUND =
            new Color(245, 247, 250);

    public static final Color LIGHT_PANEL =
            Color.WHITE;

    public static final Color LIGHT_CARD =
            Color.WHITE;

    public static final Color LIGHT_TEXT =
            new Color(30, 30, 30);

    public static final Color LIGHT_SECONDARY_TEXT =
            new Color(100, 100, 100);

    public static final Color LIGHT_BORDER =
            new Color(220, 220, 220);

    public static final Color LIGHT_HIGHLIGHT =
            new Color(235, 242, 255);


    // =========================
    // DARK MODE
    // =========================

    public static final Color DARK_BACKGROUND =
            new Color(25, 25, 28);

    public static final Color DARK_PANEL =
            new Color(32, 32, 36);

    public static final Color DARK_CARD =
            new Color(40, 40, 45);

    public static final Color DARK_TEXT =
            new Color(240, 240, 240);

    public static final Color DARK_SECONDARY_TEXT =
            new Color(170, 170, 170);

    public static final Color DARK_BORDER =
            new Color(65, 65, 70);

    public static final Color DARK_HIGHLIGHT =
            new Color(55, 65, 80);


    // =========================
    // CURRENT THEME
    // =========================

    private static boolean darkMode = false;


    public static boolean isDarkMode() {
        return darkMode;
    }


    public static void setDarkMode(boolean dark) {
        darkMode = dark;
    }


    // =========================
    // COLORS
    // =========================

    public static Color background() {

        return darkMode
                ? DARK_BACKGROUND
                : LIGHT_BACKGROUND;
    }


    public static Color panel() {

        return darkMode
                ? DARK_PANEL
                : LIGHT_PANEL;
    }


    public static Color card() {

        return darkMode
                ? DARK_CARD
                : LIGHT_CARD;
    }


    public static Color text() {

        return darkMode
                ? DARK_TEXT
                : LIGHT_TEXT;
    }


    public static Color secondaryText() {

        return darkMode
                ? DARK_SECONDARY_TEXT
                : LIGHT_SECONDARY_TEXT;
    }


    public static Color border() {

        return darkMode
                ? DARK_BORDER
                : LIGHT_BORDER;
    }


    public static Color highlight() {

        return darkMode
                ? DARK_HIGHLIGHT
                : LIGHT_HIGHLIGHT;
    }
}