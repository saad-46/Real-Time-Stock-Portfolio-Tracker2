package com.portfolio.service;

import java.util.*;
import java.util.concurrent.*;

/**
 * DailyPortfolioScheduler - Schedules daily portfolio update emails
 * Runs at a specific time each day (e.g., 9:00 AM)
 */
public class DailyPortfolioScheduler {
    
    private final PortfolioService portfolioService;
    private final EmailService emailService;
    private ScheduledExecutorService scheduler;
    private String recipientEmail;
    private boolean isRunning = false;
    
    public DailyPortfolioScheduler(PortfolioService portfolioService, EmailService emailService) {
        this.portfolioService = portfolioService;
        this.emailService = emailService;
    }
    
    /**
     * Start daily email scheduler at 8:00 PM
     * @param email Recipient email address
     */
    public void start(String email) {
        if (isRunning) {
            System.out.println("⚠️ Scheduler already running");
            return;
        }
        
        this.recipientEmail = email;
        this.scheduler = Executors.newScheduledThreadPool(1);
        
        // Calculate delay until next 8:00 PM (20:00)
        Calendar now = Calendar.getInstance();
        Calendar nextRun = Calendar.getInstance();
        nextRun.set(Calendar.HOUR_OF_DAY, 20);  // 8 PM = 20:00
        nextRun.set(Calendar.MINUTE, 0);
        nextRun.set(Calendar.SECOND, 0);
        
        // If it's past 8 PM today, schedule for tomorrow
        if (now.after(nextRun)) {
            nextRun.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        long initialDelay = nextRun.getTimeInMillis() - now.getTimeInMillis();
        long period = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("📧 Sending daily portfolio update at 8:00 PM...");
                emailService.sendDailyPortfolioUpdate(recipientEmail, portfolioService);
            } catch (Exception e) {
                System.err.println("❌ Failed to send scheduled email: " + e.getMessage());
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);
        
        isRunning = true;
        System.out.println("✅ Daily email scheduler started. Next email at: " + nextRun.getTime());
        System.out.println("📧 Will send to: " + recipientEmail + " every day at 8:00 PM");
    }
    
    /**
     * Stop the scheduler
     */
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            isRunning = false;
            System.out.println("⏹️ Daily email scheduler stopped");
        }
    }
    
    /**
     * Send immediate test email
     */
    public void sendTestEmail() {
        try {
            System.out.println("📧 Sending test email...");
            emailService.sendDailyPortfolioUpdate(recipientEmail, portfolioService);
            System.out.println("✅ Test email sent successfully!");
        } catch (Exception e) {
            System.err.println("❌ Test email failed: " + e.getMessage());
        }
    }
    
    /**
     * Check if scheduler is running
     */
    public boolean isRunning() {
        return isRunning;
    }
}
