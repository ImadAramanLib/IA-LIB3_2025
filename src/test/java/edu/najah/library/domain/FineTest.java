package edu.najah.library.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Fine entity.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class FineTest {
    
    private Fine fine;
    private User user;
    
    @BeforeEach
    public void setUp() {
        user = new User("U001", "John Doe", "john@example.com");
        fine = new Fine(user, 5.0, LocalDate.of(2025, 1, 1));
    }
    
    @Test
    public void testFineCreation() {
        assertNotNull(fine);
        assertEquals(user, fine.getUser());
        assertEquals(5.0, fine.getAmount());
        assertFalse(fine.isPaid());
        assertEquals(LocalDate.of(2025, 1, 1), fine.getCreatedDate());
    }
    
    @Test
    public void testDefaultConstructor() {
        Fine defaultFine = new Fine();
        assertNotNull(defaultFine);
        assertFalse(defaultFine.isPaid());
    }
    
    @Test
    public void testPayFinePartial() {
        double remaining = fine.payFine(2.0);
        
        assertEquals(3.0, remaining);
        assertEquals(3.0, fine.getRemainingBalance());
        assertFalse(fine.isPaid());
    }
    
    @Test
    public void testPayFineFull() {
        double remaining = fine.payFine(5.0);
        
        assertEquals(0, remaining);
        assertEquals(0, fine.getRemainingBalance());
        assertTrue(fine.isPaid());
    }
    
    @Test
    public void testPayFineOver() {
        double remaining = fine.payFine(10.0);
        
        assertEquals(0, remaining);
        assertEquals(0, fine.getRemainingBalance());
        assertTrue(fine.isPaid());
    }
    
    @Test
    public void testPayFineNegative() {
        assertThrows(IllegalArgumentException.class, () -> fine.payFine(-1.0));
    }
    
    @Test
    public void testGetRemainingBalance() {
        assertEquals(5.0, fine.getRemainingBalance());
        fine.payFine(2.0);
        assertEquals(3.0, fine.getRemainingBalance());
    }
    
    @Test
    public void testSettersAndGetters() {
        User newUser = new User("U002", "Jane Doe", "jane@example.com");
        LocalDate newDate = LocalDate.of(2025, 2, 1);
        
        fine.setUser(newUser);
        fine.setAmount(10.0);
        fine.setCreatedDate(newDate);
        fine.setPaid(true);
        
        assertEquals(newUser, fine.getUser());
        assertEquals(10.0, fine.getAmount());
        assertEquals(newDate, fine.getCreatedDate());
        assertTrue(fine.isPaid());
    }
    
    @Test
    public void testCalculateFineAmount() {
        // $0.50 per day
        assertEquals(1.0, Fine.calculateFineAmount(2));
        assertEquals(2.5, Fine.calculateFineAmount(5));
        assertEquals(5.0, Fine.calculateFineAmount(10));
    }
    
    @Test
    public void testToString() {
        String toString = fine.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("Fine"));
    }
}
