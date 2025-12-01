package edu.najah.library.presentation;

/**
 * Main launcher class for the Library Management System GUI.
 * This class sets up environment variables and launches the GUI.
 * 
 * <p>Use this as the main class in Eclipse Run Configurations.
 * 
 * @author Imad Araman
 * @version 1.0
 */
public class Main {
    
    /**
     * Database environment variable name for the database URL.
     */
    private static final String ENV_DB_URL = "NEON_DB_URL";
    
    /**
     * Database environment variable name for the database user.
     */
    private static final String ENV_DB_USER = "NEON_DB_USER";
    
    /**
     * Database environment variable name for the database password.
     */
    private static final String ENV_DB_PASSWORD = "NEON_DB_PASSWORD";
    
    /**
     * Main entry point. Sets database environment variables and launches GUI.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set database environment variables programmatically
        // This ensures they're always set, even if not in Eclipse Run Configuration
        setDatabaseEnvironmentVariables();
        
        // Launch the GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });
    }
    
    /**
     * Sets the database system properties if environment variables aren't set.
     * DatabaseConfig will check both environment variables and system properties.
     * This allows the application to work even if environment variables
     * weren't configured in Eclipse Run Configurations.
     */
    private static void setDatabaseEnvironmentVariables() {
        // Set as system properties (DatabaseConfig checks both env vars and system properties)
        // Only set if not already set (allows override from Eclipse Run Configuration)
        if (System.getenv(ENV_DB_URL) == null && System.getProperty(ENV_DB_URL) == null) {
            System.setProperty(ENV_DB_URL, 
                "jdbc:postgresql://ep-red-sun-agapswm0-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require");
        }
        if (System.getenv(ENV_DB_USER) == null && System.getProperty(ENV_DB_USER) == null) {
            System.setProperty(ENV_DB_USER, "neondb_owner");
        }
        if (System.getenv(ENV_DB_PASSWORD) == null && System.getProperty(ENV_DB_PASSWORD) == null) {
            System.setProperty(ENV_DB_PASSWORD, "npg_vFeS7Qoi3WuT");
        }
    }
}

