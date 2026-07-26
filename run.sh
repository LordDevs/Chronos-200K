#!/bin/bash

# Check if API_KEY.txt exists and has content
if [ ! -f "API_KEY.txt" ] || [ ! -s "API_KEY.txt" ]; then
    read -p "Enter your API key: " API_KEY
    echo "$API_KEY" > API_KEY.txt
    echo "API key saved to API_KEY.txt"
else
    echo "Using existing API key from API_KEY.txt"
    API_KEY=$(cat API_KEY.txt)
fi

# Compile all Java files
echo "Compiling Java files..."
CLASSPATH="./Backend/lib/*"
javac -cp "$CLASSPATH" ./Backend/src/*.java -d ./Backend/bin

# Run server
echo "Starting Tomcat server..."
java -cp "./Backend/bin:$CLASSPATH" ServletMain