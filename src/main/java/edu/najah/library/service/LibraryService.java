package edu.najah.library.service;

import edu.najah.library.domain.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Service providing basic book management and search capabilities.
 * <p>Responsibilities:
 * - Maintain an in-memory list of books
 * - Allow admins to add books (requires login via AuthenticationService)
 * - Search books by title, author (partial matching), and ISBN
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class LibraryService {

    private final List<Book> bookList = new ArrayList<>();
    private final AuthenticationService authenticationService;

    /**
     * Creates a LibraryService with its own AuthenticationService instance.
     */
    public LibraryService() {
        this(new AuthenticationService());
    }

    /**
     * Creates a LibraryService using the provided AuthenticationService.
     *
     * @param authenticationService the authentication service to use
     */
    public LibraryService(AuthenticationService authenticationService) {
        this.authenticationService = Objects.requireNonNull(authenticationService);
    }

    /**
     * Adds a new book to the library. Requires an admin to be logged in.
     * Rejects duplicate ISBNs.
     *
     * @param book the book to add
     * @throws IllegalStateException    if no admin is logged in
     * @throws IllegalArgumentException if the book is null or ISBN is duplicate
     */
    public void addBook(Book book) {
        if (!authenticationService.isLoggedIn()) {
            throw new IllegalStateException("Admin must be logged in to add books.");
        }
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        String isbn = book.getIsbn();
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("Book ISBN cannot be null or blank");
        }
        boolean duplicate = bookList.stream()
                .anyMatch(b -> isbn.equals(b.getIsbn()));
        if (duplicate) {
            throw new IllegalArgumentException("Duplicate ISBN: " + isbn);
        }
        bookList.add(book);
    }

    /**
     * Searches books by title using case-insensitive partial matching.
     *
     * @param title the title or fragment to search for
     * @return list of matching books (possibly empty)
     */
    public List<Book> searchByTitle(String title) {
        if (title == null) return Collections.emptyList();
        String q = title.toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<>();
        for (Book b : bookList) {
            if (b.getTitle() != null && b.getTitle().toLowerCase(Locale.ROOT).contains(q)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Searches books by author using case-insensitive partial matching.
     *
     * @param author the author name or fragment to search for
     * @return list of matching books (possibly empty)
     */
    public List<Book> searchByAuthor(String author) {
        if (author == null) return Collections.emptyList();
        String q = author.toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<>();
        for (Book b : bookList) {
            if (b.getAuthor() != null && b.getAuthor().toLowerCase(Locale.ROOT).contains(q)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Finds a book by exact ISBN.
     *
     * @param isbn the ISBN
     * @return the matching book or null if not found
     */
    public Book searchByISBN(String isbn) {
        if (isbn == null) return null;
        for (Book b : bookList) {
            if (isbn.equals(b.getIsbn())) {
                return b;
            }
        }
        return null;
    }

    /**
     * Returns a copy of all books in the library.
     *
     * @return list of all books
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookList);
    }
    
    /**
     * Adds a book directly to the list without authentication check.
     * Used for loading books from database on startup.
     * 
     * @param book the book to add
     * @return true if added, false if duplicate
     */
    public boolean addBookDirectly(Book book) {
        if (book == null || book.getIsbn() == null || book.getIsbn().isBlank()) {
            return false;
        }
        boolean duplicate = bookList.stream()
                .anyMatch(b -> book.getIsbn().equals(b.getIsbn()));
        if (duplicate) {
            return false;
        }
        bookList.add(book);
        return true;
    }
}
