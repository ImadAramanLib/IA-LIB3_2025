package edu.najah.library.persistence;

import edu.najah.library.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.logging.Logger;

/**
 * Demo class to test database connection and save data to Neon PostgreSQL.
 * 
 * <p>This class demonstrates how to:
 * <ul>
 *   <li>Connect to the Neon PostgreSQL database</li>
 *   <li>Create tables automatically (via Hibernate)</li>
 *   <li>Save data to the database</li>
 *   <li>Query data from the database</li>
 * </ul>
 * 
 * <p>To run this demo:
 * <ol>
 *   <li>Make sure your Neon database credentials are set as environment variables</li>
 *   <li>Run: mvn exec:java -Dexec.mainClass="edu.najah.library.persistence.DatabaseDemo"</li>
 * </ol>
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class DatabaseDemo {
    
    private static final Logger logger = Logger.getLogger(DatabaseDemo.class.getName());
    
    public static void main(String[] args) {
        logger.info("=== Database Connection Demo ===");
        
        // Get EntityManager using environment variables
        EntityManager em = DatabaseConfig.createEntityManager();
        
        // Check if EntityManager creation failed (can happen if database initialization fails)
        if (em == null) {
            logger.severe("ERROR: Could not create EntityManager.");
            logger.severe("Please check your environment variables:");
            logger.severe("  - NEON_DB_URL");
            logger.severe("  - NEON_DB_USER");
            logger.severe("  - NEON_DB_PASSWORD");
            return;
        }
        
        // EntityManager is not null, proceed with database operations
        
        logger.info("✓ Successfully connected to database!");
        
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            logger.info("Transaction started...");
            
            // Create a test book
            Book testBook = new Book(
                "The Great Gatsby",
                "F. Scott Fitzgerald",
                "978-0-7432-7356-5"
            );
            
            logger.info("Creating test book: " + testBook.getTitle());
            
            // Save the book to database
            em.persist(testBook);
            
            // Commit the transaction
            transaction.commit();
            logger.info("✓ Book saved to database successfully!");
            
            // Query the book back
            logger.info("Querying saved books from database...");
            Book savedBook = em.find(Book.class, testBook.getId());
            
            if (savedBook != null) {
                logger.info("✓ Successfully retrieved book from database:");
                logger.info("  ID: " + savedBook.getId());
                logger.info("  Title: " + savedBook.getTitle());
                logger.info("  Author: " + savedBook.getAuthor());
                logger.info("  ISBN: " + savedBook.getIsbn());
                logger.info("  Available: " + savedBook.isAvailable());
            } else {
                logger.warning("✗ Could not retrieve book from database");
            }
            
            // Query all books using JPQL
            logger.info("Querying all books...");
            var allBooks = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
            logger.info("✓ Found " + allBooks.size() + " book(s) in database:");
            for (Book book : allBooks) {
                logger.info("  - " + book.getTitle() + " by " + book.getAuthor() + " (ID: " + book.getId() + ")");
            }
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
                logger.severe("Transaction rolled back due to error.");
            }
            logger.severe("ERROR: " + e.getMessage());
            logger.log(java.util.logging.Level.SEVERE, "Exception details", e);
        } finally {
            em.close();
            logger.info("Database connection closed.");
        }
        
        logger.info("=== Demo Complete ===");
        logger.info("To verify in Neon Console:");
        logger.info("1. Go to https://console.neon.tech");
        logger.info("2. Select your project");
        logger.info("3. Click on 'SQL Editor' tab");
        logger.info("4. Run: SELECT * FROM books;");
    }
}

