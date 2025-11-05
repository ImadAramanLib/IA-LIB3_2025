package edu.najah.library.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Loan entity.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class LoanTest {
    
    private Loan loan;
    private Book book;
    private User user;
    private LocalDate borrowDate;
    
    @BeforeEach
    public void setUp() {
        book = new Book("Test Book", "Test Author", "ISBN123");
        user = new User("U001", "John Doe", "john@example.com");
        borrowDate = LocalDate.of(2025, 1, 1);
        loan = new Loan(book, user, borrowDate);
    }
    
    @Test
    public void testLoanCreation() {
        assertNotNull(loan);
        assertEquals(book, loan.getBook());
        assertEquals(user, loan.getUser());
        assertEquals(borrowDate, loan.getBorrowDate());
        assertEquals(borrowDate.plusDays(28), loan.getDueDate());
        assertNull(loan.getReturnDate());
    }
    
    @Test
    public void testIsOverdueWhenNotYet() {
        LocalDate currentDate = borrowDate.plusDays(20);
        assertFalse(loan.isOverdue(currentDate));
    }
    
    @Test
    public void testIsOverdueWhenExactlyDue() {
        LocalDate currentDate = borrowDate.plusDays(28);
        assertFalse(loan.isOverdue(currentDate));
    }
    
    @Test
    public void testIsOverdueWhenOverdue() {
        LocalDate currentDate = borrowDate.plusDays(30);
        assertTrue(loan.isOverdue(currentDate));
    }
    
    @Test
    public void testIsOverdueWhenReturned() {
        loan.setReturnDate(borrowDate.plusDays(15));
        LocalDate currentDate = borrowDate.plusDays(30);
        assertFalse(loan.isOverdue(currentDate));
    }
    
    @Test
    public void testGetDaysOverdueWhenNotOverdue() {
        LocalDate currentDate = borrowDate.plusDays(20);
        assertEquals(0, loan.getDaysOverdue(currentDate));
    }
    
    @Test
    public void testGetDaysOverdueWhenOverdue() {
        LocalDate currentDate = borrowDate.plusDays(32);
        assertEquals(4, loan.getDaysOverdue(currentDate));
    }
    
    @Test
    public void testIsReturned() {
        assertFalse(loan.isReturned());
        
        loan.setReturnDate(borrowDate.plusDays(10));
        assertTrue(loan.isReturned());
    }
    
    @Test
    public void testSettersAndGetters() {
        Book newBook = new Book("New Book", "New Author", "ISBN456");
        User newUser = new User("U002", "Jane Doe", "jane@example.com");
        LocalDate newBorrowDate = LocalDate.of(2025, 2, 1);
        LocalDate newDueDate = LocalDate.of(2025, 3, 1);
        LocalDate returnDate = LocalDate.of(2025, 2, 15);
        
        loan.setBook(newBook);
        loan.setUser(newUser);
        loan.setBorrowDate(newBorrowDate);
        loan.setDueDate(newDueDate);
        loan.setReturnDate(returnDate);
        
        assertEquals(newBook, loan.getBook());
        assertEquals(newUser, loan.getUser());
        assertEquals(newBorrowDate, loan.getBorrowDate());
        assertEquals(newDueDate, loan.getDueDate());
        assertEquals(returnDate, loan.getReturnDate());
    }
    
    @Test
    public void testToString() {
        String toString = loan.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("Loan"));
    }
}
