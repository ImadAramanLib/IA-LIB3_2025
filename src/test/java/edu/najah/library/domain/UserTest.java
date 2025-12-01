package edu.najah.library.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User entity.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class UserTest {
    
    private User user;
    
    @BeforeEach
    void setUp() {
        user = new User("U001", "John Doe", "john@example.com");
    }
    
    @Test
    void testUserCreation() {
        assertNotNull(user);
        assertEquals("U001", user.getUserId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
    }
    
    @Test
    void testDefaultConstructor() {
        User defaultUser = new User();
        assertNotNull(defaultUser);
    }
    
    @Test
    void testSettersAndGetters() {
        user.setUserId("U002");
        user.setName("Jane Smith");
        user.setEmail("jane@example.com");
        
        assertEquals("U002", user.getUserId());
        assertEquals("Jane Smith", user.getName());
        assertEquals("jane@example.com", user.getEmail());
    }
    
    @Test
    void testEquality() {
        User user2 = new User("U001", "Different Name", "different@example.com");
        
        // Users with same ID should be equal
        assertEquals(user, user2);
    }
    
    @Test
    void testInequality() {
        User user2 = new User("U002", "John Doe", "john@example.com");
        
        // Users with different IDs should not be equal
        assertNotEquals(user, user2);
    }
    
    @Test
    void testHashCode() {
        User user2 = new User("U001", "John Doe", "john@example.com");
        
        // Users with same ID should have same hash code
        assertEquals(user.hashCode(), user2.hashCode());
    }
    
    @Test
    void testToString() {
        String toString = user.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("U001"));
        assertTrue(toString.contains("John Doe"));
    }
}
