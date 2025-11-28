package edu.najah.library.service;

import edu.najah.library.domain.User;

/**
 * Observer interface for email notifications.
 * Implements the Observer pattern to allow multiple notification channels.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public interface EmailNotifier {
    
    /**
     * Sends a notification message to a user.
     * 
     * @param user the user to notify
     * @param message the message to send
     */
    void notify(User user, String message);
}
