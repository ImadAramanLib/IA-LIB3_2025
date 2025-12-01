package edu.najah.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MockEmailServer.
 * Tests email recording functionality in test mode.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class MockEmailServerTest {
    
    private MockEmailServer emailServer;
    
    @BeforeEach
    void setUp() {
        emailServer = new MockEmailServer(true); // Test mode enabled
    }
    
    @Test
    @DisplayName("MockEmailServer: records sent emails in test mode")
    void testRecordsEmailsInTestMode() {
        emailServer.sendEmail("user@example.com", "Test Subject", "Test Message");
        
        assertEquals(1, emailServer.getSentEmailCount());
        List<MockEmailServer.EmailRecord> emails = emailServer.getSentEmails();
        assertEquals(1, emails.size());
        
        MockEmailServer.EmailRecord record = emails.get(0);
        assertEquals("user@example.com", record.getTo());
        assertEquals("Test Subject", record.getSubject());
        assertEquals("Test Message", record.getMessage());
    }
    
    @Test
    @DisplayName("MockEmailServer: isTestMode returns true when in test mode")
    void testIsTestMode() {
        assertTrue(emailServer.isTestMode());
        
        MockEmailServer productionServer = new MockEmailServer(false);
        assertFalse(productionServer.isTestMode());
    }
    
    @Test
    @DisplayName("MockEmailServer: default constructor enables test mode")
    void testDefaultConstructorEnablesTestMode() {
        MockEmailServer defaultServer = new MockEmailServer();
        assertTrue(defaultServer.isTestMode());
    }
    
    @Test
    @DisplayName("MockEmailServer: records multiple emails")
    void testRecordsMultipleEmails() {
        emailServer.sendEmail("user1@example.com", "Subject 1", "Message 1");
        emailServer.sendEmail("user2@example.com", "Subject 2", "Message 2");
        emailServer.sendEmail("user3@example.com", "Subject 3", "Message 3");
        
        assertEquals(3, emailServer.getSentEmailCount());
        List<MockEmailServer.EmailRecord> emails = emailServer.getSentEmails();
        assertEquals(3, emails.size());
    }
    
    @Test
    @DisplayName("MockEmailServer: clearSentEmails removes all records")
    void testClearSentEmails() {
        emailServer.sendEmail("user@example.com", "Subject", "Message");
        assertEquals(1, emailServer.getSentEmailCount());
        
        emailServer.clearSentEmails();
        assertEquals(0, emailServer.getSentEmailCount());
        assertTrue(emailServer.getSentEmails().isEmpty());
    }
    
    @Test
    @DisplayName("MockEmailServer: returns false for null parameters")
    void testReturnsFalseForNullParameters() {
        assertFalse(emailServer.sendEmail(null, "Subject", "Message"));
        assertFalse(emailServer.sendEmail("user@example.com", null, "Message"));
        assertFalse(emailServer.sendEmail("user@example.com", "Subject", null));
        assertFalse(emailServer.sendEmail(null, null, null));
        
        assertEquals(0, emailServer.getSentEmailCount());
    }
    
    @Test
    @DisplayName("MockEmailServer: getSentEmails returns a copy")
    void testGetSentEmailsReturnsCopy() {
        emailServer.sendEmail("user@example.com", "Subject", "Message");
        
        List<MockEmailServer.EmailRecord> emails1 = emailServer.getSentEmails();
        List<MockEmailServer.EmailRecord> emails2 = emailServer.getSentEmails();
        
        assertNotSame(emails1, emails2, "Should return different list instances");
        assertEquals(emails1, emails2, "Content should be equal");
    }
    
    @Test
    @DisplayName("MockEmailServer: EmailRecord toString includes all fields")
    void testEmailRecordToString() {
        MockEmailServer.EmailRecord record = new MockEmailServer.EmailRecord(
            "user@example.com", "Test Subject", "Test Message");
        
        String toString = record.toString();
        assertTrue(toString.contains("user@example.com"));
        assertTrue(toString.contains("Test Subject"));
        assertTrue(toString.contains("Test Message"));
    }
}

