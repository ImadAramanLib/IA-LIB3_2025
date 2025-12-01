package edu.najah.library.service;

import edu.najah.library.domain.Book;
import edu.najah.library.domain.CD;
import edu.najah.library.domain.LibraryItem;
import edu.najah.library.domain.Loan;
import edu.najah.library.domain.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.najah.library.service.Observer;

/**
 * Unit tests for Sprint 5 features: Media Extension (Polymorphism & Different Rules).
 * 
 * <p>Tests:
 * - US5.1: Borrow CD (7-day loan period)
 * - US5.2: CD Overdue fine (20 NIS per day vs 10 NIS for books)
 * - US5.3: Mixed media handling (overdue reports include both books and CDs)
 * 
 * <p>Uses Mockito for:
 * - Email server mocking
 * - Time manipulation (overdue detection)
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class Sprint5Test {
    
    private BorrowingService borrowingService;
    private OverdueDetectionService overdueService;
    private NotificationService notificationService;
    
    @Mock
    private EmailService mockEmailService;
    
    private User user;
    private Book book;
    private CD cd;
    private LocalDate borrowDate;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        borrowingService = new BorrowingService();
        overdueService = new OverdueDetectionService(borrowingService);
        
        Observer emailObserver = new Observer() {
            @Override
            public boolean notify(User user, String message) {
                if (user != null && user.getEmail() != null) {
                    mockEmailService.sendEmail(user.getEmail(), "Overdue Reminder", message);
                    return true;
                }
                return false;
            }
            
            @Override
            public String getObserverType() {
                return "Email";
            }
        };
        notificationService = new NotificationService(overdueService, emailObserver);
        
        user = new User("U001", "John Doe", "john@example.com");
        book = new Book("Test Book", "Test Author", "ISBN123");
        cd = new CD("Test CD", "Test Artist", "CD001");
        borrowDate = LocalDate.of(2025, 1, 1);
    }
    
    // ========== US5.1: Borrow CD ==========
    
    @Test
    @DisplayName("US5.1: CD can be borrowed for 7 days")
    void testBorrowCDFor7Days() {
        Loan loan = borrowingService.borrowCD(user, cd, borrowDate);
        
        assertNotNull(loan);
        assertEquals(user, loan.getUser());
        assertEquals(cd, loan.getItem());
        assertEquals(borrowDate.plusDays(7), loan.getDueDate()); // US5.1: 7 days for CDs
        assertFalse(cd.isAvailable());
    }
    
    @Test
    @DisplayName("US5.1: Book loan period is 28 days (different from CD)")
    void testBookLoanPeriodIs28Days() {
        Loan bookLoan = borrowingService.borrowBook(user, book, borrowDate);
        Loan cdLoan = borrowingService.borrowCD(user, cd, borrowDate);
        
        assertEquals(borrowDate.plusDays(28), bookLoan.getDueDate()); // Books: 28 days
        assertEquals(borrowDate.plusDays(7), cdLoan.getDueDate()); // CDs: 7 days
    }
    
    @Test
    @DisplayName("US5.1: Cannot borrow CD with unpaid fines")
    void testCannotBorrowCDWithUnpaidFines() {
        // Add unpaid fine
        borrowingService.addFine(new edu.najah.library.domain.Fine(user, 10.0, borrowDate));
        
        // With unpaid fines, borrowCD should throw exception
        assertThrows(IllegalStateException.class,
            () -> borrowingService.borrowCD(user, cd, borrowDate));
    }
    
    @Test
    @DisplayName("US5.1: Cannot borrow CD with overdue items")
    void testCannotBorrowCDWithOverdueItems() {
        // Borrow a book and make it overdue
        Book overdueBook = new Book("Overdue Book", "Author", "ISBN456");
        borrowingService.borrowBook(user, overdueBook, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        // With overdue items, borrowCD should throw exception
        assertThrows(IllegalStateException.class,
            () -> borrowingService.borrowCD(user, cd, currentDate));
    }
    
    @Test
    @DisplayName("US5.1: Return CD makes it available again")
    void testReturnCD() {
        Loan loan = borrowingService.borrowCD(user, cd, borrowDate);
        LocalDate returnDate = borrowDate.plusDays(5);
        
        borrowingService.returnItem(loan, returnDate);
        
        assertEquals(returnDate, loan.getReturnDate());
        assertTrue(cd.isAvailable());
    }
    
    // ========== US5.2: CD Overdue Fine ==========
    
    @Test
    @DisplayName("US5.2: CD overdue fine is 20 NIS per day")
    void testCDOverdueFineIs20NISPerDay() {
        Loan cdLoan = borrowingService.borrowCD(user, cd, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(10); // 3 days overdue (due date is +7)
        
        // Calculate fine using strategy
        FineStrategy cdStrategy = FineStrategyFactory.getStrategy(FineStrategyFactory.ItemType.CD);
        int fine = overdueService.calculateOverdueFine(cdLoan, currentDate, cdStrategy);
        
        assertEquals(60, fine); // 3 days * 20 NIS = 60 NIS
    }
    
    @Test
    @DisplayName("US5.2: Book overdue fine is 10 NIS per day (different from CD)")
    void testBookOverdueFineIs10NISPerDay() {
        Loan bookLoan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30); // 2 days overdue (due date is +28)
        
        // Calculate fine using strategy
        FineStrategy bookStrategy = FineStrategyFactory.getStrategy(FineStrategyFactory.ItemType.BOOK);
        int fine = overdueService.calculateOverdueFine(bookLoan, currentDate, bookStrategy);
        
        assertEquals(20, fine); // 2 days * 10 NIS = 20 NIS
    }
    
    @Test
    @DisplayName("US5.2: OverdueDetectionService automatically uses correct fine strategy based on item type")
    void testAutomaticFineStrategySelection() {
        // Book loan
        Loan bookLoan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30); // 2 days overdue
        
        double bookFine = overdueService.calculateOverdueFine(bookLoan, currentDate);
        assertEquals(20.0, bookFine, 0.01); // 2 days * 10 NIS = 20 NIS
        
        // CD loan
        Loan cdLoan = borrowingService.borrowCD(user, cd, borrowDate);
        LocalDate cdCurrentDate = borrowDate.plusDays(10); // 3 days overdue
        
        double cdFine = overdueService.calculateOverdueFine(cdLoan, cdCurrentDate);
        assertEquals(60.0, cdFine, 0.01); // 3 days * 20 NIS = 60 NIS
    }
    
    // ========== US5.3: Mixed Media Handling ==========
    
    @Test
    @DisplayName("US5.3: Mixed media overdue report includes both books and CDs")
    void testMixedMediaOverdueReport() {
        // Borrow a book and a CD on the same date
        Loan bookLoan = borrowingService.borrowBook(user, book, borrowDate);
        CD cd2 = new CD("CD 2", "Artist 2", "CD002");
        Loan cdLoan = borrowingService.borrowCD(user, cd2, borrowDate);
        
        // Use a date where both are overdue (after 30 days, book is 2 days overdue, CD is 23 days overdue)
        LocalDate currentDate = borrowDate.plusDays(30);
        
        // Generate mixed media report
        Map<String, Object> report = overdueService.getMixedMediaOverdueReport(user, currentDate);
        
        assertNotNull(report);
        assertEquals(2, report.get("totalItems")); // 1 book + 1 CD
        
        // Check totals: book (2 days * 10 NIS) + CD (23 days * 20 NIS) = 20 + 460 = 480 NIS
        assertEquals(480, report.get("totalFine"));
        
        // Check by type
        @SuppressWarnings("unchecked")
        Map<String, Object> byType = (Map<String, Object>) report.get("byType");
        assertNotNull(byType);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> bookInfo = (Map<String, Object>) byType.get("BOOK");
        assertNotNull(bookInfo);
        assertEquals(1, bookInfo.get("count"));
        assertEquals(20, bookInfo.get("totalFine"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> cdInfo = (Map<String, Object>) byType.get("CD");
        assertNotNull(cdInfo);
        assertEquals(1, cdInfo.get("count"));
        assertEquals(460, cdInfo.get("totalFine"));
    }
    
    @Test
    @DisplayName("US5.3: Mixed media report handles multiple items of same type")
    void testMixedMediaReportMultipleItemsSameType() {
        // Borrow multiple books
        Book book2 = new Book("Book 2", "Author 2", "ISBN456");
        Loan loan1 = borrowingService.borrowBook(user, book, borrowDate);
        Loan loan2 = borrowingService.borrowBook(user, book2, borrowDate);
        
        LocalDate currentDate = borrowDate.plusDays(30); // Both 2 days overdue
        
        Map<String, Object> report = overdueService.getMixedMediaOverdueReport(user, currentDate);
        
        assertEquals(2, report.get("totalItems"));
        assertEquals(40, report.get("totalFine")); // 2 books * 2 days * 10 NIS = 40 NIS
        
        @SuppressWarnings("unchecked")
        Map<String, Object> byType = (Map<String, Object>) report.get("byType");
        @SuppressWarnings("unchecked")
        Map<String, Object> bookInfo = (Map<String, Object>) byType.get("BOOK");
        assertEquals(2, bookInfo.get("count"));
    }
    
    // ========== Mockito Tests: Email Server Mocking ==========
    
    @Test
    @DisplayName("Mockito: Email server is mocked and can verify email sending")
    void testEmailServerMocking() {
        // Setup: User has overdue items
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        // Send reminder
        notificationService.sendReminderToUser(user, currentDate);
        
        // Verify email was sent using Mockito
        verify(mockEmailService, times(1)).sendEmail(
            eq(user.getEmail()),
            anyString(),
            contains("overdue book")
        );
    }
    
    @Test
    @DisplayName("Mockito: Email server mock can verify multiple emails")
    void testEmailServerMockingMultipleEmails() {
        User user2 = new User("U002", "Jane Doe", "jane@example.com");
        Book book2 = new Book("Book 2", "Author 2", "ISBN456");
        
        Loan loan1 = borrowingService.borrowBook(user, book, borrowDate);
        Loan loan2 = borrowingService.borrowBook(user2, book2, borrowDate);
        LocalDate currentDate = borrowDate.plusDays(30);
        
        notificationService.sendReminderToUser(user, currentDate);
        notificationService.sendReminderToUser(user2, currentDate);
        
        // Verify both emails were sent
        verify(mockEmailService, times(2)).sendEmail(anyString(), anyString(), anyString());
        verify(mockEmailService, times(1)).sendEmail(eq(user.getEmail()), anyString(), anyString());
        verify(mockEmailService, times(1)).sendEmail(eq(user2.getEmail()), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Mockito: Email server mock can verify no email sent when no overdue items")
    void testEmailServerMockingNoOverdueItems() {
        // User has no overdue items
        LocalDate currentDate = borrowDate.plusDays(10);
        
        notificationService.sendReminderToUser(user, currentDate);
        
        // Verify no email was sent
        verify(mockEmailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
    
    // ========== Mockito Tests: Time Manipulation ==========
    
    @Test
    @DisplayName("Mockito: Time manipulation for overdue detection - CD becomes overdue after 7 days")
    void testTimeManipulationCDOverdue() {
        Loan cdLoan = borrowingService.borrowCD(user, cd, borrowDate);
        
        // Simulate time passing: 5 days (not overdue)
        LocalDate day5 = borrowDate.plusDays(5);
        assertFalse(cdLoan.isOverdue(day5));
        
        // Simulate time passing: 8 days (overdue by 1 day)
        LocalDate day8 = borrowDate.plusDays(8);
        assertTrue(cdLoan.isOverdue(day8));
        assertEquals(1, cdLoan.getDaysOverdue(day8));
        
        // Verify fine calculation
        double fine = overdueService.calculateOverdueFine(cdLoan, day8);
        assertEquals(20.0, fine, 0.01); // 1 day * 20 NIS
    }
    
    @Test
    @DisplayName("Mockito: Time manipulation for overdue detection - Book becomes overdue after 28 days")
    void testTimeManipulationBookOverdue() {
        Loan bookLoan = borrowingService.borrowBook(user, book, borrowDate);
        
        // Simulate time passing: 20 days (not overdue)
        LocalDate day20 = borrowDate.plusDays(20);
        assertFalse(bookLoan.isOverdue(day20));
        
        // Simulate time passing: 30 days (overdue by 2 days)
        LocalDate day30 = borrowDate.plusDays(30);
        assertTrue(bookLoan.isOverdue(day30));
        assertEquals(2, bookLoan.getDaysOverdue(day30));
        
        // Verify fine calculation
        double fine = overdueService.calculateOverdueFine(bookLoan, day30);
        assertEquals(20.0, fine, 0.01); // 2 days * 10 NIS
    }
    
    @Test
    @DisplayName("Mockito: Time manipulation - Mixed media with different due dates")
    void testTimeManipulationMixedMedia() {
        Loan bookLoan = borrowingService.borrowBook(user, book, borrowDate);
        Loan cdLoan = borrowingService.borrowCD(user, cd, borrowDate);
        
        // Day 5: CD not overdue, book not overdue
        LocalDate day5 = borrowDate.plusDays(5);
        assertFalse(cdLoan.isOverdue(day5));
        assertFalse(bookLoan.isOverdue(day5));
        
        // Day 10: CD overdue (3 days), book not overdue
        LocalDate day10 = borrowDate.plusDays(10);
        assertTrue(cdLoan.isOverdue(day10));
        assertFalse(bookLoan.isOverdue(day10));
        
        // Day 30: Both overdue
        LocalDate day30 = borrowDate.plusDays(30);
        assertTrue(cdLoan.isOverdue(day30));
        assertTrue(bookLoan.isOverdue(day30));
        
        // Verify mixed media report
        Map<String, Object> report = overdueService.getMixedMediaOverdueReport(user, day30);
        assertEquals(2, report.get("totalItems"));
    }
    
    // ========== Polymorphism Tests ==========
    
    @Test
    @DisplayName("Polymorphism: LibraryItem interface allows uniform handling of different item types")
    void testPolymorphismLibraryItem() {
        LibraryItem bookItem = book;
        LibraryItem cdItem = cd;
        
        // Both implement LibraryItem
        assertTrue(bookItem instanceof LibraryItem);
        assertTrue(cdItem instanceof LibraryItem);
        
        // Different loan periods
        assertEquals(28, bookItem.getLoanPeriodDays());
        assertEquals(7, cdItem.getLoanPeriodDays());
        
        // Different item types
        assertEquals(LibraryItem.ItemType.BOOK, bookItem.getItemType());
        assertEquals(LibraryItem.ItemType.CD, cdItem.getItemType());
    }
    
    @Test
    @DisplayName("Polymorphism: borrowItem method handles different item types uniformly")
    void testPolymorphismBorrowItem() {
        Loan bookLoan = borrowingService.borrowItem(user, book, borrowDate);
        Loan cdLoan = borrowingService.borrowItem(user, cd, borrowDate);
        
        assertNotNull(bookLoan);
        assertNotNull(cdLoan);
        
        // Both loans created successfully, but with different due dates
        assertEquals(borrowDate.plusDays(28), bookLoan.getDueDate());
        assertEquals(borrowDate.plusDays(7), cdLoan.getDueDate());
    }
}

