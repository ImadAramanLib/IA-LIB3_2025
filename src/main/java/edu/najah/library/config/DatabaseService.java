package edu.najah.library.config;

import edu.najah.library.domain.Admin;
import edu.najah.library.domain.Book;
import edu.najah.library.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * Service for database operations (CRUD) for the Library Management System.
 * Handles persistence of books, users, and admins to Neon PostgreSQL database.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class DatabaseService {
    
    private EntityManager entityManager;
    
    /**
     * Constructs a DatabaseService with the given EntityManager.
     * 
     * @param entityManager the EntityManager to use for database operations
     */
    public DatabaseService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    // ========== Book Operations ==========
    
    /**
     * Saves a book to the database.
     * 
     * @param book the book to save
     * @return true if saved successfully, false otherwise
     */
    public boolean saveBook(Book book) {
        if (book == null) {
            return false;
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(book);
            entityManager.flush();
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Loads all books from the database.
     * 
     * @return list of all books
     */
    public List<Book> loadAllBooks() {
        try {
            TypedQuery<Book> query = entityManager.createQuery("SELECT b FROM Book b", Book.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Finds a book by ISBN.
     * 
     * @param isbn the ISBN to search for
     * @return the book if found, null otherwise
     */
    public Book findBookByISBN(String isbn) {
        try {
            TypedQuery<Book> query = entityManager.createQuery(
                "SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
            query.setParameter("isbn", isbn);
            List<Book> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Updates a book in the database.
     * 
     * @param book the book to update
     * @return true if updated successfully, false otherwise
     */
    public boolean updateBook(Book book) {
        if (book == null) {
            return false;
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(book);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    // ========== User Operations ==========
    
    /**
     * Saves a user to the database.
     * 
     * @param user the user to save
     * @return true if saved successfully, false otherwise
     */
    public boolean saveUser(User user) {
        if (user == null) {
            return false;
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
            entityManager.flush();
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Loads all users from the database.
     * 
     * @return list of all users
     */
    public List<User> loadAllUsers() {
        try {
            TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Finds a user by user ID.
     * 
     * @param userId the user ID to search for
     * @return the user if found, null otherwise
     */
    public User findUserById(String userId) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.userId = :userId", User.class);
            query.setParameter("userId", userId);
            List<User> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return the user if found, null otherwise
     */
    public User findUserByUsername(String username) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            List<User> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Deletes a user from the database.
     * 
     * @param user the user to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteUser(User user) {
        if (user == null) {
            return false;
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            // Find user by userId
            User managedUser = findUserById(user.getUserId());
            if (managedUser != null && managedUser.getId() != null) {
                // Use entity manager to remove by ID
                User toRemove = entityManager.find(User.class, managedUser.getId());
                if (toRemove != null) {
                    entityManager.remove(toRemove);
                }
            } else if (managedUser != null) {
                // Fallback: use JPQL delete
                entityManager.createQuery(
                    "DELETE FROM User u WHERE u.userId = :userId")
                    .setParameter("userId", user.getUserId())
                    .executeUpdate();
            }
            transaction.commit();
            return managedUser != null;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    // ========== Admin Operations ==========
    
    /**
     * Finds an admin by username.
     * 
     * @param username the username to search for
     * @return the admin if found, null otherwise
     */
    public Admin findAdminByUsername(String username) {
        try {
            TypedQuery<Admin> query = entityManager.createQuery(
                "SELECT a FROM Admin a WHERE a.username = :username", Admin.class);
            query.setParameter("username", username);
            List<Admin> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Loads all admins from the database.
     * 
     * @return list of all admins
     */
    public List<Admin> loadAllAdmins() {
        try {
            TypedQuery<Admin> query = entityManager.createQuery("SELECT a FROM Admin a", Admin.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Checks if a username belongs to an admin.
     * 
     * @param username the username to check
     * @return true if username is an admin, false otherwise
     */
    public boolean isAdmin(String username) {
        return findAdminByUsername(username) != null;
    }
    
    /**
     * Checks if a username belongs to a user.
     * 
     * @param username the username (user ID) to check
     * @return true if username is a user, false otherwise
     */
    public boolean isUser(String username) {
        return findUserById(username) != null;
    }
    
    /**
     * Closes the EntityManager.
     */
    public void close() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}

