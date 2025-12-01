package edu.najah.library.service;

import edu.najah.library.domain.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AuthenticationService.
 * Tests admin login, logout, and session management.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class AuthenticationServiceTest {
    
    private AuthenticationService authService;
    private Admin testAdmin;
    
    /**
     * Set up test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        authService = new AuthenticationService();
        testAdmin = new Admin("admin", "password123");
        authService.registerAdmin(testAdmin);
    }
    
    /**
     * Test US1.1: Valid credentials should result in successful login.
     */
    @Test
    void testLoginWithValidCredentials() {
        boolean result = authService.login("admin", "password123");
        
        assertTrue(result, "Login with valid credentials should return true");
        assertTrue(authService.isLoggedIn(), "Admin should be logged in");
        assertEquals(testAdmin, authService.getCurrentAdmin(), "Current admin should be set");
    }
    
    /**
     * Test US1.1: Invalid username should result in failed login.
     */
    @Test
    void testLoginWithInvalidUsername() {
        boolean result = authService.login("wronguser", "password123");
        
        assertFalse(result, "Login with invalid username should return false");
        assertFalse(authService.isLoggedIn(), "Admin should not be logged in");
        assertNull(authService.getCurrentAdmin(), "Current admin should be null");
    }
    
    /**
     * Test US1.1: Invalid password should result in failed login.
     */
    @Test
    void testLoginWithInvalidPassword() {
        boolean result = authService.login("admin", "wrongpassword");
        
        assertFalse(result, "Login with invalid password should return false");
        assertFalse(authService.isLoggedIn(), "Admin should not be logged in");
    }
    
    /**
     * Test US1.1: Null credentials should result in failed login.
     */
    @Test
    void testLoginWithNullCredentials() {
        assertFalse(authService.login(null, "password"), "Null username should fail");
        assertFalse(authService.login("admin", null), "Null password should fail");
        assertFalse(authService.login(null, null), "Null credentials should fail");
    }
    
    /**
     * Test US1.2: Logout should clear the session.
     */
    @Test
    void testLogout() {
        // First login
        authService.login("admin", "password123");
        assertTrue(authService.isLoggedIn(), "Admin should be logged in");
        
        // Then logout
        authService.logout();
        
        assertFalse(authService.isLoggedIn(), "Admin should not be logged in after logout");
        assertNull(authService.getCurrentAdmin(), "Current admin should be null after logout");
    }
    
    /**
     * Test US1.2: After logout, admin actions should require re-login.
     */
    @Test
    void testRequiresReloginAfterLogout() {
        // Login
        authService.login("admin", "password123");
        assertFalse(authService.requiresLogin(), "Should not require login when logged in");
        
        // Logout
        authService.logout();
        assertTrue(authService.requiresLogin(), "Should require login after logout");
        
        // Re-login
        authService.login("admin", "password123");
        assertFalse(authService.requiresLogin(), "Should not require login after re-login");
    }
    
    /**
     * Test initial state: no admin should be logged in.
     */
    @Test
    void testInitialState() {
        AuthenticationService newService = new AuthenticationService();
        
        assertFalse(newService.isLoggedIn(), "No admin should be logged in initially");
        assertNull(newService.getCurrentAdmin(), "Current admin should be null initially");
        assertTrue(newService.requiresLogin(), "Should require login initially");
    }
    
    /**
     * Test registering an admin.
     */
    @Test
    void testRegisterAdmin() {
        AuthenticationService newService = new AuthenticationService();
        Admin newAdmin = new Admin("newadmin", "newpass");
        
        newService.registerAdmin(newAdmin);
        
        assertTrue(newService.login("newadmin", "newpass"), 
                "Should be able to login with registered admin");
    }
    
    /**
     * Test registering null admin should not cause error.
     */
    @Test
    void testRegisterNullAdmin() {
        assertDoesNotThrow(() -> authService.registerAdmin(null), 
                "Registering null admin should not throw exception");
    }
    
    /**
     * Test multiple login attempts.
     */
    @Test
    void testMultipleLoginAttempts() {
        assertFalse(authService.login("admin", "wrong1"), "First wrong attempt");
        assertFalse(authService.login("admin", "wrong2"), "Second wrong attempt");
        assertTrue(authService.login("admin", "password123"), "Correct credentials");
    }
}
