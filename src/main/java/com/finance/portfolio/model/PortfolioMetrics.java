package com.finance.portfolio.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable class representing portfolio performance metrics.
 * Follows Single Responsibility Principle by focusing only on metrics calculation.
 */
public final class PortfolioMetrics {
    private final BigDecimal totalValue;
    private final BigDecimal totalCost;
    private final BigDecimal unrealizedPnL;
    private final BigDecimal realizedPnL;
    private final BigDecimal totalReturnPercentage;
    private final BigDecimal volatility;
    private final BigDecimal sharpeRatio;
    private final int assetCount;
    private final int transactionCount;
    private final LocalDateTime calculationTimestamp;

    private PortfolioMetrics(Builder builder) {
        this.totalValue = builder.totalValue;
        this.totalCost = builder.totalCost;
        this.unrealizedPnL = builder.unrealizedPnL;
        this.realizedPnL = builder.realizedPnL;
        this.totalReturnPercentage = builder.totalReturnPercentage;
        this.volatility = builder.volatility;
        this.sharpeRatio = builder.sharpeRatio;
        this.assetCount = builder.assetCount;
        this.transactionCount = builder.transactionCount;
        this.calculationTimestamp = builder.calculationTimestamp;
    }

    // Getters - immutable object pattern
    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public BigDecimal getUnrealizedPnL() {
        return unrealizedPnL;
    }

    public BigDecimal getRealizedPnL() {
        return realizedPnL;
    }

    public BigDecimal getTotalReturnPercentage() {
        return totalReturnPercentage;
    }

    public BigDecimal getVolatility() {
        return volatility;
    }

    public BigDecimal getSharpeRatio() {
        return sharpeRatio;
    }

    public int getAssetCount() {
        return assetCount;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public LocalDateTime getCalculationTimestamp() {
        return calculationTimestamp;
    }

    /**
     * Builder pattern for creating PortfolioMetrics instances.
     * Follows Builder pattern for complex object construction.
     */
    public static class Builder {
        private BigDecimal totalValue = BigDecimal.ZERO.setScale(4);
        private BigDecimal totalCost = BigDecimal.ZERO.setScale(4);
        private BigDecimal unrealizedPnL = BigDecimal.ZERO.setScale(4);
        private BigDecimal realizedPnL = BigDecimal.ZERO.setScale(4);
        private BigDecimal totalReturnPercentage = BigDecimal.ZERO.setScale(4);
        private BigDecimal volatility = BigDecimal.ZERO.setScale(4);
        private BigDecimal sharpeRatio = BigDecimal.ZERO.setScale(4);
        private int assetCount = 0;
        private int transactionCount = 0;
        private LocalDateTime calculationTimestamp = LocalDateTime.now();

        public Builder totalValue(BigDecimal totalValue) {
            this.totalValue = Objects.requireNonNull(totalValue, "Total value cannot be null");
            return this;
        }

        public Builder totalCost(BigDecimal totalCost) {
            this.totalCost = Objects.requireNonNull(totalCost, "Total cost cannot be null");
            return this;
        }

        public Builder unrealizedPnL(BigDecimal unrealizedPnL) {
            this.unrealizedPnL = Objects.requireNonNull(unrealizedPnL, "Unrealized P&L cannot be null");
            return this;
        }

        public Builder realizedPnL(BigDecimal realizedPnL) {
            this.realizedPnL = Objects.requireNonNull(realizedPnL, "Realized P&L cannot be null");
            return this;
        }

        public Builder totalReturnPercentage(BigDecimal totalReturnPercentage) {
            this.totalReturnPercentage = Objects.requireNonNull(totalReturnPercentage, "Total return percentage cannot be null");
            return this;
        }

        public Builder volatility(BigDecimal volatility) {
            this.volatility = Objects.requireNonNull(volatility, "Volatility cannot be null");
            return this;
        }

        public Builder sharpeRatio(BigDecimal sharpeRatio) {
            this.sharpeRatio = Objects.requireNonNull(sharpeRatio, "Sharpe ratio cannot be null");
            return this;
        }

        public Builder assetCount(int assetCount) {
            if (assetCount < 0) {
                throw new IllegalArgumentException("Asset count cannot be negative");
            }
            this.assetCount = assetCount;
            return this;
        }

        public Builder transactionCount(int transactionCount) {
            if (transactionCount < 0) {
                throw new IllegalArgumentException("Transaction count cannot be negative");
            }
            this.transactionCount = transactionCount;
            return this;
        }

        public Builder calculationTimestamp(LocalDateTime calculationTimestamp) {
            this.calculationTimestamp = Objects.requireNonNull(calculationTimestamp, "Calculation timestamp cannot be null");
            return this;
        }

        public PortfolioMetrics build() {
            return new PortfolioMetrics(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PortfolioMetrics that = (PortfolioMetrics) obj;
        return assetCount == that.assetCount &&
               transactionCount == that.transactionCount &&
               Objects.equals(totalValue, that.totalValue) &&
               Objects.equals(totalCost, that.totalCost) &&
               Objects.equals(unrealizedPnL, that.unrealizedPnL) &&
               Objects.equals(realizedPnL, that.realizedPnL) &&
               Objects.equals(totalReturnPercentage, that.totalReturnPercentage) &&
               Objects.equals(volatility, that.volatility) &&
               Objects.equals(sharpeRatio, that.sharpeRatio) &&
               Objects.equals(calculationTimestamp, that.calculationTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalValue, totalCost, unrealizedPnL, realizedPnL,
                           totalReturnPercentage, volatility, sharpeRatio,
                           assetCount, transactionCount, calculationTimestamp);
    }

    @Override
    public String toString() {
        return String.format("PortfolioMetrics{totalValue=%s, totalCost=%s, unrealizedPnL=%s, " +
                           "realizedPnL=%s, totalReturnPercentage=%s%%, volatility=%s, " +
                           "sharpeRatio=%s, assetCount=%d, transactionCount=%d, " +
                           "calculationTimestamp=%s}",
                           totalValue, totalCost, unrealizedPnL, realizedPnL,
                           totalReturnPercentage, volatility, sharpeRatio,
                           assetCount, transactionCount, calculationTimestamp);
    }
} 