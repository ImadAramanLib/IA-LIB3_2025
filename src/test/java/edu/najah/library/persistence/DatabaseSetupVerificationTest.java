package edu.najah.library.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verification test for database setup configuration files.
 * Verifies that all necessary configuration files are in place.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class DatabaseSetupVerificationTest {
    
    @Test
    @DisplayName("Verify persistence.xml exists in resources")
    void testPersistenceXmlExists() {
        // Check if persistence.xml exists in the classpath
        InputStream persistenceXml = this.getClass()
                .getClassLoader()
                .getResourceAsStream("META-INF/persistence.xml");
        
        assertNotNull(persistenceXml, "persistence.xml should exist in src/main/resources/META-INF/");
    }
    
    @Test
    @DisplayName("Verify database.properties.example exists")
    void testDatabasePropertiesExampleExists() {
        // Check if database.properties.example exists
        InputStream propsExample = this.getClass()
                .getClassLoader()
                .getResourceAsStream("database.properties.example");
        
        assertNotNull(propsExample, "database.properties.example should exist in src/main/resources/");
    }
    
    @Test
    @DisplayName("Verify DatabaseConfig class exists and is accessible")
    void testDatabaseConfigClassExists() {
        assertNotNull(DatabaseConfig.class);
        assertEquals("edu.najah.library.persistence.DatabaseConfig", DatabaseConfig.class.getName());
    }
    
    @Test
    @DisplayName("Verify DatabaseConfig has required methods")
    void testDatabaseConfigMethods() {
        // Verify static methods exist by checking they can be called
        assertDoesNotThrow(DatabaseConfig::isInitialized);
        assertDoesNotThrow(DatabaseConfig::closeEntityManagerFactory);
        
        // getEntityManagerFactory without params should return null if no env vars set
        // This is expected behavior - not an error
        assertDoesNotThrow(() -> {
            jakarta.persistence.EntityManagerFactory emf = DatabaseConfig.getEntityManagerFactory();
            // Will be null without credentials, which is fine
        });
    }
}

