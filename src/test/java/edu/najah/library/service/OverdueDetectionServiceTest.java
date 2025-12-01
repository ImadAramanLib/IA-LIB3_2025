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
 * Unit tests for the OverdueDetectionService.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class OverdueDetectionServiceTest {
    
    private OverdueDetectionService overdueService;
    private BorrowingService borrowingService;
    private Book book;
    private User user;
    private LocalDate borrowDate;
    
    @BeforeEach
    void setUp() {
        borrowingService = new BorrowingService();
        overdueService = new OverdueDetectionService(borrowingService);
        book = new Book("Test Book", "Test Author", "ISBN123");
        user = new User("U001", "John Doe", "john@example.com");
        borrowDate = LocalDate.of(2025, 1, 1);
    }
    
    @Test
    void testGetOverdueLoansNone() {
        borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(20);
        
        List<Loan> overdueLoans = overdueService.getOverdueLoans(currentDate);
        
        assertTrue(overdueLoans.isEmpty());
    }
    
    @Test
    void testGetOverdueLoans() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        List<Loan> overdueLoans = overdueService.getOverdueLoans(currentDate);
        
        assertEquals(1, overdueLoans.size());
        assertEquals(loan, overdueLoans.get(0));
    }
    
    @Test
    void testGetOverdueLoansForUser() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        List<Loan> overdueLoans = overdueService.getOverdueLoansForUser(user, currentDate);
        
        assertEquals(1, overdueLoans.size());
        assertEquals(loan, overdueLoans.get(0));
    }
    
    @Test
    void testGetOverdueLoansForUserNone() {
        User otherUser = new User("U002", "Jane Doe", "jane@example.com");
        borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        List<Loan> overdueLoans = overdueService.getOverdueLoansForUser(otherUser, currentDate);
        
        assertTrue(overdueLoans.isEmpty());
    }
    
    @Test
    void testCalculateOverdueFine() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(32); // 4 days overdue
        
        double fine = overdueService.calculateOverdueFine(loan, currentDate);
        
        // US5.2: Now uses strategy-based calculation (10 NIS per day for books)
        assertEquals(40.0, fine, 0.01); // 4 days * 10 NIS = 40 NIS
    }
    
    @Test
    void testCalculateOverdueFineNotOverdue() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(20);
        
        double fine = overdueService.calculateOverdueFine(loan, currentDate);
        
        assertEquals(0, fine);
    }
    
    @Test
    void testIsOverdue() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        assertTrue(overdueService.isOverdue(loan, currentDate));
    }
    
    @Test
    void testIsOverdueNotOverdue() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(20);
        
        assertFalse(overdueService.isOverdue(loan, currentDate));
    }
    
    @Test
    void testGetDaysOverdue() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(32);
        
        long daysOverdue = overdueService.getDaysOverdue(loan, currentDate);
        
        assertEquals(4, daysOverdue);
    }
    
    @Test
    void testGetDaysOverdueNotOverdue() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(20);
        
        long daysOverdue = overdueService.getDaysOverdue(loan, currentDate);
        
        assertEquals(0, daysOverdue);
    }
    
    @Test
    void testMultipleOverdueLoans() {
        borrowingService.borrowBook(user, book, borrowDate);
        Book book2 = new Book("Another Book", "Another Author", "ISBN456");
        borrowingService.borrowBook(user, book2, borrowDate);
        
        LocalDate currentDate = borrowDate.plusDays(30);
        List<Loan> overdueLoans = overdueService.getOverdueLoans(currentDate);
        
        assertEquals(2, overdueLoans.size());
    }
}
