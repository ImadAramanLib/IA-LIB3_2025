package edu.najah.library.domain;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Represents a book in the library management system.
 * Each book has a title, author, ISBN, availability status, and quantity.
 * The quantity field tracks how many copies of this book are available in the library.
 * 
 * <p>This entity is mapped to the "books" table in the database.</p>
 * 
 * @author Imad Araman
 * @version 1.0
 */
@Entity
@Table(name = "books", uniqueConstraints = {
    @UniqueConstraint(columnNames = "isbn")
})
public class Book implements LibraryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "author", nullable = false, length = 255)
    private String author;
    
    @Column(name = "isbn", nullable = false, unique = true, length = 50)
    private String isbn;
    
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    /**
     * Default constructor required by JPA.
     * Initializes book as available with quantity of 1.
     */
    public Book() {
        this.isAvailable = true; // New books are available by default
        this.quantity = 1; // Default quantity is 1
    }
    
    /**
     * Gets the book's database ID.
     * 
     * @return the book ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the book's database ID.
     * 
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Constructs a Book with specified details.
     * Book is available by default with quantity of 1.
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
        this.quantity = 1;
    }
    
    /**
     * Constructs a Book with specified details and availability.
     * Quantity defaults to 1.
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
        this.quantity = 1;
    }
    
    /**
     * Constructs a Book with specified details, availability, and quantity.
     * 
     * @param title the book's title
     * @param author the book's author
     * @param isbn the book's ISBN
     * @param isAvailable the book's availability status
     * @param quantity the number of copies available
     */
    public Book(String title, String author, String isbn, boolean isAvailable, int quantity) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = isAvailable;
        this.quantity = quantity;
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
     * A book is available if it is marked as available and has quantity greater than 0.
     * 
     * @return true if available and quantity > 0, false otherwise
     */
    public boolean isAvailable() {
        return isAvailable && quantity > 0;
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
     * Gets the quantity (number of copies) of this book.
     * 
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }
    
    /**
     * Sets the quantity (number of copies) of this book.
     * 
     * @param quantity the quantity to set (must be >= 0)
     */
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }
    
    /**
     * Increments the quantity by 1.
     * Used when adding a new copy of an existing book.
     */
    public void incrementQuantity() {
        this.quantity++;
    }
    
    /**
     * Decrements the quantity by 1.
     * Used when a copy is borrowed.
     * 
     * @throws IllegalStateException if quantity is already 0
     */
    public void decrementQuantity() {
        if (quantity <= 0) {
            throw new IllegalStateException("Cannot decrement quantity: already at 0");
        }
        this.quantity--;
    }
    
    /**
     * Gets the unique identifier for this book (LibraryItem interface).
     * 
     * @return the ISBN
     */
    @Override
    public String getUniqueIdentifier() {
        return isbn;
    }
    
    /**
     * Gets the loan period in days for books (LibraryItem interface).
     * Books have a 28-day loan period.
     * 
     * @return 28 days
     */
    @Override
    public int getLoanPeriodDays() {
        return 28;
    }
    
    /**
     * Gets the item type (LibraryItem interface).
     * 
     * @return ItemType.BOOK
     */
    @Override
    public LibraryItem.ItemType getItemType() {
        return LibraryItem.ItemType.BOOK;
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
                ", quantity=" + quantity +
                '}';
    }
}
