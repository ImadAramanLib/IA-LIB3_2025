package edu.najah.library.persistence;

import edu.najah.library.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

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
    
    public static void main(String[] args) {
        System.out.println("=== Database Connection Demo ===");
        System.out.println();
        
        // Get EntityManager using environment variables
        EntityManager em = DatabaseConfig.createEntityManager();
        
        if (em == null) {
            System.err.println("ERROR: Could not create EntityManager.");
            System.err.println("Please check your environment variables:");
            System.err.println("  - NEON_DB_URL");
            System.err.println("  - NEON_DB_USER");
            System.err.println("  - NEON_DB_PASSWORD");
            return;
        }
        
        System.out.println("✓ Successfully connected to database!");
        System.out.println();
        
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            System.out.println("Transaction started...");
            
            // Create a test book
            Book testBook = new Book(
                "The Great Gatsby",
                "F. Scott Fitzgerald",
                "978-0-7432-7356-5"
            );
            
            System.out.println("Creating test book: " + testBook.getTitle());
            
            // Save the book to database
            em.persist(testBook);
            
            // Commit the transaction
            transaction.commit();
            System.out.println("✓ Book saved to database successfully!");
            System.out.println();
            
            // Query the book back
            System.out.println("Querying saved books from database...");
            Book savedBook = em.find(Book.class, testBook.getId());
            
            if (savedBook != null) {
                System.out.println("✓ Successfully retrieved book from database:");
                System.out.println("  ID: " + savedBook.getId());
                System.out.println("  Title: " + savedBook.getTitle());
                System.out.println("  Author: " + savedBook.getAuthor());
                System.out.println("  ISBN: " + savedBook.getIsbn());
                System.out.println("  Available: " + savedBook.isAvailable());
            } else {
                System.out.println("✗ Could not retrieve book from database");
            }
            
            // Query all books using JPQL
            System.out.println();
            System.out.println("Querying all books...");
            var allBooks = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
            System.out.println("✓ Found " + allBooks.size() + " book(s) in database:");
            for (Book book : allBooks) {
                System.out.println("  - " + book.getTitle() + " by " + book.getAuthor() + " (ID: " + book.getId() + ")");
            }
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
                System.err.println("Transaction rolled back due to error.");
            }
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            System.out.println();
            System.out.println("Database connection closed.");
        }
        
        System.out.println();
        System.out.println("=== Demo Complete ===");
        System.out.println();
        System.out.println("To verify in Neon Console:");
        System.out.println("1. Go to https://console.neon.tech");
        System.out.println("2. Select your project");
        System.out.println("3. Click on 'SQL Editor' tab");
        System.out.println("4. Run: SELECT * FROM books;");
    }
}

