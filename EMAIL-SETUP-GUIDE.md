# 📧 Email Notification Setup Guide

## ⚠️ IMPORTANT: Gmail App Password Required

Your email notifications are **NOT working** because Gmail no longer accepts regular passwords for SMTP authentication. You need to generate a special **App Password**.

---

## 🔧 Quick Fix (5 minutes)

### Step 1: Enable 2-Factor Authentication
1. Go to: https://myaccount.google.com/security
2. Find "2-Step Verification" section
3. Click "Get Started" and follow the prompts
4. Verify your phone number

### Step 2: Generate App Password
1. Go to: https://myaccount.google.com/apppasswords
2. You'll see "App passwords" section
3. Click "Select app" → Choose **"Mail"**
4. Click "Select device" → Choose **"Other (Custom name)"**
5. Type: **"StockVault"**
6. Click **"Generate"**
7. You'll see a 16-character password like: `xxxx xxxx xxxx xxxx`
8. **COPY THIS PASSWORD** (you won't see it again!)

### Step 3: Update EmailService.java
1. Open file: `src/com/portfolio/service/EmailService.java`
2. Find line 14 (around line 14-20):
   ```java
   private String senderPassword = "Stockvault321";  // ⚠️ REPLACE WITH APP PASSWORD!
   ```
3. Replace `"Stockvault321"` with your App Password (remove spaces):
   ```java
   private String senderPassword = "abcdabcdabcdabcd";  // Your 16-char App Password
   ```
4. Save the file

### Step 4: Recompile
Run this command in your terminal:
```bash
javac -cp "lib/*;src" -d bin src/com/portfolio/service/EmailService.java
```

### Step 5: Test
1. Restart your application
2. Go to "My Portfolio" page
3. Click "📧 Send Email Now" button
4. Check your Gmail inbox at: mhnu.6180@gmail.com

---

## ✅ What Will Work After Setup

1. **Manual Email**: Click "Send Email Now" button anytime
2. **Daily Auto-Email**: Automatic email every day at 8:00 PM
3. **Portfolio Summary**: Shows total value, profit/loss, holdings
4. **Market Alerts**: Warns about stocks with >5% change
5. **Beautiful HTML**: Professional email design with colors

---

## 📊 Email Content Preview

Your daily email will include:
- 💎 Total Portfolio Value
- 💰 Total Investment
- 📈 Profit/Loss (with percentage)
- 📋 All your holdings with current prices
- ⚠️ Market alerts for significant changes
- 🎨 Color-coded gains (green) and losses (red)

---

## 🔍 Troubleshooting

### Error: "Username and Password not accepted"
- ❌ You're using regular Gmail password
- ✅ Generate and use App Password (see steps above)

### Error: "Authentication failed"
- Check if 2FA is enabled on your Gmail account
- Make sure you copied the App Password correctly (no spaces)
- Verify the email is: stockvault123@gmail.com

### Email not arriving?
- Check spam/junk folder
- Verify recipient email: mhnu.6180@gmail.com
- Check console output for error messages

### Still not working?
- Enable debug mode (already enabled in code)
- Check console for detailed SMTP logs
- Verify internet connection
- Try generating a new App Password

---

## 📝 Current Configuration

- **Sender Email**: stockvault123@gmail.com
- **Recipient Email**: mhnu.6180@gmail.com
- **Schedule**: Every day at 8:00 PM (20:00)
- **SMTP Server**: smtp.gmail.com:587
- **Security**: TLS 1.2

---

## 🎯 Next Steps

1. ✅ Generate App Password (5 minutes)
2. ✅ Update EmailService.java with App Password
3. ✅ Recompile the code
4. ✅ Test by clicking "Send Email Now"
5. ✅ Wait for automatic email at 8:00 PM

---

## 💡 Pro Tips

- **Keep App Password secure**: Don't share it or commit to Git
- **Multiple devices**: Generate separate App Passwords for each app
- **Revoke access**: You can revoke App Passwords anytime from Google Account settings
- **Backup**: Save your App Password in a secure password manager

---

## 🔗 Useful Links

- Google Account Security: https://myaccount.google.com/security
- App Passwords: https://myaccount.google.com/apppasswords
- 2-Step Verification: https://myaccount.google.com/signinoptions/two-step-verification

---

**Need Help?** Check the console output when you click "Send Email Now" - it will show detailed error messages and instructions.
