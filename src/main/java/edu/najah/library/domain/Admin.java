package edu.najah.library.domain;

import java.util.Objects;
import jakarta.persistence.*;

/**
 * Represents an administrator in the library management system.
 * Administrators can log in to manage books and users.
 * 
 * @author Imad Araman
 * @version 1.0
 */
@Entity
@Table(name = "admins")
public class Admin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column
    private String email;
    
    /**
     * Default constructor.
     */
    public Admin() {
    }
    
    /**
     * Constructs an Admin with specified username and password.
     * 
     * @param username the administrator's username
     * @param password the administrator's password
     */
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * Gets the administrator's ID.
     * 
     * @return the id
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the administrator's ID.
     * 
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the administrator's username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the administrator's username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the administrator's password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the administrator's password.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Gets the administrator's email.
     * 
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the administrator's email.
     * 
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Compares this admin with another object for equality.
     * Two admins are equal if they have the same username.
     * 
     * @param obj the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Admin admin = (Admin) obj;
        return Objects.equals(username, admin.username);
    }
    
    /**
     * Generates a hash code for this admin.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
    
    /**
     * Returns a string representation of this admin.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return "Admin{username='" + username + "'}";
    }
}
