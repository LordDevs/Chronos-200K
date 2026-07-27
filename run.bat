@echo off
setlocal EnableExtensions

cd /d "%~dp0"

echo Compiling Chronos-200K...

set "JAVA_EXE="
set "JAVAC_EXE="

if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
  set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
  set "JAVAC_EXE=%JAVA_HOME%\bin\javac.exe"
)

if not defined JAVA_EXE if exist "%USERPROFILE%\AppData\Roaming\Code\User\globalStorage\pleiades.java-extension-pack-jdk\java\21\bin\java.exe" (
  set "JAVA_EXE=%USERPROFILE%\AppData\Roaming\Code\User\globalStorage\pleiades.java-extension-pack-jdk\java\21\bin\java.exe"
  set "JAVAC_EXE=%USERPROFILE%\AppData\Roaming\Code\User\globalStorage\pleiades.java-extension-pack-jdk\java\21\bin\javac.exe"
)

if not defined JAVA_EXE (
  where java >nul 2>&1
  if not errorlevel 1 (
    for /f "delims=" %%i in ('where java') do (
      if not defined JAVA_EXE set "JAVA_EXE=%%i"
    )
  )
)

if not defined JAVA_EXE (
  echo ERROR: Java not found. Install JDK 17+ or set JAVA_HOME.
  exit /b 1
)

if not defined JAVAC_EXE (
  for %%I in ("%JAVA_EXE%") do set "JAVAC_EXE=%%~dpIjavac.exe"
)

echo Using: %JAVA_EXE%
"%JAVA_EXE%" -version 2>&1
if errorlevel 1 (
  echo ERROR: Java failed to start. Avoid Oracle javapath shims — set JAVA_HOME to a real JDK.
  exit /b 1
)

set "CLASSPATH=./Backend/lib/*"
if not exist Backend\bin mkdir Backend\bin

"%JAVAC_EXE%" -cp "%CLASSPATH%" Backend\src\evolution\*.java Backend\src\simulation\*.java Backend\src\*.java -d Backend\bin
if errorlevel 1 (
  echo ERROR: Compilation failed.
  exit /b 1
)

echo.
echo Starting CHRONOS on http://127.0.0.1:8080/
echo Keep this window open. Press Ctrl+C to stop.
echo.
"%JAVA_EXE%" -cp "./Backend/bin;%CLASSPATH%" ServletMain
