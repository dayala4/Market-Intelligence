import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class StockChartPanel extends JPanel {

    private LinkedList<StockPrice> prices;

    private boolean show20MA = false;
    private boolean show50MA = false;
    private boolean show200MA = false;

    private String timeRange = "1Y";

    // =====================================================
    // HOVER
    // =====================================================

    private int mouseX = -1;
    private int mouseY = -1;

    private int hoveredIndex = -1;

    // =====================================================
    // COLORS
    // =====================================================

    private final Color BACKGROUND =
            new Color(18, 18, 18);

    private final Color GRID =
            new Color(45, 45, 45);

    private final Color TEXT =
            new Color(190, 190, 190);

    private final Color GREEN =
            new Color(0, 200, 120);

    private final Color RED =
            new Color(240, 70, 70);

    private final Color CROSSHAIR =
            new Color(120, 120, 120);

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public StockChartPanel(
            LinkedList<StockPrice> prices) {

        this.prices = prices;

        setBackground(BACKGROUND);

        // =================================================
        // MOUSE HOVER
        // =================================================

        addMouseMotionListener(
                new MouseMotionAdapter() {

                    @Override
                    public void mouseMoved(
                            MouseEvent e) {

                        mouseX = e.getX();
                        mouseY = e.getY();

                        updateHoveredPoint();

                        repaint();
                    }
                }
        );
    }

    // =====================================================
    // MOVING AVERAGES
    // =====================================================

    public void setShow20MA(boolean show) {

        show20MA = show;

        repaint();
    }

    public void setShow50MA(boolean show) {

        show50MA = show;

        repaint();
    }

    public void setShow200MA(boolean show) {

        show200MA = show;

        repaint();
    }

    // =====================================================
    // TIME RANGE
    // =====================================================

    public void setTimeRange(String range) {

        timeRange = range;

        hoveredIndex = -1;

        repaint();
    }

    // =====================================================
    // GET DISPLAYED PRICES
    // =====================================================

    private LinkedList<StockPrice> getDisplayedPrices() {

        if (prices == null ||
                prices.isEmpty()) {

            return new LinkedList<>();
        }

        int numberOfPoints;

        switch (timeRange) {

            case "5Y":
                numberOfPoints = 1260;
                break;

            case "1Y":
                numberOfPoints = 252;
                break;

            case "3M":
                numberOfPoints = 63;
                break;

            case "1M":
                numberOfPoints = 21;
                break;

            case "1W":
                numberOfPoints = 5;
                break;

            default:
                numberOfPoints = 252;
        }

        int start =
                Math.max(
                        0,
                        prices.size() - numberOfPoints
                );

        return new LinkedList<>(
                prices.subList(
                        start,
                        prices.size()
                )
        );
    }

    // =====================================================
    // PAINT
    // =====================================================

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        LinkedList<StockPrice> displayedPrices =
                getDisplayedPrices();

        if (displayedPrices.size() < 2) {

            g.setColor(TEXT);

            g.drawString(
                    "Not enough stock data",
                    20,
                    30
            );

            return;
        }

        Graphics2D g2 =
                (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int width = getWidth();
        int height = getHeight();

        // =================================================
        // CHART MARGINS
        // =================================================

        int left = 15;
        int right = 75;
        int top = 45;
        int bottom = 45;

        int chartWidth =
                width - left - right;

        int chartHeight =
                height - top - bottom;

        // =================================================
        // PRICE RANGE
        // =================================================

        double minPrice =
                Double.MAX_VALUE;

        double maxPrice =
                -Double.MAX_VALUE;

        for (StockPrice price :
                displayedPrices) {

            minPrice =
                    Math.min(
                            minPrice,
                            price.getLow()
                    );

            maxPrice =
                    Math.max(
                            maxPrice,
                            price.getHigh()
                    );
        }

        double priceRange =
                maxPrice - minPrice;

        if (priceRange <= 0) {

            priceRange = 1;
        }

        double padding =
                priceRange * 0.08;

        minPrice -= padding;
        maxPrice += padding;

        // =================================================
        // HEADER
        // =================================================

        drawChartHeader(
                g2,
                displayedPrices
        );

        // =================================================
        // GRID
        // =================================================

        drawGrid(
                g2,
                minPrice,
                maxPrice,
                left,
                top,
                chartWidth,
                chartHeight
        );

        // =================================================
        // CANDLES
        // =================================================

        drawCandles(
                g2,
                displayedPrices,
                minPrice,
                maxPrice,
                left,
                top,
                chartWidth,
                chartHeight
        );

        // =================================================
        // MOVING AVERAGES
        // =================================================

        if (show20MA) {

            drawMovingAverage(
                    g2,
                    displayedPrices,
                    20,
                    minPrice,
                    maxPrice,
                    left,
                    top,
                    chartWidth,
                    chartHeight,
                    new Color(255, 190, 0)
            );
        }

        if (show50MA) {

            drawMovingAverage(
                    g2,
                    displayedPrices,
                    50,
                    minPrice,
                    maxPrice,
                    left,
                    top,
                    chartWidth,
                    chartHeight,
                    new Color(80, 160, 255)
            );
        }

        if (show200MA) {

            drawMovingAverage(
                    g2,
                    displayedPrices,
                    200,
                    minPrice,
                    maxPrice,
                    left,
                    top,
                    chartWidth,
                    chartHeight,
                    new Color(190, 100, 255)
            );
        }

        // =================================================
        // RIGHT PRICE SCALE
        // =================================================

        drawPriceScale(
                g2,
                minPrice,
                maxPrice,
                top,
                chartHeight,
                width - right
        );

        // =================================================
        // DATE SCALE
        // =================================================

        drawDateLabels(
                g2,
                displayedPrices,
                left,
                bottom,
                chartWidth,
                height
        );

        // =================================================
        // HOVER
        // =================================================

        drawHover(
                g2,
                displayedPrices,
                minPrice,
                maxPrice,
                left,
                top,
                chartWidth,
                chartHeight
        );
    }

    // =====================================================
    // CHART HEADER
    // =====================================================

    private void drawChartHeader(
            Graphics2D g2,
            LinkedList<StockPrice> displayedPrices) {

        StockPrice first =
                displayedPrices.getFirst();

        StockPrice last =
                displayedPrices.getLast();

        double change =
                last.getClose()
                - first.getClose();

        double percent =
                (change / first.getClose())
                * 100;

        if (change >= 0) {

            g2.setColor(GREEN);

        } else {

            g2.setColor(RED);
        }

        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        String text =
                String.format(
                        "%s%.2f  %.2f%%",
                        change >= 0 ? "+" : "",
                        change,
                        percent
                );

        g2.drawString(
                text,
                15,
                25
        );
    }

    // =====================================================
    // GRID
    // =====================================================

    private void drawGrid(
            Graphics2D g2,
            double minPrice,
            double maxPrice,
            int left,
            int top,
            int chartWidth,
            int chartHeight) {

        g2.setColor(GRID);

        // Horizontal lines

        for (int i = 0; i <= 5; i++) {

            int y =
                    top +
                    (chartHeight * i / 5);

            g2.drawLine(
                    left,
                    y,
                    left + chartWidth,
                    y
            );
        }

        // Vertical lines

        for (int i = 0; i <= 5; i++) {

            int x =
                    left +
                    (chartWidth * i / 5);

            g2.drawLine(
                    x,
                    top,
                    x,
                    top + chartHeight
            );
        }
    }

    // =====================================================
    // CANDLESTICKS
    // =====================================================

    private void drawCandles(
            Graphics2D g2,
            LinkedList<StockPrice> displayedPrices,
            double minPrice,
            double maxPrice,
            int left,
            int top,
            int chartWidth,
            int chartHeight) {

        int count =
                displayedPrices.size();

        double candleSpacing =
                (double) chartWidth
                / count;

        int candleWidth =
                Math.max(
                        2,
                        (int) (
                                candleSpacing * 0.65
                        )
                );

        for (int i = 0;
             i < count;
             i++) {

            StockPrice stock =
                    displayedPrices.get(i);

            double open =
                    stock.getOpen();

            double close =
                    stock.getClose();

            double high =
                    stock.getHigh();

            double low =
                    stock.getLow();

            int x =
                    left +
                    (int) (
                            i * candleSpacing
                            + candleSpacing / 2
                    );

            int highY =
                    getY(
                            high,
                            minPrice,
                            maxPrice,
                            top,
                            chartHeight
                    );

            int lowY =
                    getY(
                            low,
                            minPrice,
                            maxPrice,
                            top,
                            chartHeight
                    );

            int openY =
                    getY(
                            open,
                            minPrice,
                            maxPrice,
                            top,
                            chartHeight
                    );

            int closeY =
                    getY(
                            close,
                            minPrice,
                            maxPrice,
                            top,
                            chartHeight
                    );

            // =================================================
            // GREEN / RED
            // =================================================

            if (close >= open) {

                g2.setColor(GREEN);

            } else {

                g2.setColor(RED);
            }

            // =================================================
            // WICK
            // =================================================

            g2.drawLine(
                    x,
                    highY,
                    x,
                    lowY
            );

            // =================================================
            // BODY
            // =================================================

            int bodyTop =
                    Math.min(
                            openY,
                            closeY
                    );

            int bodyHeight =
                    Math.abs(
                            closeY - openY
                    );

            if (bodyHeight < 2) {

                bodyHeight = 2;
            }

            g2.fillRect(
                    x - candleWidth / 2,
                    bodyTop,
                    candleWidth,
                    bodyHeight
            );
        }
    }

    // =====================================================
    // PRICE SCALE
    // =====================================================

    private void drawPriceScale(
            Graphics2D g2,
            double minPrice,
            double maxPrice,
            int top,
            int chartHeight,
            int rightX) {

        g2.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        for (int i = 0; i <= 5; i++) {

            double percentage =
                    (double) i / 5;

            double price =
                    maxPrice
                    -
                    (
                            (maxPrice - minPrice)
                            * percentage
                    );

            int y =
                    top +
                    (int) (
                            chartHeight
                            * percentage
                    );

            g2.setColor(TEXT);

            String text =
                    String.format(
                            "$%.2f",
                            price
                    );

            g2.drawString(
                    text,
                    rightX + 8,
                    y + 4
            );
        }
    }

    // =====================================================
    // DATE LABELS
    // =====================================================

    private void drawDateLabels(
            Graphics2D g2,
            LinkedList<StockPrice> displayedPrices,
            int left,
            int bottom,
            int chartWidth,
            int height) {

        g2.setColor(TEXT);

        g2.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        10
                )
        );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "MMM d"
                );

        int labels = 5;

        for (int i = 0;
             i < labels;
             i++) {

            double position =
                    (double) i
                    / (labels - 1);

            int index =
                    (int) (
                            position
                            * (
                                    displayedPrices.size()
                                    - 1
                            )
                    );

            StockPrice price =
                    displayedPrices.get(index);

            String date =
                    price.getDate()
                            .format(formatter);

            int x =
                    left +
                    (int) (
                            position
                            * chartWidth
                    );

            g2.drawString(
                    date,
                    x - 15,
                    height - 12
            );
        }
    }

    // =====================================================
    // MOVING AVERAGE
    // =====================================================

    private void drawMovingAverage(
            Graphics2D g2,
            LinkedList<StockPrice> displayedPrices,
            int days,
            double minPrice,
            double maxPrice,
            int left,
            int top,
            int chartWidth,
            int chartHeight,
            Color color) {

        if (displayedPrices.size() < days) {

            return;
        }

        g2.setColor(color);

        double previousMA = 0;

        for (int i = days - 1;
             i < displayedPrices.size();
             i++) {

            double sum = 0;

            for (int j = i - days + 1;
                 j <= i;
                 j++) {

                sum +=
                        displayedPrices
                                .get(j)
                                .getClose();
            }

            double movingAverage =
                    sum / days;

            if (i > days - 1) {

                int x1 =
                        getX(
                                i - 1,
                                displayedPrices.size(),
                                left,
                                chartWidth
                        );

                int x2 =
                        getX(
                                i,
                                displayedPrices.size(),
                                left,
                                chartWidth
                        );

                int y1 =
                        getY(
                                previousMA,
                                minPrice,
                                maxPrice,
                                top,
                                chartHeight
                        );

                int y2 =
                        getY(
                                movingAverage,
                                minPrice,
                                maxPrice,
                                top,
                                chartHeight
                        );

                g2.drawLine(
                        x1,
                        y1,
                        x2,
                        y2
                );
            }

            previousMA =
                    movingAverage;
        }
    }

    // =====================================================
    // HOVER
    // =====================================================

    private void drawHover(
            Graphics2D g2,
            LinkedList<StockPrice> displayedPrices,
            double minPrice,
            double maxPrice,
            int left,
            int top,
            int chartWidth,
            int chartHeight) {

        if (hoveredIndex < 0 ||
                hoveredIndex >= displayedPrices.size()) {

            return;
        }

        StockPrice stock =
                displayedPrices.get(
                        hoveredIndex
                );

        int x =
                getX(
                        hoveredIndex,
                        displayedPrices.size(),
                        left,
                        chartWidth
                );

        int closeY =
                getY(
                        stock.getClose(),
                        minPrice,
                        maxPrice,
                        top,
                        chartHeight
                );

        // =================================================
        // SUBTLE HOVER HIGHLIGHT
        // =================================================

        g2.setColor(GREEN);
        g2.setStroke(new BasicStroke(1.5f));

        g2.drawOval(
        x - 5,
        closeY - 5,
        10,
        10
);
        // =================================================
        // HIGHLIGHT POINT
        // =================================================

        g2.setColor(Color.WHITE);

        g2.fillOval(
                x - 4,
                closeY - 4,
                8,
                8
        );

        // =================================================
        // TOOLTIP
        // =================================================

        LocalDate date =
                stock.getDate();

        String dateText =
                date.format(
                        DateTimeFormatter.ofPattern(
                                "MMM d, yyyy"
                        )
                );

        String openText =
                String.format(
                        "Open:  $%.2f",
                        stock.getOpen()
                );

        String highText =
                String.format(
                        "High:  $%.2f",
                        stock.getHigh()
                );

        String lowText =
                String.format(
                        "Low:   $%.2f",
                        stock.getLow()
                );

        String closeText =
                String.format(
                        "Close: $%.2f",
                        stock.getClose()
                );

        int boxWidth = 155;
        int boxHeight = 125;

        int boxX =
                x + 12;

        if (boxX + boxWidth >
                getWidth() - 5) {

            boxX =
                    x - boxWidth - 12;
        }

        int boxY =
                closeY - boxHeight / 2;

        if (boxY < top) {

            boxY = top;
        }

        if (boxY + boxHeight >
                top + chartHeight) {

            boxY =
                    top
                    +
                    chartHeight
                    -
                    boxHeight;
        }

        // Background

        g2.setColor(
                new Color(
                        28,
                        28,
                        28
                )
        );

        g2.fillRoundRect(
                boxX,
                boxY,
                boxWidth,
                boxHeight,
                8,
                8
        );

        // Border

        g2.setColor(
                new Color(
                        90,
                        90,
                        90
                )
        );

        g2.drawRoundRect(
                boxX,
                boxY,
                boxWidth,
                boxHeight,
                8,
                8
        );

        // Text

        g2.setColor(Color.WHITE);

        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        g2.drawString(
                dateText,
                boxX + 10,
                boxY + 20
        );

        g2.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        g2.drawString(
                openText,
                boxX + 10,
                boxY + 42
        );

        g2.drawString(
                highText,
                boxX + 10,
                boxY + 59
        );

        g2.drawString(
                lowText,
                boxX + 10,
                boxY + 76
        );

        g2.drawString(
                closeText,
                boxX + 10,
                boxY + 93
        );

        // =================================================
        // MA VALUES
        // =================================================

        int maY = boxY + 110;

        if (show20MA) {

            double ma20 =
                    calculateMA(
                            displayedPrices,
                            hoveredIndex,
                            20
                    );

            if (!Double.isNaN(ma20)) {

                g2.setColor(
                        new Color(
                                255,
                                190,
                                0
                        )
                );

                g2.drawString(
                        String.format(
                                "20MA: $%.2f",
                                ma20
                        ),
                        boxX + 10,
                        maY
                );
            }
        }
    }

    // =====================================================
    // CALCULATE MA
    // =====================================================

    private double calculateMA(
            LinkedList<StockPrice> displayedPrices,
            int index,
            int days) {

        if (index < days - 1) {

            return Double.NaN;
        }

        double sum = 0;

        for (int i = index - days + 1;
             i <= index;
             i++) {

            sum +=
                    displayedPrices
                            .get(i)
                            .getClose();
        }

        return sum / days;
    }

    // =====================================================
    // FIND HOVERED CANDLE
    // =====================================================

   
private void updateHoveredPoint() {

    LinkedList<StockPrice> displayedPrices =
            getDisplayedPrices();

    hoveredIndex = -1;

    if (displayedPrices.size() < 2) {
        return;
    }

    int left = 15;
    int right = 75;
    int top = 45;
    int bottom = 45;

    int chartWidth = getWidth() - left - right;
    int chartHeight = getHeight() - top - bottom;

    if (chartWidth <= 0 || chartHeight <= 0) {
        return;
    }

    // Match the price range used to draw the candles.
    double minPrice = Double.MAX_VALUE;
    double maxPrice = -Double.MAX_VALUE;

    for (StockPrice price : displayedPrices) {
        minPrice = Math.min(minPrice, price.getLow());
        maxPrice = Math.max(maxPrice, price.getHigh());
    }

    double priceRange = maxPrice - minPrice;

    if (priceRange <= 0) {
        priceRange = 1;
    }

    double padding = priceRange * 0.08;
    minPrice -= padding;
    maxPrice += padding;

    int count = displayedPrices.size();
    double candleSpacing = (double) chartWidth / count;

    int candleWidth = Math.max(
            2,
            (int) (candleSpacing * 0.65)
    );

    // Only activate hover when the pointer touches a candle's
    // body or wick.
    for (int i = 0; i < count; i++) {

        StockPrice stock = displayedPrices.get(i);

        int x = left + (int) (
                i * candleSpacing + candleSpacing / 2
        );

        int highY = getY(
                stock.getHigh(), minPrice, maxPrice,
                top, chartHeight
        );

        int lowY = getY(
                stock.getLow(), minPrice, maxPrice,
                top, chartHeight
        );

        int openY = getY(
                stock.getOpen(), minPrice, maxPrice,
                top, chartHeight
        );

        int closeY = getY(
                stock.getClose(), minPrice, maxPrice,
                top, chartHeight
        );

        int candleLeft = x - candleWidth / 2;
        int candleRight = x + candleWidth / 2;

        int candleTop = Math.min(openY, closeY);
        int candleBottom = Math.max(openY, closeY);

        boolean overCandleBody =
                mouseX >= candleLeft - 2
                && mouseX <= candleRight + 2
                && mouseY >= candleTop - 2
                && mouseY <= candleBottom + 2;

        boolean overCandleWick =
                Math.abs(mouseX - x) <= 3
                && mouseY >= highY
                && mouseY <= lowY;

        if (overCandleBody || overCandleWick) {
            hoveredIndex = i;
            return;
        }
    }
}


    // =====================================================
    // X POSITION
    // =====================================================

    private int getX(
            int index,
            int size,
            int left,
            int chartWidth) {

        if (size <= 1) {

            return left;
        }

        return left +
                (int) (
                        (
                                (double) index
                                /
                                (size - 1)
                        )
                        *
                        chartWidth
                );
    }

    // =====================================================
    // Y POSITION
    // =====================================================

    private int getY(
            double price,
            double minPrice,
            double maxPrice,
            int top,
            int chartHeight) {

        double percentage =
                (
                        price - minPrice
                )
                /
                (
                        maxPrice - minPrice
                );

        return top +
                (int) (
                        chartHeight
                        *
                        (1 - percentage)
                );
    }
}