package edu.najah.library.service;

import edu.najah.library.domain.Loan;
import edu.najah.library.domain.User;
import edu.najah.library.domain.Fine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for detecting overdue loans and calculating fines.
 * Identifies loans that exceed the 28-day borrowing period.
 * 
 * @author Imad Araman, Hamza Abuobaid
 * @version 1.0
 */
public class OverdueDetectionService {
    
    private BorrowingService borrowingService;
    
    /**
     * Constructs an OverdueDetectionService with a reference to BorrowingService.
     * 
     * @param borrowingService the borrowing service to query loans
     */
    public OverdueDetectionService(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }
    
    /**
     * Gets all overdue loans as of a given date.
     * 
     * @param currentDate the date to check against
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans(LocalDate currentDate) {
        if (borrowingService == null || currentDate == null) {
            return new ArrayList<>();
        }
        
        return borrowingService.getLoans().stream()
                .filter(loan -> loan.isOverdue(currentDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets overdue loans for a specific user.
     * 
     * @param user the user
     * @param currentDate the date to check against
     * @return list of overdue loans for the user
     */
    public List<Loan> getOverdueLoansForUser(User user, LocalDate currentDate) {
        if (user == null || currentDate == null) {
            return new ArrayList<>();
        }
        
        return getOverdueLoans(currentDate).stream()
                .filter(loan -> user.equals(loan.getUser()))
                .collect(Collectors.toList());
    }
    
    /**
     * Calculates the fine amount for an overdue loan.
     * Fine is $0.50 per day overdue.
     * 
     * @param loan the loan
     * @param currentDate the date to calculate fine as of
     * @return the fine amount, or 0 if loan is not overdue
     */
    public double calculateOverdueFine(Loan loan, LocalDate currentDate) {
        if (loan == null || currentDate == null) {
            return 0;
        }
        
        long daysOverdue = loan.getDaysOverdue(currentDate);
        
        if (daysOverdue <= 0) {
            return 0;
        }
        
        return Fine.calculateFineAmount(daysOverdue);
    }
    
    /**
     * Gets all overdue loans as of today.
     * 
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        return getOverdueLoans(LocalDate.now());
    }
    
    /**
     * Gets overdue loans for a user as of today.
     * 
     * @param user the user
     * @return list of overdue loans for the user
     */
    public List<Loan> getOverdueLoansForUser(User user) {
        return getOverdueLoansForUser(user, LocalDate.now());
    }
    
    /**
     * Checks if a loan is overdue as of a given date.
     * 
     * @param loan the loan
     * @param currentDate the date to check against
     * @return true if the loan is overdue, false otherwise
     */
    public boolean isOverdue(Loan loan, LocalDate currentDate) {
        if (loan == null || currentDate == null) {
            return false;
        }
        
        return loan.isOverdue(currentDate);
    }
    
    /**
     * Checks if a loan is overdue as of today.
     * 
     * @param loan the loan
     * @return true if the loan is overdue, false otherwise
     */
    public boolean isOverdue(Loan loan) {
        return isOverdue(loan, LocalDate.now());
    }
    
    /**
     * Gets the number of days overdue for a loan.
     * 
     * @param loan the loan
     * @param currentDate the date to check against
     * @return number of days overdue, or 0 if not overdue
     */
    public long getDaysOverdue(Loan loan, LocalDate currentDate) {
        if (loan == null || currentDate == null) {
            return 0;
        }
        
        return loan.getDaysOverdue(currentDate);
    }
    
    /**
     * Gets the number of days overdue for a loan as of today.
     * 
     * @param loan the loan
     * @return number of days overdue, or 0 if not overdue
     */
    public long getDaysOverdue(Loan loan) {
        return getDaysOverdue(loan, LocalDate.now());
    }
}
