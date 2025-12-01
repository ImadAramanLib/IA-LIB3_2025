package edu.najah.library.persistence;

import edu.najah.library.domain.Admin;
import edu.najah.library.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

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
    
    private static final Logger logger = Logger.getLogger(PopulateDatabase.class.getName());
    
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
        logger.info("=== Populating Database ===");
        
        EntityManager em = DatabaseConfig.createEntityManager();
        
        // Check if EntityManager creation failed (can happen if database initialization fails)
        if (em == null) {
            logger.severe("ERROR: Could not create EntityManager.");
            logger.severe("Please check your environment variables:");
            logger.severe("  - NEON_DB_URL");
            logger.severe("  - NEON_DB_USER");
            logger.severe("  - NEON_DB_PASSWORD");
            return;
        }
        
        // EntityManager is not null, proceed with database operations
        
        logger.info("✓ Successfully connected to database!");
        
        EntityTransaction transaction = em.getTransaction();
        Random random = new Random();
        
        try {
            transaction.begin();
            logger.info("Transaction started...");
            
            // Add Admin
            logger.info("Adding Admin...");
            Admin admin = new Admin("ImadAR", "ImadImad119");
            em.persist(admin);
            logger.info("✓ Admin added: " + admin.getUsername());
            
            // Add random users
            logger.info("Adding random users...");
            List<User> users = generateRandomUsers(5, random);
            for (User user : users) {
                em.persist(user);
                logger.info("✓ User added: " + user.getName() + " (" + user.getUserId() + ") - " + user.getEmail());
            }
            
            // Commit the transaction
            transaction.commit();
            logger.info("✓ All data saved to database successfully!");
            
            // Verify by querying
            logger.info("Verifying saved data...");
            
            // Query admin
            Admin savedAdmin = em.createQuery(
                "SELECT a FROM Admin a WHERE a.username = :username", Admin.class)
                .setParameter("username", "ImadAR")
                .getSingleResult();
            logger.info("✓ Admin verified: " + savedAdmin.getUsername() + " (ID: " + savedAdmin.getId() + ")");
            
            // Query users
            List<User> allUsers = em.createQuery("SELECT u FROM User u", User.class).getResultList();
            logger.info("✓ Users verified: " + allUsers.size() + " user(s) in database");
            for (User user : allUsers) {
                logger.info("  - " + user.getName() + " (" + user.getUserId() + ") - " + user.getEmail());
            }
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
                logger.severe("Transaction rolled back due to error.");
            }
            logger.severe("ERROR: " + e.getMessage());
            logger.log(java.util.logging.Level.SEVERE, "Exception details", e);
        } finally {
            em.close();
            logger.info("Database connection closed.");
        }
        
        logger.info("=== Population Complete ===");
        logger.info("To verify in Neon Console:");
        logger.info("1. Go to https://console.neon.tech");
        logger.info("2. Click on your project");
        logger.info("3. Click on 'SQL Editor' tab");
        logger.info("4. Run: SELECT * FROM admins;");
        logger.info("5. Run: SELECT * FROM users;");
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

