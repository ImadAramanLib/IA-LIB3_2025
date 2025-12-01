package edu.najah.library.service;

import edu.najah.library.domain.Admin;
import edu.najah.library.domain.Book;
import edu.najah.library.domain.Fine;
import edu.najah.library.domain.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserManagementService.
 * Tests user registration and unregistration functionality.
 * 
 * <p>US4.2: Unregister user - Only admins can unregister, users with active
 * loans or unpaid fines cannot be unregistered.</p>
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class UserManagementServiceTest {
    
    private MockEmailServer emailServer;
    private AuthenticationService authenticationService;
    private BorrowingService borrowingService;
    private UserManagementService userManagementService;
    private Admin admin;
    private User user;
    
    @BeforeEach
    void setUp() {
        emailServer = new MockEmailServer(true);
        authenticationService = new AuthenticationService();
        borrowingService = new BorrowingService();
        userManagementService = new UserManagementService(
            borrowingService, authenticationService, emailServer);
        
        admin = new Admin("admin", "password123");
        authenticationService.registerAdmin(admin);
        
        user = new User("U001", "John Doe", "john@example.com");
        userManagementService.registerUser(user);
    }
    
    @Test
    @DisplayName("registerUser adds user to the system")
    void testRegisterUser() {
        User newUser = new User("U002", "Jane Doe", "jane@example.com");
        
        boolean result = userManagementService.registerUser(newUser);
        
        assertTrue(result);
        assertTrue(userManagementService.isUserRegistered("U002"));
        assertEquals(newUser, userManagementService.findUserById("U002"));
    }
    
    @Test
    @DisplayName("registerUser returns false for duplicate user")
    void testRegisterUserDuplicate() {
        boolean result = userManagementService.registerUser(user);
        
        assertFalse(result);
        assertEquals(1, userManagementService.getAllUsers().size());
    }
    
    @Test
    @DisplayName("registerUser returns false for null user")
    void testRegisterUserNull() {
        assertFalse(userManagementService.registerUser(null));
    }
    
    @Test
    @DisplayName("US4.2: unregisterUser requires admin login")
    void testUnregisterUserRequiresAdminLogin() {
        // Not logged in
        assertFalse(authenticationService.isLoggedIn());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> userManagementService.unregisterUser("U001"));
        
        assertTrue(exception.getMessage().contains("administrators"));
        assertTrue(userManagementService.isUserRegistered("U001"));
    }
    
    @Test
    @DisplayName("US4.2: unregisterUser successfully removes user when admin is logged in")
    void testUnregisterUserSuccess() {
        authenticationService.login("admin", "password123");
        
        boolean result = userManagementService.unregisterUser("U001");
        
        assertTrue(result);
        assertFalse(userManagementService.isUserRegistered("U001"));
        assertEquals(1, emailServer.getSentEmailCount()); // Email sent after removal
    }
    
    @Test
    @DisplayName("US4.2: unregisterUser sends notification email")
    void testUnregisterUserSendsNotificationEmail() {
        authenticationService.login("admin", "password123");
        
        userManagementService.unregisterUser("U001");
        
        List<MockEmailServer.EmailRecord> emails = emailServer.getSentEmails();
        assertEquals(1, emails.size());
        
        MockEmailServer.EmailRecord email = emails.get(0);
        assertEquals("john@example.com", email.getTo());
        assertEquals("Account Unregistration Confirmation", email.getSubject());
        assertTrue(email.getMessage().contains("unregistered"));
    }
    
    @Test
    @DisplayName("US4.2: unregisterUser blocks user with active loans")
    void testUnregisterUserWithActiveLoans() {
        authenticationService.login("admin", "password123");
        
        // Create an active loan
        Book book = new Book("Test Book", "Test Author", "ISBN123");
        borrowingService.borrowBook(user, book, LocalDate.now());
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userManagementService.unregisterUser("U001"));
        
        assertTrue(exception.getMessage().contains("active loans"));
        assertTrue(userManagementService.isUserRegistered("U001"));
    }
    
    @Test
    @DisplayName("US4.2: unregisterUser blocks user with unpaid fines")
    void testUnregisterUserWithUnpaidFines() {
        authenticationService.login("admin", "password123");
        
        // Create an unpaid fine
        Fine fine = new Fine(user, 5.0, LocalDate.now());
        borrowingService.addFine(fine);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userManagementService.unregisterUser("U001"));
        
        assertTrue(exception.getMessage().contains("unpaid fines"));
        assertTrue(userManagementService.isUserRegistered("U001"));
    }
    
    @Test
    @DisplayName("US4.2: unregisterUser allows user with returned loans and paid fines")
    void testUnregisterUserWithReturnedLoansAndPaidFines() {
        authenticationService.login("admin", "password123");
        
        // Create a loan and return it
        Book book = new Book("Test Book", "Test Author", "ISBN123");
        var loan = borrowingService.borrowBook(user, book, LocalDate.now().minusDays(30));
        borrowingService.returnBook(loan, LocalDate.now());
        
        // Create and pay a fine
        Fine fine = new Fine(user, 5.0, LocalDate.now());
        borrowingService.addFine(fine);
        borrowingService.payFine(user, 5.0);
        
        boolean result = userManagementService.unregisterUser("U001");
        
        assertTrue(result);
        assertFalse(userManagementService.isUserRegistered("U001"));
    }
    
    @Test
    @DisplayName("unregisterUser returns false for non-existent user")
    void testUnregisterUserNonExistent() {
        authenticationService.login("admin", "password123");
        
        boolean result = userManagementService.unregisterUser("NONEXISTENT");
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("unregisterUser returns false for null userId")
    void testUnregisterUserNull() {
        authenticationService.login("admin", "password123");
        
        boolean result = userManagementService.unregisterUser(null);
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("unregisterUser does not send email if user has no email")
    void testUnregisterUserNoEmail() {
        authenticationService.login("admin", "password123");
        
        User userNoEmail = new User("U003", "No Email", null);
        userManagementService.registerUser(userNoEmail);
        
        boolean result = userManagementService.unregisterUser("U003");
        
        assertTrue(result);
        assertEquals(0, emailServer.getSentEmailCount());
    }
    
    @Test
    @DisplayName("isUserRegistered returns true for registered user")
    void testIsUserRegistered() {
        assertTrue(userManagementService.isUserRegistered("U001"));
        assertFalse(userManagementService.isUserRegistered("NONEXISTENT"));
    }
    
    @Test
    @DisplayName("findUserById returns correct user")
    void testFindUserById() {
        User found = userManagementService.findUserById("U001");
        assertEquals(user, found);
        
        User notFound = userManagementService.findUserById("NONEXISTENT");
        assertNull(notFound);
    }
    
    @Test
    @DisplayName("getAllUsers returns all registered users")
    void testGetAllUsers() {
        User user2 = new User("U002", "Jane Doe", "jane@example.com");
        userManagementService.registerUser(user2);
        
        List<User> users = userManagementService.getAllUsers();
        
        assertEquals(2, users.size());
        assertTrue(users.contains(user));
        assertTrue(users.contains(user2));
    }
    
    @Test
    @DisplayName("constructor throws exception for null dependencies")
    void testConstructorThrowsExceptionForNullDependencies() {
        assertThrows(IllegalArgumentException.class, 
            () -> new UserManagementService(null, authenticationService, emailServer));
        assertThrows(IllegalArgumentException.class, 
            () -> new UserManagementService(borrowingService, null, emailServer));
        assertThrows(IllegalArgumentException.class, 
            () -> new UserManagementService(borrowingService, authenticationService, null));
    }
}

