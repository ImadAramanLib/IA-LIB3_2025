# Library Management System

A comprehensive Library Management System built with Java, Maven, JUnit, Mockito, and PostgreSQL (Neon).

## Features

### Core Functionality
- **User Management**: Admin and regular user roles with authentication
- **Book Management**: Add, search, and manage books
- **Borrowing System**: Borrow and return books with due date tracking
- **Fine Management**: Automatic fine calculation and payment processing
- **Overdue Detection**: Automatic detection of overdue items
- **Email Notifications**: Reminder emails for overdue books (mock implementation)
- **Database Integration**: Full persistence with Neon PostgreSQL

### GUI Features
- **Role-based Access Control**: Different interfaces for admins and users
- **Database Integration**: All data persists to Neon PostgreSQL
- **Modern UI**: Clean, user-friendly interface with tabbed navigation
- **Real-time Updates**: Tables refresh automatically after operations

## Technology Stack

- **Java 21**
- **Maven** - Build and dependency management
- **JUnit 5** - Unit testing
- **Mockito** - Mocking framework
- **Jacoco** - Code coverage
- **JPA/Hibernate** - ORM for database operations
- **PostgreSQL (Neon)** - Cloud database
- **Java Swing** - GUI framework

## Design Patterns

1. **Strategy Pattern**: Fine calculation for different media types (Books, CDs, Journals)
2. **Observer Pattern**: Notification system for multiple channels (Email, SMS, etc.)

## Architecture

Layered (N-Tier) Architecture:
- **Presentation Layer**: Swing GUI
- **Service Layer**: Business logic and orchestration
- **Domain Layer**: Core entities (Book, User, Loan, Fine, etc.)
- **Persistence Layer**: Database operations via JPA

## Setup

### Prerequisites
- JDK 21 or higher
- Maven 3.6+
- Neon PostgreSQL database account

### Database Configuration

Set the following environment variables:
- `NEON_DB_URL`: Your Neon database JDBC URL
- `NEON_DB_USER`: Your Neon database username
- `NEON_DB_PASSWORD`: Your Neon database password

### Running the Application

#### Option 1: Using Main.java (Easiest)
1. Open Eclipse
2. Navigate to `src/main/java/edu/najah/library/presentation/Main.java`
3. Right-click → Run As → Java Application

#### Option 2: Using Command Line
```powershell
.\START_GUI.bat
```

Or:
```powershell
.\RUN_GUI.ps1
```

#### Option 3: Using Maven
```bash
mvn clean compile
java -cp "target/classes" edu.najah.library.presentation.Main
```

## Default Login Credentials

- **Admin**: 
  - Username: `admin`
  - Password: `admin123`

- **Users**: Register through the GUI (Admin only)

## Running Tests

```bash
mvn test
```

## Code Coverage

```bash
mvn clean test jacoco:report
```

Coverage report will be in `target/site/jacoco/index.html`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── edu/najah/library/
│   │       ├── config/          # Database configuration
│   │       ├── domain/          # Entity classes
│   │       ├── presentation/    # GUI classes
│   │       ├── service/         # Business logic
│   │       └── util/            # Utilities
│   └── resources/
│       └── META-INF/
│           └── persistence.xml  # JPA configuration
└── test/
    └── java/                    # Test classes
```

## Features by Sprint

### Sprint 1 - Core Admin & Book Management
- Admin login/logout
- Add and search books

### Sprint 2 - Borrowing & Overdue Logic
- Borrow books (28 days)
- Overdue detection
- Pay fines

### Sprint 3 - Communication & Mocking
- Send reminder emails
- Mock email server for testing

### Sprint 4 - Advanced Borrowing Rules
- Borrow restrictions (overdue books, unpaid fines)
- Unregister users (admin only)

### Sprint 5 - Media Extension
- Support for CDs (7-day loan period)
- Different fine rates per media type
- Mixed media overdue reports

## License

This project is part of a university course assignment.

## Authors

- Imad Araman
