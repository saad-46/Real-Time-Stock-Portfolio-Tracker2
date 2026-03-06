package com.portfolio.service;

import java.net.URI;
import java.net.http.*;
import java.util.*;
import com.portfolio.model.PortfolioItem;

/**
 * Groq AI Service for Natural Language Conversations
 * Library-independent JSON handling
 */
public class GroqAIService {

    private static final String API_KEY = System.getenv("GROQ_API_KEY") != null ? System.getenv("GROQ_API_KEY")
            : ApiKeyManager.getGroqKey();
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final HttpClient httpClient;
    private final PortfolioService portfolioService;
    private final List<Message> conversationHistory;

    public GroqAIService(PortfolioService portfolioService) {
        this.httpClient = HttpClient.newHttpClient();
        this.portfolioService = portfolioService;
        this.conversationHistory = new ArrayList<>();
    }

    public void clearHistory() {
        conversationHistory.clear();
    }

    public String chat(String userMessage) throws Exception {
        String portfolioContext = buildPortfolioContext();
        if (conversationHistory.isEmpty()) {
            conversationHistory.add(new Message("system",
                    "You are 'StockVault AI', a warm and concise portfolio assistant. Indian Rupees (₹) focus. " +
                            "IMPORTANT: Do NOT use the words 'Buy' or 'Sell' in market condition analysis or recommendations. "
                            +
                            "Use terms like 'Add potential', 'Reduce exposure', 'Strong position', 'Underperforming', etc. "
                            +
                            "Respond with professional insights. Portfolio:\n"
                            + portfolioContext));
        }
        conversationHistory.add(new Message("user", userMessage));

        // Build JSON manually to avoid library issues
        StringBuilder json = new StringBuilder();
        json.append("{ \"model\": \"llama-3.3-70b-versatile\", \"messages\": [");
        for (int i = 0; i < conversationHistory.size(); i++) {
            Message m = conversationHistory.get(i);
            json.append(String.format("{\"role\":\"%s\",\"content\":\"%s\"}", m.role, escapeJson(m.content)));
            if (i < conversationHistory.size() - 1)
                json.append(",");
        }
        json.append("], \"temperature\": 0.7, \"max_tokens\": 150 }");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
            throw new Exception("AI error: " + response.body());

        String aiResponse = parseAIResponse(response.body());
        conversationHistory.add(new Message("assistant", aiResponse));

        // Keep history manageable
        if (conversationHistory.size() > 11) {
            conversationHistory.remove(1);
            conversationHistory.remove(1);
        }

        return aiResponse.trim();
    }

    public String getRecommendations() throws Exception {
        String portfolioContext = buildPortfolioContext();
        String prompt = "As a professional financial advisor, analyze this portfolio and provide 3-5 high-quality recommendations in Markdown. "
                +
                "IMPORTANT: Do NOT use the words 'Buy' or 'Sell'. Use modern financial terminology instead. Portfolio:\n"
                + portfolioContext;

        String json = String.format(
                "{ \"model\": \"llama-3.3-70b-versatile\", \"messages\": [{\"role\":\"user\",\"content\":\"%s\"}], \"temperature\": 0.6, \"max_tokens\": 800 }",
                escapeJson(prompt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
            throw new Exception("AI Recommendation error");

        return parseAIResponse(response.body());
    }

    public String executeAction(String userIntent) {
        try {
            String lowerIntent = userIntent.toLowerCase();
            // Basic hardcoded logic for common intents
            if (lowerIntent.contains("sell all") || lowerIntent.contains("clear portfolio") ||
                    lowerIntent.contains("remove all")) {
                StringBuilder result = new StringBuilder("✅ Selling all stocks:\n");
                List<PortfolioItem> items = new ArrayList<>(portfolioService.getPortfolioItems());
                if (items.isEmpty())
                    return "❌ Portfolio is already empty.";
                for (PortfolioItem item : items) {
                    portfolioService.sellAllStock(item.getStock().getSymbol());
                    result.append("• Sold all of ").append(item.getStock().getSymbol()).append("\n");
                }
                return result.toString();
            }

            // AI Intent extraction
            String prompt = "Extract trading action from: \"" + userIntent
                    + "\". Respond ONLY with JSON: {\"action\":\"buy|sell|show_portfolio\",\"symbol\":\"TICKER\",\"quantity\":10,\"price\":150}";
            String json = String.format(
                    "{ \"model\": \"llama-3.3-70b-versatile\", \"messages\": [{\"role\":\"user\",\"content\":\"%s\"}], \"temperature\": 0.1, \"max_tokens\": 100 }",
                    escapeJson(prompt));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check for errors
            if (response.statusCode() != 200) {
                throw new Exception("API returned status " + response.statusCode() + ": " + response.body());
            }

            String aiResp = parseAIResponse(response.body());

            // Simple manual extraction from AI JSON response
            if (aiResp.contains("\"buy\"") || aiResp.contains("buy")) {
                String symbol = extractField(aiResp, "symbol");
                String qtyStr = extractField(aiResp, "quantity");
                String priceStr = extractField(aiResp, "price");

                if (symbol.equals("0") || qtyStr.equals("0")) {
                    return "❌ Could not understand the buy command. Please specify: 'Buy [quantity] [symbol] at [price]'";
                }

                int qty = Integer.parseInt(qtyStr);
                double price = Double.parseDouble(priceStr);
                portfolioService.buyStock(symbol, symbol, qty, price);
                return String.format("✅ Bought %d %s @ ₹%.2f", qty, symbol, price);
            } else if (aiResp.contains("\"sell\"") || aiResp.contains("sell")) {
                String symbol = extractField(aiResp, "symbol");
                String qtyStr = extractField(aiResp, "quantity");

                if (symbol.equals("0")) {
                    return "❌ Could not understand the sell command. Please specify: 'Sell [quantity] [symbol]'";
                }

                int qty = Integer.parseInt(qtyStr);
                portfolioService.sellStock(symbol, qty);
                return String.format("✅ Sold %d %s", qty, symbol);
            } else if (aiResp.contains("show_portfolio") || aiResp.contains("portfolio")) {
                return buildPortfolioContext();
            }

            return "❌ I couldn't understand that command. Try: 'Buy 10 AAPL at 150' or 'Sell 5 GOOGL'";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }

    private String buildPortfolioContext() {
        try {
            double totalValue = portfolioService.calculateCurrentValue();
            double profitLoss = portfolioService.calculateProfitLoss();
            StringBuilder context = new StringBuilder();
            context.append(String.format("Value: ₹%.2f, P/L: ₹%.2f\n", totalValue, profitLoss));
            for (PortfolioItem item : portfolioService.getPortfolioItems()) {
                context.append(String.format("- %s: %d shares @ ₹%.2f\n",
                        item.getStock().getSymbol(), item.getQuantity(), item.getStock().getCurrentPrice()));
            }
            return context.toString();
        } catch (Exception e) {
            return "Portfolio data unavailable.";
        }
    }

    private String parseAIResponse(String body) {
        try {
            int contentIdx = body.indexOf("\"content\":");
            if (contentIdx == -1)
                return "AI response error.";
            int start = body.indexOf("\"", contentIdx + 11) + 1;
            int end = body.lastIndexOf("\"", body.lastIndexOf("}") - 1);
            // Improving robustness of end index search
            String sub = body.substring(start);
            int lastQuote = sub.lastIndexOf("\"");
            // This is a naive parser; for production a real JSON lib is better, but here we
            // avoid dependencies
            String content = sub.substring(0, lastQuote);
            return content.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
        } catch (Exception e) {
            return "Error parsing AI response.";
        }
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String extractField(String json, String field) {
        int idx = json.indexOf("\"" + field + "\"");
        if (idx == -1)
            return "0";
        int start = json.indexOf(":", idx) + 1;
        int end = json.indexOf(",", start);
        if (end == -1)
            end = json.indexOf("}", start);
        String val = json.substring(start, end).trim().replace("\"", "");
        return val;
    }

    private static class Message {
        String role, content;

        Message(String r, String c) {
            this.role = r;
            this.content = c;
        }
    }
}
