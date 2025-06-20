package com.finance.portfolio.service;

import com.finance.portfolio.model.Portfolio;
import com.finance.portfolio.model.PortfolioMetrics;
import com.finance.portfolio.model.Asset;
import com.finance.portfolio.model.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for calculating portfolio analytics and performance metrics.
 * Follows Single Responsibility Principle by focusing only on analytics calculations.
 */
public class PortfolioAnalyticsService {
    
    private static final int DECIMAL_PLACES = 4;
    private static final BigDecimal RISK_FREE_RATE = new BigDecimal("0.02"); // 2% risk-free rate
    
    private final Portfolio portfolio;
    
    /**
     * Constructor with dependency injection.
     * @param portfolio The portfolio to analyze
     */
    public PortfolioAnalyticsService(Portfolio portfolio) {
        this.portfolio = Objects.requireNonNull(portfolio, "Portfolio cannot be null");
    }
    
    /**
     * Calculate comprehensive portfolio metrics.
     * @return PortfolioMetrics object containing all calculated metrics
     */
    public PortfolioMetrics calculatePortfolioMetrics() {
        BigDecimal totalValue = calculateTotalValue();
        BigDecimal totalCost = calculateTotalCost();
        BigDecimal unrealizedPnL = calculateUnrealizedPnL(totalValue, totalCost);
        BigDecimal realizedPnL = calculateRealizedPnL();
        BigDecimal totalReturnPercentage = calculateTotalReturnPercentage(totalCost, totalValue);
        BigDecimal volatility = calculateVolatility();
        BigDecimal sharpeRatio = calculateSharpeRatio(totalReturnPercentage, volatility);
        
        return new PortfolioMetrics.Builder()
                .totalValue(totalValue)
                .totalCost(totalCost)
                .unrealizedPnL(unrealizedPnL)
                .realizedPnL(realizedPnL)
                .totalReturnPercentage(totalReturnPercentage)
                .volatility(volatility)
                .sharpeRatio(sharpeRatio)
                .assetCount(portfolio.getAssetCount())
                .transactionCount(portfolio.getTransactionCount())
                .build();
    }
    
    /**
     * Calculate the total market value of the portfolio.
     * @return Total portfolio value
     */
    public BigDecimal calculateTotalValue() {
        if (portfolio.getAssets().isEmpty()) {
            return BigDecimal.ZERO.setScale(DECIMAL_PLACES);
        }
        return portfolio.getAssets().values().stream()
                .map(asset -> BigDecimal.valueOf(asset.getMarketValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate the total cost basis of the portfolio.
     * @return Total cost basis
     */
    public BigDecimal calculateTotalCost() {
        if (portfolio.getAssets().isEmpty()) {
            return BigDecimal.ZERO.setScale(DECIMAL_PLACES);
        }
        return portfolio.getAssets().values().stream()
                .map(asset -> BigDecimal.valueOf(asset.getQuantity())
                        .multiply(BigDecimal.valueOf(asset.getAveragePrice())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate unrealized profit/loss.
     * @param totalValue Current total value
     * @param totalCost Total cost basis
     * @return Unrealized P&L
     */
    private BigDecimal calculateUnrealizedPnL(BigDecimal totalValue, BigDecimal totalCost) {
        return totalValue.subtract(totalCost).setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate realized profit/loss from completed transactions.
     * @return Realized P&L
     */
    public BigDecimal calculateRealizedPnL() {
        List<Transaction> sellTransactions = portfolio.getTransactions().stream()
                .filter(transaction -> transaction.getType() == Transaction.Type.SELL)
                .collect(Collectors.toList());
        
        BigDecimal realizedPnL = BigDecimal.ZERO;
        
        for (Transaction sellTransaction : sellTransactions) {
            BigDecimal sellValue = BigDecimal.valueOf(sellTransaction.getTotalValue());
            BigDecimal averageCost = BigDecimal.valueOf(sellTransaction.getAsset().getAveragePrice());
            BigDecimal costBasis = averageCost.multiply(BigDecimal.valueOf(sellTransaction.getQuantity()));
            BigDecimal transactionPnL = sellValue.subtract(costBasis);
            realizedPnL = realizedPnL.add(transactionPnL);
        }
        
        return realizedPnL.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate total return percentage.
     * @param totalCost Total cost basis
     * @param totalValue Current total value
     * @return Total return percentage
     */
    private BigDecimal calculateTotalReturnPercentage(BigDecimal totalCost, BigDecimal totalValue) {
        if (totalCost.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(DECIMAL_PLACES);
        }
        
        return totalValue.subtract(totalCost)
                .divide(totalCost, DECIMAL_PLACES, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    /**
     * Calculate portfolio volatility based on asset price variations.
     * @return Portfolio volatility
     */
    public BigDecimal calculateVolatility() {
        Map<String, Asset> assets = portfolio.getAssets();
        if (assets.isEmpty()) {
            return BigDecimal.ZERO.setScale(DECIMAL_PLACES);
        }
        
        // Calculate weighted average volatility based on asset allocation
        BigDecimal totalValue = calculateTotalValue();
        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(DECIMAL_PLACES);
        }
        
        BigDecimal weightedVolatility = assets.values().stream()
                .map(asset -> {
                    BigDecimal assetValue = BigDecimal.valueOf(asset.getMarketValue());
                    BigDecimal weight = assetValue.divide(totalValue, DECIMAL_PLACES, RoundingMode.HALF_UP);
                    // Simplified volatility calculation - in real implementation, 
                    // this would use historical price data
                    BigDecimal assetVolatility = BigDecimal.valueOf(0.15); // 15% assumed volatility
                    return weight.multiply(assetVolatility);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return weightedVolatility.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate Sharpe ratio (risk-adjusted return).
     * @param totalReturn Total return percentage
     * @param volatility Portfolio volatility
     * @return Sharpe ratio
     */
    private BigDecimal calculateSharpeRatio(BigDecimal totalReturn, BigDecimal volatility) {
        if (volatility.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(DECIMAL_PLACES);
        }
        
        BigDecimal excessReturn = totalReturn.subtract(RISK_FREE_RATE.multiply(BigDecimal.valueOf(100)));
        return excessReturn.divide(volatility, DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
    
    /**
     * Get asset allocation breakdown.
     * @return Map of asset symbols to their percentage allocation
     */
    public Map<String, BigDecimal> getAssetAllocation() {
        BigDecimal totalValue = calculateTotalValue();
        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            return Map.of();
        }
        
        return portfolio.getAssets().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            BigDecimal assetValue = BigDecimal.valueOf(entry.getValue().getMarketValue());
                            return assetValue.divide(totalValue, DECIMAL_PLACES, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100));
                        }
                ));
    }
    
    /**
     * Get the best performing asset.
     * @return Optional containing the best performing asset symbol and return
     */
    public Optional<AssetPerformance> getBestPerformingAsset() {
        return portfolio.getAssets().values().stream()
                .map(this::calculateAssetPerformance)
                .max((a, b) -> a.returnPercentage().compareTo(b.returnPercentage()));
    }
    
    /**
     * Get the worst performing asset.
     * @return Optional containing the worst performing asset symbol and return
     */
    public Optional<AssetPerformance> getWorstPerformingAsset() {
        return portfolio.getAssets().values().stream()
                .map(this::calculateAssetPerformance)
                .min((a, b) -> a.returnPercentage().compareTo(b.returnPercentage()));
    }
    
    /**
     * Calculate performance metrics for a specific asset.
     * @param asset The asset to analyze
     * @return AssetPerformance object
     */
    private AssetPerformance calculateAssetPerformance(Asset asset) {
        BigDecimal currentPrice = BigDecimal.valueOf(asset.getCurrentPrice());
        BigDecimal averagePrice = BigDecimal.valueOf(asset.getAveragePrice());
        BigDecimal returnPercentage = currentPrice.subtract(averagePrice)
                .divide(averagePrice, DECIMAL_PLACES, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        return new AssetPerformance(asset.getSymbol(), returnPercentage);
    }
    
    /**
     * Record class for asset performance data.
     * @param symbol Asset symbol
     * @param returnPercentage Return percentage
     */
    public record AssetPerformance(String symbol, BigDecimal returnPercentage) {
        public AssetPerformance {
            Objects.requireNonNull(symbol, "Symbol cannot be null");
            Objects.requireNonNull(returnPercentage, "Return percentage cannot be null");
        }
    }
} 