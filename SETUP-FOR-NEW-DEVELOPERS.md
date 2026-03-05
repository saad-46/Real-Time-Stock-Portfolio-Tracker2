# 🚀 StockVault - Complete Setup Guide for New Developers

## 📋 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Project Setup](#project-setup)
3. [API Keys Configuration](#api-keys-configuration)
4. [Email Configuration](#email-configuration)
5. [Dependencies](#dependencies)
6. [Running the Application](#running-the-application)
7. [Troubleshooting](#troubleshooting)

---

## 🔧 Prerequisites

### Required Software
- **Java JDK 11 or higher** (Java 17+ recommended)
  - Download: https://www.oracle.com/java/technologies/downloads/
  - Verify: `java -version` and `javac -version`

- **Git** (for cloning repository)
  - Download: https://git-scm.com/downloads

### System Requirements
- **OS**: Windows 10/11, macOS, or Linux
- **RAM**: 4GB minimum, 8GB recommended
- **Disk Space**: 500MB for project + dependencies
- **Display**: 1280x720 minimum resolution

---

## 📦 Project Setup

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd <project-folder>
```

### 2. Verify Project Structure
```
StockVault/
├── src/                    # Source code
│   └── com/portfolio/
│       ├── model/          # Data models
│       ├── service/        # Business logic & APIs
│       ├── database/       # Database operations
│       ├── ui/             # GUI components
│       └── data/           # Static data
├── lib/                    # External libraries (JARs)
├── bin/                    # Compiled classes (auto-generated)
├── docs/                   # Documentation
└── RUN-PREMIUM-DASHBOARD.bat  # Launch script
```

### 3. Check Dependencies
Ensure all JAR files are in the `lib/` folder:
- flatlaf-3.2.jar
- jfreechart-1.5.3.jar
- jcommon-1.0.24.jar
- javax.mail.jar
- activation.jar
- sqlite-jdbc-3.45.1.0.jar
- json-20230227.jar
- gson-2.10.1.jar

If any are missing, download them or contact the team.

---

## 🔑 API Keys Configuration

### CRITICAL: Set Up API Keys

The project uses multiple APIs for stock data, news, and AI features. You MUST configure API keys before running.

### Step 1: Create ApiKeyManager.java

1. Navigate to: `src/com/portfolio/service/`
2. Copy `ApiKeyManager.java.template` to `ApiKeyManager.java`
3. Open `ApiKeyManager.java` in your editor

### Step 2: Get API Keys

#### Stock Price APIs (Required)

**1. Finnhub (Primary - Best for real-time data)**
- Sign up: https://finnhub.io/register
- Free tier: 60 calls/minute
- Get API key from dashboard
- Add to `FINNHUB_KEYS` array
- **Optional**: Get 2 keys for higher rate limits

**2. Twelve Data (Secondary - Backup)**
- Sign up: https://twelvedata.com/pricing
- Free tier: 800 calls/day
- Get API key from dashboard
- Add to `TWELVE_DATA_KEY`

**3. Alpha Vantage (Fallback - Last resort)**
- Sign up: https://www.alphavantage.co/support/#api-key
- Free tier: 25 calls/day (very limited!)
- Get API key instantly
- Add to `ALPHA_VANTAGE_KEY`

#### News APIs (Optional but Recommended)

**4. GNews (Primary news source)**
- Sign up: https://gnews.io/register
- Free tier: 100 requests/day
- Get API key from dashboard
- Add to `GNEWS_KEY`

**5. NewsData.io (Backup news source)**
- Sign up: https://newsdata.io/register
- Free tier: 200 credits/day
- Get API key from dashboard
- Add to `NEWSDATA_KEY`

#### AI & Voice APIs (Optional - For advanced features)

**6. Groq AI (For AI chatbot & portfolio analysis)**
- Sign up: https://console.groq.com/keys
- Free tier: Very generous limits
- Get API key from dashboard
- Add to `GROQ_KEY`

**7. AssemblyAI (For voice commands)**
- Sign up: https://www.assemblyai.com/dashboard/signup
- Free tier: 5 hours/month
- Get API key from dashboard
- Add to `ASSEMBLYAI_KEY`

### Step 3: Update ApiKeyManager.java

Replace all `YOUR_KEY_HERE` placeholders with your actual API keys:

```java
// Example - Replace with your actual keys:
private static final String[] FINNHUB_KEYS = {
    "your_finnhub_key_1",
    "your_finnhub_key_2"   // Optional second key
};

private static final String TWELVE_DATA_KEY = "your_twelve_data_key";
private static final String ALPHA_VANTAGE_KEY = "your_alpha_vantage_key";
private static final String GNEWS_KEY = "your_gnews_key";
private static final String NEWSDATA_KEY = "your_newsdata_key";
private static final String GROQ_KEY = "your_groq_key";
private static final String ASSEMBLYAI_KEY = "your_assemblyai_key";
```

### Step 4: Verify API Keys Work

Run this test command:
```bash
java -cp "bin;lib/*" com.portfolio.service.AlphaVantageService
```

If you see stock prices, your API keys are working!

---

## 📧 Email Configuration

The application sends daily portfolio reports via email at 8:00 PM.

### Step 1: Gmail Account Setup

**Use this Gmail account**:
- Email: `stockvault123@gmail.com`
- Password: (Ask team lead for password)

### Step 2: Generate Gmail App Password

1. Go to: https://myaccount.google.com/security
2. Enable **2-Step Verification** (if not already enabled)
3. Go to: https://myaccount.google.com/apppasswords
4. Select:
   - App: **Mail**
   - Device: **Other (Custom name)** → Type "StockVault"
5. Click **Generate**
6. Copy the 16-character password (format: `xxxx xxxx xxxx xxxx`)
7. Remove spaces: `xxxxxxxxxxxxxxxx`

### Step 3: Update EmailService.java

1. Open: `src/com/portfolio/service/EmailService.java`
2. Find line ~24:
   ```java
   private String senderPassword = "YOUR_GMAIL_APP_PASSWORD_HERE";
   ```
3. Replace with your App Password:
   ```java
   private String senderPassword = "abcdabcdabcdabcd";  // Your 16-char App Password
   ```
4. Save the file

### Step 4: Configure Recipient Email

By default, emails are sent to: `mhnu.6180@gmail.com`

To change recipient:
1. Open: `src/com/portfolio/ui/PremiumStockDashboard.java`
2. Find line ~183:
   ```java
   emailScheduler.start("mhnu.6180@gmail.com");
   ```
3. Replace with your email address

---

## 📚 Dependencies

### Core Libraries (Already in lib/)

| Library | Version | Purpose |
|---------|---------|---------|
| FlatLaf | 3.2 | Modern dark theme UI |
| JFreeChart | 1.5.3 | Charts and graphs |
| JCommon | 1.0.24 | JFreeChart dependency |
| JavaMail | 1.6.2 | Email functionality |
| Activation | 1.1.1 | JavaMail dependency |
| SQLite JDBC | 3.45.1.0 | Database driver |
| JSON | 20230227 | JSON parsing |
| Gson | 2.10.1 | JSON serialization |

### Download Missing Dependencies

If any JARs are missing from `lib/`, download them:

```bash
# FlatLaf
curl -L -o lib/flatlaf-3.2.jar https://repo1.maven.org/maven2/com/formdev/flatlaf/3.2/flatlaf-3.2.jar

# JFreeChart
curl -L -o lib/jfreechart-1.5.3.jar https://repo1.maven.org/maven2/org/jfree/jfreechart/1.5.3/jfreechart-1.5.3.jar

# SQLite JDBC
curl -L -o lib/sqlite-jdbc-3.45.1.0.jar https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar

# JSON
curl -L -o lib/json-20230227.jar https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar

# Gson
curl -L -o lib/gson-2.10.1.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
```

For JavaMail, run:
```bash
.\DOWNLOAD-JAVAMAIL.bat
```

---

## 🚀 Running the Application

### Method 1: Using Batch Script (Recommended)

**Windows:**
```bash
.\RUN-PREMIUM-DASHBOARD.bat
```

**Mac/Linux:**
```bash
chmod +x RUN-PREMIUM-DASHBOARD.sh
./RUN-PREMIUM-DASHBOARD.sh
```

### Method 2: Manual Compilation & Run

```bash
# Compile
javac -cp "lib/*;src" -d bin src/com/portfolio/**/*.java

# Run
java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.portfolio.ui.PremiumStockDashboard
```

### Method 3: Using Launch Script

```bash
.\LAUNCH-GUI.bat
```

### What Should Happen

1. Console shows: "Database connected successfully!"
2. Console shows: "Daily email scheduler started..."
3. GUI window appears with Welcome Screen
4. Click "Get Started" to enter main dashboard
5. Portfolio data loads with live stock prices

---

## 🐛 Troubleshooting

### Issue 1: Compilation Errors

**Error**: `cannot find symbol` or `package does not exist`

**Solution**:
- Verify all JARs are in `lib/` folder
- Check classpath: `-cp "lib/*;src"`
- Ensure Java version is 11+

### Issue 2: API Key Errors

**Error**: `401 Unauthorized` or `API key invalid`

**Solution**:
- Verify API keys are correct in `ApiKeyManager.java`
- Check API key limits (daily/monthly quotas)
- Try alternative API (Finnhub → Twelve Data → Alpha Vantage)

### Issue 3: Email Not Sending

**Error**: `Authentication failed` or `Username and Password not accepted`

**Solution**:
- You MUST use Gmail App Password, not regular password
- Follow email setup steps above
- Verify 2FA is enabled on Gmail account
- Check `EmailService.java` has correct App Password

### Issue 4: GUI Not Showing

**Solution**:
- Check taskbar for minimized window
- Press `Alt+Tab` to cycle through windows
- Run `.\TEST-GUI.bat` to test GUI system
- See `GUI-TROUBLESHOOTING.md` for detailed solutions

### Issue 5: Database Errors

**Error**: `database is locked` or `unable to open database`

**Solution**:
- Close all instances of the application
- Delete `portfolio.db-shm` and `portfolio.db-wal` files
- Restart application

### Issue 6: Stock Prices Not Updating

**Solution**:
- Check internet connection
- Verify API keys are valid
- Check API rate limits (may be exceeded)
- Wait a few minutes and try again

---

## 📊 Application Features

### Core Features
- ✅ Real-time stock price tracking
- ✅ Portfolio management (buy/sell stocks)
- ✅ Profit/Loss calculations
- ✅ Transaction history
- ✅ Watchlist
- ✅ Multi-currency support (INR, USD, EUR, SAR)

### Advanced Features
- ✅ AI-powered portfolio analysis (Groq AI)
- ✅ Voice commands (AssemblyAI)
- ✅ Daily email reports (8:00 PM)
- ✅ Market news feed
- ✅ Interactive charts (JFreeChart)
- ✅ AI rebalancing suggestions
- ✅ 100+ stocks database

### Email Notifications
- Automatic daily reports at 8:00 PM
- Portfolio summary with P/L
- Market alerts for significant changes
- Beautiful HTML email design

---

## 🔐 Security Notes

### NEVER Commit These Files:
- ❌ `ApiKeyManager.java` (contains API keys)
- ❌ `portfolio.db` (user data)
- ❌ `.env` files
- ❌ Any file with passwords/keys

### Always Use:
- ✅ `ApiKeyManager.java.template` (template without keys)
- ✅ `.gitignore` (excludes sensitive files)
- ✅ Environment variables for production

### Best Practices:
- Keep API keys private
- Don't share Gmail App Password
- Use separate keys for dev/prod
- Rotate keys periodically
- Monitor API usage limits

---

## 📞 Support

### Documentation
- `README.md` - Project overview
- `EMAIL-SETUP-GUIDE.md` - Email configuration
- `GUI-TROUBLESHOOTING.md` - GUI issues
- `JAVAX-PACKAGES-ANALYSIS.md` - Technical details

### Common Commands
```bash
# Compile only
javac -cp "lib/*;src" -d bin src/com/portfolio/**/*.java

# Run GUI
java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.portfolio.ui.PremiumStockDashboard

# Run console mode
java -cp "bin;lib/*" com.portfolio.Main

# Test GUI system
.\TEST-GUI.bat

# Check Java version
java -version
```

### Getting Help
1. Check documentation files
2. Review console error messages
3. Verify API keys and configuration
4. Contact team lead
5. Check GitHub issues

---

## ✅ Quick Start Checklist

- [ ] Java 11+ installed
- [ ] Repository cloned
- [ ] All JARs in `lib/` folder
- [ ] `ApiKeyManager.java` created from template
- [ ] All API keys configured
- [ ] Gmail App Password set in `EmailService.java`
- [ ] Compilation successful
- [ ] Application runs and GUI appears
- [ ] Stock prices loading
- [ ] Email test successful

---

## 🎉 You're Ready!

Once all steps are complete, you should have a fully functional StockVault application with:
- Real-time stock tracking
- AI-powered insights
- Daily email reports
- Beautiful modern UI

Happy coding! 🚀

---

**Last Updated**: March 2026
**Version**: 3.0
**Maintainer**: StockVault Team
