package com.finance.portfolio.model;

import com.finance.portfolio.util.PriceFetcher;

/**
 * Represents a stock asset in the portfolio.
 */
public class Stock extends Asset {
    private final PriceFetcher priceFetcher;

    /**
     * Constructor for Stock asset.
     * @param symbol The stock symbol (e.g., "AAPL")
     * @param name The company name (e.g., "Apple Inc.")
     */
    public Stock(String symbol, String name) {
        super(symbol, name);
        this.priceFetcher = new PriceFetcher();
    }

    /**
     * Constructor with custom PriceFetcher for testing.
     * @param symbol The stock symbol
     * @param name The company name
     * @param priceFetcher Custom price fetcher instance
     */
    public Stock(String symbol, String name, PriceFetcher priceFetcher) {
        super(symbol, name);
        this.priceFetcher = priceFetcher;
    }

    @Override
    public double getCurrentPrice() {
        return priceFetcher.getCurrentPrice(symbol);
    }

    @Override
    public String toString() {
        return "Stock: " + super.toString();
    }
} 