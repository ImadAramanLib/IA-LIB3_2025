package edu.najah.library.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock email server for testing purposes.
 * Records all sent messages in test mode without actually sending emails.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class MockEmailServer implements EmailService {
    
    private List<EmailRecord> sentEmails;
    private boolean testMode;
    
    /**
     * Record of a sent email message.
     */
    public static class EmailRecord {
        private String to;
        private String subject;
        private String message;
        
        public EmailRecord(String to, String subject, String message) {
            this.to = to;
            this.subject = subject;
            this.message = message;
        }
        
        public String getTo() {
            return to;
        }
        
        public String getSubject() {
            return subject;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return "EmailRecord{" +
                    "to='" + to + '\'' +
                    ", subject='" + subject + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
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
     * @param to the recipient email address
     * @param subject the email subject
     * @param message the email message body
     * @return true if email was sent/recorded successfully, false otherwise
     */
    @Override
    public boolean sendEmail(String to, String subject, String message) {
        if (to == null || subject == null || message == null) {
            return false;
        }
        
        // Record the email (in test mode, this is the only action;
        // in production mode, actual email sending would occur here before recording)
        sentEmails.add(new EmailRecord(to, subject, message));
        
        return true;
    }
    
    /**
     * Checks if this email server is in test mode.
     * 
     * @return true if in test mode, false otherwise
     */
    @Override
    public boolean isTestMode() {
        return testMode;
    }
    
    /**
     * Gets all recorded emails sent through this server.
     * 
     * @return list of email records
     */
    public List<EmailRecord> getSentEmails() {
        return new ArrayList<>(sentEmails);
    }
    
    /**
     * Clears all recorded emails.
     */
    public void clearSentEmails() {
        sentEmails.clear();
    }
    
    /**
     * Gets the count of sent emails.
     * 
     * @return number of emails sent/recorded
     */
    public int getSentEmailCount() {
        return sentEmails.size();
    }
}

