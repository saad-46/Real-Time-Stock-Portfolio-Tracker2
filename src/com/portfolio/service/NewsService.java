package com.portfolio.service;

import java.net.URI;
import java.net.http.*;
import java.util.*;

public class NewsService {
    private static final String API_KEY = "YOUR_API_KEY"; // Should be retrieved from AlphaVantageService
    private final HttpClient httpClient;

    public NewsService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<NewsItem> fetchMarketNews() {
        List<NewsItem> newsItems = new ArrayList<>();
        try {
            // Placeholder: In a real app, use Alpha Vantage NEWS_SENTIMENT endpoint
            // For now, providing high-quality mock news with sentiment
            newsItems.add(new NewsItem("Fed Signals Potential Rate Cut in Q3", "Economic", "Neutral",
                    "https://example.com/news1"));
            newsItems.add(new NewsItem("Tech Stocks Surge as AI Demand Hits Record Highs", "Technology", "Bullish",
                    "https://example.com/news2"));
            newsItems.add(new NewsItem("Oil Prices Dip Amid Global Supply Concerns", "Energy", "Bearish",
                    "https://example.com/news3"));
            newsItems.add(new NewsItem("Banking Sector Shows Resilience in Stress Tests", "Finance", "Bullish",
                    "https://example.com/news4"));
            newsItems.add(new NewsItem("Automobile Manufacturers Pivot Faster to EVs", "Automobile", "Bullish",
                    "https://example.com/news5"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsItems;
    }

    public static class NewsItem {
        private String title;
        private String category;
        private String sentiment;
        private String url;

        public NewsItem(String title, String category, String sentiment, String url) {
            this.title = title;
            this.category = category;
            this.sentiment = sentiment;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        public String getSentiment() {
            return sentiment;
        }

        public String getUrl() {
            return url;
        }
    }
}
