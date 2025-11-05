package edu.najah.library.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an overdue fine for a user.
 * Tracks the fine amount, payment status, and creation date.
 * 
 * @author Imad Araman, Hamza Abuobaid
 * @version 1.0
 */
public class Fine {
    
    private User user;
    private double amount;
    private boolean isPaid;
    private LocalDate createdDate;
    
    private static final double FINE_PER_DAY = 0.5; // $0.50 per day overdue
    
    /**
     * Default constructor.
     */
    public Fine() {
        this.isPaid = false;
    }
    
    /**
     * Constructs a Fine with user, amount, and creation date.
     * 
     * @param user the user owing the fine
     * @param amount the fine amount
     * @param createdDate the date the fine was created
     */
    public Fine(User user, double amount, LocalDate createdDate) {
        this.user = user;
        this.amount = amount;
        this.createdDate = createdDate;
        this.isPaid = false;
    }
    
    /**
     * Gets the user owing this fine.
     * 
     * @return the user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Sets the user owing this fine.
     * 
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * Gets the fine amount.
     * 
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }
    
    /**
     * Sets the fine amount.
     * 
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    /**
     * Checks if the fine is paid.
     * 
     * @return true if paid, false otherwise
     */
    public boolean isPaid() {
        return isPaid;
    }
    
    /**
     * Sets the payment status of the fine.
     * 
     * @param paid the payment status to set
     */
    public void setPaid(boolean paid) {
        isPaid = paid;
    }
    
    /**
     * Gets the creation date of the fine.
     * 
     * @return the creation date
     */
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    /**
     * Sets the creation date of the fine.
     * 
     * @param createdDate the creation date to set
     */
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    /**
     * Pays a portion or the full amount of the fine.
     * 
     * @param paymentAmount the amount to pay
     * @return the remaining balance
     */
    public double payFine(double paymentAmount) {
        if (paymentAmount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative");
        }
        
        amount -= paymentAmount;
        
        if (amount <= 0) {
            amount = 0;
            isPaid = true;
        }
        
        return amount;
    }
    
    /**
     * Gets the remaining balance of the fine.
     * 
     * @return the remaining balance
     */
    public double getRemainingBalance() {
        return amount;
    }
    
    /**
     * Calculates fine amount for a number of overdue days.
     * 
     * @param daysOverdue the number of days overdue
     * @return the calculated fine amount
     */
    public static double calculateFineAmount(long daysOverdue) {
        return daysOverdue * FINE_PER_DAY;
    }
    
    /**
     * Compares this fine with another object for equality.
     * 
     * @param obj the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fine fine = (Fine) obj;
        return Objects.equals(user, fine.user) &&
               Objects.equals(createdDate, fine.createdDate);
    }
    
    /**
     * Generates a hash code for this fine.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(user, createdDate);
    }
    
    /**
     * Returns a string representation of this fine.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return "Fine{" +
                "user=" + user +
                ", amount=" + amount +
                ", isPaid=" + isPaid +
                ", createdDate=" + createdDate +
                '}';
    }
}
