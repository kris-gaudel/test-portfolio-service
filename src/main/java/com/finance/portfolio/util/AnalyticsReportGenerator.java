package com.finance.portfolio.util;

import com.finance.portfolio.model.PortfolioMetrics;
import com.finance.portfolio.service.PortfolioAnalyticsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for generating portfolio analytics reports.
 * Follows Single Responsibility Principle by focusing only on report generation.
 */
public class AnalyticsReportGenerator {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String REPORT_HEADER = "Portfolio Analytics Report";
    private static final String SEPARATOR = "=".repeat(80);
    
    private final PortfolioAnalyticsService analyticsService;
    
    /**
     * Constructor with dependency injection.
     * @param analyticsService The analytics service to use for calculations
     */
    public AnalyticsReportGenerator(PortfolioAnalyticsService analyticsService) {
        this.analyticsService = Objects.requireNonNull(analyticsService, "Analytics service cannot be null");
    }
    
    /**
     * Generate a comprehensive analytics report and save it to a file.
     * @param filename The output filename
     * @throws IOException If file writing fails
     */
    public void generateReport(String filename) throws IOException {
        Objects.requireNonNull(filename, "Filename cannot be null");
        if (filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        
        Path filePath = Paths.get(filename);
        ensureDirectoryExists(filePath);
        
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            writeReport(writer);
        }
        
        System.out.println("Analytics report generated successfully: " + filename);
    }
    
    /**
     * Generate a comprehensive analytics report as a string.
     * @return The report as a formatted string
     */
    public String generateReportString() {
        StringBuilder report = new StringBuilder();
        try (PrintWriter writer = new PrintWriter(new java.io.StringWriter()) {
            @Override
            public void write(String str) {
                report.append(str);
            }
        }) {
            writeReport(writer);
        }
        return report.toString();
    }
    
    /**
     * Write the complete report to the provided writer.
     * @param writer The PrintWriter to write to
     */
    private void writeReport(PrintWriter writer) {
        PortfolioMetrics metrics = analyticsService.calculatePortfolioMetrics();
        Map<String, java.math.BigDecimal> allocation = analyticsService.getAssetAllocation();
        
        writeHeader(writer);
        writeSummary(writer, metrics);
        writePerformanceMetrics(writer, metrics);
        writeAssetAllocation(writer, allocation);
        writeTopPerformers(writer);
        writeFooter(writer, metrics);
    }
    
    /**
     * Write the report header.
     * @param writer The PrintWriter to write to
     */
    private void writeHeader(PrintWriter writer) {
        writer.println(REPORT_HEADER);
        writer.println(SEPARATOR);
        writer.println("Generated on: " + java.time.LocalDateTime.now().format(DATE_FORMATTER));
        writer.println();
    }
    
    /**
     * Write the portfolio summary section.
     * @param writer The PrintWriter to write to
     * @param metrics The portfolio metrics
     */
    private void writeSummary(PrintWriter writer, PortfolioMetrics metrics) {
        writer.println("PORTFOLIO SUMMARY");
        writer.println("-".repeat(40));
        writer.printf("Total Value: $%,.2f%n", metrics.getTotalValue());
        writer.printf("Total Cost: $%,.2f%n", metrics.getTotalCost());
        writer.printf("Number of Assets: %d%n", metrics.getAssetCount());
        writer.printf("Number of Transactions: %d%n", metrics.getTransactionCount());
        writer.println();
    }
    
    /**
     * Write the performance metrics section.
     * @param writer The PrintWriter to write to
     * @param metrics The portfolio metrics
     */
    private void writePerformanceMetrics(PrintWriter writer, PortfolioMetrics metrics) {
        writer.println("PERFORMANCE METRICS");
        writer.println("-".repeat(40));
        writer.printf("Unrealized P&L: $%,.2f%n", metrics.getUnrealizedPnL());
        writer.printf("Realized P&L: $%,.2f%n", metrics.getRealizedPnL());
        writer.printf("Total Return: %.2f%%%n", metrics.getTotalReturnPercentage());
        writer.printf("Volatility: %.2f%%%n", metrics.getVolatility());
        writer.printf("Sharpe Ratio: %.4f%n", metrics.getSharpeRatio());
        writer.println();
    }
    
    /**
     * Write the asset allocation section.
     * @param writer The PrintWriter to write to
     * @param allocation The asset allocation map
     */
    private void writeAssetAllocation(PrintWriter writer, Map<String, java.math.BigDecimal> allocation) {
        writer.println("ASSET ALLOCATION");
        writer.println("-".repeat(40));
        
        if (allocation.isEmpty()) {
            writer.println("No assets in portfolio");
        } else {
            allocation.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // Sort by percentage descending
                    .forEach(entry -> writer.printf("%s: %.2f%%%n", entry.getKey(), entry.getValue()));
        }
        writer.println();
    }
    
    /**
     * Write the top performers section.
     * @param writer The PrintWriter to write to
     */
    private void writeTopPerformers(PrintWriter writer) {
        writer.println("TOP PERFORMERS");
        writer.println("-".repeat(40));
        
        analyticsService.getBestPerformingAsset().ifPresentOrElse(
                best -> writer.printf("Best Performer: %s (%.2f%%)%n", best.symbol(), best.returnPercentage()),
                () -> writer.println("No assets to analyze")
        );
        
        analyticsService.getWorstPerformingAsset().ifPresentOrElse(
                worst -> writer.printf("Worst Performer: %s (%.2f%%)%n", worst.symbol(), worst.returnPercentage()),
                () -> writer.println("No assets to analyze")
        );
        writer.println();
    }
    
    /**
     * Write the report footer.
     * @param writer The PrintWriter to write to
     * @param metrics The portfolio metrics
     */
    private void writeFooter(PrintWriter writer, PortfolioMetrics metrics) {
        writer.println(SEPARATOR);
        writer.println("Report generated by PortfolioTracker Analytics");
        writer.printf("Calculation timestamp: %s%n", 
                     metrics.getCalculationTimestamp().format(DATE_FORMATTER));
    }
    
    /**
     * Ensure the directory for the file exists.
     * @param filePath The file path
     * @throws IOException If directory creation fails
     */
    private void ensureDirectoryExists(Path filePath) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
} 