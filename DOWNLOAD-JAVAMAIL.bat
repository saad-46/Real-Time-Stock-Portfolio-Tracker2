@echo off
echo ========================================
echo   Downloading JavaMail Libraries
echo   100%% Pure Java - No External Services
echo ========================================
echo.

echo Downloading JavaMail API...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/sun/mail/javax.mail/1.6.2/javax.mail-1.6.2.jar' -OutFile 'lib\javax.mail.jar'"

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] JavaMail downloaded to lib\javax.mail.jar
) else (
    echo [ERROR] Failed to download JavaMail
    pause
    exit /b
)

echo.
echo Downloading Activation Framework...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/javax/activation/activation/1.1.1/activation-1.1.1.jar' -OutFile 'lib\activation.jar'"

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Activation Framework downloaded to lib\activation.jar
) else (
    echo [ERROR] Failed to download Activation Framework
    pause
    exit /b
)

echo.
echo ========================================
echo   SUCCESS! JavaMail Libraries Installed
echo ========================================
echo.
echo You can now send real emails from Java!
echo.
echo Next steps:
echo 1. Restart your application
echo 2. Go to Portfolio page
echo 3. Click "Send Email Report"
echo 4. Email will be sent to mhnu.6180@gmail.com
echo.
pause
