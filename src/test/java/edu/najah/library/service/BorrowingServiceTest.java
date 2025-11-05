package edu.najah.library.service;

import edu.najah.library.domain.Book;
import edu.najah.library.domain.User;
import edu.najah.library.domain.Loan;
import edu.najah.library.domain.Fine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BorrowingService.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class BorrowingServiceTest {
    
    private BorrowingService borrowingService;
    private Book book;
    private User user;
    private LocalDate borrowDate;
    
    @BeforeEach
    public void setUp() {
        borrowingService = new BorrowingService();
        book = new Book("Test Book", "Test Author", "ISBN123");
        user = new User("U001", "John Doe", "john@example.com");
        borrowDate = LocalDate.of(2025, 1, 1);
    }
    
    @Test
    public void testBorrowBookSuccess() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        
        assertNotNull(loan);
        assertEquals(user, loan.getUser());
        assertEquals(book, loan.getBook());
        assertFalse(book.isAvailable());
    }
    
    @Test
    public void testBorrowBookUnavailable() {
        book.setAvailable(false);
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        
        assertNull(loan);
    }
    
    @Test
    public void testBorrowBookWithUnpaidFines() {
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        
        assertNull(loan);
    }
    
    @Test
    public void testReturnBook() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate returnDate = borrowDate.plusDays(10);
        
        borrowingService.returnBook(loan, returnDate);
        
        assertEquals(returnDate, loan.getReturnDate());
        assertTrue(book.isAvailable());
    }
    
    @Test
    public void testGetActiveLoans() {
        Loan loan1 = borrowingService.borrowBook(user, book, borrowDate);
        Book book2 = new Book("Another Book", "Another Author", "ISBN456");
        Loan loan2 = borrowingService.borrowBook(user, book2, borrowDate);
        
        borrowingService.returnBook(loan1, borrowDate.plusDays(5));
        
        List<Loan> activeLoans = borrowingService.getActiveLoans(user);
        
        assertEquals(1, activeLoans.size());
        assertEquals(loan2, activeLoans.get(0));
    }
    
    @Test
    public void testCanBorrowWithoutFines() {
        assertTrue(borrowingService.canBorrow(user));
    }
    
    @Test
    public void testCannotBorrowWithFines() {
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        assertFalse(borrowingService.canBorrow(user));
    }
    
    @Test
    public void testPayFine() {
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        assertTrue(borrowingService.payFine(user, 5.0));
        assertTrue(fine.isPaid());
    }
    
    @Test
    public void testPayFinePartial() {
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        assertTrue(borrowingService.payFine(user, 2.0));
        assertFalse(fine.isPaid());
        assertEquals(3.0, fine.getRemainingBalance());
    }
    
    @Test
    public void testHasUnpaidFines() {
        assertFalse(borrowingService.hasUnpaidFines(user));
        
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        assertTrue(borrowingService.hasUnpaidFines(user));
    }
    
    @Test
    public void testGetUnpaidFines() {
        Fine fine1 = new Fine(user, 5.0, borrowDate);
        Fine fine2 = new Fine(user, 3.0, borrowDate);
        borrowingService.addFine(fine1);
        borrowingService.addFine(fine2);
        
        List<Fine> unpaidFines = borrowingService.getUnpaidFines(user);
        
        assertEquals(2, unpaidFines.size());
    }
}
