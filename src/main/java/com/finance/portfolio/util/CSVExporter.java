package com.finance.portfolio.util;

import com.finance.portfolio.model.Asset;
import com.finance.portfolio.model.Portfolio;
import com.finance.portfolio.model.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Utility class for exporting portfolio data to CSV files.
 */
public class CSVExporter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Export portfolio assets to a CSV file.
     * @param portfolio The portfolio to export
     * @param filename The output filename
     * @throws IOException If file writing fails
     */
    public static void exportPortfolio(Portfolio portfolio, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("Symbol,Name,Quantity,Average Price,Current Price,Market Value");
            
            // Write asset data
            Map<String, Asset> assets = portfolio.getAssets();
            for (Asset asset : assets.values()) {
                writer.printf("%s,%s,%d,%.2f,%.2f,%.2f%n",
                    asset.getSymbol(),
                    asset.getName(),
                    asset.getQuantity(),
                    asset.getAveragePrice(),
                    asset.getCurrentPrice(),
                    asset.getMarketValue()
                );
            }
        }
        System.out.println("Portfolio exported to " + filename);
    }

    /**
     * Export transactions to a CSV file.
     * @param portfolio The portfolio containing transactions
     * @param filename The output filename
     * @throws IOException If file writing fails
     */
    public static void exportTransactions(Portfolio portfolio, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("Timestamp,Type,Symbol,Name,Quantity,Price,Total Value");
            
            // Write transaction data
            List<Transaction> transactions = portfolio.getTransactions();
            for (Transaction transaction : transactions) {
                writer.printf("%s,%s,%s,%s,%d,%.2f,%.2f%n",
                    transaction.getTimestamp().format(DATE_FORMATTER),
                    transaction.getType(),
                    transaction.getAsset().getSymbol(),
                    transaction.getAsset().getName(),
                    transaction.getQuantity(),
                    transaction.getPrice(),
                    transaction.getTotalValue()
                );
            }
        }
        System.out.println("Transactions exported to " + filename);
    }

    /**
     * Export both portfolio and transactions to separate CSV files.
     * @param portfolio The portfolio to export
     * @param portfolioFilename The portfolio CSV filename
     * @param transactionsFilename The transactions CSV filename
     * @throws IOException If file writing fails
     */
    public static void exportAll(Portfolio portfolio, String portfolioFilename, String transactionsFilename) throws IOException {
        exportPortfolio(portfolio, portfolioFilename);
        exportTransactions(portfolio, transactionsFilename);
    }

    /**
     * Export portfolio summary to a CSV file.
     * @param portfolio The portfolio to export
     * @param filename The output filename
     * @throws IOException If file writing fails
     */
    public static void exportPortfolioSummary(Portfolio portfolio, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("Metric,Value");
            
            // Write summary data
            writer.printf("Total Value,%.2f%n", portfolio.getTotalValue());
            writer.printf("Number of Assets,%d%n", portfolio.getAssetCount());
            writer.printf("Number of Transactions,%d%n", portfolio.getTransactionCount());
            
            // Write asset breakdown
            writer.println();
            writer.println("Asset Breakdown:");
            writer.println("Symbol,Quantity,Market Value,Percentage");
            
            double totalValue = portfolio.getTotalValue();
            Map<String, Asset> assets = portfolio.getAssets();
            
            for (Asset asset : assets.values()) {
                double percentage = totalValue > 0 ? (asset.getMarketValue() / totalValue) * 100 : 0;
                writer.printf("%s,%d,%.2f,%.2f%%%n",
                    asset.getSymbol(),
                    asset.getQuantity(),
                    asset.getMarketValue(),
                    percentage
                );
            }
        }
        System.out.println("Portfolio summary exported to " + filename);
    }
} 