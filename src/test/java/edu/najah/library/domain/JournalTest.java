package edu.najah.library.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Journal entity.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class JournalTest {
    
    /**
     * Test creating a Journal with constructor.
     */
    @Test
    public void testJournalCreation() {
        Journal journal = new Journal("Nature", "Springer", "0028-0836");
        
        assertEquals("Nature", journal.getTitle());
        assertEquals("Springer", journal.getPublisher());
        assertEquals("0028-0836", journal.getIssn());
        assertTrue(journal.isAvailable(), "New journals should be available by default");
    }
    
    /**
     * Test Journal creation with availability parameter.
     */
    @Test
    public void testJournalCreationWithAvailability() {
        Journal journal = new Journal("Science", "AAAS", "0036-8075", false);
        
        assertFalse(journal.isAvailable(), "Journal should be unavailable as specified");
    }
    
    /**
     * Test Journal setters.
     */
    @Test
    public void testJournalSetters() {
        Journal journal = new Journal();
        journal.setTitle("New Journal");
        journal.setPublisher("New Publisher");
        journal.setIssn("1234-5678");
        journal.setAvailable(false);
        
        assertEquals("New Journal", journal.getTitle());
        assertEquals("New Publisher", journal.getPublisher());
        assertEquals("1234-5678", journal.getIssn());
        assertFalse(journal.isAvailable());
    }
    
    /**
     * Test Journal equality based on ISSN.
     */
    @Test
    public void testJournalEquality() {
        Journal journal1 = new Journal("Title1", "Publisher1", "1234-5678");
        Journal journal2 = new Journal("Title2", "Publisher2", "1234-5678");
        Journal journal3 = new Journal("Title1", "Publisher1", "8765-4321");
        
        assertEquals(journal1, journal2, "Journals with same ISSN should be equal");
        assertNotEquals(journal1, journal3, "Journals with different ISSN should not be equal");
    }
    
    /**
     * Test Journal hash code consistency.
     */
    @Test
    public void testJournalHashCode() {
        Journal journal1 = new Journal("Title1", "Publisher1", "1234-5678");
        Journal journal2 = new Journal("Different Title", "Different Publisher", "1234-5678");
        
        assertEquals(journal1.hashCode(), journal2.hashCode(), 
                "Journals with same ISSN should have same hash code");
    }
    
    /**
     * Test Journal toString method.
     */
    @Test
    public void testJournalToString() {
        Journal journal = new Journal("Nature", "Springer", "0028-0836");
        String result = journal.toString();
        
        assertTrue(result.contains("Nature"), "toString should contain title");
        assertTrue(result.contains("Springer"), "toString should contain publisher");
        assertTrue(result.contains("0028-0836"), "toString should contain ISSN");
    }
    
    /**
     * Test default constructor creates available Journal.
     */
    @Test
    public void testDefaultConstructor() {
        Journal journal = new Journal();
        assertTrue(journal.isAvailable(), "Default constructor should create available journal");
    }
}

