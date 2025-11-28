# PowerShell script to run the Library Management System GUI

Write-Host "Starting Library Management System GUI..." -ForegroundColor Cyan
Write-Host ""

# Set database environment variables
$env:NEON_DB_URL = "jdbc:postgresql://ep-red-sun-agapswm0-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require"
$env:NEON_DB_USER = "neondb_owner"
$env:NEON_DB_PASSWORD = "npg_vFeS7Qoi3WuT"

Write-Host "Database variables set!" -ForegroundColor Green
Write-Host ""

# Compile first
Write-Host "Compiling..." -ForegroundColor Yellow
mvn clean compile -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit 1
}

Write-Host "Compilation successful!" -ForegroundColor Green
Write-Host ""

# Run the GUI
Write-Host "Launching GUI..." -ForegroundColor Cyan
Write-Host ""

java -cp "target/classes" edu.najah.library.presentation.LibraryGUI

Write-Host ""
Write-Host "GUI closed." -ForegroundColor Green

