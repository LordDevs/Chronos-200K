#!/bin/bash
set -euo pipefail

echo "Compiling Chronos-200K Java sources..."
CLASSPATH="./Backend/lib/*"
mkdir -p ./Backend/bin
javac -cp "$CLASSPATH" \
  ./Backend/src/*.java \
  ./Backend/src/evolution/*.java \
  -d ./Backend/bin

echo "Starting Tomcat on :8080 ..."
java -cp "./Backend/bin:$CLASSPATH" ServletMain
