import java.awt.geom.Path2D;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;

public class HomeScreen extends JFrame {

    private StockService service;
    private User user;

    private JPanel rightPanel;
    private JPanel watchlistPanel;

    private JPanel topPanel;
    private JPanel centeredSearch;
    private JPanel searchBox;

    private boolean settingsOpen = false;

    private String currentSymbol = "GOOGL";

    private JTextField searchField;
    private JPopupMenu searchPopup;
    private JPopupMenu userMenu;
    private DefaultListModel<StockSearchResult> searchModel;
    private JList<StockSearchResult> searchResults;
    private Timer searchTimer;
    private boolean searchPopupSizeLocked = false;
    private boolean lightMode = false;

    private static final int SEARCH_WIDTH = 300;
    private static final int SEARCH_HEIGHT = 34;
    private static final int SEARCH_MIN_POPUP_WIDTH = 420;
    private static final int SEARCH_MAX_POPUP_WIDTH = 700;
    private static final int SEARCH_DEFAULT_POPUP_HEIGHT = 80;
    private static final int SEARCH_MAX_POPUP_HEIGHT = 300;

    private static final Color DARK_BG = new Color(15, 15, 15);
private static final Color DARK_PANEL = new Color(24, 24, 24);
private static final Color DARK_CARD = new Color(32, 32, 32);
private static final Color DARK_BORDER = new Color(55, 55, 55);
private static final Color TEXT = new Color(235, 235, 235);
private static final Color SECONDARY_TEXT = new Color(145, 145, 145);
private static final Color ACCENT_GREEN =
        new Color(0, 200, 120);

private static final Color LIGHT_MODE_GREEN =
        new Color(0, 130, 85);
private static final Color GREEN = new Color(0, 200, 120);

private static final Color LIGHT_BG = new Color(245, 246, 248);
private static final Color LIGHT_PANEL = Color.WHITE;
private static final Color LIGHT_CARD = new Color(238, 240, 243);
private static final Color LIGHT_BORDER = new Color(210, 214, 220);
private static final Color LIGHT_TEXT = new Color(30, 33, 38);
private static final Color LIGHT_SECONDARY_TEXT =
        new Color(95, 101, 112);
    

    public HomeScreen(User user) {
        this.user = user;
        this.service = new StockService();

        setTitle("Market Intelligence");
        setSize(1200, 750);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(DARK_BG);

        JLabel title = new JLabel("MARKET INTELLIGENCE");
        title.setForeground(GREEN);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        
        JPanel searchContainer = new JPanel(new GridBagLayout());
        searchContainer.setBackground(DARK_BG);


        searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(DARK_CARD);
        searchBox.setPreferredSize(
                new Dimension(SEARCH_WIDTH, SEARCH_HEIGHT));
        searchBox.setMinimumSize(
                new Dimension(SEARCH_WIDTH, SEARCH_HEIGHT));
        searchBox.setMaximumSize(
                new Dimension(SEARCH_WIDTH, SEARCH_HEIGHT));

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 15));
        searchField.setBackground(DARK_CARD);
        searchField.setForeground(TEXT);
        searchField.setCaretColor(TEXT);
        searchField.setToolTipText(
                "Search for a company or ticker symbol");
        searchField.setPreferredSize(
                new Dimension(SEARCH_WIDTH, SEARCH_HEIGHT));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DARK_BORDER),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        searchField.getCaret().setVisible(false);

       
        searchField.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
        searchField.getCaret().setVisible(true);
        searchField.getCaret().setBlinkRate(500);
    }

    @Override
    public void focusLost(FocusEvent e) {
        searchField.getCaret().setVisible(false);
    }
});


        searchBox.add(searchField, BorderLayout.CENTER);

       JLabel searchIcon = new JLabel(
        new MIIcon("search", currentAccent(), 18)
);

searchIcon.setForeground(currentAccent());
        searchIcon.setBorder(
        BorderFactory.createEmptyBorder(0, 8, 0, 5));
        searchBox.add(searchIcon, BorderLayout.WEST);

        searchContainer.add(searchBox);
        searchContainer.add(Box.createHorizontalGlue());

        searchModel = new DefaultListModel<>();
        searchResults = new JList<>(searchModel);
        searchResults.setFont(new Font("Arial", Font.PLAIN, 14));
        searchResults.setBackground(DARK_PANEL);
        searchResults.setForeground(TEXT);
        searchResults.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        searchResults.setFixedCellHeight(42);

        // The result list is mouse-selectable but does not take
        // keyboard focus away from the search field.
        searchResults.setFocusable(false);

        searchResults.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean selected,
                    boolean focused) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, focused);

                if (value instanceof StockSearchResult stock) {
                    label.setText(
                            stock.getSymbol() + "  —  "
                            + stock.getName() + "  ("
                            + stock.getExchange() + ")");
                }

                label.setForeground(currentText());
                label.setBackground(selected
                        ? (lightMode
                                ? new Color(220, 225, 232)
                                : new Color(55, 55, 55))
                        : currentPanel());

                label.setBorder(BorderFactory.createEmptyBorder(
                        8, 10, 8, 10));

                return label;
            }
        });

        JScrollPane searchScroll = new JScrollPane(searchResults);
        searchScroll.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
        searchScroll.setBackground(DARK_PANEL);
        searchScroll.getViewport().setBackground(DARK_PANEL);
        searchScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel searchPopupPanel = new JPanel(new BorderLayout());
        searchPopupPanel.setBackground(DARK_PANEL);
        searchPopupPanel.setBorder(
                BorderFactory.createLineBorder(DARK_BORDER));
        searchPopupPanel.add(searchScroll, BorderLayout.CENTER);

        JPanel resizeHandle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(120, 120, 120));
                g2.drawLine(7, 14, 14, 7);
                g2.drawLine(11, 14, 14, 11);
                g2.drawLine(14, 14, 14, 14);
                g2.dispose();
            }
        };

        resizeHandle.setPreferredSize(new Dimension(18, 18));
        resizeHandle.setOpaque(false);
        resizeHandle.setCursor(
                Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        resizeHandle.setToolTipText("Drag to resize search results");
        searchPopupPanel.add(resizeHandle, BorderLayout.SOUTH);

        searchPopup = new JPopupMenu();
        searchPopup.setBackground(DARK_PANEL);
        searchPopup.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
        searchPopup.setLayout(new BorderLayout());

        // Prevent the popup itself from requesting keyboard focus.
        searchPopup.setFocusable(false);

        searchPopup.add(searchPopupPanel, BorderLayout.CENTER);
        searchPopup.setPopupSize(
                SEARCH_MIN_POPUP_WIDTH,
                SEARCH_DEFAULT_POPUP_HEIGHT);

        final Point[] resizeStart = new Point[1];
        final Dimension[] resizeStartSize = new Dimension[1];

        resizeHandle.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                resizeStart[0] = e.getLocationOnScreen();
                resizeStartSize[0] = searchPopup.getSize();
                searchPopupSizeLocked = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                resizeStart[0] = null;
                resizeStartSize[0] = null;
            }
        });

        resizeHandle.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (resizeStart[0] == null
                        || resizeStartSize[0] == null) {
                    return;
                }

                Point current = e.getLocationOnScreen();

                int width = resizeStartSize[0].width
                        + current.x - resizeStart[0].x;

                int height = resizeStartSize[0].height
                        + current.y - resizeStart[0].y;

                width = Math.max(
                        SEARCH_MIN_POPUP_WIDTH,
                        Math.min(SEARCH_MAX_POPUP_WIDTH, width));

                height = Math.max(
                        SEARCH_DEFAULT_POPUP_HEIGHT,
                        Math.min(SEARCH_MAX_POPUP_HEIGHT, height));

                searchPopup.setPopupSize(width, height);
                searchPopup.revalidate();
                searchPopup.repaint();
            }
        });

        searchTimer = new Timer(400, e -> performSearch());
        searchTimer.setRepeats(false);

        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    private void restartSearch() {
                        searchTimer.restart();
                    }

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        restartSearch();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        restartSearch();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        restartSearch();
                    }
                });

        // Keep keyboard focus in the text field while moving the
        // highlighted result with the Up and Down arrow keys.
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (!searchModel.isEmpty()) {
                        int selected = searchResults.getSelectedIndex();

                        if (selected < 0
                                || selected >= searchModel.size() - 1) {
                            selected = 0;
                        } else {
                            selected++;
                        }

                        searchResults.setSelectedIndex(selected);
                        searchResults.ensureIndexIsVisible(selected);
                    }

                    e.consume();

                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (!searchModel.isEmpty()) {
                        int selected = searchResults.getSelectedIndex();

                        if (selected <= 0) {
                            selected = searchModel.size() - 1;
                        } else {
                            selected--;
                        }

                        searchResults.setSelectedIndex(selected);
                        searchResults.ensureIndexIsVisible(selected);
                    }

                    e.consume();

                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hideSearchResults();
                    e.consume();

                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    StockSearchResult selected =
                            searchResults.getSelectedValue();

                    if (selected == null && !searchModel.isEmpty()) {
                        selected = searchModel.getElementAt(0);
                    }

                    openSelectedStock(selected);
                    e.consume();
                }
            }
        });

        // Keep the existing result-list keyboard handling as a fallback.
        searchResults.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    openSelectedStock(searchResults.getSelectedValue());
                }

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hideSearchResults();
                    searchField.requestFocusInWindow();
                }
            }
        });

        searchResults.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = searchResults.locationToIndex(e.getPoint());

                    if (index >= 0) {
                        Rectangle bounds =
                                searchResults.getCellBounds(index, index);

                        if (bounds != null && bounds.contains(e.getPoint())) {
                            searchResults.setSelectedIndex(index);
                            openSelectedStock(
                                    searchResults.getSelectedValue());
                        }
                    }
                }
            }
        });

        JButton topInvestorsButton = new JButton("TOP INVESTORS");
       styleButton(topInvestorsButton);
topInvestorsButton.setForeground(currentAccent());

        topInvestorsButton.setFont(new Font("Arial", Font.BOLD, 13));
        topInvestorsButton.setPreferredSize(new Dimension(145, 42));
        topInvestorsButton.setMinimumSize(new Dimension(145, 42));
        topInvestorsButton.setMaximumSize(new Dimension(145, 42));
        topInvestorsButton.setMargin(new Insets(0, 10, 0, 10));
        topInvestorsButton.setToolTipText("View investor activity");
        topInvestorsButton.setCursor(
        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        topInvestorsButton.addActionListener(e -> {
        dispose();
        new TopInvestorsScreen(user).setVisible(true);
});

     
JButton userButton = new JButton();
styleButton(userButton);
userButton.setIcon(new MIIcon("user", currentAccent(), 20));

userButton.setPreferredSize(new Dimension(50, 42));
userButton.setMinimumSize(new Dimension(50, 42));
userButton.setMaximumSize(new Dimension(50, 42));

userButton.setToolTipText(user.getUsername());
userButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

userMenu = new JPopupMenu();
userMenu.setBackground(DARK_CARD);
userMenu.setBorder(BorderFactory.createLineBorder(DARK_BORDER));

JMenuItem settingsItem = new JMenuItem("Settings");
JMenuItem financialTermsItem = new JMenuItem("Financial Terms");
JMenuItem aboutItem = new JMenuItem("About");
JMenuItem logoutItem = new JMenuItem("Log Out");

styleMenuItem(settingsItem);
styleMenuItem(financialTermsItem);
styleMenuItem(aboutItem);
styleMenuItem(logoutItem);

settingsItem.setIcon(new MIIcon("settings", currentAccent(), 18));
financialTermsItem.setIcon(new MIIcon("book-open", currentAccent(), 18));
aboutItem.setIcon(new MIIcon("info", currentAccent(), 18));
logoutItem.setIcon(new MIIcon("log-out", currentAccent(), 18));

settingsItem.setIconTextGap(12);
financialTermsItem.setIconTextGap(12);
aboutItem.setIconTextGap(12);
logoutItem.setIconTextGap(12);

userMenu.add(settingsItem);
userMenu.add(financialTermsItem);
userMenu.add(aboutItem);
userMenu.addSeparator();
userMenu.add(logoutItem);

// Set the width AFTER adding all menu items.
Dimension menuSize = userMenu.getPreferredSize();


settingsItem.addActionListener(e -> showSettingsScreen());
financialTermsItem.addActionListener(e -> showFinancialTermsScreen());
aboutItem.addActionListener(e -> showAboutScreen());

logoutItem.addActionListener(e -> {
    AppSession.logout();
    dispose();
    new LoginScreen().setVisible(true);
});


userButton.addActionListener(e -> {
    // Align the dropdown's left edge with TOP INVESTORS.
    int menuX = topInvestorsButton.getX() - userButton.getX();

    // Align its right edge with the user/profile button.
    int menuWidth = userButton.getX()
            + userButton.getWidth()
            - topInvestorsButton.getX();

    // Preserve the menu's natural height and existing items.
    int menuHeight = userMenu.getPreferredSize().height;

    userMenu.setPopupSize(menuWidth, menuHeight);

    // Display directly below the header's profile button.
    userMenu.show(userButton, menuX, userButton.getHeight());
});




settingsItem.addActionListener(e -> showSettingsScreen());

financialTermsItem.addActionListener(
        e -> showFinancialTermsScreen());

aboutItem.addActionListener(e -> showAboutScreen());


        logoutItem.addActionListener(e -> {
            AppSession.logout();
            dispose();
            new LoginScreen().setVisible(true);
        });

        JPanel topRight = new JPanel(
        new FlowLayout(FlowLayout.RIGHT, 8, 0));

        topRight.setBackground(DARK_BG);
        topRight.setAlignmentY(Component.CENTER_ALIGNMENT);

        topInvestorsButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        userButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        topRight.add(topInvestorsButton);
        topRight.add(userButton);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(topRight, BorderLayout.EAST);

    centeredSearch = new JPanel(new GridBagLayout());
    centeredSearch.setOpaque(false);

    topPanel.add(centeredSearch, BorderLayout.CENTER);

    updateSearchPosition();

        watchlistPanel = new JPanel();
        watchlistPanel.setLayout(
                new BoxLayout(watchlistPanel, BoxLayout.Y_AXIS));
        watchlistPanel.setBackground(DARK_PANEL);
        watchlistPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loadWatchlist();

        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(DARK_BG);

        currentSymbol = user.getWatchlist().isEmpty()
                ? "GOOGL" : user.getWatchlist().getFirst();

        
JScrollPane watchlistScroll = new JScrollPane(watchlistPanel);
        watchlistScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        watchlistScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        watchlistScroll.getVerticalScrollBar().setUnitIncrement(32);
        styleUniversalScrollBar(watchlistScroll);
        watchlistScroll.setBackground(DARK_PANEL);
        watchlistScroll.getViewport().setBackground(DARK_PANEL);
        watchlistScroll.setBorder(
                BorderFactory.createLineBorder(DARK_BORDER));


        JSplitPane splitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        watchlistScroll,
        rightPanel);

splitPane.setDividerLocation(230);
splitPane.setResizeWeight(0.0);
splitPane.setDividerSize(0);
splitPane.setBorder(null);
splitPane.setBackground(DARK_BG);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);
        getContentPane().setBackground(DARK_BG);

        showStock(currentSymbol);
        applyTheme();
    }

    private Color currentBackground() {
        return lightMode ? LIGHT_BG : DARK_BG;
    }

    private Color currentPanel() {
        return lightMode ? LIGHT_PANEL : DARK_PANEL;
    }

    private Color currentCard() {
        return lightMode ? LIGHT_CARD : DARK_CARD;
    }

    private Color currentBorder() {
        return lightMode ? LIGHT_BORDER : DARK_BORDER;
    }

    private Color currentText() {
        return lightMode ? LIGHT_TEXT : TEXT;
    }

    private Color currentSecondaryText() {
        return lightMode ? LIGHT_SECONDARY_TEXT : SECONDARY_TEXT;
    }

    // Recolors components already built by the existing dashboard.
    private Color themedColor(Color color) {
        if (color == null) {
            return null;
        }
    
        if (color.equals(DARK_BG) || color.equals(LIGHT_BG)) {
            return currentBackground();
        }
    
        if (color.equals(DARK_PANEL) || color.equals(LIGHT_PANEL)) {
            return currentPanel();
        }
    
        if (color.equals(DARK_CARD) || color.equals(LIGHT_CARD)) {
            return currentCard();
        }
    
        if (color.equals(DARK_BORDER) || color.equals(LIGHT_BORDER)) {
            return currentBorder();
        }
    
        if (color.equals(TEXT) || color.equals(LIGHT_TEXT)) {
            return currentText();
        }
    
        if (color.equals(SECONDARY_TEXT)
                || color.equals(LIGHT_SECONDARY_TEXT)) {
            return currentSecondaryText();
        }
    
        if (color.equals(new Color(35, 35, 35))) {
            return lightMode
                    ? LIGHT_CARD
                    : new Color(35, 35, 35);
        }
    
        if (color.equals(new Color(70, 70, 70))) {
            return lightMode
                    ? LIGHT_BORDER
                    : new Color(70, 70, 70);
        }
    
        return color;
    }

    private void applyTheme() {
        Color background = currentBackground();

        if (searchBox != null) {
    for (Component component : searchBox.getComponents()) {
        if (component instanceof JLabel label
                && label.getIcon() instanceof MIIcon) {
            label.setForeground(currentAccent());
            label.setIcon(new MIIcon("search", currentAccent(), 18));
        }
    }
}
        getContentPane().setBackground(background);
    
        if (searchPopup != null) {
            searchPopup.setBackground(currentPanel());
            searchPopup.setBorder(
                    BorderFactory.createLineBorder(currentBorder()));
            applyThemeToComponent(searchPopup);
        }
    
        if (userMenu != null) {
            userMenu.setBackground(currentCard());
            userMenu.setBorder(
                    BorderFactory.createLineBorder(currentBorder()));
            applyThemeToComponent(userMenu);
        }
    
        applyThemeToComponent(getContentPane());
    
        revalidate();
        repaint();
    }

    private void applyThemeToComponent(Component component) {
        if (component == null) return;

        if (component instanceof JComponent jc) {
            Color background = jc.getBackground();
            Color foreground = jc.getForeground();

            if (background != null) {
                jc.setBackground(themedColor(background));
            }
            if (foreground != null) {
                jc.setForeground(themedColor(foreground));
            }

            Border border = jc.getBorder();

            if (border instanceof LineBorder lineBorder) {
                jc.setBorder(BorderFactory.createLineBorder(
                        themedColor(lineBorder.getLineColor()),
                        lineBorder.getThickness(),
                        lineBorder.getRoundedCorners()));

            } else if (border instanceof CompoundBorder compound) {
                jc.setBorder(BorderFactory.createCompoundBorder(
                        themeBorder(compound.getOutsideBorder()),
                        themeBorder(compound.getInsideBorder())));
            }
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyThemeToComponent(child);
            }
        }
    }

    private Border themeBorder(Border border) {
        if (border instanceof LineBorder lineBorder) {
            return BorderFactory.createLineBorder(
                    themedColor(lineBorder.getLineColor()),
                    lineBorder.getThickness(),
                    lineBorder.getRoundedCorners());
        }

        return border;
    }

    private JPanel createPageHeader(
        String pageTitle,
        String subtitle) {

    JPanel header = new JPanel(null);
    header.setBackground(currentBackground());
    header.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 20, 10));
    header.setPreferredSize(new Dimension(0, 115));

    JButton backButton = new JButton("←  Back to Stock");
    styleButton(backButton);
    backButton.setFont(new Font("Arial", Font.BOLD, 13));
    backButton.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    backButton.setPreferredSize(new Dimension(150, 38));
    backButton.addActionListener(e -> returnToCurrentStock());

    JLabel title = new JLabel(pageTitle, SwingConstants.CENTER);
    title.setForeground(currentText());
    title.setFont(new Font("SansSerif", Font.BOLD, 27));

    JLabel description = new JLabel(subtitle, SwingConstants.CENTER);
    description.setForeground(currentSecondaryText());
    description.setFont(new Font("SansSerif", Font.PLAIN, 14));

    header.add(backButton);
    header.add(title);
    header.add(description);

    header.addComponentListener(new java.awt.event.ComponentAdapter() {
        @Override
        public void componentResized(java.awt.event.ComponentEvent e) {
            int width = header.getWidth();
            int height = header.getHeight();

            backButton.setBounds(0, 0, 150, 38);

            int titleWidth = Math.min(700, width - 40);

// Match the horizontal alignment used by Financial Terms.
int titleX = (width - titleWidth) / 2 - 85;

title.setBounds(titleX, 5, titleWidth, 35);
description.setBounds(titleX, 43, titleWidth, 25);
        }
    });

    return header;
}



private void updateSearchPosition() {
    centeredSearch.removeAll();

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;

    // Keep the search bar centered in the full window.
    gbc.weightx = 1.0;

    centeredSearch.add(searchBox, gbc);

    centeredSearch.revalidate();
    centeredSearch.repaint();
}



    private void returnToCurrentStock() {
        showStock(currentSymbol);
    }

    // SETTINGS
    private void showSettingsScreen() {


        settingsOpen = true;
        updateSearchPosition();

        rightPanel.removeAll();
        rightPanel.setLayout(new BorderLayout());

        JPanel page = new JPanel(new BorderLayout(0, 10));
        page.setBackground(currentBackground());
        page.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        page.add(createPageHeader(
                "Settings",
                "Manage your Market Intelligence experience"),
                BorderLayout.NORTH);
               

        JPanel settingsContent = new JPanel();
        settingsContent.setBackground(currentBackground());
        settingsContent.setLayout(
                new BoxLayout(settingsContent, BoxLayout.Y_AXIS));

        JPanel accountCard = createAnalysisCard("ACCOUNT");
        accountCard.add(createLargeLabel(
                "Username: " + user.getUsername()));
        accountCard.add(createLargeLabel(
                "Account: Local application account"));
        accountCard.add(createLargeLabel(
                "Your account is managed by the application's local user system."));
        accountCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel appearanceCard = createAnalysisCard("APPEARANCE");

        JLabel themeLabel = createLargeLabel(
                "Theme: " + (lightMode ? "Light" : "Dark"));

        JToggleButton themeSwitch = new JToggleButton(
                lightMode ? "☀  Light Mode" : "☾  Dark Mode",
                lightMode);

        themeSwitch.setFont(new Font("Arial", Font.BOLD, 14));
        themeSwitch.setFocusPainted(false);
        themeSwitch.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeSwitch.setPreferredSize(new Dimension(180, 40));
        themeSwitch.setMaximumSize(new Dimension(220, 40));
        styleButton((AbstractButton) themeSwitch);
        themeSwitch.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel themeDescription = createLargeLabel(
                "Choose the appearance you prefer. Your dashboard and pages update immediately.");

                themeSwitch.addActionListener(e -> {
                        lightMode = themeSwitch.isSelected();
                    
                        // Rebuild Settings using the newly selected palette.
                        showSettingsScreen();
                    
                        // Reapply colors to the visible window and its components.
                        applyTheme();
                    });

        appearanceCard.add(themeLabel);
        appearanceCard.add(Box.createVerticalStrut(8));
        appearanceCard.add(themeSwitch);
        appearanceCard.add(Box.createVerticalStrut(8));
        appearanceCard.add(themeDescription);
        appearanceCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel marketCard = createAnalysisCard("MARKET DATA");
        marketCard.add(createLargeLabel(
                "Stock search and analysis use the configured data services."));
        marketCard.add(createLargeLabel(
                "Availability and completeness of financial metrics depend on the data provider."));
        marketCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel privacyCard = createAnalysisCard("DATA AND PRIVACY");
        privacyCard.add(createLargeLabel(
                "User data is saved through the application's local user-storage implementation."));
        privacyCard.add(createLargeLabel(
                "API keys should be kept private and should not be committed to a public repository."));
        privacyCard.add(createLargeLabel(
                "This page displays information; it does not change storage or API settings."));
       

       



settingsContent.add(accountCard);
settingsContent.add(Box.createVerticalStrut(14));
settingsContent.add(appearanceCard);
settingsContent.add(Box.createVerticalStrut(14));
settingsContent.add(marketCard);
settingsContent.add(Box.createVerticalStrut(14));
settingsContent.add(privacyCard);


JScrollPane scrollPane = new JScrollPane(settingsContent);
stylePageScrollPane(scrollPane);
scrollPane.getViewport().setBackground(currentBackground());

page.add(scrollPane, BorderLayout.CENTER);

rightPanel.add(page, BorderLayout.CENTER);

applyTheme();
rightPanel.revalidate();
rightPanel.repaint();
}

    

    // ABOUT
    private void showAboutScreen() {

        settingsOpen = false;
        updateSearchPosition();

        rightPanel.removeAll();
        rightPanel.setLayout(new BorderLayout());

        JPanel page = new JPanel(new BorderLayout(0, 10));
        page.setBackground(currentBackground());
        page.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        page.add(createPageHeader(
                "About Market Intelligence",
                "Read. Predict. Act."),
                BorderLayout.NORTH);

        JTextPane storyPane = new JTextPane();
        storyPane.setEditable(false);
        storyPane.setBackground(currentCard());
        storyPane.setForeground(currentText());
        storyPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
        storyPane.setMargin(new Insets(24, 26, 24, 26));

        StyledDocument doc = storyPane.getStyledDocument();

        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setFontFamily(normal, "SansSerif");
        StyleConstants.setFontSize(normal, 14);
        StyleConstants.setForeground(normal, currentText());
        StyleConstants.setSpaceAbove(normal, 2);
        StyleConstants.setSpaceBelow(normal, 10);

        SimpleAttributeSet heading1 = new SimpleAttributeSet();
        StyleConstants.setFontFamily(heading1, "SansSerif");
        StyleConstants.setFontSize(heading1, 23);
        StyleConstants.setBold(heading1, true);
        StyleConstants.setForeground(heading1, GREEN);
        StyleConstants.setSpaceAbove(heading1, 8);
        StyleConstants.setSpaceBelow(heading1, 14);

        SimpleAttributeSet heading2 = new SimpleAttributeSet();
        StyleConstants.setFontFamily(heading2, "SansSerif");
        StyleConstants.setFontSize(heading2, 18);
        StyleConstants.setBold(heading2, true);
        StyleConstants.setForeground(heading2, currentText());
        StyleConstants.setSpaceAbove(heading2, 22);
        StyleConstants.setSpaceBelow(heading2, 10);

        SimpleAttributeSet bold = new SimpleAttributeSet(normal);
        StyleConstants.setBold(bold, true);

        SimpleAttributeSet italic = new SimpleAttributeSet(normal);
        StyleConstants.setItalic(italic, true);
        StyleConstants.setForeground(italic, currentSecondaryText());

        SimpleAttributeSet mission = new SimpleAttributeSet(normal);
        StyleConstants.setBold(mission, true);
        StyleConstants.setForeground(mission, GREEN);
        StyleConstants.setFontSize(mission, 15);
        StyleConstants.setSpaceAbove(mission, 8);
        StyleConstants.setSpaceBelow(mission, 12);

        String story = """
                # Why Market Intelligence Exists

                ### Read. Predict. Act.

                I started building Market Intelligence for a personal reason: the cost of living keeps rising, and I wanted to find ways to build financial independence while I was still young.

                At 22, I've spent my entire adult life learning about the financial markets. I've invested in courses, studied different strategies, researched companies, and put what I've learned into practice. Through my own investing, I've been fortunate enough to outperform the S&P 500, strengthening my belief in disciplined research, understanding the numbers, and making decisions based on evidence rather than noise.

                One of my biggest frustrations was that I couldn't easily find all the information I cared about in one place.

                Markets can react dramatically to world events, economic uncertainty, and frightening headlines. Sometimes fear spreads faster than understanding, and investors feel pressured to sell before taking the time to examine what's actually changed. I wanted a tool that would help me step back, study the data, understand the metrics, and form my own conclusions rather than simply follow what everyone else was saying.

                That's why I began building Market Intelligence.

                ## More Than a Stock Tracker

                My goal is to create a tool I can continue using throughout my life—one that makes it easier to explore companies across the market, study historical performance, examine financial metrics, and understand what's happening beneath the surface.

                Market Intelligence brings together market data, company fundamentals, historical trends, and an experimental prediction model that uses selected metrics to estimate potential market direction. I also want users to be able to reference reported activity from politicians and other investors as another source of information when researching companies.

                These features aren't meant to predict the future with certainty or tell people what they should buy. They're designed to provide more context, making it easier to evaluate a company and understand the information behind a potential investment.

                **History matters. Metrics matter. Facts matter.**

                None of them guarantees what happens next, but together they can help us ask better questions, recognize risks, and make more informed decisions.

                ## Facts Over Fear

                One of my biggest goals is to help people avoid making financial decisions purely out of panic.

                When the market drops, headlines can create the impression that everything is falling apart. Sometimes the risks are real and deserve serious attention. Other times, the reaction may be driven by uncertainty, emotion, or short-term expectations.

                I believe it's important to examine the facts before reacting. Looking at a company's fundamentals, historical performance, and relevant market conditions can help put a sudden drop into perspective.

                Market Intelligence is designed to support that process—not by pretending every downturn is an opportunity, but by giving people more information to consider before making a decision.

                The goal is to encourage research over reaction, discipline over panic, and evidence over hype.

                ## Built for Beginners and Experienced Investors

                I want Market Intelligence to be useful to people at different stages of their investing journey.

                You shouldn't have to jump between countless websites just to start researching a company you care about. My vision is to make market research more accessible, organized, and understandable, whether you're exploring your first stock or looking for additional information to support an established strategy.

                By bringing different sources of information together, I want users to spend less time searching and more time understanding what the data actually means.

                ## Why Share It?

                Building Market Intelligence started as a way to improve my own investing process and create a tool I could rely on for years to come. But I also wanted to make something that could benefit other people.

                If learning about the market has helped me work toward my financial goals, I want others to have the opportunity to research, learn, and make better-informed decisions for themselves.

                It wouldn't feel right to build something useful solely for my own benefit when I could share it with others. I want beginners to have a place to start, experienced investors to have another resource to reference, and everyone to have the freedom to examine the information and reach their own conclusions.

                Market Intelligence will never tell you what you must buy or sell. It is a research tool—not a guarantee of returns.

                ## The Mission

                **Make market research more accessible. Put facts before hype. Help people understand the market before fear makes the decision for them.**

                I built Market Intelligence to strengthen my own investing process, but its purpose extends beyond me. I want it to become a tool people can use throughout their lives to explore companies, evaluate information, and work toward their own financial goals.

                Because the point isn't to tell you what to buy.

                **It's to help you understand what you're buying—and why.**

                ---

                *Market Intelligence is an independent market research project. Predictions are estimates, reported transactions may be disclosed after they occur, and past investment performance does not guarantee future results. All investments carry risk. Users should conduct their own research.*
                """;

        try {
            for (String line : story.split("\\n", -1)) {
                String trimmed = line.trim();

                if (trimmed.isEmpty()) {
                    doc.insertString(doc.getLength(), "\n", normal);

                } else if (trimmed.equals(
                        "# Why Market Intelligence Exists")) {
                    doc.insertString(
                            doc.getLength(),
                            "Why Market Intelligence Exists\n",
                            heading1);

                } else if (trimmed.startsWith("### ")) {
                    doc.insertString(
                            doc.getLength(),
                            trimmed.substring(4) + "\n",
                            mission);

                } else if (trimmed.startsWith("## ")) {
                    doc.insertString(
                            doc.getLength(),
                            trimmed.substring(3) + "\n",
                            heading2);

                } else if (trimmed.equals("---")) {
                    doc.insertString(
                            doc.getLength(),
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n",
                            italic);

                } else if (trimmed.startsWith("**")
                        && trimmed.endsWith("**")) {
                    doc.insertString(
                            doc.getLength(),
                            trimmed.substring(2, trimmed.length() - 2) + "\n",
                            mission);

                } else if (trimmed.startsWith("*")
                        && trimmed.endsWith("*")) {
                    doc.insertString(
                            doc.getLength(),
                            trimmed.substring(1, trimmed.length() - 1) + "\n",
                            italic);

                } else {
                    insertFormattedParagraph(doc, trimmed, normal, bold);
                }
            }

        } catch (BadLocationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to display the About information.",
                    "About",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JScrollPane scrollPane = new JScrollPane(storyPane);
        stylePageScrollPane(scrollPane);
        storyPane.setCaretPosition(0);

        page.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(page, BorderLayout.CENTER);

        applyTheme();
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void stylePageScrollPane(JScrollPane scrollPane) {
    scrollPane.setBorder(null);
    scrollPane.setBackground(currentCard());
    scrollPane.getViewport().setBackground(currentCard());

    scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    styleUniversalScrollBar(scrollPane);
}

    
private void styleUniversalScrollBar(JScrollPane scrollPane) {
    JScrollBar scrollBar = scrollPane.getVerticalScrollBar();

    scrollBar.setPreferredSize(new Dimension(8, 0));
    scrollBar.setOpaque(false);
    scrollBar.setBorder(null);
    scrollBar.setUnitIncrement(32);
    scrollBar.setBlockIncrement(120);

    scrollBar.setUI(new BasicScrollBarUI() {
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createEmptyButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createEmptyButton();
        }

        private JButton createEmptyButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            button.setFocusable(false);
            button.setBorderPainted(false);
            button.setOpaque(false);
            return button;
        }

        @Override
        protected void paintTrack(
                Graphics g,
                JComponent component,
                Rectangle bounds) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(currentBackground());
            g2.fillRect(
                    bounds.x, bounds.y,
                    bounds.width, bounds.height);
            g2.dispose();
        }

        @Override
        protected void paintThumb(
                Graphics g,
                JComponent component,
                Rectangle bounds) {

            if (bounds.isEmpty() || !scrollBar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

          Color thumbColor = isDragging
        ? currentAccent().brighter()
        : currentAccent();

            g2.setColor(thumbColor);
            g2.fillRoundRect(
                    bounds.x + 2,
                    bounds.y + 2,
                    Math.max(1, bounds.width - 4),
                    Math.max(1, bounds.height - 4),
                    8, 8);
            g2.dispose();
        }
    });
}


    private void insertFormattedParagraph(
            StyledDocument doc,
            String line,
            AttributeSet normal,
            AttributeSet bold
    ) throws BadLocationException {

        int index = 0;

        while (index < line.length()) {
            int startBold = line.indexOf("**", index);

            if (startBold == -1) {
                doc.insertString(
                        doc.getLength(),
                        line.substring(index) + "\n",
                        normal);
                return;
            }

            if (startBold > index) {
                doc.insertString(
                        doc.getLength(),
                        line.substring(index, startBold),
                        normal);
            }

            int endBold = line.indexOf("**", startBold + 2);

            if (endBold == -1) {
                doc.insertString(
                        doc.getLength(),
                        line.substring(startBold) + "\n",
                        normal);
                return;
            }

            doc.insertString(
                    doc.getLength(),
                    line.substring(startBold + 2, endBold),
                    bold);

            index = endBold + 2;
        }

        doc.insertString(doc.getLength(), "\n", normal);
    }

    // SEARCH
    private void performSearch() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            searchModel.clear();
            searchResults.clearSelection();
            hideSearchResults();
            return;
        }

        // Remember the query so old results cannot replace results
        // belonging to a newer query.
        String requestedQuery = query;

        List<StockSearchResult> stocks = service.searchStocks(query);

        if (!searchField.getText().trim().equals(requestedQuery)) {
            return;
        }

        searchModel.clear();
        searchResults.clearSelection();

        if (stocks != null) {
            for (StockSearchResult stock : stocks) {
                if (stock != null) {
                    searchModel.addElement(stock);
                }
            }
        }

        if (!searchModel.isEmpty()) {
            updateSearchPopupSize();
            showSearchResults();

            // Showing a popup can affect focus on some platforms.
            // Restore focus without requiring the user to click again.
            SwingUtilities.invokeLater(() -> {
                if (searchField.isShowing()
                        && searchPopup != null
                        && searchPopup.isVisible()) {
                    searchField.requestFocusInWindow();
                }
            });

        } else {
            hideSearchResults();
        }
    }

    private void updateSearchPopupSize() {
        if (searchPopupSizeLocked) return;

        int rowHeight = searchResults.getFixedCellHeight();

        if (rowHeight <= 0) {
            rowHeight = 42;
        }

        int height = searchModel.size() * rowHeight + 8;
        height = Math.max(SEARCH_DEFAULT_POPUP_HEIGHT, height);
        height = Math.min(SEARCH_MAX_POPUP_HEIGHT, height);

        int width = Math.max(
                SEARCH_MIN_POPUP_WIDTH,
                searchField.getWidth());

        width = Math.min(SEARCH_MAX_POPUP_WIDTH, width);

        searchPopup.setPopupSize(width, height);
    }

    private void showSearchResults() {
        if (searchPopup == null || searchModel.isEmpty()) {
            return;
        }

        if (!searchPopup.isVisible()) {
            searchPopup.show(
                    searchField,
                    0,
                    searchField.getHeight());
        } else {
            searchPopup.revalidate();
            searchPopup.repaint();
        }
    }

    private void hideSearchResults() {
        if (searchTimer != null) {
            searchTimer.stop();
        }

        if (searchPopup != null && searchPopup.isVisible()) {
            searchPopup.setVisible(false);
        }

        searchPopupSizeLocked = false;
    }

    private void openSelectedStock(StockSearchResult selected) {
        if (selected == null) return;

        String symbol = selected.getSymbol();

        hideSearchResults();
        searchModel.clear();
        searchResults.clearSelection();
        searchField.setText("");
        searchPopupSizeLocked = false;

        showStock(symbol);

        SwingUtilities.invokeLater(() ->
                rightPanel.requestFocusInWindow());
    }

    // STYLING
    private void styleMenuItem(JMenuItem item) {
        item.setBackground(currentCard());
        item.setForeground(currentText());
        item.setFont(new Font("Arial", Font.PLAIN, 15));
        item.setBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 12));
        item.setOpaque(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(currentCard());
        button.setForeground(currentText());
        button.setBorder(
                BorderFactory.createLineBorder(currentBorder()));
        button.setFocusPainted(false);
        button.setOpaque(true);
    }

    private void styleButton(AbstractButton button) {
        button.setBackground(
                lightMode ? LIGHT_CARD : new Color(35, 35, 35));
        button.setForeground(currentText());
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(
                lightMode ? LIGHT_BORDER : new Color(70, 70, 70)));
        button.setOpaque(true);
    }

    // WATCHLIST
    private void loadWatchlist() {
        watchlistPanel.removeAll();

        JLabel watchlistTitle = new JLabel("WATCHLIST");
        watchlistTitle.setForeground(currentText());
        watchlistTitle.setFont(new Font("Arial", Font.BOLD, 18));
        watchlistTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        watchlistPanel.add(watchlistTitle);
        watchlistPanel.add(Box.createVerticalStrut(15));

        if (user.getWatchlist().isEmpty()) {
            JLabel empty = new JLabel(
                    "<html>No stocks saved.<br><br>"
                    + "Use Search Stock<br>to add one.</html>");

            empty.setForeground(currentSecondaryText());
            empty.setBorder(
                    BorderFactory.createEmptyBorder(20, 10, 20, 10));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            watchlistPanel.add(empty);

            watchlistPanel.revalidate();
            watchlistPanel.repaint();
            return;
        }

        for (String symbol : user.getWatchlist()) {
            watchlistPanel.add(createStockCard(symbol));
            watchlistPanel.add(Box.createVerticalStrut(8));
        }

        watchlistPanel.revalidate();
        watchlistPanel.repaint();
    }

    private void removeStock(String symbol) {
    user.removeStock(symbol);
    AppSession.getUserManager().saveUsers();
    loadWatchlist();
}

private JPanel createStockCard(String symbol) {
    JPanel card = new JPanel(new BorderLayout());
    card.putClientProperty("watchlistSymbol", symbol);

    card.setBackground(currentCard());
    card.setPreferredSize(new Dimension(200, 55));
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
    card.setAlignmentX(Component.LEFT_ALIGNMENT);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentBorder()),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));

    // Drag handle and stock ticker.
    JPanel leftContent = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 0));
    leftContent.setOpaque(false);

      JLabel dragHandle = new JLabel(
        new MIIcon("drag", currentSecondaryText(), 20)
);

dragHandle.setToolTipText("Drag to reorder watchlist");
dragHandle.setCursor(
        Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
);

// Larger invisible hitbox around the six-dot icon.
dragHandle.setBorder(
        BorderFactory.createEmptyBorder(8, 10, 8, 10)
);

dragHandle.setToolTipText("Drag to reorder watchlist");
dragHandle.setCursor(
        Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    dragHandle.setFont(new Font("Arial", Font.BOLD, 20));
    dragHandle.setForeground(currentSecondaryText());
    dragHandle.setToolTipText("Drag to reorder watchlist");
    dragHandle.setCursor(
        Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

    JLabel symbolLabel = new JLabel(symbol);
    symbolLabel.setFont(new Font("Arial", Font.BOLD, 17));
    symbolLabel.setForeground(currentText());
    symbolLabel.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    leftContent.add(dragHandle);
    leftContent.add(symbolLabel);
    card.add(leftContent, BorderLayout.WEST);

    // Remove stock button.
    JButton removeButton = new JButton("×");
    removeButton.setFont(new Font("Arial", Font.BOLD, 16));
    removeButton.setForeground(currentSecondaryText());
    removeButton.setToolTipText("Remove from watchlist");
    removeButton.setBorderPainted(false);
    removeButton.setContentAreaFilled(false);
    removeButton.setFocusPainted(false);
    removeButton.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    removeButton.addActionListener(e -> removeStock(symbol));
    card.add(removeButton, BorderLayout.EAST);

    // Clicking the stock area opens its dashboard.
MouseAdapter stockClickListener = new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        showStock(symbol);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        symbolLabel.setForeground(currentAccent());
        symbolLabel.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    @Override
    public void mouseExited(MouseEvent e) {
        symbolLabel.setForeground(currentText());
    }
};

symbolLabel.addMouseListener(stockClickListener);
leftContent.addMouseListener(stockClickListener);

    // Drag-and-drop state.
    final int[] originalIndex = {-1};
    final int[] dropIndex = {-1};
    final boolean[] dragging = {false};

    MouseAdapter dragListener = new MouseAdapter() {

        private void resetBorder(JPanel panel) {
            if (panel != null) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(currentBorder()),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (user.getWatchlist().size() < 2) {
                return;
            }

            originalIndex[0] = user.getWatchlist().indexOf(symbol);

            if (originalIndex[0] < 0) {
                return;
            }

            dropIndex[0] = originalIndex[0];
            dragging[0] = true;

            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(currentAccent(), 2),
                    BorderFactory.createEmptyBorder(7, 9, 7, 9)));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!dragging[0]) {
                return;
            }

            Point point = SwingUtilities.convertPoint(
                    dragHandle, e.getPoint(), watchlistPanel);

            int targetIndex = user.getWatchlist().size();

            for (Component component : watchlistPanel.getComponents()) {
                if (!(component instanceof JPanel candidate)) {
                    continue;
                }

                Object value = candidate.getClientProperty("watchlistSymbol");

                if (!(value instanceof String targetSymbol)) {
                    continue;
                }

                int midpoint = candidate.getY()
                        + candidate.getHeight() / 2;

                if (point.y < midpoint) {
                    targetIndex = user.getWatchlist().indexOf(targetSymbol);
                    break;
                }
            }

            dropIndex[0] = Math.max(0, targetIndex);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!dragging[0]) {
                return;
            }

            dragging[0] = false;

            LinkedList<String> stocks = user.getWatchlist();

            int from = originalIndex[0];
            int to = dropIndex[0];

            if (from >= 0 && to >= 0 && from != to
                    && from < stocks.size()) {

                stocks.remove(symbol);

                if (to > from) {
                    to--;
                }

                to = Math.max(0, Math.min(to, stocks.size()));
                stocks.add(to, symbol);

                AppSession.getUserManager().saveUsers();
            }

            resetBorder(card);
            loadWatchlist();
        }
    };

    dragHandle.addMouseListener(dragListener);
    dragHandle.addMouseMotionListener(dragListener);

    // Card hover effect.
    card.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            if (!dragging[0]) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(currentAccent()),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (!dragging[0]) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(currentBorder()),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }
        }
    });

    return card;
}



    // STOCK DASHBOARD
    private void showStock(String symbol) {
        StockReport report = service.getStockAnalysis(symbol);
        LinkedList<StockPrice> prices =
                service.getHistoricalPrices(symbol);

        if (report == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not find stock: " + symbol,
                    "Stock Not Found",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentSymbol = symbol.toUpperCase();

        rightPanel.removeAll();
        rightPanel.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(currentBackground());
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel stockTitle = new JLabel(
                symbol + " — " + report.getCompanyName());
        stockTitle.setForeground(currentText());
        stockTitle.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel priceLabel = new JLabel(
                String.format("$%.2f", report.getCurrentPrice()));
        priceLabel.setForeground(currentText());
        priceLabel.setFont(new Font("Arial", Font.BOLD, 30));

        JLabel directionLabel = new JLabel(
                "▲ " + report.getPredictionDirection());
        directionLabel.setForeground(new Color(80, 200, 120));
        directionLabel.setFont(new Font("Arial", Font.BOLD, 17));

        header.add(stockTitle);
        header.add(Box.createVerticalStrut(3));
        header.add(priceLabel);
        header.add(directionLabel);

        JPanel stockHeader = new JPanel(new BorderLayout());
        stockHeader.setBackground(currentBackground());
        stockHeader.add(header, BorderLayout.WEST);

       JButton saveButton = new JButton(
         new MIIcon(
                user.hasStock(symbol)
                        ? "bookmark-filled"
                        : "save-stock",
                currentAccent(),
                20
        )
);


saveButton.setPreferredSize(new Dimension(44, 44));
saveButton.setMinimumSize(new Dimension(44, 44));
saveButton.setOpaque(false);
saveButton.setBorderPainted(false);
saveButton.setContentAreaFilled(false);
saveButton.setFocusPainted(false);
saveButton.setCursor(
        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
);


saveButton.setToolTipText(
        user.hasStock(symbol)
                ? "Remove from watchlist"
                : "Save to watchlist"
);

        saveButton.setBorderPainted(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(
        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
);
        saveButton.setFont(new Font("Arial", Font.PLAIN, 30));
        saveButton.setForeground(currentText());
        saveButton.setBackground(currentBackground());
        saveButton.setBorderPainted(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        
    Runnable updateStar = () -> {
    boolean saved = user.hasStock(symbol);

    saveButton.setIcon(new BookmarkIcon(saved));

    saveButton.setToolTipText(
            saved ? "Remove from watchlist" : "Save to watchlist"
    );

    saveButton.repaint();
};


        updateStar.run();

        saveButton.addActionListener(e -> {
            if (user.hasStock(symbol)) {
                user.removeStock(symbol);
            } else {
                user.addStock(symbol);
            }

            AppSession.getUserManager().saveUsers();
            updateStar.run();
            loadWatchlist();
        });

        stockHeader.add(saveButton, BorderLayout.EAST);

        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(currentPanel());
        chartPanel.setBorder(
                BorderFactory.createLineBorder(currentBorder()));

        StockChartPanel stockChart = new StockChartPanel(prices);
        chartPanel.add(stockChart, BorderLayout.CENTER);

        JPanel chartControls = new JPanel(
                new FlowLayout(FlowLayout.LEFT));
        chartControls.setBackground(currentPanel());

        JButton oneWeek = new JButton("1W");
        JButton oneMonth = new JButton("1M");
        JButton threeMonth = new JButton("3M");
        JButton oneYear = new JButton("1Y");
        JButton fiveYear = new JButton("5Y");

        JToggleButton ma20 = new JToggleButton("20 MA");
        JToggleButton ma50 = new JToggleButton("50 MA");
        JToggleButton ma200 = new JToggleButton("200 MA");

        styleButton(oneWeek);
        styleButton(oneMonth);
        styleButton(threeMonth);
        styleButton(oneYear);
        styleButton(fiveYear);
        styleButton((AbstractButton) ma20);
        styleButton((AbstractButton) ma50);
        styleButton((AbstractButton) ma200);

        chartControls.add(oneWeek);
        chartControls.add(oneMonth);
        chartControls.add(threeMonth);
        chartControls.add(oneYear);
        chartControls.add(fiveYear);
        chartControls.add(Box.createHorizontalStrut(15));
        chartControls.add(ma20);
        chartControls.add(ma50);
        chartControls.add(ma200);

        chartPanel.add(chartControls, BorderLayout.SOUTH);

        oneWeek.addActionListener(e -> stockChart.setTimeRange("1W"));
        oneMonth.addActionListener(e -> stockChart.setTimeRange("1M"));
        threeMonth.addActionListener(e -> stockChart.setTimeRange("3M"));
        oneYear.addActionListener(e -> stockChart.setTimeRange("1Y"));
        fiveYear.addActionListener(e -> stockChart.setTimeRange("5Y"));

        ma20.addActionListener(
                e -> stockChart.setShow20MA(ma20.isSelected()));
        ma50.addActionListener(
                e -> stockChart.setShow50MA(ma50.isSelected()));
        ma200.addActionListener(
                e -> stockChart.setShow200MA(ma200.isSelected()));

        JPanel lowerContent = new JPanel();
        lowerContent.setBackground(currentBackground());
        lowerContent.setLayout(
                new BoxLayout(lowerContent, BoxLayout.Y_AXIS));
        lowerContent.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JPanel fourStats = new JPanel(new GridLayout(1, 4, 12, 0));
        fourStats.setBackground(currentBackground());
        fourStats.setAlignmentX(Component.LEFT_ALIGNMENT);

        fourStats.add(createInfoCard(
        "P/E",
        formatMetric(report.getPeRatio(), 1, ""),
        getPeStatus(report.getPeRatio())));

fourStats.add(createInfoCard(
        "GROWTH",
        formatMetric(report.getEarningsGrowth(), 2, "%"),
        getGrowthStatus(report.getEarningsGrowth())));

        fourStats.add(createInfoCard(
                "RSI",
                String.format("%.1f", report.getRsi()),
                getRsiStatus(report.getRsi())));

        double dividendPerShare = report.getCurrentPrice()
                * report.getDividendYield() / 100.0;

        fourStats.add(createInfoCard(
        "DIVIDEND",
        formatMetric(report.getDividendYield(), 2, "%"),
        getDividendStatus(report.getDividendYield())));

        lowerContent.add(fourStats);
        lowerContent.add(Box.createVerticalStrut(15));

        JPanel rowOne = new JPanel(new GridLayout(1, 2, 12, 0));
        rowOne.setBackground(currentBackground());
        rowOne.setAlignmentX(Component.LEFT_ALIGNMENT);

        double current = report.getCurrentPrice();

        double difference20 = report.getMa20() == 0 ? 0
                : ((current - report.getMa20()) / report.getMa20()) * 100;

        double difference50 = report.getMa50() == 0 ? 0
                : ((current - report.getMa50()) / report.getMa50()) * 100;

        double difference200 = report.getMa200() == 0 ? 0
                : ((current - report.getMa200()) / report.getMa200()) * 100;

        JPanel movingAverageCard =
                createAnalysisCard("MOVING AVERAGES");

        movingAverageCard.add(createLargeLabel(String.format(
                "20-Day:   $%.2f   %+.2f%%   %s",
                report.getMa20(),
                difference20,
                getMaPosition(difference20))));

        movingAverageCard.add(createLargeLabel(String.format(
                "50-Day:   $%.2f   %+.2f%%   %s",
                report.getMa50(),
                difference50,
                getMaPosition(difference50))));

        movingAverageCard.add(createLargeLabel(String.format(
                "200-Day:  $%.2f   %+.2f%%   %s",
                report.getMa200(),
                difference200,
                getMaPosition(difference200))));

        double predictedPrice = report.getPredictedPrice();
        double predictionChange = current == 0 ? 0
                : ((predictedPrice - current) / current) * 100;

        JPanel predictionCard = createAnalysisCard("PREDICTION");

        predictionCard.add(createLargeLabel(
                String.format("Current:      $%.2f", current)));

        predictionCard.add(createLargeLabel(
                String.format("Predicted:    $%.2f", predictedPrice)));

        predictionCard.add(createLargeLabel(
                String.format("Potential:    %+.2f%%", predictionChange)));

        predictionCard.add(createLargeLabel(
                "Direction: " + report.getPredictionDirection()));

        rowOne.add(movingAverageCard);
        rowOne.add(predictionCard);

        lowerContent.add(rowOne);
        lowerContent.add(Box.createVerticalStrut(15));

        JPanel rowTwo = new JPanel(new GridLayout(1, 2, 12, 0));
        rowTwo.setBackground(currentBackground());
        rowTwo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel financialCard =
                createAnalysisCard("FINANCIAL INFORMATION");

        financialCard.add(createLargeLabel(
        "Earnings Growth: "
                + formatMetric(report.getEarningsGrowth(), 2, "%")));

financialCard.add(createLargeLabel(
        "P/E Ratio: "
                + formatMetric(report.getPeRatio(), 1, "")));

financialCard.add(createLargeLabel(String.format(
        "Dividend Yield: %.2f%%",
        report.getDividendYield())));


                                                                // more info card
        JPanel moreInfoCard =
                createAnalysisCard("MORE INFORMATION");

        moreInfoCard.add(createLargeLabel(
                "Company: " + report.getCompanyName()));

        moreInfoCard.add(createLargeLabel("Symbol: " + symbol));
        moreInfoCard.add(createLargeLabel(
             "Sector: " + report.getSector()));
             moreInfoCard.add(createLargeLabel(
                "Trend: " + report.getTrend()));

        rowTwo.add(financialCard);
        rowTwo.add(moreInfoCard);

        lowerContent.add(rowTwo);
        lowerContent.add(Box.createVerticalStrut(15));

        JPanel aboutCard = createAboutCard(
                report.getCompanyName(),
                report.getCompanyDescription());

        aboutCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        lowerContent.add(aboutCard);

        JScrollPane lowerScroll = new JScrollPane(lowerContent);
        SwingUtilities.invokeLater(() -> {
    lowerScroll.getVerticalScrollBar().setValue(0);
});
        lowerScroll.setBorder(null);
        lowerScroll.setBackground(currentBackground());
        lowerScroll.getViewport().setBackground(currentBackground());
        lowerScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        lowerScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        styleUniversalScrollBar(lowerScroll);
        lowerScroll.setPreferredSize(new Dimension(800, 380));

        JPanel stockContent = new JPanel(new BorderLayout(0, 5));
        stockContent.setBackground(currentBackground());
        stockContent.add(stockHeader, BorderLayout.NORTH);
        stockContent.add(chartPanel, BorderLayout.CENTER);
        stockContent.add(lowerScroll, BorderLayout.SOUTH);

        rightPanel.add(stockContent, BorderLayout.CENTER);

        applyTheme();
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private JPanel createInfoCard(
            String title,
            String value,
            String status) {

        JPanel card = new JPanel();
        card.setBackground(currentCard());
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(currentBorder(), 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(currentSecondaryText());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(currentText());
        valueLabel.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel statusLabel = new JLabel(status);
        statusLabel.setForeground(currentSecondaryText());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(statusLabel);

        return card;
    }

    private JPanel createAnalysisCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(currentCard());
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(currentBorder(), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(currentText());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));

        return card;
    }

    private JPanel createAboutCard(
            String companyName,
            String description) {

        JPanel card = new JPanel();
        card.setBackground(currentCard());
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(currentBorder()),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel title = new JLabel("ABOUT " + companyName);
        title.setForeground(currentText());
        title.setFont(new Font("Arial", Font.BOLD, 19));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(12));

        if (description == null || description.trim().isEmpty()) {
            description = "Company description unavailable.";
        }

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setForeground(currentSecondaryText());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 15));
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionArea.setRows(5);

        card.add(descriptionArea);

        return card;
    }

    private JLabel createLargeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(currentSecondaryText());
        label.setFont(new Font("Arial", Font.PLAIN, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(
                BorderFactory.createEmptyBorder(3, 0, 3, 0));

        return label;
    }

    private String getMaPosition(double difference) {
        if (difference >= 5) return "ABOVE";
        if (difference <= -5) return "BELOW";
        return "NEAR";
    }

    private String formatMetric(double value, int decimalPlaces, String suffix) {
    if (Double.isNaN(value) || Double.isInfinite(value) || value == 0.0) {
        return "Unavailable";
    }

    return String.format("%." + decimalPlaces + "f%s", value, suffix);
}

    private String getPeStatus(double pe) {
        if (pe < 0) return "NEGATIVE EARNINGS";
        if (pe == 0) return "N/A";
        if (pe < 15) return "LOW";
        if (pe <= 25) return "FAIR";
        if (pe <= 40) return "HIGH";
        return "VERY HIGH";
    }

    private String getGrowthStatus(double growth) {
        if (growth >= 15) return "STRONG";
        if (growth >= 5) return "POSITIVE";
        if (growth >= 0) return "SLOW";
        return "DECLINING";
    }
    private String getDividendStatus(double dividend) {
    if (Double.isNaN(dividend)
            || Double.isInfinite(dividend)
            || dividend == 0.0) {
        return "UNAVAILABLE";
    }

    if (dividend < 0) {
        return "NEGATIVE";
    }

    return "ANNUAL YIELD";
}

    private String getRsiStatus(double rsi) {
        if (rsi < 30) return "OVERSOLD";
        if (rsi > 70) return "OVERBOUGHT";
        return "NEUTRAL";
    }
    

private void showFinancialTermsScreen() {

    settingsOpen = false;
    updateSearchPosition();

    rightPanel.removeAll();
    rightPanel.setLayout(new BorderLayout());

    JPanel page = new JPanel(new BorderLayout(0, 14));
    page.setBackground(currentBackground());
    page.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    // Header centered using the same approach as Settings.
    JPanel header = new JPanel(null);
    header.setBackground(currentBackground());
    header.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 20, 10));
    header.setPreferredSize(new Dimension(0, 115));

    JButton backButton = new JButton("←  Back to Stock");
    styleButton(backButton);
    backButton.setFont(new Font("Arial", Font.BOLD, 13));
    backButton.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    backButton.setPreferredSize(new Dimension(150, 38));
    backButton.addActionListener(e -> returnToCurrentStock());

    JLabel title = new JLabel(
            "Financial Terms for Beginners",
            SwingConstants.CENTER);
    title.setForeground(currentText());
    title.setFont(new Font("SansSerif", Font.BOLD, 27));

    JLabel subtitle = new JLabel(
            "Understand the numbers used by Market Intelligence.",
            SwingConstants.CENTER);
    subtitle.setForeground(currentSecondaryText());
    subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

    header.add(backButton);
    header.add(title);
    header.add(subtitle);

    header.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            int width = header.getWidth();

            backButton.setBounds(0, 0, 150, 38);

           int titleWidth = Math.min(700, width - 40);

// Center the title in the same left-side area as the search bar.
int titleX = (width - titleWidth) / 2 - 45;

title.setBounds(titleX, 5, titleWidth, 35);
subtitle.setBounds(titleX, 43, titleWidth, 25);

        }
    });

    JPanel lessons = new JPanel();
    lessons.setBackground(currentBackground());
    lessons.setLayout(new BoxLayout(lessons, BoxLayout.Y_AXIS));

    addFinancialLesson(lessons,
            "1. P/E Ratio — Valuation",
            "The price-to-earnings ratio compares a stock's price with "
            + "its earnings per share. Investors use it to help evaluate "
            + "how expensive a stock is relative to its earnings.",
            "A P/E of 20 means the share price is 20 times annual earnings "
            + "per share. A low P/E does not automatically mean a stock is cheap.");

    addFinancialLesson(lessons,
            "2. Earnings Growth vs. Stock-Price Growth",
            "Earnings growth measures how a company's profits change. "
            + "our dashboard's GROWTH card displays the earnings-growth "
            + "metric provided by THE company-data source.",
            "our prediction engine separately calculates historical "
            + "stock-price growth by comparing the latest price with a "
            + "price roughly 252 trading days earlier. Earnings growth "
            + "and stock-price growth are not the same thing.");

    addFinancialLesson(lessons,
            "3. RSI — Relative Strength Index",
            "RSI is a momentum indicator calculated from recent price "
            + "changes. Our prediction engine uses a 14-period RSI.",
            "In our current model, RSI below 30 adds a positive momentum "
            + "score, while RSI above 70 adds a negative momentum score. "
            + "These are model rules, not automatic buy or sell signals.");

    addFinancialLesson(lessons,
            "4. Moving Averages",
            "A moving average smooths price data over a set number of "
            + "trading days. Our dashboard can show 20-day, 50-day, "
            + "and 200-day averages.",
            "If a stock is above its 200-day moving average, its current "
            + "price is above that historical average. This describes past "
            + "prices; it does not guarantee future direction.");

    addFinancialLesson(lessons,
            "5. Dividends",
            "A dividend is a payment a company may make to its shareholders. "
            + "Dividend yield compares annual dividends per share with the "
            + "current share price.",
            "A $2 annual dividend on a $100 share price equals a 2% yield. "
            + "Companies can reduce or stop dividends, so yield alone "
            + "does not establish whether a stock is a good investment.");

    addFinancialLesson(lessons,
            "6. Financial Health",
            "Our model combines revenue growth, earnings growth, profit "
            + "margin, return on equity, debt-to-equity, and free cash flow.",
            "These metrics describe different aspects of a business: "
            + "profitability, growth, debt, and cash generation. No single "
            + "metric tells the whole story.");

    addFinancialLesson(lessons,
            "7. Sector-Specific Weights",
            "Our app selects a weighting model based on the company's "
            + "classified sector. Different sectors assign different "
            + "weights to growth, valuation, financial health, technical "
            + "analysis, momentum, and the market factor.",
            "For example, Technology assigns 35% to growth, while Real "
            + "Estate assigns 25% to valuation and 25% to the market factor. "
            + "These are configured model weights, not proof of accuracy. "
            + "Currently, the market score is zero, so it contributes zero.");

    addFinancialLesson(lessons,
            "8. Predicted Price and Prediction Period",
            "Our prediction engine combines its weighted score with the "
            + "selected prediction period to calculate an estimated price.",
            "The estimate is an output of your current formula, not a "
            + "guaranteed future price or promised investment return. "
            + "Actual stock prices can move in either direction.");

    JScrollPane scrollPane = new JScrollPane(lessons);

    stylePageScrollPane(scrollPane);
    scrollPane.getViewport().setBackground(currentBackground());

    SwingUtilities.invokeLater(() ->
            scrollPane.getVerticalScrollBar().setValue(0));

    page.add(header, BorderLayout.NORTH);
    page.add(scrollPane, BorderLayout.CENTER);

    rightPanel.add(page, BorderLayout.CENTER);

    applyTheme();
    rightPanel.revalidate();
    rightPanel.repaint();
}


private void addFinancialLesson(
        JPanel container,
        String lessonTitle,
        String meaning,
        String example) {

    JPanel card = createAnalysisCard(lessonTitle);
    card.setAlignmentX(Component.LEFT_ALIGNMENT);
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
    addLessonParagraph(card, "WHAT IT MEANS", meaning);
    addLessonParagraph(card, "EXAMPLE / HOW MARKET INTELLIGENCE USES IT", example);

    container.add(card);
    container.add(Box.createVerticalStrut(12));
}


private void addLessonParagraph(
        JPanel card,
        String headingText,
        String bodyText) {

    JLabel heading = new JLabel(headingText);
    heading.setFont(new Font("Arial", Font.BOLD, 11));
    heading.setForeground(currentSecondaryText());
    heading.setAlignmentX(Component.LEFT_ALIGNMENT);

    JTextArea body = new JTextArea(bodyText);
    body.setEditable(false);
    body.setFocusable(false);
    body.setLineWrap(true);
    body.setWrapStyleWord(true);
    body.setOpaque(false);
    body.setFont(new Font("Arial", Font.PLAIN, 13));
    body.setForeground(currentText());
    body.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));
    body.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Calculate the wrapped text height when the available width changes.
    body.setColumns(1);
    body.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

    card.add(heading);
    card.add(Box.createVerticalStrut(4));
    card.add(body);
    card.add(Box.createVerticalStrut(8));
}


private class BookmarkIcon implements Icon {

    private final boolean saved;

    public BookmarkIcon(boolean saved) {
        this.saved = saved;
    }

    @Override
    public int getIconWidth() {
        return 20;
    }

    @Override
    public int getIconHeight() {
        return 20;
    }

    @Override
    public void paintIcon(
            Component component,
            Graphics graphics,
            int x,
            int y) {

        Graphics2D g2 = (Graphics2D) graphics.create();

        try {
            g2.translate(x, y);
            g2.scale(20.0 / 24.0, 20.0 / 24.0);

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(currentAccent());
            g2.setStroke(new BasicStroke(
                    1.8f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            Path2D bookmark = new Path2D.Double();
            bookmark.moveTo(6, 3);
            bookmark.lineTo(18, 3);
            bookmark.lineTo(18, 21);
            bookmark.lineTo(12, 17);
            bookmark.lineTo(6, 21);
            bookmark.closePath();

            if (saved) {
                g2.fill(bookmark);
            } else {
                g2.draw(bookmark);
            }

        } finally {
            g2.dispose();
        }
    }
}


private static class MIIcon implements Icon {

    private final String type;
    private final Color color;
    private final int size;

    public MIIcon(String type, Color color, int size) {
        this.type = type;
        this.color = color;
        this.size = size;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics,
                          int x, int y) {

        Graphics2D g2 = (Graphics2D) graphics.create();


                            

        try {
            g2.translate(x, y);
            g2.scale(size / 24.0, size / 24.0);

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);
            g2.setStroke(new BasicStroke(
                    1.8f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            switch (type) {

                case "user": {
                    // Head
                    g2.draw(new java.awt.geom.Ellipse2D.Double(
                            9, 3, 6, 6
                    ));

                    // Shoulders
                    java.awt.geom.Path2D body =
                            new java.awt.geom.Path2D.Double();

                    body.moveTo(4, 21);
                    body.lineTo(4, 19);
                    body.curveTo(4, 15, 7, 13, 12, 13);
                    body.curveTo(17, 13, 20, 15, 20, 19);
                    body.lineTo(20, 21);

                    g2.draw(body);
                    break;
                }

                case "search": {
                    g2.draw(new java.awt.geom.Ellipse2D.Double(
                            3, 3, 13, 13
                    ));

                    g2.draw(new java.awt.geom.Line2D.Double(
                            14, 14, 21, 21
                    ));
                    break;
                }

              

                case "settings": {
                    // Gear center
                    g2.draw(new java.awt.geom.Ellipse2D.Double(
                            8, 8, 8, 8
                    ));

                    // Eight gear spokes
                    for (int i = 0; i < 8; i++) {
                        double angle = i * Math.PI / 4.0;
                        double innerRadius = 6.5;
                        double outerRadius = 9.5;

                        double x1 = 12 + Math.cos(angle) * innerRadius;
                        double y1 = 12 + Math.sin(angle) * innerRadius;
                        double x2 = 12 + Math.cos(angle) * outerRadius;
                        double y2 = 12 + Math.sin(angle) * outerRadius;

                        g2.draw(new java.awt.geom.Line2D.Double(
                                x1, y1, x2, y2
                        ));
                    }
                    break;
                }

                case "book-open": {
                    java.awt.geom.Path2D book =
                            new java.awt.geom.Path2D.Double();

                    book.moveTo(12, 7);
                    book.curveTo(9, 5, 5, 5, 3, 7);
                    book.lineTo(3, 19);
                    book.curveTo(6, 17, 9, 18, 12, 20);

                    book.moveTo(12, 7);
                    book.curveTo(15, 5, 19, 5, 21, 7);
                    book.lineTo(21, 19);
                    book.curveTo(18, 17, 15, 18, 12, 20);
                    book.lineTo(12, 7);

                    g2.draw(book);
                    break;
                }

                case "info": {
                    g2.draw(new java.awt.geom.Ellipse2D.Double(
                            3, 3, 18, 18
                    ));

                    g2.draw(new java.awt.geom.Line2D.Double(
                            12, 11, 12, 17
                    ));

                    g2.fill(new java.awt.geom.Ellipse2D.Double(
                            11.1, 7, 1.8, 1.8
                    ));
                    break;
                }

                case "log-out": {
                    // Door
                    g2.draw(new java.awt.geom.Path2D.Double() {{
                        moveTo(13, 3);
                        lineTo(20, 3);
                        lineTo(20, 21);
                        lineTo(13, 21);
                    }});

                    // Arrow
                    g2.draw(new java.awt.geom.Line2D.Double(
                            3, 12, 15, 12
                    ));

                    g2.draw(new java.awt.geom.Line2D.Double(
                            10, 7, 15, 12
                    ));

                    g2.draw(new java.awt.geom.Line2D.Double(
                            10, 17, 15, 12
                    ));
                    break;
                }

                case "drag": {
    // Six evenly spaced dots for the watchlist drag handle.
    double dotSize = 2.2;

    for (int row = 0; row < 3; row++) {
        for (int col = 0; col < 2; col++) {
            double dotX = 7 + (col * 6);
            double dotY = 5 + (row * 7);

            g2.fill(new java.awt.geom.Ellipse2D.Double(
                    dotX, dotY, dotSize, dotSize
            ));
        }
    }
    break;
}



case "save-stock":
case "bookmark-filled": {
    java.awt.geom.Path2D bookmark =
            new java.awt.geom.Path2D.Double();

    bookmark.moveTo(6, 3);
    bookmark.lineTo(18, 3);
    bookmark.lineTo(18, 21);
    bookmark.lineTo(12, 17);
    bookmark.lineTo(6, 21);
    bookmark.closePath();

    if ("bookmark-filled".equals(type)) {
        g2.fill(bookmark);
    } else {
        g2.draw(bookmark);
    }

    break;
}

case "light-mode": {
    // Sun.
    g2.draw(new java.awt.geom.Ellipse2D.Double(
            8, 8, 8, 8
    ));

    // Eight sun rays.
    for (int i = 0; i < 8; i++) {
        double angle = i * Math.PI / 4.0;

        double x1 = 12 + Math.cos(angle) * 6.5;
        double y1 = 12 + Math.sin(angle) * 6.5;
        double x2 = 12 + Math.cos(angle) * 9;
        double y2 = 12 + Math.sin(angle) * 9;

        g2.draw(new java.awt.geom.Line2D.Double(
                x1, y1, x2, y2
        ));
    }
    break;
}

case "dark-mode": {
    // Crescent moon.
    java.awt.geom.Path2D moon =
            new java.awt.geom.Path2D.Double();

    moon.moveTo(19, 15);
    moon.curveTo(15, 17, 9, 14, 9, 9);
    moon.curveTo(9, 6, 10, 4, 12, 3);
    moon.curveTo(6, 3, 3, 8, 4, 13);
    moon.curveTo(5, 19, 11, 22, 16, 19);
    moon.curveTo(18, 18, 19, 17, 19, 15);
    moon.closePath();

    g2.draw(moon);
    break;
}


default:
    // Unknown icon type: draw nothing.
    break;
            }

        } finally {
            g2.dispose();
        }
    }
}

private Color currentAccent() {
    return ACCENT_GREEN;
}


}