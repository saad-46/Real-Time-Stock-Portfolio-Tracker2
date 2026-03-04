package com.portfolio.service;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

/**
 * NotificationManager - Handles system tray and in-app notifications.
 * Provides a unified way to alert the user about price movements, trade status,
 * and system events.
 */
public class NotificationManager {
    private static NotificationManager instance;
    private TrayIcon trayIcon;
    private boolean isSystemTraySupported;

    private NotificationManager() {
        this.isSystemTraySupported = SystemTray.isSupported();
        if (isSystemTraySupported) {
            setupSystemTray();
        }
    }

    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    private void setupSystemTray() {
        try {
            SystemTray tray = SystemTray.getSystemTray();

            // Using a generic 💡 emoji as an icon if a custom one isn't available
            // In a real production app, we'd load a .png/ico resourceful image
            Image image = Toolkit.getDefaultToolkit().createImage("");
            trayIcon = new TrayIcon(image, "StockVault");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("StockVault Premium Portfolio");

            // Basic event listener for notifications
            trayIcon.addActionListener(e -> {
                // Focus application when notification is clicked
                // This would be handled by the UI controller
            });

            tray.add(trayIcon);
        } catch (Exception e) {
            System.err.println("Could not initialize system tray: " + e.getMessage());
            isSystemTraySupported = false;
        }
    }

    /**
     * Shows a desktop notification (OS-native)
     */
    public void showNotification(String title, String message, MessageType type) {
        if (isSystemTraySupported && trayIcon != null) {
            trayIcon.displayMessage(title, message, type);
        } else {
            // Fallback to console if system tray isn't available
            System.out.println("[" + type + "] " + title + ": " + message);
        }
    }

    /**
     * Convenience method for price alerts
     */
    public void showPriceAlert(String symbol, double target, double current) {
        String direction = current >= target ? "reached" : "dropped to";
        String message = String.format("%s has %s $%.2f (Target: $%.2f)", symbol, direction, current, target);
        showNotification("📈 Price Alert", message, MessageType.INFO);
    }

    /**
     * Convenience method for trade confirmations
     */
    public void showTradeConfirmation(String symbol, int qty, boolean isBuy) {
        String action = isBuy ? "bought" : "sold";
        String message = String.format("Successfully %s %d shares of %s.", action, qty, symbol);
        showNotification("📝 Trade Confirmed", message, MessageType.INFO);
    }
}
