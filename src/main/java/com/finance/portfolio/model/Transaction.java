package com.finance.portfolio.model;

import java.time.LocalDateTime;

/**
 * Represents a buy or sell transaction for an asset.
 */
public class Transaction {
    private final Asset asset;
    private final int quantity;
    private final double price;
    private final LocalDateTime timestamp;
    private final Type type;

    /**
     * Transaction types.
     */
    public enum Type {
        BUY, SELL
    }

    /**
     * Constructor for a transaction.
     * @param asset The asset being traded
     * @param quantity The quantity of shares/units
     * @param price The price per share/unit
     * @param type The transaction type (BUY or SELL)
     */
    public Transaction(Asset asset, int quantity, double price, Type type) {
        this.asset = asset;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = LocalDateTime.now();
        this.type = type;
    }

    /**
     * Constructor with custom timestamp for testing.
     * @param asset The asset being traded
     * @param quantity The quantity of shares/units
     * @param price The price per share/unit
     * @param type The transaction type (BUY or SELL)
     * @param timestamp Custom timestamp
     */
    public Transaction(Asset asset, int quantity, double price, Type type, LocalDateTime timestamp) {
        this.asset = asset;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
        this.type = type;
    }

    // Getters
    public Asset getAsset() {
        return asset;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Type getType() {
        return type;
    }

    /**
     * Calculate the total value of this transaction.
     * @return Total transaction value
     */
    public double getTotalValue() {
        return quantity * price;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d shares of %s at $%.2f on %s (Total: $%.2f)",
                type, quantity, quantity, asset.getSymbol(), price, 
                timestamp.toString(), getTotalValue());
    }
} 