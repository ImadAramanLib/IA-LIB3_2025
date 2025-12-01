package edu.najah.library.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Admin entity.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class AdminTest {
    
    /**
     * Test creating an admin with constructor.
     */
    @Test
    void testAdminCreation() {
        Admin admin = new Admin("admin", "password123");
        
        assertEquals("admin", admin.getUsername());
        assertEquals("password123", admin.getPassword());
    }
    
    /**
     * Test admin setters.
     */
    @Test
    void testAdminSetters() {
        Admin admin = new Admin();
        admin.setUsername("newadmin");
        admin.setPassword("newpass");
        
        assertEquals("newadmin", admin.getUsername());
        assertEquals("newpass", admin.getPassword());
    }
    
    /**
     * Test admin equality based on username.
     */
    @Test
    void testAdminEquality() {
        Admin admin1 = new Admin("admin", "pass1");
        Admin admin2 = new Admin("admin", "pass2");
        Admin admin3 = new Admin("different", "pass1");
        
        assertEquals(admin1, admin2, "Admins with same username should be equal");
        assertNotEquals(admin1, admin3, "Admins with different usernames should not be equal");
    }
    
    /**
     * Test admin hash code consistency.
     */
    @Test
    void testAdminHashCode() {
        Admin admin1 = new Admin("admin", "pass");
        Admin admin2 = new Admin("admin", "differentpass");
        
        assertEquals(admin1.hashCode(), admin2.hashCode(), 
                "Admins with same username should have same hash code");
    }
    
    /**
     * Test admin toString method.
     */
    @Test
    void testAdminToString() {
        Admin admin = new Admin("admin", "password");
        String result = admin.toString();
        
        assertTrue(result.contains("admin"), "toString should contain username");
        assertFalse(result.contains("password"), "toString should not contain password");
    }
}
