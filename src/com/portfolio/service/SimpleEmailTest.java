package com.portfolio.service;

import com.portfolio.model.PortfolioItem;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * SimpleEmailTest - Basic email testing without JavaMail
 * Uses a simple HTTP service to send test emails
 */
public class SimpleEmailTest {
    
    private final PortfolioService portfolioService;
    
    public SimpleEmailTest(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }
    
    /**
     * Generate portfolio email content and save to file
     * This simulates what would be sent via email
     */
    public void generatePortfolioReport(String userEmail) {
        try {
            // Calculate portfolio metrics
            double totalValue = portfolioService.calculateCurrentValue();
            double totalInvestment = portfolioService.calculateTotalInvestment();
            double profitLoss = portfolioService.calculateProfitLoss();
            double profitPercent = totalInvestment > 0 ? (profitLoss / totalInvestment) * 100 : 0;
            
            List<PortfolioItem> items = portfolioService.getPortfolioItems();
            
            // Build email content
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<!DOCTYPE html><html><head><title>StockVault Portfolio Report</title></head>");
            emailBody.append("<body style='font-family: Arial, sans-serif; background: #f5f5f5; padding: 20px;'>");
            emailBody.append("<div style='max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>");
            
            // Header
            emailBody.append("<h1 style='color: #667eea; margin: 0 0 10px 0;'>💎 StockVault Portfolio Report</h1>");
            emailBody.append("<p style='color: #666; font-size: 14px;'>Generated on: ").append(new Date().toString()).append("</p>");
            emailBody.append("<p style='color: #666; font-size: 14px;'>For: ").append(userEmail).append("</p>");
            emailBody.append("<hr style='border: none; border-top: 2px solid #667eea; margin: 20px 0;'>");
            
            // Summary Cards
            emailBody.append("<div style='display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px; margin: 20px 0;'>");
            
            emailBody.append("<div style='background: #f0f4ff; padding: 15px; border-radius: 8px;'>");
            emailBody.append("<div style='color: #667eea; font-size: 12px; font-weight: bold;'>TOTAL VALUE</div>");
            emailBody.append("<div style='color: #333; font-size: 24px; font-weight: bold;'>₹").append(String.format("%.2f", totalValue)).append("</div>");
            emailBody.append("</div>");
            
            emailBody.append("<div style='background: #f0f4ff; padding: 15px; border-radius: 8px;'>");
            emailBody.append("<div style='color: #667eea; font-size: 12px; font-weight: bold;'>INVESTED</div>");
            emailBody.append("<div style='color: #333; font-size: 24px; font-weight: bold;'>₹").append(String.format("%.2f", totalInvestment)).append("</div>");
            emailBody.append("</div>");
            
            String plColor = profitLoss >= 0 ? "#4ade80" : "#f87171";
            emailBody.append("<div style='background: ").append(profitLoss >= 0 ? "#f0fdf4" : "#fef2f2").append("; padding: 15px; border-radius: 8px;'>");
            emailBody.append("<div style='color: ").append(plColor).append("; font-size: 12px; font-weight: bold;'>PROFIT/LOSS</div>");
            emailBody.append("<div style='color: ").append(plColor).append("; font-size: 24px; font-weight: bold;'>₹").append(String.format("%.2f", profitLoss)).append("</div>");
            emailBody.append("</div>");
            
            emailBody.append("<div style='background: ").append(profitLoss >= 0 ? "#f0fdf4" : "#fef2f2").append("; padding: 15px; border-radius: 8px;'>");
            emailBody.append("<div style='color: ").append(plColor).append("; font-size: 12px; font-weight: bold;'>RETURN</div>");
            emailBody.append("<div style='color: ").append(plColor).append("; font-size: 24px; font-weight: bold;'>").append(String.format("%.2f%%", profitPercent)).append("</div>");
            emailBody.append("</div>");
            
            emailBody.append("</div>");
            
            // Holdings Table
            if (!items.isEmpty()) {
                emailBody.append("<h3 style='color: #333; margin: 30px 0 15px 0;'>📊 Your Holdings</h3>");
                emailBody.append("<table style='width: 100%; border-collapse: collapse;'>");
                emailBody.append("<thead>");
                emailBody.append("<tr style='background: #f5f5f5; text-align: left;'>");
                emailBody.append("<th style='padding: 10px; border-bottom: 2px solid #ddd;'>Symbol</th>");
                emailBody.append("<th style='padding: 10px; border-bottom: 2px solid #ddd;'>Qty</th>");
                emailBody.append("<th style='padding: 10px; border-bottom: 2px solid #ddd;'>Price</th>");
                emailBody.append("<th style='padding: 10px; border-bottom: 2px solid #ddd;'>Value</th>");
                emailBody.append("<th style='padding: 10px; border-bottom: 2px solid #ddd;'>P/L</th>");
                emailBody.append("</tr>");
                emailBody.append("</thead>");
                emailBody.append("<tbody>");
                
                for (PortfolioItem item : items) {
                    double gainLoss = item.getGainLoss();
                    String rowColor = gainLoss >= 0 ? "#4ade80" : "#f87171";
                    
                    emailBody.append("<tr>");
                    emailBody.append("<td style='padding: 10px; border-bottom: 1px solid #eee;'><strong>").append(item.getStock().getSymbol()).append("</strong></td>");
                    emailBody.append("<td style='padding: 10px; border-bottom: 1px solid #eee;'>").append(item.getQuantity()).append("</td>");
                    emailBody.append("<td style='padding: 10px; border-bottom: 1px solid #eee;'>₹").append(String.format("%.2f", item.getStock().getCurrentPrice())).append("</td>");
                    emailBody.append("<td style='padding: 10px; border-bottom: 1px solid #eee;'>₹").append(String.format("%.2f", item.getTotalValue())).append("</td>");
                    emailBody.append("<td style='padding: 10px; border-bottom: 1px solid #eee; color: ").append(rowColor).append("; font-weight: bold;'>₹").append(String.format("%.2f", gainLoss)).append("</td>");
                    emailBody.append("</tr>");
                }
                
                emailBody.append("</tbody>");
                emailBody.append("</table>");
            }
            
            // Alerts Section
            emailBody.append("<div style='background: #fff7ed; border-left: 4px solid #fb923c; padding: 15px; margin: 20px 0; border-radius: 4px;'>");
            emailBody.append("<h4 style='color: #fb923c; margin: 0 0 10px 0;'>⚠️ Alerts & Recommendations</h4>");
            
            if (profitLoss < 0) {
                emailBody.append("<p style='margin: 5px 0; color: #666;'>• Your portfolio is currently in loss. Consider reviewing your holdings.</p>");
            } else {
                emailBody.append("<p style='margin: 5px 0; color: #666;'>• Your portfolio is performing well! Keep monitoring market trends.</p>");
            }
            
            // Check for big movers
            for (PortfolioItem item : items) {
                double changePercent = item.getStock().getChangePercent();
                if (Math.abs(changePercent) > 5) {
                    emailBody.append("<p style='margin: 5px 0; color: #666;'>• <strong>").append(item.getStock().getSymbol()).append("</strong> moved ").append(String.format("%.2f%%", changePercent)).append(" today!</p>");
                }
            }
            
            emailBody.append("</div>");
            
            // Footer
            emailBody.append("<div style='text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee;'>");
            emailBody.append("<p style='color: #999; font-size: 12px;'>This is a test email from StockVault Portfolio Tracker</p>");
            emailBody.append("<p style='color: #999; font-size: 12px;'>Generated at: ").append(new Date()).append("</p>");
            emailBody.append("</div>");
            
            emailBody.append("</div>");
            emailBody.append("</body></html>");
            
            // Save to file
            String filename = "Portfolio_Report_" + System.currentTimeMillis() + ".html";
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(emailBody.toString());
            }
            
            System.out.println("✅ Portfolio report generated: " + filename);
            System.out.println("📧 This would be sent to: " + userEmail);
            
            // Open in browser
            try {
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().browse(new File(filename).toURI());
                }
            } catch (Exception ex) {
                System.out.println("Could not open browser: " + ex.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Failed to generate portfolio report: " + e.getMessage());
            e.printStackTrace();
        }
    }
}