package com.portfolio.service;

import com.portfolio.model.Stock;

public class MultiSourceStockService implements StockPriceService {

    private AlphaVantageService alphaVantageService;

    public MultiSourceStockService() {
        this.alphaVantageService = new AlphaVantageService();
    }

    @Override
    public double getCurrentPrice(String symbol) throws Exception {
        System.out.println("🔄 Fetching from MultiSourceStockService...");
        // Fallback chain: simply using AlphaVantage for now since others aren't
        // implemented in the repo
        return alphaVantageService.getCurrentPrice(symbol);
    }

    @Override
    public void updateStockPrice(Stock stock) throws Exception {
        System.out.println("🔄 Updating via MultiSourceStockService...");
        alphaVantageService.updateStockPrice(stock);
    }

    @Override
    public String getHistoricalData(String symbol) throws Exception {
        System.out.println("🔄 Fetching historical data via MultiSourceStockService...");
        return alphaVantageService.getHistoricalData(symbol);
    }
}
