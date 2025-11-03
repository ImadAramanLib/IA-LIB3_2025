package edu.najah.library.service;

import edu.najah.library.domain.Admin;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for handling administrator authentication.
 * Manages admin login, logout, and session state.
 * 
 * <p>US1.1: Admin can login with valid credentials
 * <p>US1.2: Admin can logout and session is closed securely
 * 
 * @author Imad Araman, Hamza Abuobaid
 * @version 1.0
 */
public class AuthenticationService {
    
    private Map<String, Admin> admins;
    private Admin currentAdmin;
    
    /**
     * Constructs an AuthenticationService with no registered admins.
     */
    public AuthenticationService() {
        this.admins = new HashMap<>();
        this.currentAdmin = null;
    }
    
    /**
     * Registers an admin in the system.
     * Used for setup/testing purposes.
     * 
     * @param admin the admin to register
     */
    public void registerAdmin(Admin admin) {
        if (admin != null && admin.getUsername() != null) {
            admins.put(admin.getUsername(), admin);
        }
    }
    
    /**
     * Attempts to log in an administrator with the given credentials.
     * 
     * <p>Acceptance Criteria (US1.1):
     * <ul>
     *   <li>Valid credentials → login success (returns true)</li>
     *   <li>Invalid credentials → error (returns false)</li>
     * </ul>
     * 
     * @param username the administrator's username
     * @param password the administrator's password
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        Admin admin = admins.get(username);
        if (admin != null && admin.getPassword().equals(password)) {
            currentAdmin = admin;
            return true;
        }
        
        return false;
    }
    
    /**
     * Logs out the current administrator.
     * 
     * <p>Acceptance Criteria (US1.2):
     * After logout, any admin action requires re-login.
     */
    public void logout() {
        currentAdmin = null;
    }
    
    /**
     * Checks if an administrator is currently logged in.
     * 
     * @return true if an admin is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentAdmin != null;
    }
    
    /**
     * Gets the currently logged-in administrator.
     * 
     * @return the current admin, or null if no admin is logged in
     */
    public Admin getCurrentAdmin() {
        return currentAdmin;
    }
    
    /**
     * Checks if the current session has admin privileges.
     * This should be called before performing admin-only operations.
     * 
     * @return true if an admin is logged in, false otherwise
     */
    public boolean requiresLogin() {
        return !isLoggedIn();
    }
}
