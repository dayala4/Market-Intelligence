
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Year;
import java.util.List;

public class TopInvestorsScreen extends JFrame {

    private final User user;

    private static final Color BG = new Color(18, 18, 18);
    private static final Color CARD = new Color(25, 25, 25);
    private static final Color BORDER = new Color(55, 55, 55);
    private static final Color TEXT = new Color(235, 235, 235);
    private static final Color MUTED = new Color(165, 165, 165);
    private static final Color GREEN = new Color(0, 200, 120);

    private JTextField searchField;
    private JComboBox<String> category;
    private JComboBox<Integer> yearSelector;
    private JButton searchButton;
    private JLabel statusLabel;
    private DefaultTableModel tableModel;

    public TopInvestorsScreen(User user) {
        this.user = user;

        setTitle("Market Intelligence - Top Investors");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel root = new JPanel(new BorderLayout(0, 24));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);

        JLabel title = new JLabel("TOP INVESTORS");
        title.setForeground(GREEN);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JButton backButton = new JButton("← Back to Dashboard");
        styleButton(backButton, false);

        backButton.addActionListener(e -> {
        dispose();

        HomeScreen homeScreen = new HomeScreen(user);
        homeScreen.setExtendedState(JFrame.MAXIMIZED_BOTH);
        homeScreen.setVisible(true);
});

        header.add(title, BorderLayout.WEST);
        header.add(backButton, BorderLayout.EAST);

        // Main content
        JPanel content = new JPanel();
        content.setBackground(BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Follow disclosed activity");
        heading.setForeground(TEXT);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel(
                "Search publicly reported politician transactions. More to be added soon."
        );
        description.setForeground(MUTED);
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Search field
        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        searchField.setBackground(new Color(32, 32, 32));
        searchField.setForeground(TEXT);
        searchField.setCaretColor(TEXT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        category = new JComboBox<>(new String[]{
                "Politicians",
                "Corporate Insiders"
        });
        category.setBackground(CARD);
        category.setForeground(TEXT);
        category.setFont(new Font("SansSerif", Font.PLAIN, 14));

        yearSelector = new JComboBox<>();
        int currentYear = Year.now().getValue();

        for (int year = currentYear; year >= currentYear - 5; year--) {
            yearSelector.addItem(year);
        }

        yearSelector.setBackground(CARD);
        yearSelector.setForeground(TEXT);
        yearSelector.setFont(new Font("SansSerif", Font.PLAIN, 14));

        searchButton = new JButton("Search");
        styleButton(searchButton, true);

        JPanel searchRow = new JPanel(new BorderLayout(12, 0));
        searchRow.setBackground(BG);
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setBackground(BG);
        filters.add(category);
        filters.add(yearSelector);

        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(filters, BorderLayout.WEST);
        searchRow.add(searchButton, BorderLayout.EAST);

        // Results area
        JPanel results = new JPanel(new BorderLayout(0, 14));
        results.setBackground(CARD);
        results.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        results.setAlignmentX(Component.LEFT_ALIGNMENT);
        results.setPreferredSize(new Dimension(1100, 420));
        results.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));

        JLabel resultHeading = new JLabel("TRANSACTION RESULTS");
        resultHeading.setForeground(TEXT);
        resultHeading.setFont(new Font("SansSerif", Font.BOLD, 16));

        statusLabel = new JLabel(
                "Search a politician by name to load available disclosures."
        );
        statusLabel.setForeground(MUTED);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel resultTop = new JPanel();
        resultTop.setBackground(CARD);
        resultTop.setLayout(new BoxLayout(resultTop, BoxLayout.Y_AXIS));
        resultTop.add(resultHeading);
        resultTop.add(Box.createVerticalStrut(8));
        resultTop.add(statusLabel);

        String[] columns = {
                "Politician",
                "Ticker",
                "Company",
                "Action",
                "Transaction Date",
                "Filing Date",
                "Reported Amount"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(40, 70, 60));
        table.setSelectionForeground(TEXT);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getTableHeader().setBackground(new Color(35, 35, 35));
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        table.getTableHeader().setReorderingAllowed(false);

        int[] widths = {150, 80, 220, 100, 130, 130, 150};

        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.getViewport().setBackground(CARD);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER));

        results.add(resultTop, BorderLayout.NORTH);
        results.add(tableScroll, BorderLayout.CENTER);

        // Suggested searches
        JPanel suggestions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        suggestions.setBackground(BG);
        suggestions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel suggestedLabel = new JLabel("Try:");
        suggestedLabel.setForeground(MUTED);
        suggestions.add(suggestedLabel);

        String[] names = {
                "Nancy Pelosi",
        };

        for (String name : names) {
            JButton suggestion = new JButton(name);
            styleButton(suggestion, false);

            suggestion.addActionListener(e -> {
                searchField.setText(name);
                category.setSelectedItem("Politicians");
                searchField.requestFocusInWindow();
            });

            suggestions.add(suggestion);
        }

        // Search behavior
        searchButton.addActionListener(e -> searchTransactions());
        searchField.addActionListener(e -> searchTransactions());

        category.addActionListener(e -> {
            boolean politicianSearch =
                    "Politicians".equals(category.getSelectedItem());

            yearSelector.setEnabled(politicianSearch);

            if (!politicianSearch) {
                statusLabel.setText(
                        "Corporate insider search is not connected yet."
                );
            } else {
                statusLabel.setText(
                        "Search a politician by name to load disclosures."
                );
            }
        });

        content.add(heading);
        content.add(Box.createVerticalStrut(8));
        content.add(description);
        content.add(Box.createVerticalStrut(20));
        content.add(searchRow);
        content.add(Box.createVerticalStrut(12));
        content.add(suggestions);
        content.add(Box.createVerticalStrut(18));
        content.add(results);

        root.add(header, BorderLayout.NORTH);
        root.add(new JScrollPane(content), BorderLayout.CENTER);

        setContentPane(root);
    }

    
private void searchTransactions() {
    String query = searchField.getText().trim();

    if (query.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Enter a politician's name.",
                "Search",
                JOptionPane.INFORMATION_MESSAGE
        );
        return;
    }

    if (!"Politicians".equals(category.getSelectedItem())) {
        statusLabel.setText(
                "Corporate insider search is not connected yet."
        );
        return;
    }

    String[] nameParts = query.split("\\s+");
    String lastName = nameParts[nameParts.length - 1];
    int filingYear = (Integer) yearSelector.getSelectedItem();

    tableModel.setRowCount(0);
    searchButton.setEnabled(false);
    searchButton.setText("Searching...");
    statusLabel.setText(
            "Searching House disclosures for "
                    + query + " (" + filingYear + ")..."
    );

    SwingWorker<List<PoliticianTransaction>, Void> worker =
            new SwingWorker<>() {

        @Override
        protected List<PoliticianTransaction> doInBackground() {
            PoliticianDataService service =
                    new PoliticianDataService();

            return service.getRecentTransactions(
                    lastName,
                    filingYear
            );
        }

        @Override
        protected void done() {
            searchButton.setEnabled(true);
            searchButton.setText("Search");

            try {
                List<PoliticianTransaction> transactions = get();

                tableModel.setRowCount(0);

                for (PoliticianTransaction transaction : transactions) {
                    tableModel.addRow(new Object[]{
                            transaction.getName(),
                            transaction.getTicker(),
                            transaction.getCompany(),
                            transaction.getAction(),
                            transaction.getTransactionDate(),
                            transaction.getFilingDate(),
                            transaction.getAmountRange()
                    });
                }

                if (transactions.isEmpty()) {
                    statusLabel.setText(
                            "No transactions were parsed. The filing "
                            + "may be unavailable or the PDF format "
                            + "may not match the current parser."
                    );
                } else {
                    statusLabel.setText(
                            "Loaded " + transactions.size()
                            + " transaction(s). Verify filing details "
                            + "before drawing conclusions."
                    );
                }

            } catch (Exception ex) {
                Throwable cause = ex.getCause();

                statusLabel.setText(
                        "Search failed: "
                                + (cause != null
                                ? cause.getMessage()
                                : ex.getMessage())
                );

                ex.printStackTrace();
            }
        }
    };

    worker.execute();
}


    private void styleButton(JButton button, boolean primary) {
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(primary ? BG : TEXT);
        button.setBackground(primary ? GREEN : CARD);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary ? GREEN : BORDER),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
    }
}
