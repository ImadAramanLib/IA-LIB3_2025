package edu.najah.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Fine Strategy Pattern implementations.
 * Tests different fine calculation strategies for books, CDs, and journals.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class FineStrategyTest {
    
    @Test
    @DisplayName("BookFineStrategy: calculates fine correctly (10 NIS per day)")
    void testBookFineStrategy() {
        FineStrategy strategy = new BookFineStrategy();
        
        assertEquals(10, strategy.getFineRatePerDay());
        assertEquals(0, strategy.calculateFine(0));
        assertEquals(10, strategy.calculateFine(1));
        assertEquals(50, strategy.calculateFine(5));
        assertEquals(100, strategy.calculateFine(10));
    }
    
    @Test
    @DisplayName("CDFineStrategy: calculates fine correctly (20 NIS per day)")
    void testCDFineStrategy() {
        FineStrategy strategy = new CDFineStrategy();
        
        assertEquals(20, strategy.getFineRatePerDay());
        assertEquals(0, strategy.calculateFine(0));
        assertEquals(20, strategy.calculateFine(1));
        assertEquals(100, strategy.calculateFine(5));
        assertEquals(200, strategy.calculateFine(10));
    }
    
    @Test
    @DisplayName("JournalFineStrategy: calculates fine correctly (15 NIS per day)")
    void testJournalFineStrategy() {
        FineStrategy strategy = new JournalFineStrategy();
        
        assertEquals(15, strategy.getFineRatePerDay());
        assertEquals(0, strategy.calculateFine(0));
        assertEquals(15, strategy.calculateFine(1));
        assertEquals(75, strategy.calculateFine(5));
        assertEquals(150, strategy.calculateFine(10));
    }
    
    @Test
    @DisplayName("FineStrategyFactory: returns correct strategy for each item type")
    void testFineStrategyFactory() {
        FineStrategy bookStrategy = FineStrategyFactory.getStrategy(FineStrategyFactory.ItemType.BOOK);
        FineStrategy cdStrategy = FineStrategyFactory.getStrategy(FineStrategyFactory.ItemType.CD);
        FineStrategy journalStrategy = FineStrategyFactory.getStrategy(FineStrategyFactory.ItemType.JOURNAL);
        
        assertTrue(bookStrategy instanceof BookFineStrategy);
        assertTrue(cdStrategy instanceof CDFineStrategy);
        assertTrue(journalStrategy instanceof JournalFineStrategy);
        
        assertEquals(10, bookStrategy.getFineRatePerDay());
        assertEquals(20, cdStrategy.getFineRatePerDay());
        assertEquals(15, journalStrategy.getFineRatePerDay());
    }
    
    @Test
    @DisplayName("FineStrategyFactory: throws exception for null item type")
    void testFineStrategyFactoryNullType() {
        assertThrows(IllegalArgumentException.class, 
            () -> FineStrategyFactory.getStrategy(null));
    }
    
    @Test
    @DisplayName("FineStrategyFactory: getDefaultStrategy returns BookFineStrategy")
    void testFineStrategyFactoryDefault() {
        FineStrategy defaultStrategy = FineStrategyFactory.getDefaultStrategy();
        
        assertTrue(defaultStrategy instanceof BookFineStrategy);
        assertEquals(10, defaultStrategy.getFineRatePerDay());
    }
    
    @Test
    @DisplayName("Different strategies calculate different fines for same days")
    void testStrategyDifferences() {
        FineStrategy bookStrategy = new BookFineStrategy();
        FineStrategy cdStrategy = new CDFineStrategy();
        FineStrategy journalStrategy = new JournalFineStrategy();
        
        int daysOverdue = 5;
        
        int bookFine = bookStrategy.calculateFine(daysOverdue);
        int cdFine = cdStrategy.calculateFine(daysOverdue);
        int journalFine = journalStrategy.calculateFine(daysOverdue);
        
        assertEquals(50, bookFine);  // 5 * 10
        assertEquals(100, cdFine);    // 5 * 20
        assertEquals(75, journalFine); // 5 * 15
        
        assertTrue(cdFine > journalFine);
        assertTrue(journalFine > bookFine);
    }
}

