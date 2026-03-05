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

:: Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

echo.
echo Compiling source code...
javac -d bin -cp "lib/*;src" -sourcepath src src\com\portfolio\Main.java src\com\portfolio\ui\*.java src\com\portfolio\service\*.java src\com\portfolio\model\*.java src\com\portfolio\database\*.java src\com\portfolio\data\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Please check your JDK installation.
    pause
    exit /b
)

echo.
echo Launching StockVault Premium...
echo.

java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.portfolio.Main

echo.
echo Application closed.
pause
