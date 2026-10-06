@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if not exist "YagSatis-v1.0.0.apk" (
  echo APK bulunamadi. Once build-apk.bat calistiriliyor...
  call build-apk.bat
  if errorlevel 1 exit /b 1
)

if not defined ANDROID_HOME (
  if exist "%LOCALAPPDATA%\Android\Sdk" set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
)
set "ADB=%ANDROID_HOME%\platform-tools\adb.exe"
if not exist "%ADB%" (
  echo [HATA] adb bulunamadi. Android SDK Platform-Tools kurulu olmali.
  pause
  exit /b 1
)

echo Telefonunuzda Gelistirici Secenekleri ve USB hata ayiklama acik olmali.
"%ADB%" devices
echo.
"%ADB%" install -r "YagSatis-v1.0.0.apk"
if errorlevel 1 (
  echo.
  echo [HATA] Kurulum basarisiz. Telefonda USB hata ayiklama iznini onaylayin.
  pause
  exit /b 1
)

echo.
echo [OK] Yag Satis telefona kuruldu.
pause
