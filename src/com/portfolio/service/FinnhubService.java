package com.portfolio.service;

import com.portfolio.model.Stock;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * FinnhubService - Primary stock price provider using Finnhub.io API.
 * Free tier: 60 API calls/minute — covers US stocks and major exchanges.
 * Round-robins between multiple API keys for higher throughput.
 */
public class FinnhubService implements StockPriceService {

    private static final String BASE_URL = "https://finnhub.io/api/v1";
    private final HttpClient httpClient;

    public FinnhubService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Gets the current price for a stock symbol from Finnhub.
     * Endpoint: /quote?symbol=AAPL&token=KEY
     * Response: {"c":150.0,"d":-1.5,"dp":-0.99,"h":151.0,"l":149.0,"o":150.5,"pc":151.5,"t":...}
     *   c = current price, d = change, dp = percent change
     */
    @Override
    public double getCurrentPrice(String symbol) throws Exception {
        String apiKey = ApiKeyManager.getFinnhubKey();
        String url = String.format("%s/quote?symbol=%s&token=%s", BASE_URL, symbol.toUpperCase(), apiKey);

        System.out.println("📡 Finnhub: Fetching price for " + symbol + "...");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(java.time.Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Finnhub API request failed with status: " + response.statusCode());
        }

        String body = response.body();
        double price = parseJsonDouble(body, "\"c\":");

        if (price <= 0) {
            throw new Exception("Finnhub returned invalid price for " + symbol + ": " + price);
        }

        System.out.println("✅ Finnhub: " + symbol + " = $" + String.format("%.2f", price));
        return price;
    }

    /**
     * Updates a stock object with the latest price and change percent from Finnhub.
     */
    @Override
    public void updateStockPrice(Stock stock) throws Exception {
        String apiKey = ApiKeyManager.getFinnhubKey();
        String url = String.format("%s/quote?symbol=%s&token=%s",
                BASE_URL, stock.getSymbol().toUpperCase(), apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(java.time.Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Finnhub API failed: " + response.statusCode());
        }

        String body = response.body();
        double price = parseJsonDouble(body, "\"c\":");
        double changePercent = parseJsonDouble(body, "\"dp\":");

        if (price <= 0) {
            throw new Exception("Finnhub returned invalid price for " + stock.getSymbol());
        }

        stock.setCurrentPrice(price);
        stock.setChangePercent(changePercent);
        System.out.println("✅ Finnhub updated " + stock.getSymbol() + ": $" + 
                String.format("%.2f", price) + " (" + String.format("%.2f", changePercent) + "%)");
    }

    /**
     * Search for stocks using Finnhub symbol search.
     * Endpoint: /search?q=apple&token=KEY
     * Returns JSON with "result" array containing matches.
     */
    public String searchStocks(String query) throws Exception {
        String apiKey = ApiKeyManager.getFinnhubKey();
        String url = String.format("%s/search?q=%s&token=%s", BASE_URL, 
                java.net.URLEncoder.encode(query, "UTF-8"), apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(java.time.Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Finnhub search failed: " + response.statusCode());
        }

        return response.body();
    }

    /**
     * Parse a double value from JSON by its key.
     * Example: parseJsonDouble('{"c":150.5,"d":-1.5}', '"c":') returns 150.5
     */
    private double parseJsonDouble(String json, String key) {
        try {
            int index = json.indexOf(key);
            if (index == -1) return 0.0;

            int start = index + key.length();
            // Skip whitespace
            while (start < json.length() && json.charAt(start) == ' ') start++;

            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
                end++;
            }

            String valueStr = json.substring(start, end).trim();
            if (valueStr.equals("null")) return 0.0;
            return Double.parseDouble(valueStr);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
