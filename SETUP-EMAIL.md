# Email Notifications Setup

## Required Libraries

Download these JAR files and place them in the `lib/` folder:

1. **JavaMail API** (javax.mail)
   - Download: https://github.com/javaee/javamail/releases
   - File: `javax.mail.jar` or `jakarta.mail-2.0.1.jar`

2. **Activation Framework** (required by JavaMail)
   - Download: https://github.com/javaee/activation/releases  
   - File: `activation.jar` or `jakarta.activation-2.0.1.jar`

## Quick Download Links

```bash
# Download JavaMail
wget https://repo1.maven.org/maven2/com/sun/mail/javax.mail/1.6.2/javax.mail-1.6.2.jar -O lib/javax.mail.jar

# Download Activation
wget https://repo1.maven.org/maven2/javax/activation/activation/1.1.1/activation-1.1.1.jar -O lib/activation.jar
```

## Gmail Setup

1. Go to Google Account Settings
2. Enable 2-Factor Authentication
3. Generate an "App Password":
   - Go to Security → 2-Step Verification → App Passwords
   - Select "Mail" and "Windows Computer"
   - Copy the 16-character password

4. In StockVault Settings:
   - Enter your Gmail address
   - Paste the App Password (NOT your regular password)
   - Enable "Daily Summary at 09:00 AM"
   - Click "Test Email" to verify

## Features

- **Daily Portfolio Summary**: Automatic email at 9:00 AM every day
- **Performance Metrics**: Total value, profit/loss, return percentage
- **Holdings Table**: All your stocks with current prices
- **Alerts**: Notifications for big movers (>5% change)
- **Beautiful HTML Design**: Professional email template

## Troubleshooting

If emails don't send:
1. Check that you're using an App Password, not your regular Gmail password
2. Verify 2-Factor Authentication is enabled
3. Check firewall/antivirus isn't blocking port 587
4. Try the "Test Email" button in Settings
5. Check console output for error messages
