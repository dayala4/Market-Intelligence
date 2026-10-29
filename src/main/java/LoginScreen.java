import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // =====================================================
    // CARD LAYOUT
    // =====================================================

    private CardLayout cardLayout;
    private JPanel screenPanel;

    private CreateAccountScreen createAccountScreen;

    private static final String LOGIN_SCREEN =
            "LOGIN";

    private static final String CREATE_ACCOUNT_SCREEN =
            "CREATE_ACCOUNT";

    // =====================================================
    // COLORS
    // =====================================================

    private static final Color BACKGROUND =
            new Color(15, 15, 15);

    private static final Color CARD =
            new Color(24, 24, 24);

    private static final Color FIELD =
            new Color(32, 32, 32);

    private static final Color BORDER =
            new Color(55, 55, 55);

    private static final Color TEXT =
            new Color(235, 235, 235);

    private static final Color SECONDARY_TEXT =
            new Color(145, 145, 145);

    private static final Color GREEN =
            new Color(0, 200, 120);

    private static final int FORM_WIDTH = 320;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public LoginScreen() {

        setTitle("Market Intelligence");

        setSize(460, 520);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setResizable(false);

        buildScreens();
    }

    // =====================================================
    // BUILD SCREENS
    // =====================================================

    private void buildScreens() {

        cardLayout =
                new CardLayout();

        screenPanel =
                new JPanel(cardLayout);

        screenPanel.setBackground(
                BACKGROUND
        );

        // Create login screen
        JPanel loginPanel =
                buildLoginScreen();

        // Create account screen
        createAccountScreen =
                new CreateAccountScreen(this);

        // Add both screens to CardLayout
        screenPanel.add(
                loginPanel,
                LOGIN_SCREEN
        );

        screenPanel.add(
                createAccountScreen,
                CREATE_ACCOUNT_SCREEN
        );

        setContentPane(
                screenPanel
        );

        showLoginScreen();
    }

    // =====================================================
    // BUILD LOGIN SCREEN
    // =====================================================

    private JPanel buildLoginScreen() {

        JPanel background =
                new JPanel(
                        new GridBagLayout()
                );

        background.setBackground(
                BACKGROUND
        );

        JPanel card =
                new JPanel();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS
                )
        );

        card.setBackground(
                CARD
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER,
                                1
                        ),
                        new EmptyBorder(
                                35,
                                40,
                                35,
                                40
                        )
                )
        );

        // =====================================================
        // LOGO
        // =====================================================

        JLabel logo =
                new JLabel("MI");

        logo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        logo.setForeground(
                GREEN
        );

        logo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        // =====================================================
        // TITLE
        // =====================================================

        JLabel title =
                new JLabel(
                        "Market Intelligence"
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        25
                )
        );

        title.setForeground(
                TEXT
        );

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        // =====================================================
        // SUBTITLE
        // =====================================================

        JLabel subtitle =
                new JLabel(
                        "Read. Predict. Act."
                );

        subtitle.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        subtitle.setForeground(
                SECONDARY_TEXT
        );

        subtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        // =====================================================
        // FORM
        // =====================================================

        JPanel formPanel =
                new JPanel();

        formPanel.setLayout(
                new BoxLayout(
                        formPanel,
                        BoxLayout.Y_AXIS
                )
        );

        formPanel.setBackground(
                CARD
        );

        formPanel.setPreferredSize(
                new Dimension(
                        FORM_WIDTH,
                        255
                )
        );

        formPanel.setMinimumSize(
                new Dimension(
                        FORM_WIDTH,
                        255
                )
        );

        formPanel.setMaximumSize(
                new Dimension(
                        FORM_WIDTH,
                        255
                )
        );

        formPanel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        // =====================================================
        // USERNAME
        // =====================================================

        JLabel usernameLabel =
                createLabel("USERNAME");

        usernameField =
                new JTextField();

        styleField(
                usernameField
        );

        // =====================================================
        // PASSWORD
        // =====================================================

        JLabel passwordLabel =
                createLabel("PASSWORD");

        passwordField =
                new JPasswordField();

        styleField(
                passwordField
        );

        // =====================================================
        // LOGIN BUTTON
        // =====================================================

        loginButton =
                new JButton("LOG IN");

        styleButton(
                loginButton
        );

        // =====================================================
        // CREATE ACCOUNT BUTTON
        // =====================================================

        JButton createButton =
                new JButton(
                        "CREATE ACCOUNT"
                );

        styleButton(
                createButton
        );

        // =====================================================
        // LOGIN ACTION
        // =====================================================

        loginButton.addActionListener(e -> {

            String username =
                    usernameField
                            .getText()
                            .trim();

            String password =
                    new String(
                            passwordField
                                    .getPassword()
                    );

            User user =
                    AppSession
                            .getUserManager()
                            .login(
                                    username,
                                    password
                            );

            if (user != null) {

                AppSession.login(user);

                System.out.println(
                        "Logged in user: " +
                        user.getUsername()
                );

                System.out.println(
                        "User ID: " +
                        user.getUserId()
                );

                System.out.println(
                        "Watchlist: " +
                        user.getWatchlist()
                );

                dispose();

                new HomeScreen(user)
                        .setVisible(true);

            } else {

                showStyledMessage(
                        "LOGIN FAILED",
                        "Incorrect username or password.",
                        false
                );
            }
        });

        // =====================================================
        // CREATE ACCOUNT ACTION
        // =====================================================

        createButton.addActionListener(e -> {

            showCreateAccountScreen();

        });

        // =====================================================
        // ADD FORM COMPONENTS
        // =====================================================

        formPanel.add(
                usernameLabel
        );

        formPanel.add(
                Box.createVerticalStrut(6)
        );

        formPanel.add(
                usernameField
        );

        formPanel.add(
                Box.createVerticalStrut(18)
        );

        formPanel.add(
                passwordLabel
        );

        formPanel.add(
                Box.createVerticalStrut(6)
        );

        formPanel.add(
                passwordField
        );

        formPanel.add(
                Box.createVerticalStrut(25)
        );

        formPanel.add(
                loginButton
        );

        formPanel.add(
                Box.createVerticalStrut(12)
        );

        formPanel.add(
                createButton
        );

        // =====================================================
        // BUILD CARD
        // =====================================================

        card.add(logo);

        card.add(
                Box.createVerticalStrut(10)
        );

        card.add(title);

        card.add(
                Box.createVerticalStrut(6)
        );

        card.add(subtitle);

        card.add(
                Box.createVerticalStrut(35)
        );

        card.add(formPanel);

        background.add(card);

        return background;
    }

    // =====================================================
    // SHOW CREATE ACCOUNT
    // =====================================================

    public void showCreateAccountScreen() {

        cardLayout.show(
                screenPanel,
                CREATE_ACCOUNT_SCREEN
        );

        getRootPane().setDefaultButton(
                createAccountScreen.getCreateButton()
        );

        revalidate();
        repaint();
    }

    // =====================================================
    // SHOW LOGIN
    // =====================================================

    public void showLoginScreen() {

        cardLayout.show(
                screenPanel,
                LOGIN_SCREEN
        );

        getRootPane().setDefaultButton(
                loginButton
        );

        revalidate();
        repaint();
    }

    // =====================================================
    // STYLED MESSAGE
    // =====================================================

    public void showStyledMessage(
            String title,
            String message,
            boolean success) {

        JDialog dialog =
                new JDialog(
                        this,
                        title,
                        true
                );

        dialog.setSize(
                360,
                190
        );

        dialog.setResizable(false);

        dialog.setLocationRelativeTo(this);

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBackground(
                CARD
        );

        panel.setBorder(
                new EmptyBorder(
                        25,
                        25,
                        20,
                        25
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        titleLabel.setForeground(
                success ? GREEN : TEXT
        );

        titleLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel messageLabel =
                new JLabel(
                        "<html><div style='text-align:center;'>"
                                + message +
                        "</div></html>"
                );

        messageLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        messageLabel.setForeground(
                SECONDARY_TEXT
        );

        messageLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JButton okayButton =
                new JButton("OK");

        styleButton(okayButton);

        okayButton.setMaximumSize(
                new Dimension(
                        140,
                        40
                )
        );

        okayButton.setPreferredSize(
                new Dimension(
                        140,
                        40
                )
        );

        okayButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        okayButton.addActionListener(e ->
                dialog.dispose()
        );

        panel.add(titleLabel);

        panel.add(
                Box.createVerticalStrut(15)
        );

        panel.add(messageLabel);

        panel.add(
                Box.createVerticalStrut(20)
        );

        panel.add(okayButton);

        dialog.setContentPane(panel);

        dialog.getRootPane().setDefaultButton(
                okayButton
        );

        dialog.setVisible(true);
    }

    // =====================================================
    // LABEL
    // =====================================================

    private JLabel createLabel(
            String text) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        11
                )
        );

        label.setForeground(
                SECONDARY_TEXT
        );

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        label.setMaximumSize(
                new Dimension(
                        FORM_WIDTH,
                        20
                )
        );

        return label;
    }

    // =====================================================
    // TEXT FIELD
    // =====================================================

    private void styleField(
            JTextField field) {

        field.setPreferredSize(
                new Dimension(
                        FORM_WIDTH,
                        42
                )
        );

        field.setMinimumSize(
                new Dimension(
                        FORM_WIDTH,
                        42
                )
        );

        field.setMaximumSize(
                new Dimension(
                        FORM_WIDTH,
                        42
                )
        );

        field.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        field.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        field.setForeground(
                TEXT
        );

        field.setBackground(
                FIELD
        );

        field.setCaretColor(
                GREEN
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER,
                                1
                        ),
                        new EmptyBorder(
                                0,
                                12,
                                0,
                                12
                        )
                )
        );
    }

    // =====================================================
    // BUTTON
    // =====================================================

    public void styleButton(
            JButton button) {

        button.setPreferredSize(
                new Dimension(
                        FORM_WIDTH,
                        44
                )
        );

        button.setMinimumSize(
                new Dimension(
                        FORM_WIDTH,
                        44
                )
        );

        button.setMaximumSize(
                new Dimension(
                        FORM_WIDTH,
                        44
                )
        );

        button.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(
                Color.WHITE
        );

        button.setBackground(
                CARD
        );

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createLineBorder(
                        BORDER,
                        1
                )
        );

        button.setOpaque(true);

        button.setContentAreaFilled(true);

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e) {

                        button.setForeground(
                                Color.BLACK
                        );

                        button.setBackground(
                                GREEN
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e) {

                        button.setForeground(
                                Color.WHITE
                        );

                        button.setBackground(
                                CARD
                        );
                    }
                }
        );
    }
}