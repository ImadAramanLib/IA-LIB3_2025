package edu.najah.library.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a book loan/borrowing record.
 * Tracks when a book was borrowed, when it's due, and when it was returned.
 * 
 * @author Imad Araman, Hamza Abuobaid
 * @version 1.0
 */
public class Loan {
    
    private Book book;
    private User user;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    
    private static final int BORROW_PERIOD_DAYS = 28;
    
    /**
     * Default constructor.
     */
    public Loan() {
    }
    
    /**
     * Constructs a Loan with book, user, and borrow date.
     * Due date is calculated as borrow date + 28 days.
     * 
     * @param book the borrowed book
     * @param user the user borrowing the book
     * @param borrowDate the date of borrowing
     */
    public Loan(Book book, User user, LocalDate borrowDate) {
        this.book = book;
        this.user = user;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(BORROW_PERIOD_DAYS);
        this.returnDate = null;
    }
    
    /**
     * Gets the borrowed book.
     * 
     * @return the book
     */
    public Book getBook() {
        return book;
    }
    
    /**
     * Sets the borrowed book.
     * 
     * @param book the book to set
     */
    public void setBook(Book book) {
        this.book = book;
    }
    
    /**
     * Gets the user.
     * 
     * @return the user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Sets the user.
     * 
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * Gets the borrow date.
     * 
     * @return the borrow date
     */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    /**
     * Sets the borrow date.
     * 
     * @param borrowDate the borrow date to set
     */
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
        if (this.dueDate == null) {
            this.dueDate = borrowDate.plusDays(BORROW_PERIOD_DAYS);
        }
    }
    
    /**
     * Gets the due date.
     * 
     * @return the due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    /**
     * Sets the due date.
     * 
     * @param dueDate the due date to set
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    /**
     * Gets the return date.
     * 
     * @return the return date, or null if not yet returned
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    /**
     * Sets the return date.
     * 
     * @param returnDate the return date to set
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    /**
     * Checks if this loan is overdue as of a given date.
     * A loan is overdue if current date is after due date and book hasn't been returned.
     * 
     * @param currentDate the date to check against
     * @return true if overdue, false otherwise
     */
    public boolean isOverdue(LocalDate currentDate) {
        return returnDate == null && currentDate.isAfter(dueDate);
    }
    
    /**
     * Calculates the number of days overdue as of a given date.
     * 
     * @param currentDate the date to check against
     * @return number of days overdue, or 0 if not overdue
     */
    public long getDaysOverdue(LocalDate currentDate) {
        if (!isOverdue(currentDate)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, currentDate);
    }
    
    /**
     * Checks if the book has been returned.
     * 
     * @return true if returned, false otherwise
     */
    public boolean isReturned() {
        return returnDate != null;
    }
    
    /**
     * Compares this loan with another object for equality.
     * 
     * @param obj the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Loan loan = (Loan) obj;
        return Objects.equals(book, loan.book) &&
               Objects.equals(user, loan.user) &&
               Objects.equals(borrowDate, loan.borrowDate);
    }
    
    /**
     * Generates a hash code for this loan.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(book, user, borrowDate);
    }
    
    /**
     * Returns a string representation of this loan.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return "Loan{" +
                "book=" + book +
                ", user=" + user +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
