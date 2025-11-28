package edu.najah.library.service;

import edu.najah.library.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing user registration and unregistration.
 * Handles user account lifecycle and enforces business rules.
 * 
 * <p>US4.2: Admin can register/unregister users with validation rules.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class UserService {
    
    private List<User> users;
    private BorrowingService borrowingService;
    
    /**
     * Constructs a UserService with a reference to BorrowingService.
     * 
     * @param borrowingService the borrowing service for validation
     */
    public UserService(BorrowingService borrowingService) {
        this.users = new ArrayList<>();
        this.borrowingService = borrowingService;
    }
    
    /**
     * Registers a new user in the system.
     * 
     * @param user the user to register
     * @return true if registration successful, false if user is null or already exists
     */
    public boolean registerUser(User user) {
        if (user == null || user.getUserId() == null) {
            return false;
        }
        
        // Check if user already exists
        if (users.stream().anyMatch(u -> u.getUserId().equals(user.getUserId()))) {
            return false;
        }
        
        users.add(user);
        return true;
    }
    
    /**
     * Unregisters a user from the system.
     * 
     * <p>Acceptance Criteria (US4.2):
     * <ul>
     *   <li>Only admins can unregister (enforced by caller)</li>
     *   <li>Users with active loans cannot be unregistered</li>
     *   <li>Users with unpaid fines cannot be unregistered</li>
     * </ul>
     * 
     * @param user the user to unregister
     * @return true if unregistration successful, false if user cannot be unregistered
     */
    public boolean unregisterUser(User user) {
        if (user == null || borrowingService == null) {
            return false;
        }
        
        // Check if user has active loans
        List<User> allUsers = getUsers();
        if (borrowingService.getActiveLoans(user).size() > 0) {
            return false;
        }
        
        // Check if user has unpaid fines
        if (borrowingService.hasUnpaidFines(user)) {
            return false;
        }
        
        // Remove user from system
        return users.removeIf(u -> u.getUserId().equals(user.getUserId()));
    }
    
    /**
     * Finds a user by ID.
     * 
     * @param userId the user ID to search for
     * @return the user, or null if not found
     */
    public User findUserById(String userId) {
        if (userId == null) {
            return null;
        }
        
        return users.stream()
                .filter(u -> userId.equals(u.getUserId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets all registered users.
     * 
     * @return list of all users
     */
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }
    
    /**
     * Checks if a user is registered.
     * 
     * @param user the user to check
     * @return true if user is registered, false otherwise
     */
    public boolean isUserRegistered(User user) {
        if (user == null || user.getUserId() == null) {
            return false;
        }
        
        return users.stream()
                .anyMatch(u -> u.getUserId().equals(user.getUserId()));
    }
    
    /**
     * Gets the total number of registered users.
     * 
     * @return number of users
     */
    public int getUserCount() {
        return users.size();
    }
}
