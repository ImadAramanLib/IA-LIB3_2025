package edu.najah.library.domain;

import java.util.Objects;

/**
 * Represents a book in the library management system.
 * Each book has a title, author, ISBN, and availability status.
 * 
 * @author Imad Araman, Hamza Abuobaid
 * @version 1.0
 */
public class Book {
    
    private String title;
    private String author;
    private String isbn;
    private boolean isAvailable;
    
    /**
     * Default constructor.
     */
    public Book() {
        this.isAvailable = true; // New books are available by default
    }
    
    /**
     * Constructs a Book with specified details.
     * Book is available by default.
     * 
     * @param title the book's title
     * @param author the book's author
     * @param isbn the book's ISBN
     */
    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = true;
    }
    
    /**
     * Constructs a Book with specified details and availability.
     * 
     * @param title the book's title
     * @param author the book's author
     * @param isbn the book's ISBN
     * @param isAvailable the book's availability status
     */
    public Book(String title, String author, String isbn, boolean isAvailable) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = isAvailable;
    }
    
    /**
     * Gets the book's title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the book's title.
     * 
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Gets the book's author.
     * 
     * @return the author
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * Sets the book's author.
     * 
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }
    
    /**
     * Gets the book's ISBN.
     * 
     * @return the ISBN
     */
    public String getIsbn() {
        return isbn;
    }
    
    /**
     * Sets the book's ISBN.
     * 
     * @param isbn the ISBN to set
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    /**
     * Checks if the book is available for borrowing.
     * 
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return isAvailable;
    }
    
    /**
     * Sets the book's availability status.
     * 
     * @param available the availability status to set
     */
    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
    
    /**
     * Compares this book with another object for equality.
     * Two books are equal if they have the same ISBN.
     * 
     * @param obj the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return Objects.equals(isbn, book.isbn);
    }
    
    /**
     * Generates a hash code for this book.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
    
    /**
     * Returns a string representation of this book.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
