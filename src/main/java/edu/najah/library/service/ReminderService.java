package edu.najah.library.service;

import edu.najah.library.domain.Loan;
import edu.najah.library.domain.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for sending reminder notifications to users with overdue books.
 * Uses the Observer pattern to support multiple notification channels.
 * 
 * <p>US3.1: Admin can send reminder emails to users with overdue books.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class ReminderService {
    
    private OverdueDetectionService overdueDetectionService;
    private List<EmailNotifier> notifiers;
    
    /**
     * Constructs a ReminderService with an OverdueDetectionService.
     * 
     * @param overdueDetectionService the service to detect overdue loans
     */
    public ReminderService(OverdueDetectionService overdueDetectionService) {
        this.overdueDetectionService = overdueDetectionService;
        this.notifiers = new ArrayList<>();
    }
    
    /**
     * Registers an email notifier to receive notifications.
     * 
     * @param notifier the notifier to register
     */
    public void registerNotifier(EmailNotifier notifier) {
        if (notifier != null && !notifiers.contains(notifier)) {
            notifiers.add(notifier);
        }
    }
    
    /**
     * Unregisters an email notifier.
     * 
     * @param notifier the notifier to unregister
     */
    public void unregisterNotifier(EmailNotifier notifier) {
        notifiers.remove(notifier);
    }
    
    /**
     * Sends reminder emails to all users with overdue books as of a given date.
     * 
     * <p>Acceptance Criteria (US3.1):
     * <ul>
     *   <li>Message format: "You have n overdue book(s)."</li>
     *   <li>Mock email server records sent messages in test mode</li>
     * </ul>
     * 
     * @param currentDate the date to check for overdue books
     */
    public void sendReminders(LocalDate currentDate) {
        if (overdueDetectionService == null || currentDate == null) {
            return;
        }
        
        // Get all overdue loans
        List<Loan> overdueLoans = overdueDetectionService.getOverdueLoans(currentDate);
        
        // Group by user to send one email per user
        Set<User> usersWithOverdue = new HashSet<>();
        for (Loan loan : overdueLoans) {
            usersWithOverdue.add(loan.getUser());
        }
        
        // Send reminder to each user
        for (User user : usersWithOverdue) {
            // Count overdue books for this user
            long overdueCount = overdueLoans.stream()
                    .filter(loan -> user.equals(loan.getUser()))
                    .count();
            
            String message = String.format("You have %d overdue book(s).", overdueCount);
            
            // Notify all registered notifiers
            for (EmailNotifier notifier : notifiers) {
                notifier.notify(user, message);
            }
        }
    }
    
    /**
     * Sends reminder emails to all users with overdue books as of today.
     */
    public void sendReminders() {
        sendReminders(LocalDate.now());
    }
    
    /**
     * Gets the list of registered notifiers.
     * 
     * @return list of notifiers
     */
    public List<EmailNotifier> getNotifiers() {
        return new ArrayList<>(notifiers);
    }
}
