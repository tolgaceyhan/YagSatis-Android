@echo off
setlocal EnableExtensions
cd /d "%~dp0"

echo ==============================================
echo   Yag Satis - Android APK Derleyici
echo ==============================================
echo.

if not defined JAVA_HOME (
  if exist "C:\Program Files\Android\Android Studio\jbr\bin\java.exe" set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
)

if not defined JAVA_HOME (
  where java >nul 2>&1
  if errorlevel 1 (
    echo [HATA] Java bulunamadi.
    echo Android Studio kuruluysa varsayilan JBR yolu kullanilir.
    echo Degilse JAVA_HOME degiskenini ayarlayin.
    pause
    exit /b 1
  )
)

if not defined ANDROID_HOME (
  if exist "%LOCALAPPDATA%\Android\Sdk" set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
)
if not defined ANDROID_SDK_ROOT set "ANDROID_SDK_ROOT=%ANDROID_HOME%"

if not defined ANDROID_HOME (
  echo [HATA] Android SDK bulunamadi.
  echo Android Studio ^> Tools ^> SDK Manager uzerinden Android SDK 35 kurun.
  pause
  exit /b 1
)

set "SDK_PATH=%ANDROID_HOME:\=/%"
> local.properties echo sdk.dir=%SDK_PATH%

echo Java: %JAVA_HOME%
echo Android SDK: %ANDROID_HOME%
echo.
echo Gradle/Android bagimliliklari ilk calistirmada internetten indirilebilir.
echo.

call gradlew.bat assembleDebug
if errorlevel 1 (
  echo.
  echo [HATA] APK derlenemedi.
  echo Android Studio SDK Manager'da Android 15 / API 35 ve Build Tools kurulu oldugunu kontrol edin.
  pause
  exit /b 1
)

set "APK=app\build\outputs\apk\debug\app-debug.apk"
if not exist "%APK%" (
  echo [HATA] Derleme tamamlandi ancak APK bulunamadi.
  pause
  exit /b 1
)

copy /Y "%APK%" "YagSatis-v1.0.0.apk" >nul

echo.
echo ==============================================
echo [OK] APK hazir:
echo %CD%\YagSatis-v1.0.0.apk
echo ==============================================
echo.
pause
