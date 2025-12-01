package edu.najah.library.service;

/**
 * Strategy interface for calculating fines based on item type.
 * 
 * <p>Implements the Strategy Design Pattern to allow different fine calculation
 * rules for different library item types (books, CDs, journals) without modifying
 * the borrowing code.</p>
 * 
 * <p>Fine rates:
 * <ul>
 *   <li>Books: 10 NIS per day</li>
 *   <li>CDs: 20 NIS per day</li>
 *   <li>Journals: 15 NIS per day</li>
 * </ul>
 * 
 * @author Imad Araman
 * @version 1.0
 */
public interface FineStrategy {
    
    /**
     * Calculates the fine amount for a given number of overdue days.
     * 
     * @param overdueDays the number of days the item is overdue
     * @return the calculated fine amount in NIS
     */
    int calculateFine(int overdueDays);
    
    /**
     * Gets the fine rate per day for this strategy.
     * 
     * @return the fine rate per day in NIS
     */
    int getFineRatePerDay();
}

