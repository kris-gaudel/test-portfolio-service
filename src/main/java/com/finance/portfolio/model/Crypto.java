package com.finance.portfolio.model;

import com.finance.portfolio.util.PriceFetcher;

/**
 * Represents a cryptocurrency asset in the portfolio.
 */
public class Crypto extends Asset {
    private final PriceFetcher priceFetcher;

    /**
     * Constructor for Crypto asset.
     * @param symbol The crypto symbol (e.g., "BTC")
     * @param name The cryptocurrency name (e.g., "Bitcoin")
     */
    public Crypto(String symbol, String name) {
        super(symbol, name);
        this.priceFetcher = new PriceFetcher();
    }

    /**
     * Constructor with custom PriceFetcher for testing.
     * @param symbol The crypto symbol
     * @param name The cryptocurrency name
     * @param priceFetcher Custom price fetcher instance
     */
    public Crypto(String symbol, String name, PriceFetcher priceFetcher) {
        super(symbol, name);
        this.priceFetcher = priceFetcher;
    }

    @Override
    public double getCurrentPrice() {
        return priceFetcher.getCurrentPrice(symbol);
    }

    @Override
    public String toString() {
        return "Crypto: " + super.toString();
    }
} 