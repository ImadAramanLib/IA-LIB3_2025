@echo off
REM Batch file to start the Library Management System GUI
REM This works from any location

cd /d "%~dp0"
echo Starting Library Management System GUI...
echo.

REM Set database environment variables
set NEON_DB_URL=jdbc:postgresql://ep-red-sun-agapswm0-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require
set NEON_DB_USER=neondb_owner
set NEON_DB_PASSWORD=npg_vFeS7Qoi3WuT

echo Database variables set!
echo.

REM Compile first
echo Compiling...
call mvn clean compile -q
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

REM Run the GUI
echo Launching GUI...
echo.
java -cp "target/classes" edu.najah.library.presentation.LibraryGUI

echo.
echo GUI closed.
pause

