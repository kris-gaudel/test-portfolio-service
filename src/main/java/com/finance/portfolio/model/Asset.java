package com.finance.portfolio.model;

/**
 * Abstract base class for all financial assets in the portfolio.
 */
public abstract class Asset {
    protected String symbol;
    protected String name;
    protected int quantity;
    protected double averagePrice;

    /**
     * Constructor to initialize symbol and name.
     * @param symbol The asset symbol (e.g., "AAPL", "BTC")
     * @param name The asset name (e.g., "Apple Inc.", "Bitcoin")
     */
    public Asset(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
        this.quantity = 0;
        this.averagePrice = 0.0;
    }

    /**
     * Abstract method to get the current price of the asset.
     * @return Current price of the asset
     */
    public abstract double getCurrentPrice();

    /**
     * Calculate the current market value of the asset.
     * @return Market value (quantity * current price)
     */
    public double getMarketValue() {
        return quantity * getCurrentPrice();
    }

    // Getters and setters
    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Quantity: %d, Avg Price: $%.2f, Market Value: $%.2f",
                name, symbol, quantity, averagePrice, getMarketValue());
    }
} 