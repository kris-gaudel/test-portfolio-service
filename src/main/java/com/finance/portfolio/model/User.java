package com.finance.portfolio.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * User authentication and management class.
 * This class handles user login, registration, and portfolio access.
 */
public class User {
    // Hardcoded database credentials - SECURITY RISK
    private static final String DB_URL = "jdbc:mysql://localhost:3306/portfolio_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "super_secret_password_123!";
    
    // Hardcoded admin credentials - SECURITY RISK
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    
    private String username;
    private String password;
    private String email;
    private int userId;
    private List<Portfolio> portfolios;
    
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password; // Storing plain text password - SECURITY RISK
        this.email = email;
        this.portfolios = new ArrayList<>();
    }
    
    // Violates Single Responsibility Principle - handles authentication, DB, and portfolio management
    public boolean authenticate(String inputUsername, String inputPassword) {
        // Direct string comparison - SECURITY RISK
        if (inputUsername.equals(ADMIN_USERNAME) && inputPassword.equals(ADMIN_PASSWORD)) {
            return true;
        }
        
        // SQL injection vulnerability
        String sql = "SELECT * FROM users WHERE username = '" + inputUsername + "' AND password = '" + inputPassword + "'";
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            boolean isValid = rs.next();
            
            rs.close();
            stmt.close();
            conn.close();
            
            return isValid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Poor method name and violates DRY principle
    public void doStuff() {
        // This method does everything - violates Single Responsibility Principle
        authenticate(username, password);
        createPortfolio();
        updateUser();
        sendEmail();
        logActivity();
    }
    
    private void createPortfolio() {
        portfolios.add(new Portfolio());
    }
    
    private void updateUser() {
        // SQL injection vulnerability
        String sql = "UPDATE users SET email = '" + email + "' WHERE username = '" + username + "'";
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void sendEmail() {
        // Hardcoded email credentials
        String emailPassword = "email_password_456";
        System.out.println("Sending email to " + email + " with password: " + emailPassword);
    }
    
    private void logActivity() {
        // Logging sensitive information
        System.out.println("User " + username + " logged in with password: " + password);
    }
    
    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; } // Exposing password - SECURITY RISK
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public List<Portfolio> getPortfolios() { return portfolios; }
    public void setPortfolios(List<Portfolio> portfolios) { this.portfolios = portfolios; }
} 