package edu.najah.library.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Book entity.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class BookTest {
    
    /**
     * Test creating a book with constructor.
     */
    @Test
    void testBookCreation() {
        Book book = new Book("Clean Code", "Robert Martin", "978-0132350884");
        
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals("978-0132350884", book.getIsbn());
        assertTrue(book.isAvailable(), "New books should be available by default");
    }
    
    /**
     * Test book creation with availability parameter.
     */
    @Test
    void testBookCreationWithAvailability() {
        Book book = new Book("Java Programming", "John Doe", "123456789", false);
        
        assertFalse(book.isAvailable(), "Book should be unavailable as specified");
    }
    
    /**
     * Test book setters.
     */
    @Test
    void testBookSetters() {
        Book book = new Book();
        book.setTitle("Design Patterns");
        book.setAuthor("Gang of Four");
        book.setIsbn("978-0201633610");
        book.setAvailable(false);
        
        assertEquals("Design Patterns", book.getTitle());
        assertEquals("Gang of Four", book.getAuthor());
        assertEquals("978-0201633610", book.getIsbn());
        assertFalse(book.isAvailable());
    }
    
    /**
     * Test book equality based on ISBN.
     */
    @Test
    void testBookEquality() {
        Book book1 = new Book("Title1", "Author1", "12345");
        Book book2 = new Book("Title2", "Author2", "12345");
        Book book3 = new Book("Title1", "Author1", "67890");
        
        assertEquals(book1, book2, "Books with same ISBN should be equal");
        assertNotEquals(book1, book3, "Books with different ISBN should not be equal");
    }
    
    /**
     * Test book hash code consistency.
     */
    @Test
    void testBookHashCode() {
        Book book1 = new Book("Title1", "Author1", "12345");
        Book book2 = new Book("Different Title", "Different Author", "12345");
        
        assertEquals(book1.hashCode(), book2.hashCode(), 
                "Books with same ISBN should have same hash code");
    }
    
    /**
     * Test book toString method.
     */
    @Test
    void testBookToString() {
        Book book = new Book("Clean Code", "Robert Martin", "978-0132350884");
        String result = book.toString();
        
        assertTrue(result.contains("Clean Code"), "toString should contain title");
        assertTrue(result.contains("Robert Martin"), "toString should contain author");
        assertTrue(result.contains("978-0132350884"), "toString should contain ISBN");
    }
    
    /**
     * Test default constructor creates available book.
     */
    @Test
    void testDefaultConstructor() {
        Book book = new Book();
        assertTrue(book.isAvailable(), "Default constructor should create available book");
    }
}
