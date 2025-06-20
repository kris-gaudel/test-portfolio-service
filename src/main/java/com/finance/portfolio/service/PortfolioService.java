package com.finance.portfolio.service;

import com.finance.portfolio.model.*;

/**
 * Service class for managing portfolio operations.
 */
public class PortfolioService {
    private final Portfolio portfolio;

    /**
     * Constructor with a portfolio instance.
     * @param portfolio The portfolio to manage
     */
    public PortfolioService(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    /**
     * Buy an asset and add it to the portfolio.
     * @param asset The asset to buy
     * @param quantity The quantity to buy
     * @param price The price per unit
     * @return The created transaction
     */
    public Transaction buyAsset(Asset asset, int quantity, double price) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        Transaction transaction = new Transaction(asset, quantity, price, Transaction.Type.BUY);
        portfolio.addTransaction(transaction);
        return transaction;
    }

    /**
     * Sell an asset from the portfolio.
     * @param asset The asset to sell
     * @param quantity The quantity to sell
     * @param price The price per unit
     * @return The created transaction
     */
    public Transaction sellAsset(Asset asset, int quantity, double price) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        // Check if we have enough shares to sell
        Asset existingAsset = portfolio.getAsset(asset.getSymbol());
        if (existingAsset == null || existingAsset.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient shares to sell");
        }

        Transaction transaction = new Transaction(asset, quantity, price, Transaction.Type.SELL);
        portfolio.addTransaction(transaction);
        return transaction;
    }

    /**
     * Calculate the total value of the portfolio.
     * @return Total portfolio value
     */
    public double calculateTotalValue() {
        return portfolio.getTotalValue();
    }

    /**
     * Get the portfolio being managed.
     * @return The portfolio
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * Get portfolio summary information.
     * @return Portfolio summary as string
     */
    public String getPortfolioSummary() {
        return portfolio.toString();
    }

    /**
     * Get unrealized profit/loss for the portfolio.
     * @return Unrealized P&L
     */
    public double getUnrealizedPnL() {
        double totalCost = 0.0;
        double totalMarketValue = 0.0;

        for (Asset asset : portfolio.getAssets().values()) {
            totalCost += asset.getQuantity() * asset.getAveragePrice();
            totalMarketValue += asset.getMarketValue();
        }

        return totalMarketValue - totalCost;
    }

    /**
     * Get portfolio performance as percentage.
     * @return Performance percentage
     */
    public double getPortfolioPerformance() {
        double totalCost = 0.0;
        double totalMarketValue = 0.0;

        for (Asset asset : portfolio.getAssets().values()) {
            totalCost += asset.getQuantity() * asset.getAveragePrice();
            totalMarketValue += asset.getMarketValue();
        }

        if (totalCost == 0) {
            return 0.0;
        }

        return ((totalMarketValue - totalCost) / totalCost) * 100;
    }
} 