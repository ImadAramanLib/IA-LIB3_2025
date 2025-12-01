package edu.najah.library.service;

import edu.najah.library.domain.Book;
import edu.najah.library.domain.User;
import edu.najah.library.util.MockEmailServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the NotificationService.
 * Tests reminder email functionality for users with overdue books.
 * 
 * <p>US3.1: Send reminder emails to users with overdue books.</p>
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class NotificationServiceTest {
    
    private MockEmailServer emailServer;
    private BorrowingService borrowingService;
    private OverdueDetectionService overdueDetectionService;
    private NotificationService notificationService;
    private User user;
    private Book book;
    private LocalDate borrowDate;
    
    @BeforeEach
    void setUp() {
        emailServer = new MockEmailServer();
        borrowingService = new BorrowingService();
        overdueDetectionService = new OverdueDetectionService(borrowingService);
        // Wrap MockEmailServer as an Observer
        Observer emailObserver = new Observer() {
            @Override
            public boolean notify(User user, String message) {
                emailServer.notify(user, message);
                return true;
            }
            
            @Override
            public String getObserverType() {
                return "Email";
            }
        };
        notificationService = new NotificationService(overdueDetectionService, emailObserver);
        
        user = new User("U001", "John Doe", "john@example.com");
        book = new Book("Test Book", "Test Author", "ISBN123");
        borrowDate = LocalDate.of(2025, 1, 1);
    }
    
    @Test
    @DisplayName("US3.1: sendReminderToUser sends email with correct message format")
    void testSendReminderToUserSendsCorrectMessage() {
        // Create an overdue loan
        borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30); // 2 days overdue
        
        boolean result = notificationService.sendReminderToUser(user, currentDate);
        
        assertTrue(result);
        assertEquals(1, emailServer.getMessageCount());
        
        List<MockEmailServer.EmailMessage> emails = emailServer.getSentMessages();
        MockEmailServer.EmailMessage email = emails.get(0);
        
        assertEquals("john@example.com", email.user.getEmail());
        assertEquals("You have 1 overdue book.", email.message);
    }
    
    @Test
    @DisplayName("US3.1: reminder message uses correct format for multiple overdue books")
    void testReminderMessageMultipleBooks() {
        // Create multiple overdue loans
        Book book1 = new Book("Book 1", "Author 1", "ISBN1");
        Book book2 = new Book("Book 2", "Author 2", "ISBN2");
        Book book3 = new Book("Book 3", "Author 3", "ISBN3");
        
        borrowingService.borrowBook(user, book1, borrowDate);
        borrowingService.borrowBook(user, book2, borrowDate);
        borrowingService.borrowBook(user, book3, borrowDate);
        
        LocalDate currentDate = borrowDate.plusDays(30);
        
        boolean result = notificationService.sendReminderToUser(user, currentDate);
        
        assertTrue(result);
        MockEmailServer.EmailMessage email = emailServer.getSentMessages().get(0);
        assertEquals("You have 3 overdue book(s).", email.message);
    }
    
    @Test
    @DisplayName("US3.1: sendOverdueReminders sends emails to all users with overdue books")
    void testSendOverdueRemindersToAllUsers() {
        User user2 = new User("U002", "Jane Doe", "jane@example.com");
        Book book2 = new Book("Book 2", "Author 2", "ISBN2");
        
        // Create overdue loans for two users
        borrowingService.borrowBook(user, book, borrowDate);
        borrowingService.borrowBook(user2, book2, borrowDate);
        
        LocalDate currentDate = borrowDate.plusDays(30);
        
        int emailsSent = notificationService.sendOverdueReminders(currentDate);
        
        assertEquals(2, emailsSent);
        assertEquals(2, emailServer.getMessageCount());
    }
    
    @Test
    @DisplayName("sendReminderToUser returns false when user has no overdue books")
    void testSendReminderReturnsFalseWhenNoOverdue() {
        // Create a loan that's not overdue
        borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(20); // Not overdue yet
        
        boolean result = notificationService.sendReminderToUser(user, currentDate);
        
        assertFalse(result);
        assertEquals(0, emailServer.getMessageCount());
    }
    
    @Test
    @DisplayName("sendReminderToUser returns false for null user")
    void testSendReminderReturnsFalseForNullUser() {
        LocalDate currentDate = borrowDate.plusDays(30);
        
        boolean result = notificationService.sendReminderToUser(null, currentDate);
        
        assertFalse(result);
        assertEquals(0, emailServer.getMessageCount());
    }
    
    @Test
    @DisplayName("sendReminderToUser returns false for user without email")
    void testSendReminderReturnsFalseForUserWithoutEmail() {
        User userNoEmail = new User("U003", "No Email User", null);
        borrowingService.borrowBook(userNoEmail, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        boolean result = notificationService.sendReminderToUser(userNoEmail, currentDate);
        
        assertFalse(result);
        assertEquals(0, emailServer.getMessageCount());
    }
    
    @Test
    @DisplayName("sendOverdueReminders returns 0 when no overdue loans exist")
    void testSendOverdueRemindersReturnsZeroWhenNoOverdue() {
        // Create a loan that's not overdue
        borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(20);
        
        int emailsSent = notificationService.sendOverdueReminders(currentDate);
        
        assertEquals(0, emailsSent);
        assertEquals(0, emailServer.getMessageCount());
    }
    
    @Test
    @DisplayName("sendOverdueReminders uses current date when no date provided")
    void testSendOverdueRemindersUsesCurrentDate() {
        // This test verifies the overloaded method works
        // We can't easily test "today" without mocking, but we can verify it doesn't crash
        borrowingService.borrowBook(user, book, LocalDate.now().minusDays(30));
        
        // This should work without throwing an exception
        assertDoesNotThrow(() -> notificationService.sendOverdueReminders());
    }
    
    @Test
    @DisplayName("sendOverdueReminders doesn't send duplicate emails to same user")
    void testSendOverdueRemindersNoDuplicates() {
        // Create multiple overdue loans for same user
        Book book1 = new Book("Book 1", "Author 1", "ISBN1");
        Book book2 = new Book("Book 2", "Author 2", "ISBN2");
        
        borrowingService.borrowBook(user, book, borrowDate);
        borrowingService.borrowBook(user, book1, borrowDate);
        borrowingService.borrowBook(user, book2, borrowDate);
        
        LocalDate currentDate = borrowDate.plusDays(30);
        
        int emailsSent = notificationService.sendOverdueReminders(currentDate);
        
        // Should only send one email per user, not one per loan
        assertEquals(1, emailsSent);
        assertEquals(1, emailServer.getMessageCount());
        
        MockEmailServer.EmailMessage email = emailServer.getSentMessages().get(0);
        assertEquals("You have 3 overdue book(s).", email.message);
    }
    
    @Test
    @DisplayName("constructor throws exception for null dependencies")
    void testConstructorThrowsExceptionForNullDependencies() {
        assertThrows(IllegalArgumentException.class, 
            () -> new NotificationService(null));
        Observer emailObserver = new Observer() {
            @Override
            public boolean notify(User user, String message) {
                emailServer.notify(user, message);
                return true;
            }
            
            @Override
            public String getObserverType() {
                return "Email";
            }
        };
        assertThrows(IllegalArgumentException.class, 
            () -> new NotificationService(null, emailObserver));
    }
}

