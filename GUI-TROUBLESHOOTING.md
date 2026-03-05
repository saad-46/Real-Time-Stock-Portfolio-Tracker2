# 🖥️ GUI Not Showing - Troubleshooting Guide

## Quick Checks

### 1. Check Taskbar
The window might be minimized or behind other windows:
- Look for "StockVault" or "java" in your Windows taskbar
- Press `Alt+Tab` to cycle through open windows
- Check system tray (bottom-right corner)

### 2. Check Running Processes
```bash
# Run this to see if Java GUI is running:
Get-Process java | Select-Object Id, MainWindowTitle
```

### 3. Try Test Scripts

#### Option A: Launch GUI Directly
```bash
.\LAUNCH-GUI.bat
```
This launches the GUI in a separate window.

#### Option B: Test GUI System
```bash
.\TEST-GUI.bat
```
This creates a simple test window to verify your GUI system works.

---

## Common Issues & Solutions

### Issue 1: Window Opens But Immediately Closes
**Cause**: Application error during startup

**Solution**:
1. Run in console to see errors:
   ```bash
   java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.portfolio.ui.PremiumStockDashboard
   ```
2. Look for error messages
3. Check if all dependencies are in `lib/` folder

### Issue 2: No Window Appears At All
**Cause**: Display configuration or headless mode

**Solution**:
1. Check if Java is in headless mode:
   ```bash
   java -XshowSettings:properties -version 2>&1 | findstr "headless"
   ```
   Should show: `java.awt.headless = false`

2. If headless=true, set environment variable:
   ```bash
   set JAVA_TOOL_OPTIONS=-Djava.awt.headless=false
   ```

3. Try running with explicit display:
   ```bash
   java -Djava.awt.headless=false --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.portfolio.ui.PremiumStockDashboard
   ```

### Issue 3: Multiple Monitors
**Cause**: Window opening on different monitor

**Solution**:
1. Check all connected monitors
2. Press `Windows Key + Shift + Arrow` to move windows between monitors
3. The app uses `setLocationRelativeTo(null)` which should center on primary monitor

### Issue 4: High DPI / Scaling Issues
**Cause**: Windows display scaling

**Solution**:
1. Right-click `java.exe` → Properties → Compatibility
2. Check "Override high DPI scaling behavior"
3. Select "Application" from dropdown

### Issue 5: Graphics Driver Issues
**Cause**: Outdated or incompatible graphics drivers

**Solution**:
1. Update graphics drivers
2. Try software rendering:
   ```bash
   java -Dsun.java2d.d3d=false --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.portfolio.ui.PremiumStockDashboard
   ```

---

## Manual Launch Methods

### Method 1: Direct Java Command
```bash
java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.portfolio.ui.PremiumStockDashboard
```

### Method 2: With Debug Output
```bash
java -Djava.awt.headless=false -Dsun.java2d.d3d=false --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.portfolio.ui.PremiumStockDashboard
```

### Method 3: Using Batch File
```bash
.\LAUNCH-GUI.bat
```

### Method 4: Using RUN Script
```bash
.\RUN-PREMIUM-DASHBOARD.bat
```

---

## Diagnostic Commands

### Check Java Version
```bash
java -version
```
Should be Java 11 or higher.

### Check Display Settings
```bash
java -XshowSettings:properties -version 2>&1 | findstr "awt"
```

### List Running Java Processes
```bash
Get-Process java
```

### Check Window Titles
```bash
Get-Process java | Select-Object Id, ProcessName, MainWindowTitle
```

---

## What Should Happen

When working correctly:
1. Console shows: "Database connected successfully!"
2. Console shows: "Daily email scheduler started..."
3. A window appears with:
   - Welcome screen with "Get Started" button
   - OR directly the main dashboard
4. Window title: "StockVault — Portfolio Dashboard"
5. Window size: 1400x900 pixels
6. Centered on screen

---

## Still Not Working?

### Try Minimal Test
1. Run `TEST-GUI.bat`
2. If test window appears: StockVault should work
3. If test window doesn't appear: Java GUI system issue

### Check Dependencies
Ensure these files exist:
- `lib/flatlaf-3.2.jar` (Look and Feel)
- `lib/jfreechart-1.5.3.jar` (Charts)
- `lib/javax.mail.jar` (Email)
- All other JARs in `lib/` folder

### Reinstall FlatLaf
```bash
# Download FlatLaf if missing
curl -L -o lib/flatlaf-3.2.jar https://repo1.maven.org/maven2/com/formdev/flatlaf/3.2/flatlaf-3.2.jar
```

### Check Antivirus
Some antivirus software blocks Java GUI:
1. Temporarily disable antivirus
2. Try running again
3. If it works, add Java to antivirus exceptions

---

## Alternative: Run Without GUI (Console Mode)

If GUI absolutely won't work, you can use console mode:
```bash
java -cp "bin;lib/*" com.portfolio.Main
```

This runs the portfolio tracker in console-only mode (no GUI).

---

## Get Help

If none of these work, provide this information:
1. Java version: `java -version`
2. Windows version
3. Error messages from console
4. Result of `TEST-GUI.bat`
5. Output of: `java -XshowSettings:properties -version 2>&1 | findstr "awt"`

---

## Quick Reference

| Problem | Solution |
|---------|----------|
| Window minimized | Check taskbar, press Alt+Tab |
| Headless mode | Set `-Djava.awt.headless=false` |
| Multiple monitors | Check all screens |
| High DPI issues | Adjust compatibility settings |
| Graphics issues | Update drivers, try `-Dsun.java2d.d3d=false` |
| Missing dependencies | Check `lib/` folder |
| Antivirus blocking | Add Java to exceptions |

---

**Most Common Fix**: The window is probably just minimized or on another monitor. Check your taskbar!
