package edu.najah.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Simple JDBC test to verify Neon database connection
 */
public class DatabaseTest {
    
    private static final Logger logger = Logger.getLogger(DatabaseTest.class.getName());
    
    private static final String URL = "jdbc:postgresql://ep-red-sun-agapswm0-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    
    public static void main(String[] args) {
        String password = System.getenv("DB_PASSWORD");
        
        if (password == null || password.isEmpty()) {
            logger.severe("ERROR: DB_PASSWORD environment variable not set!");
            System.exit(1);
        }
        
        logger.info("Testing Neon PostgreSQL Connection...");
        logger.info("URL: " + URL);
        logger.info("User: " + USER);
        
        try {
            logger.info("Loading PostgreSQL driver...");
            Class.forName("org.postgresql.Driver");
            
            logger.info("Attempting connection (timeout 10 seconds)...");
            
            // Set connection timeout
            java.util.Properties props = new java.util.Properties();
            props.setProperty("user", USER);
            props.setProperty("password", password);
            props.setProperty("connectTimeout", "10");
            props.setProperty("socketTimeout", "10");
            
            Connection conn = DriverManager.getConnection(URL, props);
            
            logger.info("✅ CONNECTION SUCCESSFUL!");
            
            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT version()");
            if (rs.next()) {
                logger.info("PostgreSQL Version: " + rs.getString(1));
            }
            rs.close();
            stmt.close();
            
            // Create admins table if not exists
            logger.info("Creating 'admins' table if not exists...");
            stmt = conn.createStatement();
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS admins (" +
                "  id SERIAL PRIMARY KEY," +
                "  username VARCHAR(255) UNIQUE NOT NULL," +
                "  password VARCHAR(255) NOT NULL," +
                "  email VARCHAR(255)" +
                ")"
            );
            logger.info("✅ Table created/verified!");
            stmt.close();
            
            conn.close();
            logger.info("✅ ALL TESTS PASSED - Database is working!");
            
        } catch (Exception e) {
            logger.severe("❌ CONNECTION FAILED!");
            logger.severe("Error: " + e.getMessage());
            logger.log(java.util.logging.Level.SEVERE, "Exception details", e);
        }
    }
}
