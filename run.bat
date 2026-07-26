@echo off
setlocal

echo Compiling Chronos-200K Java sources...
set CLASSPATH=./Backend/lib/*
if not exist Backend\bin mkdir Backend\bin
javac -cp "%CLASSPATH%" Backend\src\*.java Backend\src\evolution\*.java -d Backend\bin
if errorlevel 1 exit /b 1

echo Starting Tomcat on :8080 ...
java -cp "./Backend/bin;%CLASSPATH%" ServletMain
