package edu.najah.library.util;

import edu.najah.library.domain.User;
import edu.najah.library.service.EmailNotifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock email server for testing.
 * Records all sent messages without actually sending emails.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class MockEmailServer implements EmailNotifier {
    
    private List<EmailMessage> sentMessages;
    
    /**
     * Represents a sent email message.
     */
    public static class EmailMessage {
        public final User user;
        public final String message;
        
        /**
         * Constructs an EmailMessage.
         * 
         * @param user the recipient user
         * @param message the message content
         */
        public EmailMessage(User user, String message) {
            this.user = user;
            this.message = message;
        }
        
        @Override
        public String toString() {
            return "EmailMessage{" +
                    "to='" + (user != null ? user.getEmail() : "null") + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
    
    /**
     * Constructs a MockEmailServer.
     */
    public MockEmailServer() {
        this.sentMessages = new ArrayList<>();
    }
    
    /**
     * Records a notification message without sending it.
     * 
     * @param user the user to notify
     * @param message the message to send
     */
    @Override
    public void notify(User user, String message) {
        if (user != null && message != null) {
            sentMessages.add(new EmailMessage(user, message));
        }
    }
    
    /**
     * Gets all sent messages.
     * 
     * @return list of sent email messages
     */
    public List<EmailMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }
    
    /**
     * Gets the number of messages sent.
     * 
     * @return count of sent messages
     */
    public int getMessageCount() {
        return sentMessages.size();
    }
    
    /**
     * Gets messages sent to a specific user.
     * 
     * @param user the user
     * @return list of messages sent to the user
     */
    public List<EmailMessage> getMessagesFor(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<EmailMessage> result = new ArrayList<>();
        for (EmailMessage msg : sentMessages) {
            if (user.equals(msg.user)) {
                result.add(msg);
            }
        }
        return result;
    }
    
    /**
     * Clears all recorded messages.
     */
    public void clear() {
        sentMessages.clear();
    }
    
    /**
     * Checks if any messages were sent.
     * 
     * @return true if messages were sent, false otherwise
     */
    public boolean hasSentMessages() {
        return !sentMessages.isEmpty();
    }
}
