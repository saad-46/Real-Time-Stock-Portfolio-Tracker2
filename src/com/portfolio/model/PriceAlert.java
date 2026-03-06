package com.portfolio.model;

/**
 * PriceAlert - Represents a user-defined price notification trigger.
 */
public class PriceAlert {
    private String symbol;
    private double targetPrice;
    private boolean alertAbove; // true for "Above Price", false for "Below Price"
    private boolean triggered;

    public PriceAlert(String symbol, double targetPrice, boolean alertAbove) {
        this.symbol = symbol;
        this.targetPrice = targetPrice;
        this.alertAbove = alertAbove;
        this.triggered = false;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getTargetPrice() {
        return targetPrice;
    }

    public boolean isAlertAbove() {
        return alertAbove;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    /**
     * Checks if the current price satisfies the alert condition.
     */
    public boolean checkCondition(double currentPrice) {
        if (triggered)
            return false;

        if (alertAbove && currentPrice >= targetPrice) {
            return true;
        } else if (!alertAbove && currentPrice <= targetPrice) {
            return true;
        }
        return false;
    }
}
