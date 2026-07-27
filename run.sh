#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

echo "Compiling Chronos-200K..."
CLASSPATH="./Backend/lib/*"
mkdir -p ./Backend/bin
javac -cp "$CLASSPATH" \
  ./Backend/src/evolution/*.java \
  ./Backend/src/simulation/*.java \
  ./Backend/src/*.java \
  -d ./Backend/bin

echo "Starting CHRONOS on http://127.0.0.1:8080/"
echo "Keep this terminal open. Ctrl+C to stop."
java -cp "./Backend/bin:$CLASSPATH" ServletMain
