
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateAccountScreen extends JPanel {

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton createButton;

    private final LoginScreen loginScreen;

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

    public CreateAccountScreen(LoginScreen loginScreen) {

        this.loginScreen = loginScreen;

        setLayout(new GridBagLayout());
        setBackground(BACKGROUND);

        buildScreen();
    }

    private void buildScreen() {

        JPanel card = new JPanel();

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        new EmptyBorder(30, 40, 30, 40)
                )
        );

        JLabel logo = new JLabel("MI");
        logo.setFont(new Font("Arial", Font.BOLD, 28));
        logo.setForeground(GREEN);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Join Market Intelligence.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY_TEXT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.setPreferredSize(
                new Dimension(FORM_WIDTH, 365));
        formPanel.setMinimumSize(
                new Dimension(FORM_WIDTH, 365));
        formPanel.setMaximumSize(
                new Dimension(FORM_WIDTH, 365));

        // USERNAME
        JLabel usernameLabel = createLabel("USERNAME");
        usernameField = new JTextField();
        styleField(usernameField);

        // EMAIL
        JLabel emailLabel = createLabel("EMAIL");
        emailField = new JTextField();
        styleField(emailField);

        // PASSWORD
        JLabel passwordLabel = createLabel("PASSWORD");
        passwordField = new JPasswordField();
        styleField(passwordField);

        // CREATE BUTTON
        createButton = new JButton("CREATE ACCOUNT");
        loginScreen.styleButton(createButton);

        // BACK BUTTON
        JButton backButton = new JButton("BACK TO LOGIN");
        loginScreen.styleButton(backButton);

        // SIGNUP ACTION
        createButton.addActionListener(e -> createOnlineAccount());

        // BACK ACTION
        backButton.addActionListener(e -> {
            clearFields();
            loginScreen.showLoginScreen();
        });

        // ADD USERNAME
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(usernameField);

        // ADD EMAIL
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(emailField);

        // ADD PASSWORD
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordField);

        // ADD BUTTONS
        formPanel.add(Box.createVerticalStrut(22));
        formPanel.add(createButton);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(backButton);

        // BUILD CARD
        card.add(logo);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(25));
        card.add(formPanel);

        add(card);
    }

    private void createOnlineAccount() {

        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()
                || email.isEmpty()
                || password.isEmpty()) {

            loginScreen.showStyledMessage(
                    "ACCOUNT ERROR",
                    "Please enter a username, email, and password.",
                    false
            );
            return;
        }

        // Prevent repeated clicks while the request is running.
        createButton.setEnabled(false);
        createButton.setText("CREATING ACCOUNT...");

        SwingWorker<SupabaseAuthService.SignupResult, Void> worker =
                new SwingWorker<>() {

            @Override
            protected SupabaseAuthService.SignupResult doInBackground() {
                return SupabaseAuthService.signUp(
                        email,
                        password,
                        username
                );
            }

            @Override
            protected void done() {

                createButton.setEnabled(true);
                createButton.setText("CREATE ACCOUNT");

                try {
                    SupabaseAuthService.SignupResult result = get();

                    if (result.isSuccessful()) {

                        clearFields();

                        loginScreen.showStyledMessage(
                                "CHECK YOUR EMAIL",
                                result.getMessage(),
                                true
                        );

                        loginScreen.showLoginScreen();

                    } else {

                        loginScreen.showStyledMessage(
                                "ACCOUNT ERROR",
                                result.getMessage(),
                                false
                        );
                    }

                } catch (Exception ex) {

                    loginScreen.showStyledMessage(
                            "ACCOUNT ERROR",
                            "Something went wrong. Please try again.",
                            false
                    );
                }
            }
        };

        worker.execute();
    }

    private void clearFields() {
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
    }

    public JButton getCreateButton() {
        return createButton;
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(SECONDARY_TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        label.setMaximumSize(
                new Dimension(FORM_WIDTH, 20));

        return label;
    }

    private void styleField(JTextField field) {

        field.setPreferredSize(
                new Dimension(FORM_WIDTH, 42));
        field.setMinimumSize(
                new Dimension(FORM_WIDTH, 42));
        field.setMaximumSize(
                new Dimension(FORM_WIDTH, 42));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setForeground(TEXT);
        field.setBackground(FIELD);
        field.setCaretColor(GREEN);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        new EmptyBorder(0, 12, 0, 12)
                )
        );
    }
}
