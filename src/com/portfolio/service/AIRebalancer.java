package com.portfolio.service;

import com.portfolio.model.PortfolioItem;
import java.util.*;

/**
 * AI Rebalancer Service
 * Provides institutional-grade portfolio optimization advice.
 */
public class AIRebalancer {
    private final PortfolioService portfolioService;

    public AIRebalancer(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Analyzes the portfolio and suggests optimization trades.
     */
    public List<RebalanceAdvice> suggestOptimization() {
        List<RebalanceAdvice> adviceList = new ArrayList<>();
        List<PortfolioItem> items = portfolioService.getPortfolioItems();
        if (items.isEmpty())
            return adviceList;

        double totalValue = portfolioService.calculateCurrentValue();

        // Group by sector
        Map<String, Double> sectorAllocation = new HashMap<>();
        for (PortfolioItem item : items) {
            String sector = item.getStock().getSector();
            if (sector == null)
                sector = "Other";
            sectorAllocation.put(sector, sectorAllocation.getOrDefault(sector, 0.0) + item.getTotalValue());
        }

        // Target: Max 30% in any single sector for diversification
        double threshold = totalValue * 0.30;

        for (Map.Entry<String, Double> entry : sectorAllocation.entrySet()) {
            if (entry.getValue() > threshold) {
                double excess = entry.getValue() - threshold;
                adviceList.add(new RebalanceAdvice(
                        entry.getKey(),
                        "OVERWEIGHT",
                        String.format("Sector '%s' exceeds 30%% of portfolio. Consider reducing by %s%.2f.",
                                entry.getKey(), portfolioService.getBaseCurrency(), excess),
                        "Sell a portion of your largest holdings in this sector to reduce risk."));
            }
        }

        // Diversity advice
        if (sectorAllocation.size() < 3) {
            adviceList.add(new RebalanceAdvice(
                    "DIVERSIFICATION",
                    "LOW DIVERSITY",
                    "Your portfolio is concentrated in fewer than 3 sectors.",
                    "Consider adding assets from different sectors (e.g., Tech, Healthcare, Energy) to improve resilience."));
        }

        return adviceList;
    }

    public static class RebalanceAdvice {
        public final String category;
        public final String status;
        public final String message;
        public final String action;

        public RebalanceAdvice(String category, String status, String message, String action) {
            this.category = category;
            this.status = status;
            this.message = message;
            this.action = action;
        }
    }
}
