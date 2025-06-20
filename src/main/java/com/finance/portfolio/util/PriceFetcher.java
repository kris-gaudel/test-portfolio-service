package com.finance.portfolio.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Mock service for fetching current prices of financial assets.
 * Returns pseudo-random prices based on symbol for demonstration purposes.
 */
public class PriceFetcher {
    private final Map<String, Double> basePrices;
    private final Random random;

    /**
     * Constructor initializing base prices for common assets.
     */
    public PriceFetcher() {
        this.basePrices = new HashMap<>();
        this.random = new Random();
        
        // Initialize base prices for common assets
        basePrices.put("AAPL", 150.0);
        basePrices.put("GOOGL", 2800.0);
        basePrices.put("MSFT", 300.0);
        basePrices.put("TSLA", 800.0);
        basePrices.put("AMZN", 3300.0);
        basePrices.put("NVDA", 500.0);
        basePrices.put("META", 350.0);
        basePrices.put("NFLX", 600.0);
        basePrices.put("BTC", 45000.0);
        basePrices.put("ETH", 3000.0);
        basePrices.put("ADA", 1.5);
        basePrices.put("DOT", 20.0);
        basePrices.put("LINK", 25.0);
        basePrices.put("UNI", 30.0);
    }

    /**
     * Get current price for a given symbol.
     * @param symbol The asset symbol
     * @return Current price with some random variation
     */
    public double getCurrentPrice(String symbol) {
        Double basePrice = basePrices.get(symbol.toUpperCase());
        
        if (basePrice == null) {
            // For unknown symbols, generate a random base price
            basePrice = 50.0 + random.nextDouble() * 200.0;
            basePrices.put(symbol.toUpperCase(), basePrice);
        }
        
        // Add random variation (±5%)
        double variation = 1.0 + (random.nextDouble() - 0.5) * 0.1;
        return basePrice * variation;
    }

    /**
     * Set a base price for a symbol (useful for testing).
     * @param symbol The asset symbol
     * @param basePrice The base price to set
     */
    public void setBasePrice(String symbol, double basePrice) {
        basePrices.put(symbol.toUpperCase(), basePrice);
    }

    /**
     * Get the base price for a symbol without variation.
     * @param symbol The asset symbol
     * @return Base price
     */
    public double getBasePrice(String symbol) {
        return basePrices.getOrDefault(symbol.toUpperCase(), 100.0);
    }

    /**
     * Get all known symbols.
     * @return Set of known symbols
     */
    public java.util.Set<String> getKnownSymbols() {
        return new java.util.HashSet<>(basePrices.keySet());
    }

    /**
     * Clear all cached prices (useful for testing).
     */
    public void clearPrices() {
        basePrices.clear();
    }
} 