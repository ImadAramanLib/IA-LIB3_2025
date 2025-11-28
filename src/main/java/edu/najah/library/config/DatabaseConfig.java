package edu.najah.library.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Database configuration for connecting to Neon PostgreSQL database
 * Manages EntityManager creation and lifecycle
 * Reads database credentials from environment variables: NEON_DB_URL, NEON_DB_USER, NEON_DB_PASSWORD
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class DatabaseConfig {
    
    private static EntityManagerFactory emf;
    private static final String DB_URL_ENV = "NEON_DB_URL";
    private static final String DB_USER_ENV = "NEON_DB_USER";
    private static final String DB_PASSWORD_ENV = "NEON_DB_PASSWORD";
    
    static {
        // Don't initialize here - let it be lazy-loaded
        // This allows the GUI to start even if DB vars aren't set
        emf = null;
    }
    
    /**
     * Initializes the EntityManagerFactory if not already initialized.
     * Returns true if successful, false if environment variables are missing.
     */
    private static synchronized boolean initializeIfNeeded() {
        if (emf != null) {
            return true;
        }
        
        try {
            // Read connection details from environment variables or system properties
            // Check environment variables first, then system properties as fallback
            String dbUrl = System.getenv(DB_URL_ENV);
            if (dbUrl == null || dbUrl.trim().isEmpty()) {
                dbUrl = System.getProperty(DB_URL_ENV);
            }
            
            String dbUser = System.getenv(DB_USER_ENV);
            if (dbUser == null || dbUser.trim().isEmpty()) {
                dbUser = System.getProperty(DB_USER_ENV);
            }
            
            String dbPassword = System.getenv(DB_PASSWORD_ENV);
            if (dbPassword == null || dbPassword.trim().isEmpty()) {
                dbPassword = System.getProperty(DB_PASSWORD_ENV);
            }
            
            // If still not set, use default values (hardcoded for convenience)
            if (dbUrl == null || dbUrl.trim().isEmpty()) {
                dbUrl = "jdbc:postgresql://ep-red-sun-agapswm0-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require";
                System.out.println("ℹ️  Using default database URL (environment variable not set)");
            }
            if (dbUser == null || dbUser.trim().isEmpty()) {
                dbUser = "neondb_owner";
                System.out.println("ℹ️  Using default database user (environment variable not set)");
            }
            if (dbPassword == null || dbPassword.trim().isEmpty()) {
                dbPassword = "npg_vFeS7Qoi3WuT";
                System.out.println("ℹ️  Using default database password (environment variable not set)");
            }
            
            // Create properties map with connection details
            Map<String, Object> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.url", dbUrl);
            properties.put("jakarta.persistence.jdbc.user", dbUser);
            properties.put("jakarta.persistence.jdbc.password", dbPassword);
            
            // Initialize the EntityManagerFactory from persistence.xml with properties
            emf = Persistence.createEntityManagerFactory("NeonLibraryPU", properties);
            System.out.println("✅ Database connection successful!");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize EntityManagerFactory:");
            e.printStackTrace();
            emf = null;
            return false;
        }
    }
    
    /**
     * Creates and returns a new EntityManager instance.
     * Returns null if database is not configured (offline mode).
     * 
     * @return EntityManager instance, or null if database not configured
     */
    public static EntityManager createEntityManager() {
        if (!initializeIfNeeded()) {
            return null; // Offline mode - no database connection
        }
        if (emf == null) {
            return null;
        }
        return emf.createEntityManager();
    }
    
    /**
     * Closes the EntityManagerFactory and releases resources
     * Should be called on application shutdown
     */
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
