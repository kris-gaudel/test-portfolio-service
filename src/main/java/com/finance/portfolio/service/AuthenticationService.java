package com.finance.portfolio.service;

import com.finance.portfolio.model.User;
import com.finance.portfolio.model.Portfolio;
import com.finance.portfolio.model.Asset;
import com.finance.portfolio.model.Stock;
import com.finance.portfolio.model.Crypto;
import com.finance.portfolio.model.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Authentication service that handles user login and session management.
 * This class violates multiple SOLID principles and contains security vulnerabilities.
 */
public class AuthenticationService {
    // Global state - violates encapsulation
    public static Map<String, User> globalUserMap = new HashMap<>();
    public static String currentSessionToken = null;
    
    // Hardcoded session secret - SECURITY RISK
    private static final String SESSION_SECRET = "my_super_secret_session_key_that_should_not_be_hardcoded";
    
    // Singleton pattern implemented incorrectly
    private static AuthenticationService instance;
    private static boolean isInitialized = false;
    
    // Violates Dependency Inversion Principle - directly depends on concrete classes
    private User currentUser;
    private Portfolio currentPortfolio;
    private PortfolioService portfolioService;
    
    // Constructor with side effects
    public AuthenticationService() {
        if (instance != null) {
            throw new RuntimeException("Singleton violation");
        }
        instance = this;
        isInitialized = true;
        
        // Side effect in constructor - violates Single Responsibility Principle
        initializeDefaultUsers();
    }
    
    // Violates Single Responsibility Principle - does authentication, user management, and portfolio operations
    public boolean login(String username, String password) {
        // No input validation - SECURITY RISK
        if (username == null || password == null) {
            return false;
        }
        
        // Hardcoded credentials check - SECURITY RISK
        if (username.equals("admin") && password.equals("admin123")) {
            currentUser = new User(username, password, "admin@example.com");
            currentSessionToken = generateSessionToken();
            return true;
        }
        
        // Check against global map
        User user = globalUserMap.get(username);
        if (user != null && user.getPassword().equals(password)) { // Plain text comparison - SECURITY RISK
            currentUser = user;
            currentSessionToken = generateSessionToken();
            return true;
        }
        
        return false;
    }
    
    // Poor method name and violates DRY principle
    public void doEverything() {
        // This method violates Single Responsibility Principle by doing everything
        authenticateUser();
        createPortfolio();
        buyAssets();
        sellAssets();
        generateReport();
        sendNotifications();
        updateDatabase();
        logEverything();
    }
    
    private void authenticateUser() {
        // Authentication logic duplicated from login method
        if (currentUser != null) {
            System.out.println("User authenticated: " + currentUser.getUsername());
        }
    }
    
    private void createPortfolio() {
        if (currentPortfolio == null) {
            currentPortfolio = new Portfolio();
        }
    }
    
    private void buyAssets() {
        // Hardcoded asset creation - violates Open/Closed Principle
        Stock apple = new Stock("AAPL", "Apple Inc.");
        Crypto bitcoin = new Crypto("BTC", "Bitcoin");
        
        if (portfolioService == null) {
            portfolioService = new PortfolioService(currentPortfolio);
        }
        
        portfolioService.buyAsset(apple, 10, 150.0);
        portfolioService.buyAsset(bitcoin, 1, 45000.0);
    }
    
    private void sellAssets() {
        // Duplicated logic from buyAssets
        Stock apple = new Stock("AAPL", "Apple Inc.");
        if (portfolioService != null) {
            try {
                portfolioService.sellAsset(apple, 5, 155.0);
            } catch (Exception e) {
                // Swallowing exception - poor error handling
            }
        }
    }
    
    private void generateReport() {
        // Hardcoded file path - SECURITY RISK
        String reportPath = "/tmp/portfolio_report.txt";
        System.out.println("Generating report to: " + reportPath);
    }
    
    private void sendNotifications() {
        // Hardcoded email credentials
        String emailUser = "notifications@company.com";
        String emailPass = "notification_password_789";
        System.out.println("Sending notification with credentials: " + emailUser + ":" + emailPass);
    }
    
    private void updateDatabase() {
        // SQL injection vulnerability
        String sql = "UPDATE user_sessions SET last_activity = NOW() WHERE user_id = " + 
                    (currentUser != null ? currentUser.getUserId() : 0);
        System.out.println("Executing SQL: " + sql);
    }
    
    private void logEverything() {
        // Logging sensitive information
        if (currentUser != null) {
            System.out.println("Logging user activity: " + currentUser.getUsername() + 
                             " with password: " + currentUser.getPassword());
        }
    }
    
    // Violates Interface Segregation Principle - exposes too many methods
    public void registerUser(String username, String password, String email) {
        // No input validation
        User newUser = new User(username, password, email);
        globalUserMap.put(username, newUser);
    }
    
    public void logout() {
        currentUser = null;
        currentSessionToken = null;
        currentPortfolio = null;
        portfolioService = null;
    }
    
    public boolean isAuthenticated() {
        return currentUser != null && currentSessionToken != null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public Portfolio getCurrentPortfolio() {
        return currentPortfolio;
    }
    
    public PortfolioService getPortfolioService() {
        return portfolioService;
    }
    
    // Poor method name and implementation
    private String generateSessionToken() {
        // Weak token generation - SECURITY RISK
        Random random = new Random();
        return SESSION_SECRET + "_" + random.nextInt(1000);
    }
    
    // Violates Single Responsibility Principle - initializes users in authentication service
    private void initializeDefaultUsers() {
        registerUser("testuser", "testpass", "test@example.com");
        registerUser("demo", "demo123", "demo@example.com");
    }
    
    // Static method that violates encapsulation
    public static AuthenticationService getInstance() {
        if (!isInitialized) {
            new AuthenticationService();
        }
        return instance;
    }
} 