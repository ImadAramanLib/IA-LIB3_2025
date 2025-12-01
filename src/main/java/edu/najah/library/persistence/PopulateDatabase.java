package edu.najah.library.persistence;

import edu.najah.library.domain.Admin;
import edu.najah.library.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Script to populate the database with initial admin and users.
 * 
 * <p>Adds:
 * <ul>
 *   <li>Admin: ImadAR / ImadImad119</li>
 *   <li>5 randomly generated users</li>
 * </ul>
 * 
 * <p>To run:
 * <ol>
 *   <li>Set environment variables: NEON_DB_URL, NEON_DB_USER, NEON_DB_PASSWORD</li>
 *   <li>Run: mvn exec:java -Dexec.mainClass="edu.najah.library.persistence.PopulateDatabase"</li>
 * </ol>
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class PopulateDatabase {
    
    private static final String[] FIRST_NAMES = {
        "Emma", "Liam", "Olivia", "Noah", "Ava", "Ethan", "Sophia", "Mason",
        "Isabella", "James", "Mia", "Benjamin", "Charlotte", "Lucas", "Amelia",
        "Henry", "Harper", "Alexander", "Evelyn", "Michael", "Abigail", "Daniel",
        "Emily", "Matthew", "Elizabeth", "Aiden", "Sofia", "Joseph", "Avery",
        "David", "Ella", "Jackson", "Madison", "Logan", "Scarlett", "Samuel"
    };
    
    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
        "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Wilson",
        "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee",
        "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis",
        "Robinson", "Walker", "Young", "Allen", "King", "Wright", "Scott",
        "Torres", "Nguyen", "Hill", "Flores", "Green", "Adams"
    };
    
    public static void main(String[] args) {
        System.out.println("=== Populating Database ===");
        System.out.println();
        
        EntityManager em = DatabaseConfig.createEntityManager();
        
        if (em == null) {
            System.err.println("ERROR: Could not create EntityManager.");
            System.err.println("Please check your environment variables:");
            System.err.println("  - NEON_DB_URL");
            System.err.println("  - NEON_DB_USER");
            System.err.println("  - NEON_DB_PASSWORD");
            return;
        }
        
        System.out.println("✓ Successfully connected to database!");
        System.out.println();
        
        EntityTransaction transaction = em.getTransaction();
        Random random = new Random();
        
        try {
            transaction.begin();
            System.out.println("Transaction started...");
            System.out.println();
            
            // Add Admin
            System.out.println("Adding Admin...");
            Admin admin = new Admin("ImadAR", "ImadImad119");
            em.persist(admin);
            System.out.println("✓ Admin added: " + admin.getUsername());
            System.out.println();
            
            // Add random users
            System.out.println("Adding random users...");
            List<User> users = generateRandomUsers(5, random);
            for (User user : users) {
                em.persist(user);
                System.out.println("✓ User added: " + user.getName() + " (" + user.getUserId() + ") - " + user.getEmail());
            }
            System.out.println();
            
            // Commit the transaction
            transaction.commit();
            System.out.println("✓ All data saved to database successfully!");
            System.out.println();
            
            // Verify by querying
            System.out.println("Verifying saved data...");
            System.out.println();
            
            // Query admin
            Admin savedAdmin = em.createQuery(
                "SELECT a FROM Admin a WHERE a.username = :username", Admin.class)
                .setParameter("username", "ImadAR")
                .getSingleResult();
            System.out.println("✓ Admin verified: " + savedAdmin.getUsername() + " (ID: " + savedAdmin.getId() + ")");
            
            // Query users
            List<User> allUsers = em.createQuery("SELECT u FROM User u", User.class).getResultList();
            System.out.println("✓ Users verified: " + allUsers.size() + " user(s) in database");
            for (User user : allUsers) {
                System.out.println("  - " + user.getName() + " (" + user.getUserId() + ") - " + user.getEmail());
            }
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
                System.err.println("Transaction rolled back due to error.");
            }
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            System.out.println();
            System.out.println("Database connection closed.");
        }
        
        System.out.println();
        System.out.println("=== Population Complete ===");
        System.out.println();
        System.out.println("To verify in Neon Console:");
        System.out.println("1. Go to https://console.neon.tech");
        System.out.println("2. Click on your project");
        System.out.println("3. Click on 'SQL Editor' tab");
        System.out.println("4. Run: SELECT * FROM admins;");
        System.out.println("5. Run: SELECT * FROM users;");
    }
    
    /**
     * Generates random users with unique IDs and emails.
     * 
     * @param count the number of users to generate
     * @param random the random number generator
     * @return list of generated users
     */
    private static List<User> generateRandomUsers(int count, Random random) {
        List<User> users = new ArrayList<>();
        int userIdCounter = 1;
        
        for (int i = 0; i < count; i++) {
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String name = firstName + " " + lastName;
            String userId = "U" + String.format("%03d", userIdCounter++);
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com";
            
            User user = new User(userId, name, email);
            users.add(user);
        }
        
        return users;
    }
}

