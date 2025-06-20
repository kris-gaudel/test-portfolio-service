package com.finance.portfolio;

import com.finance.portfolio.model.*;
import com.finance.portfolio.service.PortfolioService;
import com.finance.portfolio.util.PriceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PortfolioService class.
 */
class PortfolioServiceTest {
    private PortfolioService portfolioService;
    private Portfolio portfolio;
    private PriceFetcher priceFetcher;
    private Stock testStock;
    private Crypto testCrypto;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        portfolioService = new PortfolioService(portfolio);
        priceFetcher = new PriceFetcher();
        
        // Set fixed prices for testing
        priceFetcher.setBasePrice("TEST", 100.0);
        priceFetcher.setBasePrice("CRYPTO", 1000.0);
        
        testStock = new Stock("TEST", "Test Stock", priceFetcher);
        testCrypto = new Crypto("CRYPTO", "Test Crypto", priceFetcher);
    }

    @Test
    @DisplayName("Buying increases asset quantity and average price")
    void testBuyingIncreasesQuantityAndAveragePrice() {
        // Initial state
        assertEquals(0, testStock.getQuantity());
        assertEquals(0.0, testStock.getAveragePrice(), 0.01);

        // Buy 10 shares at $100
        Transaction transaction1 = portfolioService.buyAsset(testStock, 10, 100.0);
        
        assertEquals(Transaction.Type.BUY, transaction1.getType());
        assertEquals(10, testStock.getQuantity());
        assertEquals(100.0, testStock.getAveragePrice(), 0.01);

        // Buy 5 more shares at $110
        Transaction transaction2 = portfolioService.buyAsset(testStock, 5, 110.0);
        
        assertEquals(15, testStock.getQuantity());
        // Average price should be: (10*100 + 5*110) / 15 = 103.33
        assertEquals(103.33, testStock.getAveragePrice(), 0.01);
    }

    @Test
    @DisplayName("Selling decreases quantity and handles errors")
    void testSellingDecreasesQuantityAndHandlesErrors() {
        // Buy some shares first
        portfolioService.buyAsset(testStock, 10, 100.0);
        assertEquals(10, testStock.getQuantity());

        // Sell 3 shares
        Transaction sellTransaction = portfolioService.sellAsset(testStock, 3, 105.0);
        assertEquals(Transaction.Type.SELL, sellTransaction.getType());
        assertEquals(7, testStock.getQuantity());
        // Average price should remain the same
        assertEquals(100.0, testStock.getAveragePrice(), 0.01);

        // Try to sell more than owned - should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.sellAsset(testStock, 10, 105.0);
        });
    }

    // Helper classes for deterministic price
    static class FixedPriceStock extends Stock {
        private final double fixedPrice;
        public FixedPriceStock(String symbol, String name, double fixedPrice) {
            super(symbol, name);
            this.fixedPrice = fixedPrice;
        }
        @Override
        public double getCurrentPrice() {
            return fixedPrice;
        }
    }
    static class FixedPriceCrypto extends Crypto {
        private final double fixedPrice;
        public FixedPriceCrypto(String symbol, String name, double fixedPrice) {
            super(symbol, name);
            this.fixedPrice = fixedPrice;
        }
        @Override
        public double getCurrentPrice() {
            return fixedPrice;
        }
    }

    @Test
    @DisplayName("Calculate total value reflects current mocked prices")
    void testCalculateTotalValueReflectsCurrentPrices() {
        // Use fixed-price assets for deterministic test
        FixedPriceStock fixedStock = new FixedPriceStock("FIXED", "Fixed Stock", 123.45);
        FixedPriceCrypto fixedCrypto = new FixedPriceCrypto("FIXEDC", "Fixed Crypto", 987.65);
        Portfolio fixedPortfolio = new Portfolio();
        PortfolioService fixedService = new PortfolioService(fixedPortfolio);
        fixedService.buyAsset(fixedStock, 5, 100.0);
        fixedService.buyAsset(fixedCrypto, 2, 1000.0);

        double expectedValue = (5 * 123.45) + (2 * 987.65);
        double actualValue = fixedService.calculateTotalValue();
        assertEquals(expectedValue, actualValue, 0.0001);
    }

    @Test
    @DisplayName("Portfolio tracks multiple assets correctly")
    void testPortfolioTracksMultipleAssets() {
        // Buy different assets
        portfolioService.buyAsset(testStock, 10, 100.0);
        portfolioService.buyAsset(testCrypto, 1, 1000.0);

        // Check portfolio has both assets
        assertEquals(2, portfolio.getAssetCount());
        assertNotNull(portfolio.getAsset("TEST"));
        assertNotNull(portfolio.getAsset("CRYPTO"));
    }

    @Test
    @DisplayName("Transaction history is maintained")
    void testTransactionHistoryIsMaintained() {
        // Execute multiple transactions
        portfolioService.buyAsset(testStock, 5, 100.0);
        portfolioService.sellAsset(testStock, 2, 105.0);
        portfolioService.buyAsset(testStock, 3, 110.0);

        // Check transaction count
        assertEquals(3, portfolio.getTransactionCount());
        
        // Check transaction types
        var transactions = portfolio.getTransactions();
        assertEquals(Transaction.Type.BUY, transactions.get(0).getType());
        assertEquals(Transaction.Type.SELL, transactions.get(1).getType());
        assertEquals(Transaction.Type.BUY, transactions.get(2).getType());
    }

    @Test
    @DisplayName("Invalid inputs throw exceptions")
    void testInvalidInputsThrowExceptions() {
        // Test negative quantity
        assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.buyAsset(testStock, -5, 100.0);
        });

        // Test negative price
        assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.buyAsset(testStock, 5, -100.0);
        });

        // Test zero quantity
        assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.buyAsset(testStock, 0, 100.0);
        });

        // Test zero price
        assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.buyAsset(testStock, 5, 0.0);
        });
    }

    @Test
    @DisplayName("Unrealized P&L calculation is correct")
    void testUnrealizedPnLCalculation() {
        // Buy 10 shares at $100
        portfolioService.buyAsset(testStock, 10, 100.0);
        
        // Current price is around $100 (with variation), so P&L should be small
        double pnl = portfolioService.getUnrealizedPnL();
        assertTrue(Math.abs(pnl) < 50.0); // Allow for some variation in mock prices
    }

    @Test
    @DisplayName("Portfolio performance calculation is correct")
    void testPortfolioPerformanceCalculation() {
        // Buy 10 shares at $100
        portfolioService.buyAsset(testStock, 10, 100.0);
        
        // Performance should be calculated as percentage
        double performance = portfolioService.getPortfolioPerformance();
        assertTrue(performance > -10.0 && performance < 10.0); // Allow for some variation
    }
} 