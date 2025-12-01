package edu.najah.library.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CD entity.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class CDTest {
    
    /**
     * Test creating a CD with constructor.
     */
    @Test
    void testCDCreation() {
        CD cd = new CD("Greatest Hits", "The Beatles", "CD001");
        
        assertEquals("Greatest Hits", cd.getTitle());
        assertEquals("The Beatles", cd.getArtist());
        assertEquals("CD001", cd.getCatalogNumber());
        assertTrue(cd.isAvailable(), "New CDs should be available by default");
    }
    
    /**
     * Test CD creation with availability parameter.
     */
    @Test
    void testCDCreationWithAvailability() {
        CD cd = new CD("Album", "Artist", "CD002", false);
        
        assertFalse(cd.isAvailable(), "CD should be unavailable as specified");
    }
    
    /**
     * Test CD setters.
     */
    @Test
    void testCDSetters() {
        CD cd = new CD();
        cd.setTitle("New Album");
        cd.setArtist("New Artist");
        cd.setCatalogNumber("CD003");
        cd.setAvailable(false);
        
        assertEquals("New Album", cd.getTitle());
        assertEquals("New Artist", cd.getArtist());
        assertEquals("CD003", cd.getCatalogNumber());
        assertFalse(cd.isAvailable());
    }
    
    /**
     * Test CD equality based on catalog number.
     */
    @Test
    void testCDEquality() {
        CD cd1 = new CD("Title1", "Artist1", "CD001");
        CD cd2 = new CD("Title2", "Artist2", "CD001");
        CD cd3 = new CD("Title1", "Artist1", "CD002");
        
        assertEquals(cd1, cd2, "CDs with same catalog number should be equal");
        assertNotEquals(cd1, cd3, "CDs with different catalog numbers should not be equal");
    }
    
    /**
     * Test CD hash code consistency.
     */
    @Test
    void testCDHashCode() {
        CD cd1 = new CD("Title1", "Artist1", "CD001");
        CD cd2 = new CD("Different Title", "Different Artist", "CD001");
        
        assertEquals(cd1.hashCode(), cd2.hashCode(), 
                "CDs with same catalog number should have same hash code");
    }
    
    /**
     * Test CD toString method.
     */
    @Test
    void testCDToString() {
        CD cd = new CD("Greatest Hits", "The Beatles", "CD001");
        String result = cd.toString();
        
        assertTrue(result.contains("Greatest Hits"), "toString should contain title");
        assertTrue(result.contains("The Beatles"), "toString should contain artist");
        assertTrue(result.contains("CD001"), "toString should contain catalog number");
    }
    
    /**
     * Test default constructor creates available CD.
     */
    @Test
    void testDefaultConstructor() {
        CD cd = new CD();
        assertTrue(cd.isAvailable(), "Default constructor should create available CD");
    }
    
    /**
     * Test CD implements LibraryItem interface.
     */
    @Test
    void testCDImplementsLibraryItem() {
        CD cd = new CD("Test CD", "Test Artist", "CD001");
        
        assertTrue(cd instanceof LibraryItem, "CD should implement LibraryItem");
        assertEquals("CD001", cd.getUniqueIdentifier());
        assertEquals(7, cd.getLoanPeriodDays(), "CDs should have 7-day loan period");
        assertEquals(LibraryItem.ItemType.CD, cd.getItemType());
    }
    
    /**
     * Test CD loan period is 7 days (US5.1).
     */
    @Test
    void testCDLoanPeriodIs7Days() {
        CD cd = new CD("Test CD", "Test Artist", "CD001");
        
        assertEquals(7, cd.getLoanPeriodDays(), "US5.1: CDs should be borrowed for 7 days");
    }
    
    /**
     * Test CD unique identifier is catalog number.
     */
    @Test
    void testCDUniqueIdentifier() {
        CD cd = new CD("Test CD", "Test Artist", "CD123");
        
        assertEquals("CD123", cd.getUniqueIdentifier());
    }
}

