package edu.najah.library.presentation;

import edu.najah.library.domain.*;
import edu.najah.library.config.DatabaseConfig;
import edu.najah.library.config.DatabaseService;
import edu.najah.library.service.*;
import edu.najah.library.util.MockEmailServer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Main GUI application for the Library Management System.
 * Features role-based access (Admin/User), database integration, and modern UI.
 * 
 * <p>Login Screen: Blocks access until user logs in
 * <p>Admin Access: Full system access
 * <p>User Access: Borrowing, returning, viewing only
 * 
 * @author Imad Araman
 * @version 2.0
 */
public class LibraryGUI extends JFrame {
    
    // Database
    private transient DatabaseService databaseService;
    private transient jakarta.persistence.EntityManager entityManager;
    
    // Services
    private transient AuthenticationService authService;
    private transient LibraryService libraryService;
    private transient BorrowingService borrowingService;
    private transient OverdueDetectionService overdueService;
    private transient ReminderService reminderService;
    private transient UserService userService;
    private transient MockEmailServer emailServer;
    
    // Current user info
    private String currentUsername;
    private boolean isAdmin;
    private User currentUser;
    
    // Main components
    private JPanel loginPanel;
    private JTabbedPane tabbedPane;
    private JLabel statusLabel;
    private JLabel userInfoLabel;
    
    // Login components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton logoutButton;
    private JLabel loginStatusLabel;
    
    // Book Management
    private JTextField bookTitleField, bookAuthorField, bookIsbnField;
    private JButton addBookButton;
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    
    // Search
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JButton searchButton;
    private JTable searchResultsTable;
    private DefaultTableModel searchResultsTableModel;
    
    // Borrowing
    private JTextField borrowItemIdField;
    private JButton borrowButton, returnButton;
    private JTable loansTable;
    private DefaultTableModel loansTableModel;
    
    // Fines
    private JTextField fineAmountField;
    private JButton payFineButton;
    private JTable finesTable;
    private DefaultTableModel finesTableModel;
    
    // User Management (Admin only)
    private JTextField userIdField, userNameField, userEmailField, userUsernameField;
    private JPasswordField userPasswordField;
    private JButton registerUserButton, unregisterUserButton;
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    
    // Overdue (Admin only)
    private JButton checkOverdueButton, sendRemindersButton;
    private JTable overdueTable;
    private DefaultTableModel overdueTableModel;
    
    /**
     * Constructs the main GUI window.
     */
    public LibraryGUI() {
        initializeDatabase();
        initializeServices();
        initializeGUI();
        setupEventHandlers();
        showLoginScreen();
        loadDataFromDatabase();
    }
    
    /**
     * Initializes database connection.
     */
    private void initializeDatabase() {
        try {
            entityManager = DatabaseConfig.createEntityManager();
            if (entityManager != null) {
                databaseService = new DatabaseService(entityManager);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Database connection failed. Running in offline mode.\n" +
                    "Please set NEON_DB_URL, NEON_DB_USER, NEON_DB_PASSWORD environment variables.",
                    "Database Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Database initialization error: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Initializes all service instances.
     */
    private void initializeServices() {
        authService = new AuthenticationService();
        libraryService = new LibraryService(authService);
        borrowingService = new BorrowingService();
        overdueService = new OverdueDetectionService(borrowingService);
        emailServer = new MockEmailServer();
        reminderService = new ReminderService(overdueService);
        reminderService.registerNotifier(emailServer);
        userService = new UserService(borrowingService);
        
        // Load admins from database
        if (databaseService != null) {
            List<Admin> admins = databaseService.loadAllAdmins();
            for (Admin admin : admins) {
                authService.registerAdmin(admin);
            }
            // Always ensure default admin exists (in-memory fallback)
            if (admins.isEmpty()) {
                authService.registerAdmin(new Admin("admin", "admin123"));
            }
        }
        // Always add default admin as fallback (works even if database is not connected)
        authService.registerAdmin(new Admin("admin", "admin123"));
    }
    
    /**
     * Loads data from database on startup.
     */
    private void loadDataFromDatabase() {
        if (databaseService == null) return;
        
        try {
            // Load books - add directly to service list (bypass login check for initialization)
            // This is just for in-memory operations, display will come from database
            List<Book> books = databaseService.loadAllBooks();
            for (Book book : books) {
                libraryService.addBookDirectly(book);
            }
            
            // Load users
            List<User> users = databaseService.loadAllUsers();
            for (User user : users) {
                try {
                    userService.registerUser(user);
                } catch (Exception e) {
                    // User might already exist, skip
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the GUI components.
     */
    private void initializeGUI() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setLayout(new BorderLayout());
        loginPanel = createLoginPanel();
        createMainContent();
    }
    
    /**
     * Creates the login panel (main screen).
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        // Title
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(70, 130, 180));
        panel.add(titleLabel, gbc);
        
        // Subtitle
        gbc.gridy = 1;
        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        panel.add(subtitleLabel, gbc);
        
        // Login box
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            new EmptyBorder(30, 40, 30, 40)
        ));
        loginBox.setBackground(Color.WHITE);
        GridBagConstraints gbcBox = new GridBagConstraints();
        gbcBox.insets = new Insets(10, 10, 10, 10);
        
        // Username
        gbcBox.gridwidth = 1;
        gbcBox.gridx = 0;
        gbcBox.gridy = 0;
        gbcBox.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginBox.add(userLabel, gbcBox);
        gbcBox.gridx = 1;
        gbcBox.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            new EmptyBorder(5, 8, 5, 8)
        ));
        loginBox.add(usernameField, gbcBox);
        
        // Password
        gbcBox.gridy = 1;
        gbcBox.gridx = 0;
        gbcBox.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginBox.add(passLabel, gbcBox);
        gbcBox.gridx = 1;
        gbcBox.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            new EmptyBorder(5, 8, 5, 8)
        ));
        loginBox.add(passwordField, gbcBox);
        
        // Buttons
        gbcBox.gridy = 2;
        gbcBox.gridx = 0;
        gbcBox.gridwidth = 2;
        gbcBox.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        buttonPanel.add(loginButton);
        loginBox.add(buttonPanel, gbcBox);
        
        // Status
        gbcBox.gridy = 3;
        loginStatusLabel = new JLabel(" ");
        loginStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        loginStatusLabel.setForeground(Color.RED);
        loginBox.add(loginStatusLabel, gbcBox);
        
        panel.add(loginBox, gbc);
        
        // Info
        gbc.gridy = 3;
        JLabel infoLabel = new JLabel("<html><i>Login as admin or user (check database for credentials)</i></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoLabel.setForeground(new Color(150, 150, 150));
        panel.add(infoLabel, gbc);
        
        return panel;
    }
    
    /**
     * Creates the main content area (shown after login).
     */
    private void createMainContent() {
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            new EmptyBorder(5, 10, 5, 10)
        ));
        topBar.setBackground(new Color(240, 240, 245));
        
        userInfoLabel = new JLabel("Not logged in");
        userInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        topBar.add(userInfoLabel, BorderLayout.WEST);
        
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoutButton.setPreferredSize(new Dimension(80, 30));
        topBar.add(logoutButton, BorderLayout.EAST);
        
        add(topBar, BorderLayout.NORTH);
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        tabbedPane.addTab("📚 Books", createBookManagementPanel());
        tabbedPane.addTab("🔍 Search", createSearchPanel());
        tabbedPane.addTab("📖 Borrow/Return", createBorrowingPanel());
        tabbedPane.addTab("💰 Fines", createFinePanel());
        tabbedPane.addTab("👥 Users", createUserManagementPanel());
        tabbedPane.addTab("⏰ Overdue", createOverduePanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            new EmptyBorder(3, 10, 3, 10)
        ));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    /**
     * Shows the login screen (blocks access).
     */
    private void showLoginScreen() {
        getContentPane().removeAll();
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        usernameField.requestFocus();
    }
    
    /**
     * Shows the main application (after successful login).
     */
    private void showMainApplication() {
        getContentPane().removeAll();
        createMainContent();
        setupEventHandlers(); // Reconnect event handlers after creating new components
        revalidate();
        repaint();
        updateTabVisibility();
        refreshAllTables();
        enableAllButtons();
    }
    
    /**
     * Ensures all buttons are enabled and visible.
     */
    private void enableAllButtons() {
        if (addBookButton != null) addBookButton.setEnabled(true);
        if (searchButton != null) searchButton.setEnabled(true);
        if (borrowButton != null) borrowButton.setEnabled(true);
        if (returnButton != null) returnButton.setEnabled(true);
        if (payFineButton != null) payFineButton.setEnabled(true);
        if (registerUserButton != null) registerUserButton.setEnabled(true);
        if (unregisterUserButton != null) unregisterUserButton.setEnabled(true);
        if (checkOverdueButton != null) checkOverdueButton.setEnabled(true);
        if (sendRemindersButton != null) sendRemindersButton.setEnabled(true);
    }
    
    /**
     * Updates tab visibility based on user role.
     */
    private void updateTabVisibility() {
        // Users, Overdue - admin only
        tabbedPane.setEnabledAt(4, isAdmin); // Users tab
        tabbedPane.setEnabledAt(5, isAdmin); // Overdue tab
        
        if (!isAdmin) {
            try {
                tabbedPane.removeTabAt(5); // Overdue
                tabbedPane.removeTabAt(4); // Users
            } catch (Exception e) {
                // Tabs already removed
            }
        }
    }
    
    private JPanel createBookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Add New Book",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        
        inputPanel.add(new JLabel("Title:"));
        bookTitleField = new JTextField();
        inputPanel.add(bookTitleField);
        
        inputPanel.add(new JLabel("Author:"));
        bookAuthorField = new JTextField();
        inputPanel.add(bookAuthorField);
        
        inputPanel.add(new JLabel("ISBN:"));
        bookIsbnField = new JTextField();
        inputPanel.add(bookIsbnField);
        
        addBookButton = new JButton("Add Book");
        addBookButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        addBookButton.setBackground(new Color(34, 139, 34));
        addBookButton.setForeground(Color.WHITE);
        addBookButton.setOpaque(true);
        addBookButton.setBorderPainted(false);
        addBookButton.setFocusPainted(false);
        inputPanel.add(addBookButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        String[] columns = {"Title", "Author", "ISBN", "Available"};
        booksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        booksTable = new JTable(booksTableModel);
        booksTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        booksTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshButton.addActionListener(e -> refreshBooksTable());
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(25);
        searchTypeCombo = new JComboBox<>(new String[]{"Title", "Author", "ISBN"});
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        String[] columns = {"Title", "Author", "ISBN", "Available"};
        searchResultsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        searchResultsTable = new JTable(searchResultsTableModel);
        searchResultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        searchResultsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(searchResultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBorrowingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Borrow/Return Item",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        
        inputPanel.add(new JLabel("Book ISBN:"));
        borrowItemIdField = new JTextField();
        inputPanel.add(borrowItemIdField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        borrowButton = new JButton("Borrow");
        borrowButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        borrowButton.setBackground(new Color(70, 130, 180));
        borrowButton.setForeground(Color.WHITE);
        borrowButton.setOpaque(true);
        borrowButton.setBorderPainted(false);
        borrowButton.setFocusPainted(false);
        returnButton = new JButton("Return");
        returnButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        returnButton.setBackground(new Color(220, 20, 60));
        returnButton.setForeground(Color.WHITE);
        returnButton.setOpaque(true);
        returnButton.setBorderPainted(false);
        returnButton.setFocusPainted(false);
        buttonPanel.add(borrowButton);
        buttonPanel.add(returnButton);
        inputPanel.add(buttonPanel);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        String[] columns = {"User", "Book", "Borrow Date", "Due Date", "Return Date", "Status"};
        loansTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        loansTable = new JTable(loansTableModel);
        loansTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loansTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(loansTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("Refresh Loans");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshButton.addActionListener(e -> refreshLoansTable());
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFinePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Pay Fine",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        
        inputPanel.add(new JLabel("Amount (NIS):"));
        fineAmountField = new JTextField();
        inputPanel.add(fineAmountField);
        
        payFineButton = new JButton("Pay Fine");
        payFineButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        payFineButton.setBackground(new Color(34, 139, 34));
        payFineButton.setForeground(Color.WHITE);
        payFineButton.setOpaque(true);
        payFineButton.setBorderPainted(false);
        payFineButton.setFocusPainted(false);
        inputPanel.add(payFineButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        String[] columns = {"User", "Amount", "Paid", "Remaining"};
        finesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        finesTable = new JTable(finesTableModel);
        finesTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        finesTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(finesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("Refresh Fines");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshButton.addActionListener(e -> refreshFinesTable());
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "User Management (Admin Only)",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        
        inputPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField();
        inputPanel.add(userIdField);
        
        inputPanel.add(new JLabel("Username (no spaces):"));
        userUsernameField = new JTextField();
        inputPanel.add(userUsernameField);
        
        inputPanel.add(new JLabel("Password:"));
        userPasswordField = new JPasswordField();
        inputPanel.add(userPasswordField);
        
        inputPanel.add(new JLabel("Name:"));
        userNameField = new JTextField();
        inputPanel.add(userNameField);
        
        inputPanel.add(new JLabel("Email:"));
        userEmailField = new JTextField();
        inputPanel.add(userEmailField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        registerUserButton = new JButton("Register");
        registerUserButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        registerUserButton.setBackground(new Color(34, 139, 34));
        registerUserButton.setForeground(Color.WHITE);
        registerUserButton.setOpaque(true);
        registerUserButton.setBorderPainted(false);
        registerUserButton.setFocusPainted(false);
        unregisterUserButton = new JButton("Unregister");
        unregisterUserButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        unregisterUserButton.setBackground(new Color(220, 20, 60));
        unregisterUserButton.setForeground(Color.WHITE);
        unregisterUserButton.setOpaque(true);
        unregisterUserButton.setBorderPainted(false);
        unregisterUserButton.setFocusPainted(false);
        buttonPanel.add(registerUserButton);
        buttonPanel.add(unregisterUserButton);
        inputPanel.add(buttonPanel);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        String[] columns = {"User ID", "Username", "Name", "Email"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        usersTable = new JTable(usersTableModel);
        usersTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        usersTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("Refresh Users");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshButton.addActionListener(e -> refreshUsersTable());
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createOverduePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        checkOverdueButton = new JButton("Check Overdue Items");
        checkOverdueButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        checkOverdueButton.setBackground(new Color(255, 140, 0));
        checkOverdueButton.setForeground(Color.WHITE);
        checkOverdueButton.setOpaque(true);
        checkOverdueButton.setBorderPainted(false);
        checkOverdueButton.setFocusPainted(false);
        sendRemindersButton = new JButton("Send Reminders");
        sendRemindersButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sendRemindersButton.setBackground(new Color(70, 130, 180));
        sendRemindersButton.setForeground(Color.WHITE);
        sendRemindersButton.setOpaque(true);
        sendRemindersButton.setBorderPainted(false);
        sendRemindersButton.setFocusPainted(false);
        buttonPanel.add(checkOverdueButton);
        buttonPanel.add(sendRemindersButton);
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        String[] columns = {"User", "Book", "Days Overdue", "Fine (NIS)"};
        overdueTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        overdueTable = new JTable(overdueTableModel);
        overdueTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        overdueTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(overdueTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Sets up event handlers.
     */
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> handleLogin());
        logoutButton.addActionListener(e -> handleLogout());
        passwordField.addActionListener(e -> handleLogin());
        
        addBookButton.addActionListener(e -> handleAddBook());
        searchButton.addActionListener(e -> handleSearch());
        searchField.addActionListener(e -> handleSearch());
        borrowButton.addActionListener(e -> handleBorrow());
        returnButton.addActionListener(e -> handleReturn());
        payFineButton.addActionListener(e -> handlePayFine());
        registerUserButton.addActionListener(e -> handleRegisterUser());
        unregisterUserButton.addActionListener(e -> handleUnregisterUser());
        checkOverdueButton.addActionListener(e -> handleCheckOverdue());
        sendRemindersButton.addActionListener(e -> handleSendReminders());
    }
    
    // Event handlers
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Please enter username and password");
            loginStatusLabel.setForeground(Color.RED);
            return;
        }
        
        // Check if admin
        if (databaseService != null) {
            Admin admin = databaseService.findAdminByUsername(username);
            if (admin != null && admin.getPassword().equals(password)) {
                isAdmin = true;
                currentUsername = username;
                authService.login(username, password);
                showMainApplication();
                userInfoLabel.setText("Logged in as: " + username + " (Admin)");
                statusLabel.setText("Welcome, Administrator!");
                return;
            }
        }
        
        // Check if user (using username/password)
        if (databaseService != null) {
            // Try to find user by username
            User user = databaseService.findUserByUsername(username);
            if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
                // Valid user login
                isAdmin = false;
                currentUsername = username;
                currentUser = user;
                showMainApplication();
                userInfoLabel.setText("Logged in as: " + user.getName() + " (User)");
                statusLabel.setText("Welcome, " + user.getName() + "!");
                return;
            }
            // Fallback: try by user ID (for backward compatibility)
            user = databaseService.findUserById(username);
            if (user != null) {
                // If user has no password set, allow login with just user ID
                if (user.getPassword() == null || user.getPassword().isEmpty()) {
                    isAdmin = false;
                    currentUsername = username;
                    currentUser = user;
                    showMainApplication();
                    userInfoLabel.setText("Logged in as: " + user.getName() + " (User)");
                    statusLabel.setText("Welcome, " + user.getName() + "!");
                    return;
                }
            }
        }
        
        // Fallback: check in-memory services
        if (authService.login(username, password)) {
            isAdmin = true;
            currentUsername = username;
            showMainApplication();
            userInfoLabel.setText("Logged in as: " + username + " (Admin)");
            statusLabel.setText("Welcome, Administrator!");
            return;
        }
        
        // Login failed
        loginStatusLabel.setText("Invalid username or password");
        loginStatusLabel.setForeground(Color.RED);
        passwordField.setText("");
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            currentUsername = null;
            currentUser = null;
            isAdmin = false;
            usernameField.setText("");
            passwordField.setText("");
            loginStatusLabel.setText(" ");
            showLoginScreen();
        }
    }
    
    private void handleAddBook() {
        if (!isAdmin) {
            showMessage("Only administrators can add books!", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String title = bookTitleField.getText().trim();
            String author = bookAuthorField.getText().trim();
            String isbn = bookIsbnField.getText().trim();
            
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                showMessage("Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Book book = new Book(title, author, isbn);
            libraryService.addBook(book);
            
            // Save to database
            if (databaseService != null) {
                databaseService.saveBook(book);
            }
            
            showMessage("Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            bookTitleField.setText("");
            bookAuthorField.setText("");
            bookIsbnField.setText("");
            refreshBooksTable();
            statusLabel.setText("Book added: " + title);
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) return;
        
        String searchType = (String) searchTypeCombo.getSelectedItem();
        List<Book> results;
        
        switch (searchType) {
            case "Title":
                results = libraryService.searchByTitle(query);
                break;
            case "Author":
                results = libraryService.searchByAuthor(query);
                break;
            case "ISBN":
                Book book = libraryService.searchByISBN(query);
                results = book != null ? List.of(book) : List.of();
                break;
            default:
                results = List.of();
        }
        
        searchResultsTableModel.setRowCount(0);
        for (Book b : results) {
            searchResultsTableModel.addRow(new Object[]{
                b.getTitle(), b.getAuthor(), b.getIsbn(), b.isAvailable() ? "Yes" : "No"
            });
        }
        
        statusLabel.setText("Found " + results.size() + " result(s)");
    }
    
    private void handleBorrow() {
        try {
            if (currentUser == null && !isAdmin) {
                showMessage("Please login as a user to borrow items!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String isbn = borrowItemIdField.getText().trim();
            if (isbn.isEmpty()) {
                showMessage("Please enter ISBN!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User user = isAdmin ? null : currentUser;
            if (user == null && isAdmin) {
                showMessage("Admins cannot borrow items. Please login as a user.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Book book = libraryService.searchByISBN(isbn);
            if (book == null) {
                showMessage("Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Loan loan = borrowingService.borrowBook(user, book, LocalDate.now());
            
            if (loan != null) {
                showMessage("Book borrowed successfully! Due date: " + loan.getDueDate(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshLoansTable();
                statusLabel.setText("Book borrowed: " + book.getTitle());
            } else {
                showMessage("Borrowing failed! Check if book is available or user has restrictions.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalStateException e) {
            showMessage("Cannot borrow: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleReturn() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Please select a loan to return!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String userId = (String) loansTableModel.getValueAt(selectedRow, 0);
            String bookTitle = (String) loansTableModel.getValueAt(selectedRow, 1);
            String status = (String) loansTableModel.getValueAt(selectedRow, 5);
            
            // Check if already returned
            if ("Returned".equals(status)) {
                showMessage("This book has already been returned!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            User user = databaseService != null ? databaseService.findUserById(userId) : 
                       userService.findUserById(userId);
            
            if (user == null) {
                showMessage("User not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Find the specific loan for this user and book
            List<Loan> allLoans = borrowingService.getLoans();
            Loan loanToReturn = null;
            for (Loan loan : allLoans) {
                if (loan.getUser() != null && loan.getUser().getUserId().equals(userId) &&
                    loan.getBook() != null && loan.getBook().getTitle().equals(bookTitle) &&
                    !loan.isReturned()) {
                    loanToReturn = loan;
                    break;
                }
            }
            
            if (loanToReturn == null) {
                showMessage("Active loan not found for this book!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            borrowingService.returnBook(loanToReturn, LocalDate.now());
            showMessage("Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshLoansTable();
            refreshBooksTable(); // Also refresh books to show availability
            statusLabel.setText("Book returned: " + bookTitle);
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void handlePayFine() {
        try {
            if (currentUser == null && !isAdmin) {
                showMessage("Please login as a user to pay fines!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String amountStr = fineAmountField.getText().trim();
            if (amountStr.isEmpty()) {
                showMessage("Please enter amount!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountStr);
            User user = isAdmin ? null : currentUser;
            if (user == null) {
                showMessage("Please login as a user!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (borrowingService.payFine(user, amount)) {
                showMessage("Fine paid successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                fineAmountField.setText("");
                refreshFinesTable();
                statusLabel.setText("Fine paid: " + amount + " NIS");
            } else {
                showMessage("Payment failed! Check if user has unpaid fines.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            showMessage("Invalid amount format!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRegisterUser() {
        if (!isAdmin) {
            showMessage("Only administrators can register users!", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String userId = userIdField.getText().trim();
            String username = userUsernameField.getText().trim();
            String password = new String(userPasswordField.getPassword());
            String name = userNameField.getText().trim();
            String email = userEmailField.getText().trim();
            
            if (userId.isEmpty() || username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
                showMessage("Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate username: no spaces
            if (username.contains(" ")) {
                showMessage("Username cannot contain spaces!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate username: alphanumeric and underscore only (optional)
            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                showMessage("Username can only contain letters, numbers, and underscores!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User user = new User(userId, username, password, name, email);
            if (userService.registerUser(user)) {
                // Save to database
                if (databaseService != null) {
                    databaseService.saveUser(user);
                }
                
                showMessage("User registered successfully!\nUsername: " + username + "\nPassword: " + password, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                userIdField.setText("");
                userUsernameField.setText("");
                userPasswordField.setText("");
                userNameField.setText("");
                userEmailField.setText("");
                refreshUsersTable();
                statusLabel.setText("User registered: " + name + " (Username: " + username + ")");
            } else {
                showMessage("User already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUnregisterUser() {
        if (!isAdmin) {
            showMessage("Only administrators can unregister users!", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String userId = userIdField.getText().trim();
            if (userId.isEmpty()) {
                showMessage("Please enter User ID!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User user = userService.findUserById(userId);
            if (user == null) {
                showMessage("User not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (userService.unregisterUser(user)) {
                // Delete from database
                if (databaseService != null) {
                    databaseService.deleteUser(user);
                }
                
                showMessage("User unregistered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                userIdField.setText("");
                userUsernameField.setText("");
                userPasswordField.setText("");
                userNameField.setText("");
                userEmailField.setText("");
                refreshUsersTable();
                statusLabel.setText("User unregistered: " + userId);
            } else {
                showMessage("User cannot be unregistered! Check for active loans or unpaid fines.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleCheckOverdue() {
        LocalDate today = LocalDate.now();
        List<Loan> overdueLoans = overdueService.getOverdueLoans(today);
        
        overdueTableModel.setRowCount(0);
        for (Loan loan : overdueLoans) {
            long daysOverdue = loan.getDaysOverdue(today);
            double fine = overdueService.calculateOverdueFine(loan, today);
            String userInfo = loan.getUser() != null ? loan.getUser().getUserId() : "Unknown";
            String bookInfo = loan.getBook() != null ? loan.getBook().getTitle() : "Unknown";
            
            overdueTableModel.addRow(new Object[]{
                userInfo, bookInfo, daysOverdue, String.format("%.2f", fine)
            });
        }
        
        statusLabel.setText("Found " + overdueLoans.size() + " overdue items");
    }
    
    private void handleSendReminders() {
        if (!isAdmin) {
            showMessage("Only administrators can send reminders!", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        reminderService.sendReminders(LocalDate.now());
        int sentCount = emailServer.getSentEmailCount();
        
        showMessage("Sent " + sentCount + " reminder email(s)!", "Success", JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("Sent " + sentCount + " reminders");
    }
    
    // Helper methods
    
    private void refreshAllTables() {
        refreshBooksTable();
        refreshLoansTable();
        refreshFinesTable();
        refreshUsersTable();
    }
    
    private void refreshBooksTable() {
        booksTableModel.setRowCount(0);
        
        // Load directly from database (like users do)
        if (databaseService != null) {
            try {
                List<Book> books = databaseService.loadAllBooks();
                for (Book book : books) {
                    booksTableModel.addRow(new Object[]{
                        book.getTitle(), book.getAuthor(), book.getIsbn(),
                        book.isAvailable() ? "Yes" : "No"
                    });
                    // Also add to in-memory service for operations
                    libraryService.addBookDirectly(book);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Fallback to in-memory if no database
            List<Book> books = libraryService.getAllBooks();
            for (Book book : books) {
                booksTableModel.addRow(new Object[]{
                    book.getTitle(), book.getAuthor(), book.getIsbn(),
                    book.isAvailable() ? "Yes" : "No"
                });
            }
        }
    }
    
    private void refreshLoansTable() {
        loansTableModel.setRowCount(0);
        List<Loan> loans = borrowingService.getLoans();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Loan loan : loans) {
            String userInfo = loan.getUser() != null ? loan.getUser().getUserId() : "Unknown";
            String bookInfo = loan.getBook() != null ? loan.getBook().getTitle() : "Unknown";
            String borrowDate = loan.getBorrowDate() != null ? loan.getBorrowDate().format(formatter) : "";
            String dueDate = loan.getDueDate() != null ? loan.getDueDate().format(formatter) : "";
            String returnDate = loan.getReturnDate() != null ? loan.getReturnDate().format(formatter) : "Not returned";
            String status = loan.isReturned() ? "Returned" : "Active";
            
            loansTableModel.addRow(new Object[]{
                userInfo, bookInfo, borrowDate, dueDate, returnDate, status
            });
        }
    }
    
    private void refreshFinesTable() {
        finesTableModel.setRowCount(0);
        List<Fine> fines = borrowingService.getFines();
        for (Fine fine : fines) {
            String userInfo = fine.getUser() != null ? fine.getUser().getUserId() : "Unknown";
            finesTableModel.addRow(new Object[]{
                userInfo,
                String.format("%.2f", fine.getAmount()),
                fine.isPaid() ? "Yes" : "No",
                String.format("%.2f", fine.getRemainingBalance())
            });
        }
    }
    
    private void refreshUsersTable() {
        usersTableModel.setRowCount(0);
        
        // Load directly from database (this is what works perfectly!)
        if (databaseService != null) {
            try {
                List<User> users = databaseService.loadAllUsers();
                for (User user : users) {
                    usersTableModel.addRow(new Object[]{
                        user.getUserId(), user.getUsername() != null ? user.getUsername() : "", 
                        user.getName(), user.getEmail()
                    });
                    // Also register in service for operations
                    userService.registerUser(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Fallback to in-memory
            List<User> users = userService.getUsers();
            for (User user : users) {
                usersTableModel.addRow(new Object[]{
                    user.getUserId(), user.getUsername() != null ? user.getUsername() : "",
                    user.getName(), user.getEmail()
                });
            }
        }
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    /**
     * Main method to launch the GUI application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });
    }
    
    /**
     * Cleanup on close.
     */
    @Override
    protected void processWindowEvent(java.awt.event.WindowEvent e) {
        if (e.getID() == java.awt.event.WindowEvent.WINDOW_CLOSING) {
            if (databaseService != null) {
                databaseService.close();
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            DatabaseConfig.shutdown();
        }
        super.processWindowEvent(e);
    }
}

