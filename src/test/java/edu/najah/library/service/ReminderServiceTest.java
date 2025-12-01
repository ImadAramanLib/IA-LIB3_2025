package edu.najah.library.service;

import edu.najah.library.domain.Book;
import edu.najah.library.domain.Loan;
import edu.najah.library.domain.User;
import edu.najah.library.util.MockEmailServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReminderService.
 * Tests US3.1: Send reminder emails to users with overdue books.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class ReminderServiceTest {
    
    private BorrowingService borrowingService;
    private OverdueDetectionService overdueDetectionService;
    private ReminderService reminderService;
    private MockEmailServer mockEmailServer;
    
    private User user1;
    private User user2;
    private Book book1;
    private Book book2;
    private Book book3;
    
    @BeforeEach
    void setUp() {
        borrowingService = new BorrowingService();
        overdueDetectionService = new OverdueDetectionService(borrowingService);
        reminderService = new ReminderService(overdueDetectionService);
        mockEmailServer = new MockEmailServer();
        
        // Register mock email server as notifier
        reminderService.registerNotifier(mockEmailServer);
        
        // Create test users
        user1 = new User("U001", "John Doe", "john@example.com");
        user2 = new User("U002", "Jane Smith", "jane@example.com");
        
        // Create test books
        book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "ISBN001");
        book2 = new Book("To Kill a Mockingbird", "Harper Lee", "ISBN002");
        book3 = new Book("1984", "George Orwell", "ISBN003");
    }
    
    @Test
    void testRegisterNotifier() {
        ReminderService service = new ReminderService(overdueDetectionService);
        assertEquals(0, service.getNotifiers().size());
        
        service.registerNotifier(mockEmailServer);
        assertEquals(1, service.getNotifiers().size());
    }
    
    @Test
    void testUnregisterNotifier() {
        reminderService.registerNotifier(mockEmailServer);
        assertEquals(1, reminderService.getNotifiers().size());
        
        reminderService.unregisterNotifier(mockEmailServer);
        assertEquals(0, reminderService.getNotifiers().size());
    }
    
    @Test
    void testSendRemindersNoOverdueBooks() {
        // Borrow books on day 1
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        borrowingService.borrowBook(user1, book1, borrowDate);
        borrowingService.borrowBook(user1, book2, borrowDate);
        
        // Check reminders on day 20 (before overdue)
        LocalDate checkDate = LocalDate.of(2025, 1, 20);
        reminderService.sendReminders(checkDate);
        
        // No overdue books yet
        assertEquals(0, mockEmailServer.getMessageCount());
    }
    
    @Test
    void testSendRemindersOneUserOneOverdueBook() {
        // Borrow book on day 1
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        borrowingService.borrowBook(user1, book1, borrowDate);
        
        // Check reminders on day 30 (overdue since day 29)
        LocalDate checkDate = LocalDate.of(2025, 1, 30);
        reminderService.sendReminders(checkDate);
        
        // Should have one reminder
        assertEquals(1, mockEmailServer.getMessageCount());
        
        // Check message content
        List<MockEmailServer.EmailMessage> messages = mockEmailServer.getMessagesFor(user1);
        assertEquals(1, messages.size());
        assertEquals("You have 1 overdue book(s).", messages.get(0).message);
    }
    
    @Test
    void testSendRemindersOneUserMultipleOverdueBooks() {
        // Borrow multiple books on day 1
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        borrowingService.borrowBook(user1, book1, borrowDate);
        borrowingService.borrowBook(user1, book2, borrowDate);
        
        // Check reminders on day 30 (overdue)
        LocalDate checkDate = LocalDate.of(2025, 1, 30);
        reminderService.sendReminders(checkDate);
        
        // Should have one reminder for user1 with both books overdue
        assertEquals(1, mockEmailServer.getMessageCount());
        
        List<MockEmailServer.EmailMessage> messages = mockEmailServer.getMessagesFor(user1);
        assertEquals(1, messages.size());
        assertEquals("You have 2 overdue book(s).", messages.get(0).message);
    }
    
    @Test
    void testSendRemindersMultipleUsers() {
        // User1 borrows book1
        LocalDate borrowDate1 = LocalDate.of(2025, 1, 1);
        borrowingService.borrowBook(user1, book1, borrowDate1);
        
        // User2 borrows book2
        LocalDate borrowDate2 = LocalDate.of(2025, 1, 5);
        borrowingService.borrowBook(user2, book2, borrowDate2);
        
        // Check reminders on day 35
        LocalDate checkDate = LocalDate.of(2025, 2, 5);
        reminderService.sendReminders(checkDate);
        
        // Should have reminders for both users
        assertEquals(2, mockEmailServer.getMessageCount());
        
        List<MockEmailServer.EmailMessage> user1Messages = mockEmailServer.getMessagesFor(user1);
        List<MockEmailServer.EmailMessage> user2Messages = mockEmailServer.getMessagesFor(user2);
        
        assertEquals(1, user1Messages.size());
        assertEquals(1, user2Messages.size());
        assertEquals("You have 1 overdue book(s).", user1Messages.get(0).message);
        assertEquals("You have 1 overdue book(s).", user2Messages.get(0).message);
    }
    
    @Test
    void testSendRemindersAfterBookReturned() {
        // Borrow book
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        Loan loan = borrowingService.borrowBook(user1, book1, borrowDate);
        
        // Return book on day 15
        LocalDate returnDate = LocalDate.of(2025, 1, 15);
        borrowingService.returnBook(loan, returnDate);
        
        // Check reminders on day 30
        LocalDate checkDate = LocalDate.of(2025, 1, 30);
        reminderService.sendReminders(checkDate);
        
        // No reminders since book was returned
        assertEquals(0, mockEmailServer.getMessageCount());
    }
    
    @Test
    void testSendRemindersMessageFormat() {
        // Borrow 3 books
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        borrowingService.borrowBook(user1, book1, borrowDate);
        borrowingService.borrowBook(user1, book2, borrowDate);
        borrowingService.borrowBook(user1, book3, borrowDate);
        
        // Check reminders on day 30
        LocalDate checkDate = LocalDate.of(2025, 1, 30);
        reminderService.sendReminders(checkDate);
        
        List<MockEmailServer.EmailMessage> messages = mockEmailServer.getMessagesFor(user1);
        assertEquals(1, messages.size());
        
        // Verify message format
        String message = messages.get(0).message;
        assertTrue(message.startsWith("You have "));
        assertTrue(message.contains("3"));
        assertTrue(message.contains("overdue book(s)."));
    }
    
    @Test
    void testSendRemindersWithNoNotifiers() {
        ReminderService service = new ReminderService(overdueDetectionService);
        
        // Borrow book
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        borrowingService.borrowBook(user1, book1, borrowDate);
        
        // Try to send reminders with no notifiers registered
        LocalDate checkDate = LocalDate.of(2025, 1, 30);
        service.sendReminders(checkDate); // Should not throw exception
        
        assertEquals(0, mockEmailServer.getMessageCount());
    }
    
    @Test
    void testMockEmailServerRecordsMessages() {
        // Send messages directly to mock server
        mockEmailServer.notify(user1, "Test message 1");
        mockEmailServer.notify(user2, "Test message 2");
        mockEmailServer.notify(user1, "Test message 3");
        
        // Check recorded messages
        assertEquals(3, mockEmailServer.getMessageCount());
        assertEquals(2, mockEmailServer.getMessagesFor(user1).size());
        assertEquals(1, mockEmailServer.getMessagesFor(user2).size());
    }
    
    @Test
    void testMockEmailServerClear() {
        mockEmailServer.notify(user1, "Test message");
        assertEquals(1, mockEmailServer.getMessageCount());
        
        mockEmailServer.clear();
        assertEquals(0, mockEmailServer.getMessageCount());
    }
}
