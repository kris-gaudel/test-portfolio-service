package com.finance.portfolio.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a financial portfolio containing assets and transactions.
 */
public class Portfolio {
    private final List<Transaction> transactions;
    private final Map<String, Asset> assets;

    /**
     * Constructor for a new portfolio.
     */
    public Portfolio() {
        this.transactions = new ArrayList<>();
        this.assets = new HashMap<>();
    }

    /**
     * Add a transaction to the portfolio and update holdings.
     * @param transaction The transaction to add
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        updateHoldings(transaction);
    }

    /**
     * Update asset holdings based on a transaction.
     * @param transaction The transaction to process
     */
    private void updateHoldings(Transaction transaction) {
        Asset asset = transaction.getAsset();
        String symbol = asset.getSymbol();
        
        if (!assets.containsKey(symbol)) {
            assets.put(symbol, asset);
        }
        
        Asset existingAsset = assets.get(symbol);
        int currentQuantity = existingAsset.getQuantity();
        double currentAvgPrice = existingAsset.getAveragePrice();
        
        if (transaction.getType() == Transaction.Type.BUY) {
            // Calculate new average price for buying
            int newQuantity = currentQuantity + transaction.getQuantity();
            double newAvgPrice = 0.0;
            
            if (newQuantity > 0) {
                newAvgPrice = ((currentQuantity * currentAvgPrice) + 
                              (transaction.getQuantity() * transaction.getPrice())) / newQuantity;
            }
            
            existingAsset.setQuantity(newQuantity);
            existingAsset.setAveragePrice(newAvgPrice);
        } else if (transaction.getType() == Transaction.Type.SELL) {
            // For selling, just reduce quantity (average price stays the same)
            int newQuantity = currentQuantity - transaction.getQuantity();
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Cannot sell more shares than owned");
            }
            existingAsset.setQuantity(newQuantity);
        }
    }

    /**
     * Get all assets in the portfolio.
     * @return Map of symbol to asset
     */
    public Map<String, Asset> getAssets() {
        return new HashMap<>(assets);
    }

    /**
     * Get all transactions in the portfolio.
     * @return List of transactions
     */
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    /**
     * Get a specific asset by symbol.
     * @param symbol The asset symbol
     * @return The asset or null if not found
     */
    public Asset getAsset(String symbol) {
        return assets.get(symbol);
    }

    /**
     * Calculate the total market value of the portfolio.
     * @return Total portfolio value
     */
    public double getTotalValue() {
        return assets.values().stream()
                .mapToDouble(Asset::getMarketValue)
                .sum();
    }

    /**
     * Get the number of different assets in the portfolio.
     * @return Number of unique assets
     */
    public int getAssetCount() {
        return assets.size();
    }

    /**
     * Get the total number of transactions.
     * @return Number of transactions
     */
    public int getTransactionCount() {
        return transactions.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Portfolio Summary:\n");
        sb.append("Total Value: $").append(String.format("%.2f", getTotalValue())).append("\n");
        sb.append("Assets: ").append(getAssetCount()).append("\n");
        sb.append("Transactions: ").append(getTransactionCount()).append("\n\n");
        
        sb.append("Assets:\n");
        for (Asset asset : assets.values()) {
            sb.append("  ").append(asset.toString()).append("\n");
        }
        
        return sb.toString();
    }
} 