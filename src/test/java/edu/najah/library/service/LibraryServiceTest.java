package edu.najah.library.service;

import edu.najah.library.domain.Admin;
import edu.najah.library.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

    private AuthenticationService auth;
    private LibraryService library;

    @BeforeEach
    void setup() {
        auth = new AuthenticationService();
        auth.registerAdmin(new Admin("admin", "pass"));
        library = new LibraryService(auth);
    }

    @Test
    @DisplayName("addBook: requires logged-in admin and adds book to list")
    void testAddBook() {
        assertFalse(auth.isLoggedIn());
        auth.login("admin", "pass");
        Book b = new Book("Clean Code", "Robert C. Martin", "9780132350884");
        library.addBook(b);
        List<Book> all = library.getAllBooks();
        assertEquals(1, all.size());
        assertEquals(b, all.get(0));
    }

    @Test
    @DisplayName("addBook: duplicate ISBN is rejected")
    void testDuplicateIsbnRejected() {
        auth.login("admin", "pass");
        library.addBook(new Book("A", "X", "ISBN-1"));
        assertThrows(IllegalArgumentException.class,
                () -> library.addBook(new Book("B", "Y", "ISBN-1")));
    }

    @Test
    @DisplayName("addBook: throws when not logged in")
    void testAddBookRequiresLogin() {
        assertThrows(IllegalStateException.class,
                () -> library.addBook(new Book("A", "X", "I-1")));
    }

    @Test
    @DisplayName("searchByTitle: exact and partial matching")
    void testSearchByTitle() {
        auth.login("admin", "pass");
        library.addBook(new Book("Clean Code", "Robert C. Martin", "1"));
        library.addBook(new Book("Refactoring", "Martin Fowler", "2"));
        library.addBook(new Book("The Clean Coder", "Robert C. Martin", "3"));

        assertEquals(1, library.searchByTitle("Refactoring").size());
        assertEquals(2, library.searchByTitle("clean").size());
        assertEquals(0, library.searchByTitle("unknown").size());
    }

    @Test
    @DisplayName("searchByAuthor: exact and partial matching")
    void testSearchByAuthor() {
        auth.login("admin", "pass");
        library.addBook(new Book("A", "Eric Evans", "10"));
        library.addBook(new Book("B", "Robert Martin", "11"));
        library.addBook(new Book("C", "Martin Fowler", "12"));

        assertEquals(1, library.searchByAuthor("Eric Evans").size());
        assertEquals(2, library.searchByAuthor("Martin").size());
        assertEquals(0, library.searchByAuthor("Unknown").size());
    }

    @Test
    @DisplayName("searchByISBN: returns book or null when not found")
    void testSearchByIsbn() {
        auth.login("admin", "pass");
        Book b = new Book("A", "X", "I-1");
        library.addBook(b);
        assertEquals(b, library.searchByISBN("I-1"));
        assertNull(library.searchByISBN("I-2"));
    }
}
