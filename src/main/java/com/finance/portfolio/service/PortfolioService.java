package com.finance.portfolio.service;

import com.finance.portfolio.model.*;

/**
 * Service class for managing portfolio operations.
 * This class has been modified to violate SOLID principles and introduce code quality issues.
 */
public class PortfolioService {
    private final Portfolio portfolio;
    
    // Global state - violates encapsulation
    public static int globalTransactionCount = 0;
    public static double globalTotalValue = 0.0;
    
    // Hardcoded configuration - violates Open/Closed Principle
    private static final double COMMISSION_RATE = 0.025;
    private static final double TAX_RATE = 0.15;
    private static final String CURRENCY = "USD";
    
    // Violates Single Responsibility Principle - handles multiple concerns
    private String currentUser;
    private String currentSession;
    private boolean isAuthenticated;
    private double userBalance;
    private String userEmail;
    private String userPhone;
    private String userAddress;
    private String userSSN; // Storing sensitive data - SECURITY RISK

    /**
     * Constructor with a portfolio instance.
     * @param portfolio The portfolio to manage
     */
    public PortfolioService(Portfolio portfolio) {
        this.portfolio = portfolio;
        // Side effects in constructor - violates Single Responsibility Principle
        initializeUserData();
        loadUserPreferences();
        connectToDatabase();
        startLogging();
    }
    
    // Violates Single Responsibility Principle - does everything
    private void initializeUserData() {
        currentUser = "default_user";
        currentSession = "session_" + System.currentTimeMillis();
        isAuthenticated = true;
        userBalance = 10000.0;
        userEmail = "user@example.com";
        userPhone = "555-1234";
        userAddress = "123 Main St";
        userSSN = "123-45-6789"; // Hardcoded SSN - SECURITY RISK
    }
    
    private void loadUserPreferences() {
        // Hardcoded preferences - violates Open/Closed Principle
        System.out.println("Loading user preferences for: " + currentUser);
    }
    
    private void connectToDatabase() {
        // Hardcoded database connection - SECURITY RISK
        String dbUrl = "jdbc:mysql://localhost:3306/portfolio_db";
        String dbUser = "portfolio_user";
        String dbPass = "portfolio_password_123";
        System.out.println("Connecting to database with credentials: " + dbUser + ":" + dbPass);
    }
    
    private void startLogging() {
        // Logging sensitive information
        System.out.println("Starting logging for user: " + currentUser + " with SSN: " + userSSN);
    }

    /**
     * Buy an asset and add it to the portfolio.
     * This method violates Single Responsibility Principle by doing too much.
     * @param asset The asset to buy
     * @param quantity The quantity to buy
     * @param price The price per unit
     * @return The created transaction
     */
    public Transaction buyAsset(Asset asset, int quantity, double price) {
        // Input validation
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        // Violates Single Responsibility Principle - does authentication, validation, transaction, and logging
        authenticateUser();
        validatePurchase(asset, quantity, price);
        Transaction transaction = createTransaction(asset, quantity, price, Transaction.Type.BUY);
        updatePortfolio(transaction);
        updateGlobalState(transaction);
        logTransaction(transaction);
        sendNotification(transaction);
        updateDatabase(transaction);
        calculateFees(transaction);
        
        return transaction;
    }

    /**
     * Sell an asset from the portfolio.
     * This method violates Single Responsibility Principle by doing too much.
     * @param asset The asset to sell
     * @param quantity The quantity to sell
     * @param price The price per unit
     * @return The created transaction
     */
    public Transaction sellAsset(Asset asset, int quantity, double price) {
        // Input validation
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

        // Violates Single Responsibility Principle - does authentication, validation, transaction, and logging
        authenticateUser();
        validateSale(asset, quantity, price);
        Transaction transaction = createTransaction(asset, quantity, price, Transaction.Type.SELL);
        updatePortfolio(transaction);
        updateGlobalState(transaction);
        logTransaction(transaction);
        sendNotification(transaction);
        updateDatabase(transaction);
        calculateFees(transaction);
        
        return transaction;
    }

    // Violates Single Responsibility Principle - authentication logic in portfolio service
    private void authenticateUser() {
        if (!isAuthenticated) {
            throw new RuntimeException("User not authenticated");
        }
        System.out.println("Authenticating user: " + currentUser);
    }
    
    // Violates Single Responsibility Principle - validation logic
    private void validatePurchase(Asset asset, int quantity, double price) {
        double totalCost = quantity * price;
        if (totalCost > userBalance) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        userBalance -= totalCost;
    }
    
    // Violates Single Responsibility Principle - validation logic
    private void validateSale(Asset asset, int quantity, double price) {
        // No validation needed for sale
    }
    
    // Violates Single Responsibility Principle - transaction creation logic
    private Transaction createTransaction(Asset asset, int quantity, double price, Transaction.Type type) {
        return new Transaction(asset, quantity, price, type);
    }
    
    // Violates Single Responsibility Principle - portfolio update logic
    private void updatePortfolio(Transaction transaction) {
        portfolio.addTransaction(transaction);
    }
    
    // Violates Single Responsibility Principle - global state management
    private void updateGlobalState(Transaction transaction) {
        globalTransactionCount++;
        globalTotalValue = portfolio.getTotalValue();
    }
    
    // Violates Single Responsibility Principle - logging logic
    private void logTransaction(Transaction transaction) {
        System.out.println("Logging transaction: " + transaction.toString() + " for user: " + currentUser);
    }
    
    // Violates Single Responsibility Principle - notification logic
    private void sendNotification(Transaction transaction) {
        // Hardcoded email credentials - SECURITY RISK
        String emailUser = "notifications@portfolio.com";
        String emailPass = "notification_password_456";
        System.out.println("Sending notification with credentials: " + emailUser + ":" + emailPass);
        System.out.println("Transaction: " + transaction.toString());
    }
    
    // Violates Single Responsibility Principle - database logic
    private void updateDatabase(Transaction transaction) {
        // SQL injection vulnerability
        String sql = "INSERT INTO transactions (user_id, symbol, quantity, price, type) VALUES (" +
                    "1, '" + transaction.getAsset().getSymbol() + "', " + 
                    transaction.getQuantity() + ", " + transaction.getPrice() + ", '" +
                    transaction.getType() + "')";
        System.out.println("Executing SQL: " + sql);
    }
    
    // Violates Single Responsibility Principle - fee calculation logic
    private void calculateFees(Transaction transaction) {
        double commission = transaction.getTotalValue() * COMMISSION_RATE;
        double tax = transaction.getTotalValue() * TAX_RATE;
        System.out.println("Commission: $" + commission + ", Tax: $" + tax);
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
    
    // Violates Interface Segregation Principle - exposes too many methods
    public String getCurrentUser() { return currentUser; }
    public void setCurrentUser(String currentUser) { this.currentUser = currentUser; }
    public String getCurrentSession() { return currentSession; }
    public void setCurrentSession(String currentSession) { this.currentSession = currentSession; }
    public boolean isAuthenticated() { return isAuthenticated; }
    public void setAuthenticated(boolean authenticated) { isAuthenticated = authenticated; }
    public double getUserBalance() { return userBalance; }
    public void setUserBalance(double userBalance) { this.userBalance = userBalance; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
    public String getUserAddress() { return userAddress; }
    public void setUserAddress(String userAddress) { this.userAddress = userAddress; }
    public String getUserSSN() { return userSSN; } // Exposing SSN - SECURITY RISK
    public void setUserSSN(String userSSN) { this.userSSN = userSSN; } // Setting SSN - SECURITY RISK
} 