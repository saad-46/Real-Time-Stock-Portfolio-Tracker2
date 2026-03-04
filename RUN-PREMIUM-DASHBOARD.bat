@echo off
setlocal enabledelayedexpansion

echo ========================================
echo   StockVault Premium Dashboard
echo   Institutional-Grade Portfolio Tracking
echo ========================================
echo.

:: Check if lib directory exists
if not exist "lib" (
    echo [ERROR] Required "lib" directory not found. 
    echo Please ensure all dependencies are in the lib folder.
    pause
    exit /b
)

:: Compile if classes are missing or for a fresh start
echo Preparing environment...

:: Build the sources list for compilation
dir /s /b src\*.java > sources.txt

echo.
echo Compiling source code...
javac -d . -cp ".;lib/*" @sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Please check your JDK installation.
    del sources.txt
    pause
    exit /b
)

del sources.txt

echo.
echo Launching StockVault Premium...
echo.

java --enable-native-access=ALL-UNNAMED -cp ".;lib/*" com.portfolio.ui.PremiumStockDashboard

echo.
echo Application closed.
pause
