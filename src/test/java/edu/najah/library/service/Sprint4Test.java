package edu.najah.library.service;

import edu.najah.library.domain.Book;
import edu.najah.library.domain.Fine;
import edu.najah.library.domain.Loan;
import edu.najah.library.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Sprint 4 features.
 * Tests US4.1: Borrow restrictions
 * Tests US4.2: Unregister user
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class Sprint4Test {
    
    private BorrowingService borrowingService;
    private UserService userService;
    
    private User user1;
    private User user2;
    private Book book1;
    private Book book2;
    private Book book3;
    
    @BeforeEach
    void setUp() {
        borrowingService = new BorrowingService();
        userService = new UserService(borrowingService);
        
        // Create test users
        user1 = new User("U001", "John Doe", "john@example.com");
        user2 = new User("U002", "Jane Smith", "jane@example.com");
        
        // Create test books
        book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "ISBN001");
        book2 = new Book("To Kill a Mockingbird", "Harper Lee", "ISBN002");
        book3 = new Book("1984", "George Orwell", "ISBN003");
        
        // Register users
        userService.registerUser(user1);
        userService.registerUser(user2);
    }
    
    // ===== Tests for US4.1: Borrow Restrictions =====
    
    @Test
    void testBorrowWithoutOverdueOrFines() {
        // User should be able to borrow normally
        Loan loan = borrowingService.borrowBook(user1, book1);
        assertNotNull(loan);
        assertTrue(borrowingService.canBorrow(user1));
    }
    
    @Test
    void testCannotBorrowWithUnpaidFines() {
        // Add unpaid fine to user
        Fine fine = new Fine(user1, 10.0, LocalDate.now());
        borrowingService.addFine(fine);
        
        // User should not be able to borrow - should throw exception
        assertThrows(IllegalStateException.class, 
            () -> borrowingService.borrowBook(user1, book1));
        assertFalse(borrowingService.canBorrow(user1));
    }
    
    @Test
    void testCannotBorrowWithOverdueBooks() {
        // Borrow book on day 1
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        Loan loan = borrowingService.borrowBook(user1, book1, borrowDate);
        assertNotNull(loan);
        
        // Try to borrow another book on day 30 (first book is overdue)
        LocalDate day30 = LocalDate.of(2025, 1, 30);
        Loan loan2 = borrowingService.borrowBook(user2, book2, day30);
        assertNotNull(loan2); // user2 has no overdue
        
        // user1 cannot borrow because they have overdue books - should throw exception
        assertThrows(IllegalStateException.class,
            () -> borrowingService.borrowBook(user1, book3, day30));
        assertFalse(borrowingService.canBorrow(user1));
    }
    
    @Test
    void testCanBorrowAfterPayingFines() {
        // Add fine
        Fine fine = new Fine(user1, 10.0, LocalDate.now());
        borrowingService.addFine(fine);
        
        // Cannot borrow with unpaid fine - should throw exception
        assertThrows(IllegalStateException.class,
            () -> borrowingService.borrowBook(user1, book1));
        
        // Pay fine
        borrowingService.payFine(user1, 10.0);
        
        // Now can borrow
        Loan loan2 = borrowingService.borrowBook(user1, book2);
        assertNotNull(loan2);
    }
    
    @Test
    void testCanBorrowAfterReturningOverdueBook() {
        // Borrow book on day 1
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        Loan loan = borrowingService.borrowBook(user1, book1, borrowDate);
        assertNotNull(loan);
        
        // Day 30: book is overdue, cannot borrow - should throw exception
        LocalDate day30 = LocalDate.of(2025, 1, 30);
        assertThrows(IllegalStateException.class,
            () -> borrowingService.borrowBook(user1, book2, day30));
        
        // Return book
        borrowingService.returnBook(loan, day30);
        
        // Now can borrow
        Loan loan3 = borrowingService.borrowBook(user1, book3, day30);
        assertNotNull(loan3);
    }
    
    @Test
    void testHasOverdueBooks() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        borrowingService.borrowBook(user1, book1, borrowDate);
        
        // Day 20: no overdue
        LocalDate day20 = LocalDate.of(2025, 1, 20);
        assertFalse(borrowingService.hasOverdueBooks(user1, day20));
        
        // Day 30: overdue
        LocalDate day30 = LocalDate.of(2025, 1, 30);
        assertTrue(borrowingService.hasOverdueBooks(user1, day30));
    }
    
    // ===== Tests for US4.2: Unregister User =====
    
    @Test
    void testRegisterUser() {
        User user3 = new User("U003", "Bob Johnson", "bob@example.com");
        assertTrue(userService.registerUser(user3));
        assertEquals(3, userService.getUserCount());
    }
    
    @Test
    void testRegisterDuplicateUser() {
        User duplicate = new User("U001", "Another John", "another@example.com");
        assertFalse(userService.registerUser(duplicate));
        assertEquals(2, userService.getUserCount()); // Still 2 users
    }
    
    @Test
    void testRegisterNullUser() {
        assertFalse(userService.registerUser(null));
        assertEquals(2, userService.getUserCount());
    }
    
    @Test
    void testUnregisterUserNoLoansOrFines() {
        // User2 has no loans or fines, should be able to unregister
        assertTrue(userService.unregisterUser(user2));
        assertEquals(1, userService.getUserCount());
        assertFalse(userService.isUserRegistered(user2));
    }
    
    @Test
    void testCannotUnregisterUserWithActiveLoan() {
        // User1 borrows a book
        Loan loan = borrowingService.borrowBook(user1, book1);
        assertNotNull(loan);
        
        // Cannot unregister user with active loan
        assertFalse(userService.unregisterUser(user1));
        assertEquals(2, userService.getUserCount());
        assertTrue(userService.isUserRegistered(user1));
    }
    
    @Test
    void testCannotUnregisterUserWithUnpaidFines() {
        // Add unpaid fine to user1
        Fine fine = new Fine(user1, 50.0, LocalDate.now());
        borrowingService.addFine(fine);
        
        // Cannot unregister
        assertFalse(userService.unregisterUser(user1));
        assertEquals(2, userService.getUserCount());
        assertTrue(userService.isUserRegistered(user1));
    }
    
    @Test
    void testCanUnregisterUserAfterReturningBooks() {
        // User1 borrows book
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        Loan loan = borrowingService.borrowBook(user1, book1, borrowDate);
        
        // Cannot unregister with active loan
        assertFalse(userService.unregisterUser(user1));
        
        // Return book
        borrowingService.returnBook(loan, borrowDate);
        
        // Now can unregister
        assertTrue(userService.unregisterUser(user1));
        assertEquals(1, userService.getUserCount());
    }
    
    @Test
    void testCanUnregisterUserAfterPayingFines() {
        // Add unpaid fine
        Fine fine = new Fine(user1, 50.0, LocalDate.now());
        borrowingService.addFine(fine);
        
        // Cannot unregister with unpaid fines
        assertFalse(userService.unregisterUser(user1));
        
        // Pay fine
        borrowingService.payFine(user1, 50.0);
        
        // Now can unregister
        assertTrue(userService.unregisterUser(user1));
        assertEquals(1, userService.getUserCount());
    }
    
    @Test
    void testFindUserById() {
        User found = userService.findUserById("U001");
        assertNotNull(found);
        assertEquals("John Doe", found.getName());
        
        User notFound = userService.findUserById("U999");
        assertNull(notFound);
    }
    
    @Test
    void testIsUserRegistered() {
        assertTrue(userService.isUserRegistered(user1));
        assertTrue(userService.isUserRegistered(user2));
        
        User user3 = new User("U003", "Not Registered", "not@example.com");
        assertFalse(userService.isUserRegistered(user3));
    }
    
    @Test
    void testGetAllUsers() {
        List<User> users = userService.getUsers();
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }
    
    @Test
    void testComplexScenario() {
        // User1: borrows 2 books with no fines, then returns both
        LocalDate today = LocalDate.now();
        
        // User1 borrows 2 books (within 28-day period)
        Loan loan1 = borrowingService.borrowBook(user1, book1, today);
        Loan loan2 = borrowingService.borrowBook(user1, book2, today);
        assertNotNull(loan1);
        assertNotNull(loan2);
        // Users can have multiple active loans - canBorrow checks for overdue books and unpaid fines only
        assertTrue(borrowingService.canBorrow(user1)); // Can still borrow if no overdue or fines
        
        // Cannot unregister with active loans
        assertFalse(userService.unregisterUser(user1));
        
        // Return book1
        borrowingService.returnBook(loan1, today);
        // Still has active loan (loan2), cannot unregister
        assertFalse(userService.unregisterUser(user1));
        
        // Return book2
        borrowingService.returnBook(loan2, today);
        // Now can unregister (no active loans, no unpaid fines)
        assertTrue(userService.unregisterUser(user1));
        
        assertEquals(1, userService.getUserCount());
    }
}
