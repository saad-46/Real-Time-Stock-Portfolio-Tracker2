package com.portfolio.model;

import java.time.LocalDate;

public class StockPrice {
    private double price;
    private LocalDate date;

    public StockPrice(double price, LocalDate date) {
        this.price = price;
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public LocalDate getDate() {
        return date;
    }
}
