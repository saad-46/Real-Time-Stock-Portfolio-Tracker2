@echo off
echo Testing GUI Display...
echo.

:: Create a simple test GUI
echo import javax.swing.*; > TestGUI.java
echo public class TestGUI { >> TestGUI.java
echo     public static void main(String[] args) { >> TestGUI.java
echo         JFrame frame = new JFrame("GUI Test - If you see this, GUI works!"); >> TestGUI.java
echo         frame.setSize(400, 200); >> TestGUI.java
echo         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); >> TestGUI.java
echo         JLabel label = new JLabel("GUI is working! Close this window.", JLabel.CENTER); >> TestGUI.java
echo         frame.add(label); >> TestGUI.java
echo         frame.setLocationRelativeTo(null); >> TestGUI.java
echo         frame.setVisible(true); >> TestGUI.java
echo         System.out.println("Test window created"); >> TestGUI.java
echo     } >> TestGUI.java
echo } >> TestGUI.java

javac TestGUI.java
if %ERRORLEVEL% NEQ 0 (
    echo Failed to compile test
    pause
    exit /b
)

echo.
echo Launching test window...
echo If you see a window, your GUI system works.
echo If not, there may be a display/Java issue.
echo.

start "GUI Test" java TestGUI

timeout /t 3
echo.
echo Did you see a test window?
echo If YES: Your GUI works, StockVault should work too
echo If NO: There's a display/Java configuration issue
echo.
pause

del TestGUI.java TestGUI.class 2>nul
