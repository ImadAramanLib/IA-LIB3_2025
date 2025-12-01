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
    void setUp() {
        borrowingService = new BorrowingService();
        book = new Book("Test Book", "Test Author", "ISBN123");
        user = new User("U001", "John Doe", "john@example.com");
        borrowDate = LocalDate.of(2025, 1, 1);
    }
    
    @Test
    void testBorrowBookSuccess() {
        book.setQuantity(3); // Set initial quantity
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        
        assertNotNull(loan);
        assertEquals(user, loan.getUser());
        assertEquals(book, loan.getBook());
        assertEquals(2, book.getQuantity(), "Quantity should be decremented after borrowing");
        assertTrue(book.isAvailable(), "Book should still be available if quantity > 0");
    }
    
    @Test
    void testBorrowBookDecrementsQuantityToZero() {
        book.setQuantity(1); // Only one copy
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        
        assertNotNull(loan);
        assertEquals(0, book.getQuantity(), "Quantity should be 0 after borrowing last copy");
        assertFalse(book.isAvailable(), "Book should be unavailable when quantity is 0");
    }
    
    @Test
    void testBorrowBookUnavailable() {
        book.setAvailable(false);
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        
        assertNull(loan);
    }
    
    @Test
    void testBorrowBookWithUnpaidFines() {
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        // US4.1: Should throw exception with proper error message
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> borrowingService.borrowBook(user, book, borrowDate));
        
        assertTrue(exception.getMessage().contains("unpaid fines"));
    }
    
    @Test
    void testBorrowBookWithOverdueBooks() {
        // Create an overdue loan
        Book overdueBook = new Book("Overdue Book", "Author", "ISBN456");
        borrowingService.borrowBook(user, overdueBook, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30); // Loan is now overdue
        
        // US4.1: Should throw exception when trying to borrow with overdue books
        Book newBook = new Book("New Book", "Author", "ISBN789");
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> borrowingService.borrowBook(user, newBook, currentDate));
        
        assertTrue(exception.getMessage().contains("overdue books"));
    }
    
    @Test
    void testBorrowBookWithBothOverdueBooksAndFines() {
        // Create an overdue loan
        Book overdueBook = new Book("Overdue Book", "Author", "ISBN456");
        borrowingService.borrowBook(user, overdueBook, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        // Create unpaid fine
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        // Should throw exception (unpaid fines checked first)
        Book newBook = new Book("New Book", "Author", "ISBN789");
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> borrowingService.borrowBook(user, newBook, currentDate));
        
        assertTrue(exception.getMessage().contains("unpaid fines"));
    }
    
    @Test
    void testCanBorrowWithOverdueBooks() {
        // Create an overdue loan
        Book overdueBook = new Book("Overdue Book", "Author", "ISBN456");
        borrowingService.borrowBook(user, overdueBook, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        // US4.1: User should not be able to borrow if they have overdue books
        assertFalse(borrowingService.canBorrow(user, currentDate));
    }
    
    @Test
    void testCanBorrowWithReturnedOverdueBook() {
        // Create a loan that was overdue but is now returned
        Book book = new Book("Book", "Author", "ISBN456");
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate overdueDate = borrowDate.plusDays(30);
        borrowingService.returnBook(loan, overdueDate.plusDays(5));
        
        // User should be able to borrow now that book is returned
        assertTrue(borrowingService.canBorrow(user, overdueDate.plusDays(5)));
    }
    
    @Test
    void testHasOverdueBooks() {
        // No overdue books initially
        assertFalse(borrowingService.hasOverdueBooks(user, borrowDate.plusDays(20)));
        
        // Create an overdue loan
        Book overdueBook = new Book("Overdue Book", "Author", "ISBN456");
        borrowingService.borrowBook(user, overdueBook, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        assertTrue(borrowingService.hasOverdueBooks(user, currentDate));
    }
    
    @Test
    void testHasOverdueBooksReturnsFalseForReturnedLoan() {
        // Create a loan and return it before it becomes overdue
        Book book = new Book("Book", "Author", "ISBN456");
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        borrowingService.returnBook(loan, borrowDate.plusDays(20));
        
        LocalDate currentDate = borrowDate.plusDays(30);
        assertFalse(borrowingService.hasOverdueBooks(user, currentDate));
    }
    
    @Test
    void testReturnBook() {
        book.setQuantity(2);
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        assertEquals(1, book.getQuantity(), "Quantity should be 1 after borrowing");
        
        LocalDate returnDate = borrowDate.plusDays(10);
        borrowingService.returnBook(loan, returnDate);
        
        assertEquals(returnDate, loan.getReturnDate());
        assertEquals(2, book.getQuantity(), "Quantity should be incremented back to 2");
        assertTrue(book.isAvailable(), "Book should be available when quantity > 0");
    }
    
    @Test
    void testReturnBookIncrementsQuantityFromZero() {
        book.setQuantity(1);
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        assertEquals(0, book.getQuantity(), "Quantity should be 0 after borrowing");
        assertFalse(book.isAvailable(), "Book should be unavailable");
        
        LocalDate returnDate = borrowDate.plusDays(10);
        borrowingService.returnBook(loan, returnDate);
        
        assertEquals(1, book.getQuantity(), "Quantity should be incremented back to 1");
        assertTrue(book.isAvailable(), "Book should be available again");
    }
    
    @Test
    void testGetActiveLoans() {
        Loan loan1 = borrowingService.borrowBook(user, book, borrowDate);
        Book book2 = new Book("Another Book", "Another Author", "ISBN456");
        Loan loan2 = borrowingService.borrowBook(user, book2, borrowDate);
        
        borrowingService.returnBook(loan1, borrowDate.plusDays(5));
        
        List<Loan> activeLoans = borrowingService.getActiveLoans(user);
        
        assertEquals(1, activeLoans.size());
        assertEquals(loan2, activeLoans.get(0));
    }
    
    @Test
    void testCanBorrowWithoutFines() {
        assertTrue(borrowingService.canBorrow(user));
    }
    
    @Test
    void testCannotBorrowWithFines() {
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        // US4.1: User cannot borrow with unpaid fines
        assertFalse(borrowingService.canBorrow(user));
    }
    
    @Test
    void testCannotBorrowWithOverdueBooks() {
        // Create an overdue loan
        Book overdueBook = new Book("Overdue Book", "Author", "ISBN456");
        borrowingService.borrowBook(user, overdueBook, borrowDate);
        // Advance date to make loan overdue
        LocalDate currentDate = borrowDate.plusDays(30);
        
        // US4.1: User cannot borrow with overdue books
        assertFalse(borrowingService.canBorrow(user, currentDate));
    }
    
    @Test
    void testPayFine() {
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        assertTrue(borrowingService.payFine(user, 5.0));
        assertTrue(fine.isPaid());
    }
    
    @Test
    void testPayFinePartial() {
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        assertTrue(borrowingService.payFine(user, 2.0));
        assertFalse(fine.isPaid());
        assertEquals(3.0, fine.getRemainingBalance());
    }
    
    @Test
    void testHasUnpaidFines() {
        assertFalse(borrowingService.hasUnpaidFines(user));
        
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        assertTrue(borrowingService.hasUnpaidFines(user));
    }
    
    @Test
    void testGetUnpaidFines() {
        Fine fine1 = new Fine(user, 5.0, borrowDate);
        Fine fine2 = new Fine(user, 3.0, borrowDate);
        borrowingService.addFine(fine1);
        borrowingService.addFine(fine2);
        
        List<Fine> unpaidFines = borrowingService.getUnpaidFines(user);
        
        assertEquals(2, unpaidFines.size());
    }
}
