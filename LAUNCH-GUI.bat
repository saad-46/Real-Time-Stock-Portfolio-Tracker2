@echo off
echo ========================================
echo   StockVault - Launching GUI
echo ========================================
echo.
echo Starting application...
echo If you don't see a window, check for:
echo  - Java windows minimized in taskbar
echo  - Windows behind other applications
echo  - Display/graphics driver issues
echo.

start "StockVault" java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.portfolio.ui.PremiumStockDashboard

echo.
echo GUI launched in separate window.
echo Check your taskbar for "StockVault" window.
echo.
pause
