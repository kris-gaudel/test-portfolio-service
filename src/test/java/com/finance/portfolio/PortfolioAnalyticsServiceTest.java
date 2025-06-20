package com.finance.portfolio;

import com.finance.portfolio.model.*;
import com.finance.portfolio.service.PortfolioAnalyticsService;
import com.finance.portfolio.service.PortfolioService;
import com.finance.portfolio.util.PriceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PortfolioAnalyticsService class.
 * Tests follow best practices with proper setup, clear test names, and comprehensive coverage.
 */
class PortfolioAnalyticsServiceTest {
    private PortfolioAnalyticsService analyticsService;
    private Portfolio portfolio;
    private PortfolioService portfolioService;
    private PriceFetcher priceFetcher;
    private Stock testStock;
    private Crypto testCrypto;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        portfolioService = new PortfolioService(portfolio);
        analyticsService = new PortfolioAnalyticsService(portfolio);
        priceFetcher = new PriceFetcher();
        
        // Set fixed prices for deterministic testing
        priceFetcher.setBasePrice("TEST", 100.0);
        priceFetcher.setBasePrice("CRYPTO", 1000.0);
        
        testStock = new Stock("TEST", "Test Stock", priceFetcher);
        testCrypto = new Crypto("CRYPTO", "Test Crypto", priceFetcher);
    }

    @Test
    @DisplayName("Calculate portfolio metrics returns correct values for empty portfolio")
    void testCalculatePortfolioMetricsEmptyPortfolio() {
        var metrics = analyticsService.calculatePortfolioMetrics();
        
        assertEquals(BigDecimal.ZERO.setScale(4), metrics.getTotalValue());
        assertEquals(BigDecimal.ZERO.setScale(4), metrics.getTotalCost());
        assertEquals(BigDecimal.ZERO.setScale(4), metrics.getUnrealizedPnL());
        assertEquals(BigDecimal.ZERO.setScale(4), metrics.getRealizedPnL());
        assertEquals(BigDecimal.ZERO.setScale(4), metrics.getTotalReturnPercentage());
        assertEquals(BigDecimal.ZERO.setScale(4), metrics.getVolatility());
        assertEquals(BigDecimal.ZERO.setScale(4), metrics.getSharpeRatio());
        assertEquals(0, metrics.getAssetCount());
        assertEquals(0, metrics.getTransactionCount());
    }

    @Test
    @DisplayName("Calculate portfolio metrics returns correct values for portfolio with assets")
    void testCalculatePortfolioMetricsWithAssets() {
        // Buy assets
        portfolioService.buyAsset(testStock, 10, 100.0);
        portfolioService.buyAsset(testCrypto, 2, 1000.0);
        
        var metrics = analyticsService.calculatePortfolioMetrics();
        
        // Total value should be calculated based on current market prices
        BigDecimal totalValue = metrics.getTotalValue();
        assertTrue(totalValue.compareTo(BigDecimal.ZERO) > 0);
        
        // Total cost should be (10 * 100) + (2 * 1000) = 3000
        assertEquals(new BigDecimal("3000.0000"), metrics.getTotalCost());
        
        // Unrealized P&L should be totalValue - totalCost
        assertEquals(totalValue.subtract(new BigDecimal("3000.0000")).setScale(4), metrics.getUnrealizedPnL());
        
        // Realized P&L should be 0 (no sales)
        assertEquals(BigDecimal.ZERO.setScale(4), metrics.getRealizedPnL());
        
        // Total return should be calculated based on actual values (can be positive or negative)
        assertNotNull(metrics.getTotalReturnPercentage());
        
        assertEquals(2, metrics.getAssetCount());
        assertEquals(2, metrics.getTransactionCount());
    }

    @Test
    @DisplayName("Calculate total value returns correct sum of asset market values")
    void testCalculateTotalValue() {
        portfolioService.buyAsset(testStock, 5, 100.0);
        portfolioService.buyAsset(testCrypto, 1, 1000.0);
        
        BigDecimal totalValue = analyticsService.calculateTotalValue();
        
        // Total value should be positive and based on current market prices
        assertTrue(totalValue.compareTo(BigDecimal.ZERO) > 0);
        // Allow for up to 10% negative variation due to random price
        assertTrue(totalValue.compareTo(new BigDecimal("1350.0000")) >= 0);
    }

    @Test
    @DisplayName("Calculate total cost returns correct sum of asset cost basis")
    void testCalculateTotalCost() {
        portfolioService.buyAsset(testStock, 5, 100.0);
        portfolioService.buyAsset(testCrypto, 1, 1000.0);
        
        BigDecimal totalCost = analyticsService.calculateTotalCost();
        BigDecimal expectedCost = new BigDecimal("1500.0000"); // (5 * 100) + (1 * 1000)
        
        assertEquals(expectedCost, totalCost);
    }

    @Test
    @DisplayName("Calculate realized P&L returns correct value from sell transactions")
    void testCalculateRealizedPnL() {
        // Buy 10 shares at $100
        portfolioService.buyAsset(testStock, 10, 100.0);
        
        // Sell 5 shares at $110 (profit of $50)
        portfolioService.sellAsset(testStock, 5, 110.0);
        
        BigDecimal realizedPnL = analyticsService.calculateRealizedPnL();
        BigDecimal expectedPnL = new BigDecimal("50.0000"); // (5 * 110) - (5 * 100)
        
        assertEquals(expectedPnL, realizedPnL);
    }

    @Test
    @DisplayName("Calculate realized P&L returns zero when no sell transactions exist")
    void testCalculateRealizedPnLNoSales() {
        portfolioService.buyAsset(testStock, 10, 100.0);
        
        BigDecimal realizedPnL = analyticsService.calculateRealizedPnL();
        
        assertEquals(BigDecimal.ZERO.setScale(4), realizedPnL);
    }

    @Test
    @DisplayName("Calculate volatility returns weighted average volatility")
    void testCalculateVolatility() {
        portfolioService.buyAsset(testStock, 10, 100.0); // 1000 value
        portfolioService.buyAsset(testCrypto, 1, 1000.0); // 1000 value
        
        BigDecimal volatility = analyticsService.calculateVolatility();
        
        // Both assets have 15% volatility, weighted average should be around 15%
        assertTrue(volatility.compareTo(new BigDecimal("0.1400")) >= 0);
        assertTrue(volatility.compareTo(new BigDecimal("0.1600")) <= 0);
    }

    @Test
    @DisplayName("Calculate volatility returns zero for empty portfolio")
    void testCalculateVolatilityEmptyPortfolio() {
        BigDecimal volatility = analyticsService.calculateVolatility();
        
        assertEquals(BigDecimal.ZERO.setScale(4), volatility);
    }

    @Test
    @DisplayName("Get asset allocation returns correct percentage breakdown")
    void testGetAssetAllocation() {
        portfolioService.buyAsset(testStock, 10, 100.0); // 1000 value
        portfolioService.buyAsset(testCrypto, 1, 1000.0); // 1000 value
        
        Map<String, BigDecimal> allocation = analyticsService.getAssetAllocation();
        
        assertEquals(2, allocation.size());
        
        // Check that allocations sum to approximately 100% (allowing for rounding and random price)
        BigDecimal totalAllocation = allocation.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertTrue(totalAllocation.compareTo(new BigDecimal("95.0000")) >= 0);
        assertTrue(totalAllocation.compareTo(new BigDecimal("105.0000")) <= 0);
        
        // Each asset should have roughly 50% allocation, but allow for up to 20% swing due to price randomness
        assertTrue(allocation.get("TEST").compareTo(new BigDecimal("30.0000")) >= 0);
        assertTrue(allocation.get("TEST").compareTo(new BigDecimal("70.0000")) <= 0);
        assertTrue(allocation.get("CRYPTO").compareTo(new BigDecimal("30.0000")) >= 0);
        assertTrue(allocation.get("CRYPTO").compareTo(new BigDecimal("70.0000")) <= 0);
    }

    @Test
    @DisplayName("Get asset allocation returns empty map for empty portfolio")
    void testGetAssetAllocationEmptyPortfolio() {
        Map<String, BigDecimal> allocation = analyticsService.getAssetAllocation();
        
        assertTrue(allocation.isEmpty());
    }

    @Test
    @DisplayName("Get best performing asset returns correct asset")
    void testGetBestPerformingAsset() {
        // Create assets with different performance
        Stock highPerformer = new Stock("HIGH", "High Performer", priceFetcher);
        Stock lowPerformer = new Stock("LOW", "Low Performer", priceFetcher);
        
        priceFetcher.setBasePrice("HIGH", 120.0); // 20% gain
        priceFetcher.setBasePrice("LOW", 90.0);   // 10% loss
        
        portfolioService.buyAsset(highPerformer, 1, 100.0);
        portfolioService.buyAsset(lowPerformer, 1, 100.0);
        
        Optional<PortfolioAnalyticsService.AssetPerformance> best = analyticsService.getBestPerformingAsset();
        
        assertTrue(best.isPresent());
        assertEquals("HIGH", best.get().symbol());
        // Allow for some variation due to random price fluctuations
        assertTrue(best.get().returnPercentage().compareTo(new BigDecimal("10.0000")) >= 0);
        assertTrue(best.get().returnPercentage().compareTo(new BigDecimal("30.0000")) <= 0);
    }

    @Test
    @DisplayName("Get worst performing asset returns correct asset")
    void testGetWorstPerformingAsset() {
        // Create assets with different performance
        Stock highPerformer = new Stock("HIGH", "High Performer", priceFetcher);
        Stock lowPerformer = new Stock("LOW", "Low Performer", priceFetcher);
        
        priceFetcher.setBasePrice("HIGH", 120.0); // 20% gain
        priceFetcher.setBasePrice("LOW", 90.0);   // 10% loss
        
        portfolioService.buyAsset(highPerformer, 1, 100.0);
        portfolioService.buyAsset(lowPerformer, 1, 100.0);
        
        Optional<PortfolioAnalyticsService.AssetPerformance> worst = analyticsService.getWorstPerformingAsset();
        
        assertTrue(worst.isPresent());
        assertEquals("LOW", worst.get().symbol());
        // Allow for some variation due to random price fluctuations
        assertTrue(worst.get().returnPercentage().compareTo(new BigDecimal("-20.0000")) >= 0);
        assertTrue(worst.get().returnPercentage().compareTo(new BigDecimal("0.0000")) <= 0);
    }

    @Test
    @DisplayName("Get best/worst performing asset returns empty for empty portfolio")
    void testGetPerformingAssetsEmptyPortfolio() {
        Optional<PortfolioAnalyticsService.AssetPerformance> best = analyticsService.getBestPerformingAsset();
        Optional<PortfolioAnalyticsService.AssetPerformance> worst = analyticsService.getWorstPerformingAsset();
        
        assertTrue(best.isEmpty());
        assertTrue(worst.isEmpty());
    }

    @Test
    @DisplayName("PortfolioAnalyticsService constructor throws exception for null portfolio")
    void testConstructorWithNullPortfolio() {
        assertThrows(NullPointerException.class, () -> {
            new PortfolioAnalyticsService(null);
        });
    }

    @Test
    @DisplayName("AssetPerformance record validates input parameters")
    void testAssetPerformanceRecord() {
        // Valid construction
        var performance = new PortfolioAnalyticsService.AssetPerformance("TEST", new BigDecimal("10.5"));
        assertEquals("TEST", performance.symbol());
        assertEquals(new BigDecimal("10.5"), performance.returnPercentage());
        
        // Null symbol should throw exception
        assertThrows(NullPointerException.class, () -> {
            new PortfolioAnalyticsService.AssetPerformance(null, new BigDecimal("10.5"));
        });
        
        // Null return percentage should throw exception
        assertThrows(NullPointerException.class, () -> {
            new PortfolioAnalyticsService.AssetPerformance("TEST", null);
        });
    }
} 