package edu.najah.library.service;

import edu.najah.library.domain.Book;
import edu.najah.library.domain.CD;
import edu.najah.library.domain.LibraryItem;
import edu.najah.library.domain.Loan;
import edu.najah.library.domain.User;
import edu.najah.library.domain.Fine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests to increase branch coverage to 80%+.
 * Tests edge cases, null checks, error paths, and backward compatibility branches.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class BranchCoverageTest {
    
    private BorrowingService borrowingService;
    private OverdueDetectionService overdueService;
    private Book book;
    private CD cd;
    private User user;
    private LocalDate borrowDate;
    
    @BeforeEach
    void setUp() {
        borrowingService = new BorrowingService();
        overdueService = new OverdueDetectionService(borrowingService);
        book = new Book("Test Book", "Test Author", "ISBN123");
        cd = new CD("Test CD", "Test Artist", "CD123");
        user = new User("U001", "John Doe", "john@example.com");
        borrowDate = LocalDate.of(2025, 1, 1);
    }
    
    // ========== FineStrategyFactory Tests ==========
    
    @Test
    @DisplayName("FineStrategyFactory: verify error message for null item type")
    void testFineStrategyFactoryNullErrorMessage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> FineStrategyFactory.getStrategy(null));
        
        assertTrue(exception.getMessage().contains("cannot be null"));
    }
    
    @Test
    @DisplayName("FineStrategyFactory: getDefaultStrategy returns correct strategy")
    void testFineStrategyFactoryGetDefault() {
        FineStrategy strategy = FineStrategyFactory.getDefaultStrategy();
        assertNotNull(strategy);
        assertTrue(strategy instanceof BookFineStrategy);
        assertEquals(10, strategy.getFineRatePerDay());
    }
    
    // ========== BorrowingService Null Parameter Tests ==========
    
    @Test
    @DisplayName("BorrowingService: borrowBook with null user returns null")
    void testBorrowBookNullUser() {
        Loan loan = borrowingService.borrowBook(null, book, borrowDate);
        assertNull(loan);
    }
    
    @Test
    @DisplayName("BorrowingService: borrowBook with null book returns null")
    void testBorrowBookNullBook() {
        Loan loan = borrowingService.borrowBook(user, null, borrowDate);
        assertNull(loan);
    }
    
    @Test
    @DisplayName("BorrowingService: borrowBook with null date returns null")
    void testBorrowBookNullDate() {
        Loan loan = borrowingService.borrowBook(user, book, null);
        assertNull(loan);
    }
    
    @Test
    @DisplayName("BorrowingService: borrowCD with null user returns null")
    void testBorrowCDNullUser() {
        Loan loan = borrowingService.borrowCD(null, cd, borrowDate);
        assertNull(loan);
    }
    
    @Test
    @DisplayName("BorrowingService: borrowCD with null CD returns null")
    void testBorrowCDNullCD() {
        Loan loan = borrowingService.borrowCD(user, null, borrowDate);
        assertNull(loan);
    }
    
    @Test
    @DisplayName("BorrowingService: borrowCD with null date returns null")
    void testBorrowCDNullDate() {
        Loan loan = borrowingService.borrowCD(user, cd, null);
        assertNull(loan);
    }
    
    @Test
    @DisplayName("BorrowingService: borrowItem with null item returns null")
    void testBorrowItemNullItem() {
        Loan loan = borrowingService.borrowItem(user, null, borrowDate);
        assertNull(loan);
    }
    
    @Test
    @DisplayName("BorrowingService: borrowItem with unsupported item type returns null")
    void testBorrowItemUnsupportedType() {
        // Create a mock LibraryItem that's not Book or CD
        LibraryItem unsupportedItem = new LibraryItem() {
            @Override
            public String getUniqueIdentifier() { return "UNSUPPORTED"; }
            
            @Override
            public int getLoanPeriodDays() { return 14; }
            
            @Override
            public String getTitle() { return "Unsupported Item"; }
            
            @Override
            public LibraryItem.ItemType getItemType() { return LibraryItem.ItemType.JOURNAL; }
            
            @Override
            public boolean isAvailable() { return true; }
            
            @Override
            public void setAvailable(boolean available) {
                // Empty implementation for test mock - availability is fixed for unsupported item types
            }
        };
        
        Loan loan = borrowingService.borrowItem(user, unsupportedItem, borrowDate);
        assertNull(loan); // Should return null for unsupported types
    }
    
    @Test
    @DisplayName("BorrowingService: returnItem with null loan does nothing")
    void testReturnItemNullLoan() {
        assertDoesNotThrow(() -> borrowingService.returnItem(null, borrowDate));
    }
    
    @Test
    @DisplayName("BorrowingService: returnItem with null returnDate does nothing")
    void testReturnItemNullReturnDate() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        borrowingService.returnItem(loan, null);
        // Should not throw exception, but returnDate should remain null
        assertNull(loan.getReturnDate());
    }
    
    @Test
    @DisplayName("BorrowingService: returnItem with item and no book sets item available")
    void testReturnItemWithItemPath() {
        Loan loan = borrowingService.borrowCD(user, cd, borrowDate);
        assertFalse(cd.isAvailable());
        
        borrowingService.returnItem(loan, borrowDate.plusDays(5));
        
        assertTrue(cd.isAvailable()); // CD should be marked available
        assertEquals(borrowDate.plusDays(5), loan.getReturnDate());
    }
    
    @Test
    @DisplayName("BorrowingService: returnItem with both item and book null does nothing")
    void testReturnItemBothNull() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        loan.setItem(null);
        borrowingService.returnItem(loan, borrowDate.plusDays(10));
        
        // Should not throw exception
        assertEquals(borrowDate.plusDays(10), loan.getReturnDate());
    }
    
    @Test
    @DisplayName("BorrowingService: returnBook with null loan does nothing")
    void testReturnBookNullLoan() {
        assertDoesNotThrow(() -> borrowingService.returnBook(null, borrowDate));
    }
    
    @Test
    @DisplayName("BorrowingService: returnBook with null returnDate does nothing")
    void testReturnBookNullReturnDate() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        assertDoesNotThrow(() -> borrowingService.returnBook(loan, null));
        assertNull(loan.getReturnDate());
    }
    
    @Test
    @DisplayName("BorrowingService: returnBook with null book in loan does nothing")
    void testReturnBookNullBook() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        loan.setItem(null);
        
        borrowingService.returnBook(loan, borrowDate.plusDays(10));
        // Should not throw exception
        assertEquals(borrowDate.plusDays(10), loan.getReturnDate());
    }
    
    @Test
    @DisplayName("BorrowingService: getActiveLoans with null user returns empty list")
    void testGetActiveLoansNullUser() {
        List<Loan> loans = borrowingService.getActiveLoans(null);
        assertNotNull(loans);
        assertTrue(loans.isEmpty());
    }
    
    @Test
    @DisplayName("BorrowingService: getAllLoans with null user returns empty list")
    void testGetAllLoansNullUser() {
        List<Loan> loans = borrowingService.getAllLoans(null);
        assertNotNull(loans);
        assertTrue(loans.isEmpty());
    }
    
    @Test
    @DisplayName("BorrowingService: canBorrow with null user returns false")
    void testCanBorrowNullUser() {
        assertFalse(borrowingService.canBorrow(null));
    }
    
    @Test
    @DisplayName("BorrowingService: canBorrow with null date returns false for null user")
    void testCanBorrowNullUserWithDate() {
        assertFalse(borrowingService.canBorrow(null, borrowDate));
    }
    
    @Test
    @DisplayName("BorrowingService: hasOverdueBooks with null user returns false")
    void testHasOverdueBooksNullUser() {
        assertFalse(borrowingService.hasOverdueBooks(null));
        assertFalse(borrowingService.hasOverdueBooks(null, borrowDate));
    }
    
    @Test
    @DisplayName("BorrowingService: hasUnpaidFines with null user returns false")
    void testHasUnpaidFinesNullUser() {
        assertFalse(borrowingService.hasUnpaidFines(null));
    }
    
    @Test
    @DisplayName("BorrowingService: getUnpaidFines with null user returns empty list")
    void testGetUnpaidFinesNullUser() {
        List<Fine> fines = borrowingService.getUnpaidFines(null);
        assertNotNull(fines);
        assertTrue(fines.isEmpty());
    }
    
    @Test
    @DisplayName("BorrowingService: payFine with null user returns false")
    void testPayFineNullUser() {
        assertFalse(borrowingService.payFine(null, 10.0));
    }
    
    @Test
    @DisplayName("BorrowingService: payFine with zero amount returns false")
    void testPayFineZeroAmount() {
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        assertFalse(borrowingService.payFine(user, 0.0));
    }
    
    @Test
    @DisplayName("BorrowingService: payFine with negative amount returns false")
    void testPayFineNegativeAmount() {
        Fine fine = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine);
        
        assertFalse(borrowingService.payFine(user, -5.0));
    }
    
    @Test
    @DisplayName("BorrowingService: payFine with no unpaid fines returns false")
    void testPayFineNoUnpaidFines() {
        assertFalse(borrowingService.payFine(user, 10.0));
    }
    
    @Test
    @DisplayName("BorrowingService: payFine with multiple fines pays in order")
    void testPayFineMultipleFinesInOrder() {
        Fine fine1 = new Fine(user, 5.0, borrowDate);
        Fine fine2 = new Fine(user, 3.0, borrowDate);
        borrowingService.addFine(fine1);
        borrowingService.addFine(fine2);
        
        // Pay amount that covers first fine completely and part of second
        boolean result = borrowingService.payFine(user, 6.0);
        
        assertTrue(result);
        assertTrue(fine1.isPaid()); // First fine should be fully paid
        assertFalse(fine2.isPaid()); // Second fine should be partially paid
        assertEquals(2.0, fine2.getRemainingBalance()); // 3.0 - 1.0 = 2.0
    }
    
    @Test
    @DisplayName("BorrowingService: payFine with amount less than first fine")
    void testPayFineAmountLessThanFirstFine() {
        Fine fine1 = new Fine(user, 10.0, borrowDate);
        Fine fine2 = new Fine(user, 5.0, borrowDate);
        borrowingService.addFine(fine1);
        borrowingService.addFine(fine2);
        
        // Pay amount that doesn't cover first fine completely
        boolean result = borrowingService.payFine(user, 7.0);
        
        assertTrue(result);
        assertFalse(fine1.isPaid()); // First fine should be partially paid
        assertEquals(3.0, fine1.getRemainingBalance()); // 10.0 - 7.0 = 3.0
        assertFalse(fine2.isPaid()); // Second fine should not be touched
        assertEquals(5.0, fine2.getRemainingBalance());
    }
    
    @Test
    @DisplayName("BorrowingService: payFine with exact amount for multiple fines")
    void testPayFineExactAmountMultipleFines() {
        Fine fine1 = new Fine(user, 5.0, borrowDate);
        Fine fine2 = new Fine(user, 3.0, borrowDate);
        borrowingService.addFine(fine1);
        borrowingService.addFine(fine2);
        
        // Pay exact amount to cover both fines
        boolean result = borrowingService.payFine(user, 8.0);
        
        assertTrue(result);
        assertTrue(fine1.isPaid());
        assertTrue(fine2.isPaid());
    }
    
    @Test
    @DisplayName("BorrowingService: payFine with amount exceeding all fines")
    void testPayFineExcessiveAmount() {
        Fine fine1 = new Fine(user, 5.0, borrowDate);
        Fine fine2 = new Fine(user, 3.0, borrowDate);
        borrowingService.addFine(fine1);
        borrowingService.addFine(fine2);
        
        // Pay more than needed
        boolean result = borrowingService.payFine(user, 20.0);
        
        assertTrue(result);
        assertTrue(fine1.isPaid());
        assertTrue(fine2.isPaid());
    }
    
    @Test
    @DisplayName("BorrowingService: addFine with null fine does nothing")
    void testAddFineNull() {
        borrowingService.addFine(null);
        List<Fine> fines = borrowingService.getFines();
        assertTrue(fines.isEmpty());
    }
    
    // ========== OverdueDetectionService Edge Cases ==========
    
    @Test
    @DisplayName("OverdueDetectionService: calculateOverdueFine with null loan returns 0")
    void testCalculateOverdueFineNullLoan() {
        FineStrategy strategy = FineStrategyFactory.getStrategy(FineStrategyFactory.ItemType.BOOK);
        int fine = overdueService.calculateOverdueFine(null, borrowDate, strategy);
        assertEquals(0, fine);
    }
    
    @Test
    @DisplayName("OverdueDetectionService: calculateOverdueFine with null date returns 0")
    void testCalculateOverdueFineNullDate() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        FineStrategy strategy = FineStrategyFactory.getStrategy(FineStrategyFactory.ItemType.BOOK);
        int fine = overdueService.calculateOverdueFine(loan, null, strategy);
        assertEquals(0, fine);
    }
    
    @Test
    @DisplayName("OverdueDetectionService: calculateOverdueFine with null strategy returns 0")
    void testCalculateOverdueFineNullStrategy() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        FineStrategy nullStrategy = null;
        int fine = overdueService.calculateOverdueFine(loan, borrowDate.plusDays(30), nullStrategy);
        assertEquals(0, fine);
    }
    
    @Test
    @DisplayName("OverdueDetectionService: calculateOverdueFine with not overdue loan returns 0")
    void testCalculateOverdueFineNotOverdue() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        FineStrategy strategy = FineStrategyFactory.getStrategy(FineStrategyFactory.ItemType.BOOK);
        int fine = overdueService.calculateOverdueFine(loan, borrowDate.plusDays(20), strategy);
        assertEquals(0, fine); // Not overdue yet
    }
    
    @Test
    @DisplayName("OverdueDetectionService: getOverdueLoansForUser with null user returns empty list")
    void testGetOverdueLoansForUserNullUser() {
        List<Loan> loans = overdueService.getOverdueLoansForUser(null, borrowDate);
        assertNotNull(loans);
        assertTrue(loans.isEmpty());
    }
    
    @Test
    @DisplayName("OverdueDetectionService: getOverdueLoansForUser with null date returns empty list")
    void testGetOverdueLoansForUserNullDate() {
        List<Loan> loans = overdueService.getOverdueLoansForUser(user, null);
        assertNotNull(loans);
        assertTrue(loans.isEmpty());
    }
    
    @Test
    @DisplayName("OverdueDetectionService: calculateOverdueFine with null loan returns 0 (double method)")
    void testCalculateOverdueFineDoubleNullLoan() {
        double fine = overdueService.calculateOverdueFine(null, borrowDate);
        assertEquals(0.0, fine);
    }
    
    @Test
    @DisplayName("OverdueDetectionService: calculateOverdueFine with null date returns 0 (double method)")
    void testCalculateOverdueFineDoubleNullDate() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        double fine = overdueService.calculateOverdueFine(loan, null);
        assertEquals(0.0, fine);
    }
    
    @Test
    @DisplayName("OverdueDetectionService: calculateOverdueFine with not overdue loan returns 0 (double method)")
    void testCalculateOverdueFineDoubleNotOverdue() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        double fine = overdueService.calculateOverdueFine(loan, borrowDate.plusDays(20));
        assertEquals(0.0, fine); // Not overdue yet
    }
    
    @Test
    @DisplayName("OverdueDetectionService: calculateOverdueFine with null item uses fallback calculation")
    void testCalculateOverdueFineFallbackWhenItemNull() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        loan.setItem(null); // Simulate loan without item (backward compatibility)
        LocalDate currentDate = borrowDate.plusDays(30);
        
        double fine = overdueService.calculateOverdueFine(loan, currentDate);
        
        // Should use Fine.calculateFineAmount as fallback
        assertTrue(fine > 0);
        long daysOverdue = loan.getDaysOverdue(currentDate);
        assertEquals(Fine.calculateFineAmount(daysOverdue), fine);
    }
    
    @Test
    @DisplayName("OverdueDetectionService: getMixedMediaOverdueReport with null user returns empty report")
    void testGetMixedMediaOverdueReportNullUser() {
        Map<String, Object> report = overdueService.getMixedMediaOverdueReport(null, borrowDate);
        assertNotNull(report);
        assertTrue(report.isEmpty());
    }
    
    @Test
    @DisplayName("OverdueDetectionService: getMixedMediaOverdueReport with null date returns empty report")
    void testGetMixedMediaOverdueReportNullDate() {
        Map<String, Object> report = overdueService.getMixedMediaOverdueReport(user, null);
        assertNotNull(report);
        assertTrue(report.isEmpty());
    }
    
    @Test
    @DisplayName("OverdueDetectionService: getMixedMediaOverdueReport filters out loans with null items")
    void testGetMixedMediaOverdueReportFiltersNullItems() {
        Loan loan = borrowingService.borrowBook(user, book, borrowDate);
        loan.setItem(null); // Make item null
        LocalDate currentDate = borrowDate.plusDays(30);
        
        Map<String, Object> report = overdueService.getMixedMediaOverdueReport(user, currentDate);
        
        assertNotNull(report);
        // Loan with null item should be filtered out
        Map<String, Object> byType = (Map<String, Object>) report.get("byType");
        assertTrue(byType == null || byType.isEmpty());
    }
    
    @Test
    @DisplayName("NotificationService: sendOverdueReminders with null date returns 0")
    void testSendOverdueRemindersNullDate() {
        NotificationService notificationService = new NotificationService(overdueService);
        int count = notificationService.sendOverdueReminders(null);
        assertEquals(0, count);
    }
    
    @Test
    @DisplayName("NotificationService: sendReminderToUser with null user returns false")
    void testSendReminderToUserNullUser() {
        NotificationService notificationService = new NotificationService(overdueService);
        boolean result = notificationService.sendReminderToUser(null, borrowDate);
        assertFalse(result);
    }
    
    @Test
    @DisplayName("NotificationService: sendReminderToUser with null date returns false")
    void testSendReminderToUserNullDate() {
        NotificationService notificationService = new NotificationService(overdueService);
        boolean result = notificationService.sendReminderToUser(user, null);
        assertFalse(result);
    }
    
    @Test
    @DisplayName("NotificationService: sendReminderToUser with user without email returns false")
    void testSendReminderToUserNoEmail() {
        User userNoEmail = new User("U002", "No Email User", null);
        NotificationService notificationService = new NotificationService(overdueService);
        boolean result = notificationService.sendReminderToUser(userNoEmail, borrowDate);
        assertFalse(result);
    }
    
    @Test
    @DisplayName("NotificationService: sendReminderToUser with no overdue loans returns false")
    void testSendReminderToUserNoOverdueLoans() {
        NotificationService notificationService = new NotificationService(overdueService);
        boolean result = notificationService.sendReminderToUser(user, borrowDate);
        assertFalse(result); // User has no overdue loans
    }
    
    @Test
    @DisplayName("NotificationService: constructor with null OverdueDetectionService throws exception")
    void testNotificationServiceConstructorNullService() {
        assertThrows(IllegalArgumentException.class, 
            () -> new NotificationService(null));
    }
    
    @Test
    @DisplayName("NotificationService: attach with null observer does nothing")
    void testAttachNullObserver() {
        NotificationService notificationService = new NotificationService(overdueService);
        assertDoesNotThrow(() -> notificationService.attach(null));
    }
    
    @Test
    @DisplayName("NotificationService: attach with existing observer does not duplicate")
    void testAttachDuplicateObserver() {
        NotificationService notificationService = new NotificationService(overdueService);
        Observer observer = new Observer() {
            @Override
            public boolean notify(User user, String message) {
                return true;
            }
            
            @Override
            public String getObserverType() {
                return "Test";
            }
        };
        notificationService.attach(observer);
        assertDoesNotThrow(() -> notificationService.attach(observer)); // Try to attach again
    }
}

