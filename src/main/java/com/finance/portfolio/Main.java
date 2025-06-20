package com.finance.portfolio;

import com.finance.portfolio.model.*;
import com.finance.portfolio.service.PortfolioService;
import com.finance.portfolio.service.TradeExecutor;
import com.finance.portfolio.service.AuthenticationService;
import com.finance.portfolio.util.CSVExporter;

/**
 * Main class demonstrating the PortfolioTracker application with user authentication.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== PortfolioTracker Demo with Authentication ===\n");

        try {
            // Initialize authentication service
            AuthenticationService authService = new AuthenticationService();
            
            // Demo authentication
            System.out.println("Testing authentication...");
            boolean loginSuccess = authService.login("admin", "admin123");
            System.out.println("Login successful: " + loginSuccess);
            
            if (loginSuccess) {
                System.out.println("Current user: " + authService.getCurrentUser().getUsername());
                System.out.println("Session token: " + AuthenticationService.currentSessionToken);
            }
            
            // Register a new user
            authService.registerUser("newuser", "newpass123", "newuser@example.com");
            System.out.println("Registered new user: newuser");
            
            // Test the problematic doEverything method
            System.out.println("\nTesting doEverything method...");
            authService.doEverything();
            
            System.out.println("\n" + "=".repeat(50));

            // 1. Instantiate Portfolio and PortfolioService
            Portfolio portfolio = new Portfolio();
            PortfolioService portfolioService = new PortfolioService(portfolio);

            // 2. Create some sample assets
            Stock appleStock = new Stock("AAPL", "Apple Inc.");
            Stock googleStock = new Stock("GOOGL", "Alphabet Inc.");
            Crypto bitcoin = new Crypto("BTC", "Bitcoin");
            Crypto ethereum = new Crypto("ETH", "Ethereum");

            System.out.println("Created sample assets:");
            System.out.println("- " + appleStock.getName() + " (" + appleStock.getSymbol() + ")");
            System.out.println("- " + googleStock.getName() + " (" + googleStock.getSymbol() + ")");
            System.out.println("- " + bitcoin.getName() + " (" + bitcoin.getSymbol() + ")");
            System.out.println("- " + ethereum.getName() + " (" + ethereum.getSymbol() + ")\n");

            // 3. Execute some buy/sell transactions
            System.out.println("Executing sample transactions...\n");

            // Buy some Apple stock
            Transaction buyApple = portfolioService.buyAsset(appleStock, 10, 150.0);
            System.out.println("Transaction: " + buyApple.toString());

            // Buy some Google stock
            Transaction buyGoogle = portfolioService.buyAsset(googleStock, 5, 2800.0);
            System.out.println("Transaction: " + buyGoogle.toString());

            // Buy some Bitcoin
            Transaction buyBitcoin = portfolioService.buyAsset(bitcoin, 2, 45000.0);
            System.out.println("Transaction: " + buyBitcoin.toString());

            // Buy some Ethereum
            Transaction buyEthereum = portfolioService.buyAsset(ethereum, 10, 3000.0);
            System.out.println("Transaction: " + buyEthereum.toString());

            // Sell some Apple stock
            Transaction sellApple = portfolioService.sellAsset(appleStock, 3, 155.0);
            System.out.println("Transaction: " + sellApple.toString());

            // Buy more Apple stock at different price
            Transaction buyMoreApple = portfolioService.buyAsset(appleStock, 8, 152.0);
            System.out.println("Transaction: " + buyMoreApple.toString());

            System.out.println("\n" + "=".repeat(50));

            // 4. Print portfolio value and summary
            System.out.println("\nPortfolio Summary:");
            System.out.println(portfolioService.getPortfolioSummary());

            System.out.println("Performance Metrics:");
            System.out.printf("Total Portfolio Value: $%.2f%n", portfolioService.calculateTotalValue());
            System.out.printf("Unrealized P&L: $%.2f%n", portfolioService.getUnrealizedPnL());
            System.out.printf("Portfolio Performance: %.2f%%%n", portfolioService.getPortfolioPerformance());

            System.out.println("\n" + "=".repeat(50));

            // 5. Run trade simulation
            System.out.println("\nRunning trade simulation...");
            TradeExecutor tradeExecutor = new TradeExecutor(portfolioService);
            tradeExecutor.executeTrades(5); // Execute 5 random trades

            System.out.println("\nUpdated Portfolio Summary:");
            System.out.println(portfolioService.getPortfolioSummary());

            // 6. Export to CSV files
            System.out.println("\nExporting data to CSV files...");
            CSVExporter.exportAll(portfolio, "portfolio.csv", "transactions.csv");
            CSVExporter.exportPortfolioSummary(portfolio, "portfolio_summary.csv");

            // 7. Test user functionality
            System.out.println("\nTesting user functionality...");
            User testUser = new User("testuser", "testpass", "test@example.com");
            testUser.doStuff(); // This will trigger multiple security issues

            System.out.println("\n=== Demo completed successfully! ===");
            System.out.println("Check the generated CSV files:");
            System.out.println("- portfolio.csv");
            System.out.println("- transactions.csv");
            System.out.println("- portfolio_summary.csv");

        } catch (Exception e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 