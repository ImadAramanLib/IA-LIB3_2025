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
        assertEquals(1, book.getQuantity(), "Default constructor should set quantity to 1");
    }
    
    /**
     * Test quantity getter and setter.
     */
    @Test
    void testQuantityGetterSetter() {
        Book book = new Book("Test", "Author", "ISBN123");
        assertEquals(1, book.getQuantity(), "New book should have quantity of 1");
        
        book.setQuantity(5);
        assertEquals(5, book.getQuantity());
        
        book.setQuantity(0);
        assertEquals(0, book.getQuantity());
    }
    
    /**
     * Test setting negative quantity throws exception.
     */
    @Test
    void testSetNegativeQuantity() {
        Book book = new Book("Test", "Author", "ISBN123");
        assertThrows(IllegalArgumentException.class, 
            () -> book.setQuantity(-1), 
            "Setting negative quantity should throw exception");
    }
    
    /**
     * Test increment quantity.
     */
    @Test
    void testIncrementQuantity() {
        Book book = new Book("Test", "Author", "ISBN123");
        assertEquals(1, book.getQuantity());
        
        book.incrementQuantity();
        assertEquals(2, book.getQuantity());
        
        book.incrementQuantity();
        assertEquals(3, book.getQuantity());
    }
    
    /**
     * Test decrement quantity.
     */
    @Test
    void testDecrementQuantity() {
        Book book = new Book("Test", "Author", "ISBN123");
        book.setQuantity(3);
        
        book.decrementQuantity();
        assertEquals(2, book.getQuantity());
        
        book.decrementQuantity();
        assertEquals(1, book.getQuantity());
    }
    
    /**
     * Test decrement quantity when already at 0 throws exception.
     */
    @Test
    void testDecrementQuantityAtZero() {
        Book book = new Book("Test", "Author", "ISBN123");
        book.setQuantity(0);
        
        assertThrows(IllegalStateException.class, 
            book::decrementQuantity, 
            "Decrementing quantity at 0 should throw exception");
    }
    
    /**
     * Test isAvailable returns false when quantity is 0.
     */
    @Test
    void testIsAvailableWithZeroQuantity() {
        Book book = new Book("Test", "Author", "ISBN123");
        book.setAvailable(true);
        book.setQuantity(0);
        
        assertFalse(book.isAvailable(), "Book with quantity 0 should not be available");
    }
    
    /**
     * Test isAvailable returns true when quantity > 0 and isAvailable is true.
     */
    @Test
    void testIsAvailableWithQuantity() {
        Book book = new Book("Test", "Author", "ISBN123");
        book.setAvailable(true);
        book.setQuantity(3);
        
        assertTrue(book.isAvailable(), "Book with quantity > 0 and available should be available");
    }
    
    /**
     * Test constructor with quantity parameter.
     */
    @Test
    void testBookConstructorWithQuantity() {
        Book book = new Book("Test", "Author", "ISBN123", true, 5);
        assertEquals(5, book.getQuantity());
        assertTrue(book.isAvailable());
    }
    
    /**
     * Test toString includes quantity.
     */
    @Test
    void testToStringIncludesQuantity() {
        Book book = new Book("Test", "Author", "ISBN123");
        book.setQuantity(3);
        String result = book.toString();
        
        assertTrue(result.contains("quantity=3"), "toString should include quantity");
    }
}
