package edu.najah.library.domain;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Represents a library user/patron.
 * Each user has a unique ID, name, and email.
 * 
 * <p>This entity is mapped to the "users" table in the database.</p>
 * 
 * @author Imad Araman
 * @version 1.0
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "userId")
})
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "email", nullable = false, length = 255)
    private String email;
    
    @Column(name = "username", unique = true, length = 50)
    private String username;
    
    @Column(name = "password", length = 255)
    private String password;
    
    /**
     * Default constructor required by JPA.
     */
    public User() {
    }
    
    /**
     * Constructs a User with specified details.
     * 
     * @param userId the user's unique identifier
     * @param name the user's name
     * @param email the user's email
     */
    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
    
    /**
     * Constructs a User with specified details including username and password.
     * 
     * @param userId the user's unique identifier
     * @param username the user's username for login
     * @param password the user's password
     * @param name the user's name
     * @param email the user's email
     */
    public User(String userId, String username, String password, String name, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }
    
    /**
     * Gets the user's database ID.
     * 
     * @return the database ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the user's database ID.
     * 
     * @param id the database ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the user's ID.
     * 
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * Sets the user's ID.
     * 
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the user's name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the user's name.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the user's email.
     * 
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the user's email.
     * 
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the user's username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the user's username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the user's password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the user's password.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Compares this user with another object for equality.
     * Two users are equal if they have the same user ID.
     * 
     * @param obj the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(userId, user.userId);
    }
    
    /**
     * Generates a hash code for this user.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    /**
     * Returns a string representation of this user.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
