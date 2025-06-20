package com.finance.portfolio;

import com.finance.portfolio.model.*;
import com.finance.portfolio.service.PortfolioService;
import com.finance.portfolio.service.TradeExecutor;
import com.finance.portfolio.service.PortfolioAnalyticsService;
import com.finance.portfolio.util.CSVExporter;
import com.finance.portfolio.util.AnalyticsReportGenerator;

import java.math.BigDecimal;

/**
 * Main class demonstrating the PortfolioTracker application with analytics features.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== PortfolioTracker Demo with Analytics ===\n");

        try {
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

            // 6. Generate analytics report
            System.out.println("\nGenerating analytics report...");
            PortfolioAnalyticsService analyticsService = new PortfolioAnalyticsService(portfolio);
            AnalyticsReportGenerator reportGenerator = new AnalyticsReportGenerator(analyticsService);
            
            // Generate and display analytics report
            String analyticsReport = reportGenerator.generateReportString();
            System.out.println(analyticsReport);
            
            // Save analytics report to file
            reportGenerator.generateReport("analytics_report.txt");

            // 7. Export to CSV files
            System.out.println("\nExporting data to CSV files...");
            CSVExporter.exportAll(portfolio, "portfolio.csv", "transactions.csv");
            CSVExporter.exportPortfolioSummary(portfolio, "portfolio_summary.csv");

            // 8. Display additional analytics insights
            System.out.println("\nAdditional Analytics Insights:");
            System.out.println("-".repeat(40));
            
            // Asset allocation
            var allocation = analyticsService.getAssetAllocation();
            System.out.println("Asset Allocation:");
            allocation.forEach((symbol, percentage) -> 
                System.out.printf("  %s: %.2f%%%n", symbol, percentage));
            
            // Best and worst performers
            analyticsService.getBestPerformingAsset().ifPresent(best ->
                System.out.printf("Best Performer: %s (%.2f%%)%n", best.symbol(), best.returnPercentage()));
            
            analyticsService.getWorstPerformingAsset().ifPresent(worst ->
                System.out.printf("Worst Performer: %s (%.2f%%)%n", worst.symbol(), worst.returnPercentage()));

            System.out.println("\n=== Demo completed successfully! ===");
            System.out.println("Check the generated files:");
            System.out.println("- portfolio.csv");
            System.out.println("- transactions.csv");
            System.out.println("- portfolio_summary.csv");
            System.out.println("- analytics_report.txt");

        } catch (Exception e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 