package edu.najah.library.util;

import edu.najah.library.domain.User;
import edu.najah.library.service.EmailNotifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock email server for testing purposes.
 * Records all sent messages in test mode without actually sending emails.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class MockEmailServer implements EmailNotifier {
    
    private List<EmailMessage> sentEmails;
    private boolean testMode;
    
    /**
     * Record of a sent email message.
     */
    public static class EmailMessage {
        private String to;
        private String subject;
        private String message;
        
        public EmailMessage(String to, String subject, String message) {
            this.to = to;
            this.subject = subject;
            this.message = message;
        }
        
        public String getTo() { return to; }
        public String getSubject() { return subject; }
        public String getMessage() { return message; }
    }
    
    /**
     * Constructs a MockEmailServer in test mode by default.
     */
    public MockEmailServer() {
        this(true);
    }
    
    /**
     * Constructs a MockEmailServer with specified test mode.
     * 
     * @param testMode true to enable test mode (record emails), false to disable
     */
    public MockEmailServer(boolean testMode) {
        this.sentEmails = new ArrayList<>();
        this.testMode = testMode;
    }
    
    /**
     * Sends an email (or records it in test mode).
     * 
     * @param user the user to send email to
     * @param message the email message body
     */
    @Override
    public void notify(User user, String message) {
        if (user == null || message == null) {
            return;
        }
        
        if (testMode) {
            sentEmails.add(new EmailMessage(user.getEmail(), "Library Notification", message));
        }
    }
    
    /**
     * Gets all recorded emails sent through this server.
     * 
     * @return list of email records
     */
    public List<EmailMessage> getSentEmails() {
        return new ArrayList<>(sentEmails);
    }
    
    /**
     * Gets the count of sent emails.
     * 
     * @return number of emails sent/recorded
     */
    public int getSentEmailCount() {
        return sentEmails.size();
    }
    
    /**
     * Clears all recorded emails.
     */
    public void clearSentEmails() {
        sentEmails.clear();
    }
}

