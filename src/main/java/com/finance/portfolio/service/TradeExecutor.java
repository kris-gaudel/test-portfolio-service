package com.finance.portfolio.service;

import com.finance.portfolio.model.*;
import com.finance.portfolio.util.PriceFetcher;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simulates random buy/sell transactions for demo and testing purposes.
 */
public class TradeExecutor {
    private final PortfolioService portfolioService;
    private final PriceFetcher priceFetcher;
    private final Random random;
    private final ScheduledExecutorService executor;
    private boolean isRunning;

    // Sample assets for simulation
    private final Asset[] sampleAssets = {
        new Stock("AAPL", "Apple Inc."),
        new Stock("GOOGL", "Alphabet Inc."),
        new Stock("MSFT", "Microsoft Corporation"),
        new Stock("TSLA", "Tesla Inc."),
        new Crypto("BTC", "Bitcoin"),
        new Crypto("ETH", "Ethereum")
    };

    /**
     * Constructor with portfolio service.
     * @param portfolioService The portfolio service to execute trades on
     */
    public TradeExecutor(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
        this.priceFetcher = new PriceFetcher();
        this.random = new Random();
        this.executor = Executors.newScheduledThreadPool(1);
        this.isRunning = false;
    }

    /**
     * Start the trade simulation.
     * @param intervalSeconds Interval between trades in seconds
     */
    public void startSimulation(int intervalSeconds) {
        if (isRunning) {
            return;
        }

        isRunning = true;
        executor.scheduleAtFixedRate(this::executeRandomTrade, 0, intervalSeconds, TimeUnit.SECONDS);
        System.out.println("Trade simulation started with " + intervalSeconds + " second intervals");
    }

    /**
     * Stop the trade simulation.
     */
    public void stopSimulation() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
        executor.shutdown();
        System.out.println("Trade simulation stopped");
    }

    /**
     * Execute a single random trade.
     */
    private void executeRandomTrade() {
        try {
            Asset asset = sampleAssets[random.nextInt(sampleAssets.length)];
            int quantity = random.nextInt(10) + 1; // 1-10 shares
            double currentPrice = priceFetcher.getCurrentPrice(asset.getSymbol());
            
            // Add some price variation (±10%)
            double priceVariation = 1.0 + (random.nextDouble() - 0.5) * 0.2;
            double tradePrice = currentPrice * priceVariation;

            Transaction transaction;
            if (random.nextBoolean()) {
                // 50% chance to buy
                transaction = portfolioService.buyAsset(asset, quantity, tradePrice);
                System.out.println("BUY: " + transaction.toString());
            } else {
                // 50% chance to sell (if we have shares)
                Asset existingAsset = portfolioService.getPortfolio().getAsset(asset.getSymbol());
                if (existingAsset != null && existingAsset.getQuantity() >= quantity) {
                    transaction = portfolioService.sellAsset(asset, quantity, tradePrice);
                    System.out.println("SELL: " + transaction.toString());
                } else {
                    // If we can't sell, buy instead
                    transaction = portfolioService.buyAsset(asset, quantity, tradePrice);
                    System.out.println("BUY (fallback): " + transaction.toString());
                }
            }

            // Print portfolio summary every 5 trades
            if (portfolioService.getPortfolio().getTransactionCount() % 5 == 0) {
                System.out.println("\n" + portfolioService.getPortfolioSummary());
            }

        } catch (Exception e) {
            System.err.println("Error executing trade: " + e.getMessage());
        }
    }

    /**
     * Execute a specific number of trades.
     * @param numberOfTrades Number of trades to execute
     */
    public void executeTrades(int numberOfTrades) {
        System.out.println("Executing " + numberOfTrades + " trades...");
        for (int i = 0; i < numberOfTrades; i++) {
            executeRandomTrade();
            try {
                Thread.sleep(100); // Small delay between trades
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Completed " + numberOfTrades + " trades");
    }

    /**
     * Check if simulation is running.
     * @return True if simulation is active
     */
    public boolean isRunning() {
        return isRunning;
    }
} 