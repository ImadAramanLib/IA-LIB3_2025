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
        if (System.getenv("NEON_DB_URL") == null && System.getProperty("NEON_DB_URL") == null) {
            System.setProperty("NEON_DB_URL", 
                "jdbc:postgresql://ep-red-sun-agapswm0-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require");
        }
        if (System.getenv("NEON_DB_USER") == null && System.getProperty("NEON_DB_USER") == null) {
            System.setProperty("NEON_DB_USER", "neondb_owner");
        }
        if (System.getenv("NEON_DB_PASSWORD") == null && System.getProperty("NEON_DB_PASSWORD") == null) {
            System.setProperty("NEON_DB_PASSWORD", "npg_vFeS7Qoi3WuT");
        }
    }
}

