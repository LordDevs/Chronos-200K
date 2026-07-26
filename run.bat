@echo off

REM Check if API_KEY.txt exists and has content
if not exist API_KEY.txt (
    set /p API_KEY="Enter your API key: "
    echo %API_KEY% > API_KEY.txt
    echo API key saved to API_KEY.txt
) else (
    echo Using existing API key from API_KEY.txt
    set /p API_KEY=<API_KEY.txt
)

echo Compiling Java files...
set CLASSPATH=./Backend/lib/*
set JAVAFILES=./Backend/src/*.java

javac -cp "%CLASSPATH%" %JAVAFILES% -d ./Backend/bin

REM Run server
echo Starting server...
java -cp "./Backend/bin;%CLASSPATH%" ServletMain