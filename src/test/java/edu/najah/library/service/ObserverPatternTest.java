package edu.najah.library.service;

import edu.najah.library.domain.User;
import edu.najah.library.util.MockEmailServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Observer Design Pattern implementation.
 * Tests Observer interface, MockEmailServer as EmailNotifier, and NotificationService as Subject.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class ObserverPatternTest {
    
    private MockEmailServer emailServer;
    private NotificationService notificationService;
    private OverdueDetectionService overdueDetectionService;
    private User user;
    
    @BeforeEach
    public void setUp() {
        emailServer = new MockEmailServer();
        BorrowingService borrowingService = new BorrowingService();
        overdueDetectionService = new OverdueDetectionService(borrowingService);
        notificationService = new NotificationService(overdueDetectionService);
        user = new User("U001", "John Doe", "john@example.com");
    }
    
    @Test
    @DisplayName("Observer Pattern: MockEmailServer implements EmailNotifier interface")
    public void testMockEmailServerImplementsEmailNotifier() {
        assertTrue(emailServer instanceof edu.najah.library.service.EmailNotifier);
    }
    
    @Test
    @DisplayName("Observer Pattern: MockEmailServer notifies user via email")
    public void testMockEmailServerNotify() {
        emailServer.notify(user, "Test message");
        
        assertEquals(1, emailServer.getMessageCount());
        
        MockEmailServer.EmailMessage email = emailServer.getSentMessages().get(0);
        assertEquals("john@example.com", email.user.getEmail());
        assertEquals("Test message", email.message);
    }
    
    @Test
    @DisplayName("Observer Pattern: NotificationService implements Subject interface")
    public void testNotificationServiceImplementsSubject() {
        assertTrue(notificationService instanceof Subject);
    }
    
    @Test
    @DisplayName("Observer Pattern: NotificationService can attach and detach observers")
    public void testAttachDetachObservers() {
        assertEquals(0, emailServer.getMessageCount());
        
        // Attach observer (MockEmailServer implements EmailNotifier which we can wrap)
        MockObserver emailObserver = new MockObserver(emailServer);
        notificationService.attach(emailObserver);
        notificationService.notifyObservers(user, "Test message");
        assertEquals(1, emailServer.getMessageCount());
        
        // Detach observer
        notificationService.detach(emailObserver);
        emailServer.clear();
        notificationService.notifyObservers(user, "Test message");
        assertEquals(0, emailServer.getMessageCount());
    }
    
    @Test
    @DisplayName("Observer Pattern: NotificationService notifies all attached observers")
    public void testNotifyAllObservers() {
        MockEmailServer emailServer2 = new MockEmailServer();
        MockObserver observer1 = new MockObserver(emailServer);
        MockObserver observer2 = new MockObserver(emailServer2);
        
        notificationService.attach(observer1);
        notificationService.attach(observer2);
        
        notificationService.notifyObservers(user, "Test message");
        
        assertEquals(1, emailServer.getMessageCount());
        assertEquals(1, emailServer2.getMessageCount());
    }
    
    @Test
    @DisplayName("Observer Pattern: Multiple observers can be attached")
    public void testMultipleObservers() {
        MockEmailServer emailServer2 = new MockEmailServer();
        MockEmailServer emailServer3 = new MockEmailServer();
        
        MockObserver observer1 = new MockObserver(emailServer);
        MockObserver observer2 = new MockObserver(emailServer2);
        MockObserver observer3 = new MockObserver(emailServer3);
        
        notificationService.attach(observer1);
        notificationService.attach(observer2);
        notificationService.attach(observer3);
        
        notificationService.notifyObservers(user, "Multi-observer message");
        
        assertEquals(1, emailServer.getMessageCount());
        assertEquals(1, emailServer2.getMessageCount());
        assertEquals(1, emailServer3.getMessageCount());
    }
    
    @Test
    @DisplayName("Observer Pattern: Constructor with initial observer")
    public void testConstructorWithInitialObserver() {
        MockObserver emailObserver = new MockObserver(emailServer);
        NotificationService service = new NotificationService(overdueDetectionService, emailObserver);
        
        service.notifyObservers(user, "Message");
        assertEquals(1, emailServer.getMessageCount());
    }
    
    /**
     * Mock observer that wraps MockEmailServer and implements Observer interface.
     */
    private static class MockObserver implements Observer {
        private MockEmailServer emailServer;
        private List<String> notifications = new ArrayList<>();
        
        public MockObserver(MockEmailServer emailServer) {
            this.emailServer = emailServer;
        }
        
        @Override
        public boolean notify(User user, String message) {
            if (emailServer != null) {
                emailServer.notify(user, message);
            }
            notifications.add(message);
            return true;
        }
        
        @Override
        public String getObserverType() {
            return "Email";
        }
        
        public List<String> getNotifications() {
            return notifications;
        }
    }
    
    @Test
    @DisplayName("Observer Pattern: Supports different observer types (mock SMS)")
    public void testDifferentObserverTypes() {
        MockObserver smsObserver = new MockObserver(null) {
            @Override
            public String getObserverType() {
                return "SMS";
            }
        };
        
        MockObserver pushObserver = new MockObserver(null) {
            @Override
            public String getObserverType() {
                return "Push";
            }
        };
        
        MockObserver emailObserver = new MockObserver(emailServer);
        
        notificationService.attach(emailObserver);
        notificationService.attach(smsObserver);
        notificationService.attach(pushObserver);
        
        notificationService.notifyObservers(user, "Multi-channel message");
        
        assertEquals(1, emailServer.getMessageCount());
        assertEquals(1, smsObserver.getNotifications().size());
        assertEquals(1, pushObserver.getNotifications().size());
        assertEquals("Multi-channel message", smsObserver.getNotifications().get(0));
    }
}
