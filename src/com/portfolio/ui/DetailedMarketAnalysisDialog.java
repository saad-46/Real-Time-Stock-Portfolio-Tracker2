package com.portfolio.ui;

import com.portfolio.model.Stock;
import com.portfolio.model.PortfolioItem;
import com.portfolio.service.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.time.*;
import org.jfree.data.xy.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Detailed Market Analysis Dialog - Institutional-grade stock analysis modal
 * Features: Real-time charts, time range filters, crosshairs, AI insights, price alerts
 */
public class DetailedMarketAnalysisDialog extends JDialog {
    
    private static final Color BG = new Color(15, 15, 15);
    private static final Color CARD_BG = new Color(30, 30, 46);
    private static final Color ACCENT = new Color(102, 126, 234);
    private static final Color ACCENT2 = new Color(118, 75, 162);
    private static final Color GREEN = new Color(74, 222, 128);
    private static final Color RED = new Color(248, 113, 113);
    private static final Color TEXT = new Color(255, 255, 255);
    private static final Color TEXT_DIM = new Color(180, 180, 200);
    private static final Color BORDER = new Color(60, 60, 90);
    
    private Stock stock;
    private PortfolioService portfolioService;
    private RuleBasedAIAnalysis aiAnalysis;
    private ChartPanel chartPanel;
    private JFreeChart chart;
    private TimeSeriesCollection dataset;
    private javax.swing.Timer priceUpdateTimer;
    private JLabel currentPriceLabel;
    private JLabel todayHighLabel;
    private JLabel todayLowLabel;
    private String selectedTimeRange = "1M";
    
    public DetailedMarketAnalysisDialog(JFrame parent, Stock stock, PortfolioService portfolioService) {
        super(parent, stock.getSymbol() + " - Detailed Market Analysis", true);
        this.stock = stock;
        this.portfolioService = portfolioService;
        this.aiAnalysis = new RuleBasedAIAnalysis(portfolioService);
        
        setSize(1200, 750);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG);
        
        initializeUI();
        startRealTimeUpdates();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(0, 0));
        
        // Header with close button
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);
        
        // Main content split: Chart (left) + Metrics (right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(750);
        splitPane.setBackground(BG);
        splitPane.setBorder(null);
        
        // Left: Chart Panel
        JPanel chartSection = createChartSection();
        splitPane.setLeftComponent(chartSection);
        
        // Right: Metrics & Actions Panel
        JPanel metricsSection = createMetricsSection();
        splitPane.setRightComponent(metricsSection);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 20, 40));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel(stock.getSymbol() + " Real-Time Index");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);
        
        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        closeBtn.setForeground(TEXT_DIM);
        closeBtn.setBackground(new Color(40, 40, 60));
        closeBtn.setBorder(new EmptyBorder(5, 15, 5, 15));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createChartSection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBackground(BG);
        section.setBorder(new EmptyBorder(15, 20, 20, 10));
        
        // Time range filter buttons
        JPanel filterPanel = createTimeRangeFilters();
        section.add(filterPanel, BorderLayout.NORTH);
        
        // Chart with crosshairs
        chartPanel = createRealTimeChart();
        section.add(chartPanel, BorderLayout.CENTER);
        
        // Action buttons at bottom
        JPanel actionPanel = createActionButtons();
        section.add(actionPanel, BorderLayout.SOUTH);
        
        return section;
    }
    
    private JPanel createTimeRangeFilters() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(BG);
        
        String[] ranges = {"1D", "1W", "1M", "6M", "1Y"};
        ButtonGroup group = new ButtonGroup();
        
        for (String range : ranges) {
            JToggleButton btn = new JToggleButton(range);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setForeground(TEXT_DIM);
            btn.setBackground(CARD_BG);
            btn.setBorder(new EmptyBorder(8, 20, 8, 20));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (range.equals("1M")) {
                btn.setSelected(true);
                btn.setForeground(TEXT);
                btn.setBackground(ACCENT);
            }
            
            btn.addActionListener(e -> {
                selectedTimeRange = range;
                updateChartData(range);
                // Update button styles
                for (Component c : panel.getComponents()) {
                    if (c instanceof JToggleButton) {
                        JToggleButton tb = (JToggleButton) c;
                        if (tb.isSelected()) {
                            tb.setForeground(TEXT);
                            tb.setBackground(ACCENT);
                        } else {
                            tb.setForeground(TEXT_DIM);
                            tb.setBackground(CARD_BG);
                        }
                    }
                }
            });
            
            group.add(btn);
            panel.add(btn);
        }
        
        return panel;
    }
    
    private ChartPanel createRealTimeChart() {
        // Create time series dataset
        dataset = new TimeSeriesCollection();
        TimeSeries series = new TimeSeries(stock.getSymbol());
        
        // Generate historical data (simulated for demo)
        generateHistoricalData(series, 30);
        dataset.addSeries(series);
        
        // Create chart
        chart = ChartFactory.createTimeSeriesChart(
            stock.getSymbol() + " Real-Time Index",
            "Time",
            "Price (₹)",
            dataset,
            false,
            true,
            false
        );
        
        styleInstitutionalChart(chart);
        
        ChartPanel panel = new ChartPanel(chart);
        panel.setBackground(CARD_BG);
        panel.setMouseWheelEnabled(true);
        panel.setDomainZoomable(true);
        panel.setRangeZoomable(true);
        
        // Enable crosshair tracking
        panel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {}
            
            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setDomainCrosshairVisible(true);
                plot.setRangeCrosshairVisible(true);
            }
        });
        
        return panel;
    }

    
    private void styleInstitutionalChart(JFreeChart chart) {
        chart.setBackgroundPaint(CARD_BG);
        chart.getTitle().setPaint(TEXT);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.setBorderVisible(false);
        
        XYPlot plot = (XYPlot) chart.getPlot();
        
        // Gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(102, 126, 234, 20),
            0, 400, new Color(118, 75, 162, 10)
        );
        plot.setBackgroundPaint(gradient);
        plot.setOutlineVisible(false);
        
        // High-density grid lines
        plot.setRangeGridlinePaint(new Color(102, 126, 234, 30));
        plot.setDomainGridlinePaint(new Color(102, 126, 234, 20));
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        
        plot.setRangeGridlineStroke(new BasicStroke(0.7f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 1.0f, new float[]{2.0f, 2.0f}, 0.0f));
        plot.setDomainGridlineStroke(new BasicStroke(0.5f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 1.0f, new float[]{1.5f, 1.5f}, 0.0f));
        
        // Professional crosshairs
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setDomainCrosshairPaint(new Color(102, 126, 234, 150));
        plot.setRangeCrosshairPaint(new Color(102, 126, 234, 150));
        plot.setDomainCrosshairStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 1.0f, new float[]{5.0f, 3.0f}, 0.0f));
        plot.setRangeCrosshairStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 1.0f, new float[]{5.0f, 3.0f}, 0.0f));
        
        // Axes styling
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setLabelPaint(TEXT);
        domainAxis.setTickLabelPaint(TEXT_DIM);
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 11));
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        domainAxis.setDateFormatOverride(new SimpleDateFormat("MMM dd"));
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelPaint(TEXT);
        rangeAxis.setTickLabelPaint(TEXT_DIM);
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 11));
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        // Line renderer with gradient fill
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, ACCENT);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesShapesVisible(0, false);
        
        // Add gradient fill under line
        renderer.setSeriesFillPaint(0, new GradientPaint(
            0, 0, new Color(102, 126, 234, 80),
            0, 400, new Color(102, 126, 234, 10)
        ));
        renderer.setUseFillPaint(true);
        
        plot.setRenderer(renderer);
    }
    
    private void generateHistoricalData(TimeSeries series, int days) {
        Calendar cal = Calendar.getInstance();
        double basePrice = stock.getCurrentPrice();
        Random random = new Random();
        
        for (int i = days; i >= 0; i--) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            double variation = (random.nextDouble() - 0.5) * basePrice * 0.05;
            double price = basePrice + variation;
            series.add(new Day(cal.getTime()), price);
        }
    }
    
    private void updateChartData(String range) {
        TimeSeries series = dataset.getSeries(0);
        series.clear();
        
        int days = switch (range) {
            case "1D" -> 1;
            case "1W" -> 7;
            case "1M" -> 30;
            case "6M" -> 180;
            case "1Y" -> 365;
            default -> 30;
        };
        
        generateHistoricalData(series, days);
        
        // Update date format based on range
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        
        if (range.equals("1D")) {
            domainAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
        } else if (range.equals("1W")) {
            domainAxis.setDateFormatOverride(new SimpleDateFormat("EEE"));
        } else {
            domainAxis.setDateFormatOverride(new SimpleDateFormat("MMM dd"));
        }
    }
    
    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(BG);
        
        JButton buyBtn = createStyledButton("💰 Buy Stock", GREEN);
        buyBtn.addActionListener(e -> showBuyDialog());
        
        JButton sellBtn = createStyledButton("💸 Sell Stock", RED);
        sellBtn.addActionListener(e -> showSellDialog());
        
        JButton alertBtn = createStyledButton("🔔 Price Alert", ACCENT);
        alertBtn.addActionListener(e -> showPriceAlertDialog());
        
        panel.add(buyBtn);
        panel.add(sellBtn);
        panel.add(alertBtn);
        
        return panel;
    }
    
    private JPanel createMetricsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(BG);
        section.setBorder(new EmptyBorder(15, 10, 20, 20));
        
        // Security Metrics Card
        JPanel metricsCard = createSecurityMetricsCard();
        section.add(metricsCard);
        section.add(Box.createVerticalStrut(15));
        
        // AI Insights Card
        JPanel aiCard = createAIInsightsCard();
        section.add(aiCard);
        
        return section;
    }
    
    private JPanel createSecurityMetricsCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);
        
        JLabel title = new JLabel("⚡ Security Metrics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);
        header.add(title);
        
        JLabel favIcon = new JLabel("♡");
        favIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        favIcon.setForeground(GREEN);
        favIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        header.add(favIcon);
        
        card.add(header);
        card.add(Box.createVerticalStrut(15));
        
        // Metrics
        currentPriceLabel = addMetricRow(card, "Current Price", String.format("₹%.2f", stock.getCurrentPrice()));
        todayHighLabel = addMetricRow(card, "Today's High", String.format("₹%.2f", stock.getCurrentPrice() * 1.02));
        todayLowLabel = addMetricRow(card, "Today's Low", String.format("₹%.2f", stock.getCurrentPrice() * 0.98));
        addMetricRow(card, "52 Week High", "₹19,250.00");
        addMetricRow(card, "52 Week Low", "₹14,100.00");
        addMetricRow(card, "Volume", "42.5M");
        addMetricRow(card, "P/E Ratio", "28.4");
        addMetricRow(card, "Dividend Yield", "0.65%");
        addMetricRow(card, "Sector", stock.getSector() != null ? stock.getSector() : "Technology");
        addMetricRow(card, "Industry", "Consumer Electronics");
        
        return card;
    }
    
    private JLabel addMetricRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelComp.setForeground(TEXT_DIM);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valueComp.setForeground(TEXT);
        
        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);
        
        parent.add(row);
        return valueComp;
    }

    
    private JPanel createAIInsightsCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Header
        JLabel title = new JLabel("🤖 AI Insights");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(15));
        
        // Analyze stock
        RuleBasedAIAnalysis.AnalysisResult analysis = aiAnalysis.analyzeStock(stock);
        
        // AI Score Badge
        JPanel scoreBadge = createScoreBadge(analysis.totalScore);
        scoreBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(scoreBadge);
        card.add(Box.createVerticalStrut(15));
        
        // Recommendation
        JPanel recPanel = createRecommendationPanel(analysis.recommendation);
        recPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(recPanel);
        card.add(Box.createVerticalStrut(15));
        
        // Insight text
        JTextArea insightText = new JTextArea(analysis.insight);
        insightText.setWrapStyleWord(true);
        insightText.setLineWrap(true);
        insightText.setEditable(false);
        insightText.setBackground(CARD_BG);
        insightText.setForeground(TEXT_DIM);
        insightText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        insightText.setBorder(new EmptyBorder(10, 0, 10, 0));
        insightText.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(insightText);
        
        // Rule Cards
        card.add(createRuleCard("📈 Trend", analysis.trend, getTrendIcon(analysis.trend)));
        card.add(Box.createVerticalStrut(8));
        card.add(createRuleCard("⚡ Momentum", analysis.momentum, getMomentumIcon(analysis.momentum)));
        card.add(Box.createVerticalStrut(8));
        card.add(createRuleCard("📊 Volatility", analysis.volatility, getVolatilityIcon(analysis.volatility)));
        card.add(Box.createVerticalStrut(8));
        card.add(createRuleCard("🎯 Diversification", analysis.diversificationImpact, 
            getDiversificationIcon(analysis.diversificationImpact)));
        
        return card;
    }
    
    private JPanel createScoreBadge(int score) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        badge.setOpaque(false);
        
        JLabel scoreLabel = new JLabel(score + " / 10");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        Color scoreColor = score >= 8 ? GREEN : (score >= 5 ? new Color(251, 191, 36) : RED);
        scoreLabel.setForeground(scoreColor);
        
        badge.add(scoreLabel);
        
        return badge;
    }
    
    private JPanel createRecommendationPanel(String recommendation) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(true);
        
        Color bgColor;
        String icon;
        
        if (recommendation.contains("Strong Buy")) {
            bgColor = new Color(74, 222, 128, 30);
            icon = "✓";
        } else if (recommendation.contains("Buy")) {
            bgColor = new Color(74, 222, 128, 20);
            icon = "↗";
        } else if (recommendation.contains("Hold")) {
            bgColor = new Color(251, 191, 36, 20);
            icon = "=";
        } else {
            bgColor = new Color(248, 113, 113, 20);
            icon = "↓";
        }
        
        panel.setBackground(bgColor);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(102, 126, 234, 60), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconLabel.setForeground(GREEN);
        
        JLabel recLabel = new JLabel(recommendation);
        recLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        recLabel.setForeground(TEXT);
        recLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        panel.add(iconLabel);
        panel.add(recLabel);
        
        return panel;
    }
    
    private JPanel createRuleCard(String label, String value, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(40, 40, 60));
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(102, 126, 234, 40), 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelComp.setForeground(TEXT_DIM);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valueComp.setForeground(TEXT);
        
        textPanel.add(labelComp);
        textPanel.add(valueComp);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private String getTrendIcon(String trend) {
        if (trend.contains("Uptrend") || trend.contains("Bullish")) return "📈";
        if (trend.contains("Downtrend") || trend.contains("Bearish")) return "📉";
        return "➡️";
    }
    
    private String getMomentumIcon(String momentum) {
        if (momentum.contains("Strong")) return "⚡";
        if (momentum.contains("Moderate")) return "🔋";
        return "🔌";
    }
    
    private String getVolatilityIcon(String volatility) {
        if (volatility.contains("High")) return "🔴";
        if (volatility.contains("Moderate")) return "🟡";
        return "🟢";
    }
    
    private String getDiversificationIcon(String diversification) {
        if (diversification.contains("Positive")) return "✅";
        if (diversification.contains("Neutral")) return "➖";
        return "⚠️";
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }
    
    private void startRealTimeUpdates() {
        // Update price every 2 seconds
        priceUpdateTimer = new javax.swing.Timer(2000, e -> {
            // Simulate price fluctuation
            double change = (Math.random() - 0.48) * 0.01;
            double newPrice = stock.getCurrentPrice() * (1 + change);
            stock.setCurrentPrice(newPrice);
            
            // Update labels
            currentPriceLabel.setText(String.format("₹%.2f", newPrice));
            todayHighLabel.setText(String.format("₹%.2f", newPrice * 1.02));
            todayLowLabel.setText(String.format("₹%.2f", newPrice * 0.98));
            
            // Update chart
            TimeSeries series = dataset.getSeries(0);
            series.add(new Millisecond(), newPrice);
            
            // Keep only recent data points
            if (series.getItemCount() > 200) {
                series.delete(0, 0);
            }
        });
        priceUpdateTimer.start();
    }
    
    private void showBuyDialog() {
        JDialog dialog = new JDialog(this, "Buy " + stock.getSymbol(), true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(CARD_BG);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD_BG);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Buy " + stock.getSymbol());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));
        
        JLabel qtyLabel = new JLabel("Quantity:");
        qtyLabel.setForeground(TEXT_DIM);
        qtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(qtyLabel);
        
        JTextField qtyField = new JTextField("10");
        qtyField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qtyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        qtyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(qtyField);
        content.add(Box.createVerticalStrut(15));
        
        JLabel priceLabel = new JLabel("Price: ₹" + String.format("%.2f", stock.getCurrentPrice()));
        priceLabel.setForeground(TEXT);
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(priceLabel);
        content.add(Box.createVerticalStrut(20));
        
        JButton confirmBtn = createStyledButton("Confirm Purchase", GREEN);
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmBtn.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(qtyField.getText());
                portfolioService.buy(stock.getSymbol(), qty, stock.getCurrentPrice());
                JOptionPane.showMessageDialog(dialog, 
                    "Successfully bought " + qty + " shares of " + stock.getSymbol(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        content.add(confirmBtn);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showSellDialog() {
        JDialog dialog = new JDialog(this, "Sell " + stock.getSymbol(), true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(CARD_BG);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD_BG);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Sell " + stock.getSymbol());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));
        
        JLabel qtyLabel = new JLabel("Quantity:");
        qtyLabel.setForeground(TEXT_DIM);
        qtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(qtyLabel);
        
        JTextField qtyField = new JTextField("10");
        qtyField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qtyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        qtyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(qtyField);
        content.add(Box.createVerticalStrut(15));
        
        JLabel priceLabel = new JLabel("Price: ₹" + String.format("%.2f", stock.getCurrentPrice()));
        priceLabel.setForeground(TEXT);
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(priceLabel);
        content.add(Box.createVerticalStrut(20));
        
        JButton confirmBtn = createStyledButton("Confirm Sale", RED);
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmBtn.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(qtyField.getText());
                portfolioService.sellStock(stock.getSymbol(), qty);
                JOptionPane.showMessageDialog(dialog,
                    "Successfully sold " + qty + " shares of " + stock.getSymbol(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        content.add(confirmBtn);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showPriceAlertDialog() {
        JDialog dialog = new JDialog(this, "Set Price Alert", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(CARD_BG);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD_BG);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("🔔 Price Alert for " + stock.getSymbol());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));
        
        JLabel targetLabel = new JLabel("Target Price:");
        targetLabel.setForeground(TEXT_DIM);
        targetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(targetLabel);
        
        JTextField targetField = new JTextField(String.format("%.2f", stock.getCurrentPrice() * 1.05));
        targetField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        targetField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        targetField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(targetField);
        content.add(Box.createVerticalStrut(15));
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setOpaque(false);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JRadioButton aboveBtn = new JRadioButton("Above Price");
        JRadioButton belowBtn = new JRadioButton("Below Price");
        aboveBtn.setSelected(true);
        aboveBtn.setForeground(TEXT);
        belowBtn.setForeground(TEXT);
        aboveBtn.setOpaque(false);
        belowBtn.setOpaque(false);
        
        ButtonGroup group = new ButtonGroup();
        group.add(aboveBtn);
        group.add(belowBtn);
        
        radioPanel.add(aboveBtn);
        radioPanel.add(belowBtn);
        content.add(radioPanel);
        content.add(Box.createVerticalStrut(20));
        
        JButton setBtn = createStyledButton("Set Alert", ACCENT);
        setBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        setBtn.addActionListener(e -> {
            try {
                double targetPrice = Double.parseDouble(targetField.getText());
                String condition = aboveBtn.isSelected() ? "ABOVE" : "BELOW";
                
                // Add alert to portfolio service
                com.portfolio.model.PriceAlert alert = new com.portfolio.model.PriceAlert(
                    stock.getSymbol(), targetPrice, condition.equals("ABOVE"));
                portfolioService.addPriceAlert(alert);
                
                JOptionPane.showMessageDialog(dialog,
                    "Alert set: Notify when " + stock.getSymbol() + " goes " + 
                    condition.toLowerCase() + " ₹" + String.format("%.2f", targetPrice),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        content.add(setBtn);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    @Override
    public void dispose() {
        if (priceUpdateTimer != null) {
            priceUpdateTimer.stop();
        }
        super.dispose();
    }
}
