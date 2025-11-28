package edu.najah.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Simple JDBC test to verify Neon database connection
 */
public class DatabaseTest {
    
    private static final String URL = "jdbc:postgresql://ep-red-sun-agapswm0-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    
    public static void main(String[] args) {
        String password = System.getenv("DB_PASSWORD");
        
        if (password == null || password.isEmpty()) {
            System.out.println("ERROR: DB_PASSWORD environment variable not set!");
            System.exit(1);
        }
        
        System.out.println("Testing Neon PostgreSQL Connection...");
        System.out.println("URL: " + URL);
        System.out.println("User: " + USER);
        System.out.println();
        
        try {
            System.out.println("Loading PostgreSQL driver...");
            Class.forName("org.postgresql.Driver");
            
            System.out.println("Attempting connection (timeout 10 seconds)...");
            
            // Set connection timeout
            java.util.Properties props = new java.util.Properties();
            props.setProperty("user", USER);
            props.setProperty("password", password);
            props.setProperty("connectTimeout", "10");
            props.setProperty("socketTimeout", "10");
            
            Connection conn = DriverManager.getConnection(URL, props);
            
            System.out.println("✅ CONNECTION SUCCESSFUL!");
            System.out.println();
            
            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT version()");
            if (rs.next()) {
                System.out.println("PostgreSQL Version: " + rs.getString(1));
            }
            rs.close();
            stmt.close();
            
            // Create admins table if not exists
            System.out.println();
            System.out.println("Creating 'admins' table if not exists...");
            stmt = conn.createStatement();
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS admins (" +
                "  id SERIAL PRIMARY KEY," +
                "  username VARCHAR(255) UNIQUE NOT NULL," +
                "  password VARCHAR(255) NOT NULL," +
                "  email VARCHAR(255)" +
                ")"
            );
            System.out.println("✅ Table created/verified!");
            stmt.close();
            
            conn.close();
            System.out.println();
            System.out.println("✅ ALL TESTS PASSED - Database is working!");
            
        } catch (Exception e) {
            System.out.println("❌ CONNECTION FAILED!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
