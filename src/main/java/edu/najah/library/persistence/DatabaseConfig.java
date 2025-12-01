package edu.najah.library.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Database configuration and connection management for Neon PostgreSQL.
 * 
 * <p>This class manages the EntityManagerFactory and provides database connection
 * configuration for the Neon PostgreSQL database service.</p>
 * 
 * <p>Database connection properties can be set via environment variables:
 * <ul>
 *   <li>NEON_DB_URL - PostgreSQL connection URL</li>
 *   <li>NEON_DB_USER - Database username</li>
 *   <li>NEON_DB_PASSWORD - Database password</li>
 * </ul>
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class DatabaseConfig {
    
    private static final String PERSISTENCE_UNIT_NAME = "libraryPU";
    private static EntityManagerFactory entityManagerFactory;
    
    /**
     * Gets or creates the EntityManagerFactory.
     * 
     * @param dbUrl the database URL (e.g., jdbc:postgresql://host:port/database)
     * @param dbUser the database username
     * @param dbPassword the database password
     * @return the EntityManagerFactory instance
     */
    public static EntityManagerFactory getEntityManagerFactory(String dbUrl, String dbUser, String dbPassword) {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            java.util.Map<String, String> properties = new java.util.HashMap<>();
            
            properties.put("jakarta.persistence.jdbc.url", dbUrl);
            properties.put("jakarta.persistence.jdbc.user", dbUser);
            properties.put("jakarta.persistence.jdbc.password", dbPassword);
            
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
        }
        return entityManagerFactory;
    }
    
    /**
     * Gets or creates the EntityManagerFactory using environment variables.
     * 
     * @return the EntityManagerFactory instance, or null if environment variables are not set
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        String dbUrl = System.getenv("NEON_DB_URL");
        String dbUser = System.getenv("NEON_DB_USER");
        String dbPassword = System.getenv("NEON_DB_PASSWORD");
        
        if (dbUrl == null || dbUser == null || dbPassword == null) {
            System.err.println("Warning: Database environment variables not set.");
            System.err.println("Please set: NEON_DB_URL, NEON_DB_USER, NEON_DB_PASSWORD");
            return null;
        }
        
        return getEntityManagerFactory(dbUrl, dbUser, dbPassword);
    }
    
    /**
     * Creates a new EntityManager instance.
     * 
     * @param dbUrl the database URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @return a new EntityManager instance
     */
    public static EntityManager createEntityManager(String dbUrl, String dbUser, String dbPassword) {
        EntityManagerFactory emf = getEntityManagerFactory(dbUrl, dbUser, dbPassword);
        return emf.createEntityManager();
    }
    
    /**
     * Creates a new EntityManager instance using environment variables.
     * 
     * @return a new EntityManager instance, or null if environment variables are not set
     */
    public static EntityManager createEntityManager() {
        EntityManagerFactory emf = getEntityManagerFactory();
        if (emf == null) {
            return null;
        }
        return emf.createEntityManager();
    }
    
    /**
     * Closes the EntityManagerFactory and releases resources.
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }
    
    /**
     * Checks if the EntityManagerFactory is initialized and open.
     * 
     * @return true if the factory is initialized and open, false otherwise
     */
    public static boolean isInitialized() {
        return entityManagerFactory != null && entityManagerFactory.isOpen();
    }
}

