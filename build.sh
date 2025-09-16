#!/bin/bash

# Apex Compiler Build Script

echo "Building Apex Compiler..."

# Create build directories
mkdir -p build/classes
mkdir -p build/lib

# Download dependencies if not present
COMMONS_CLI_JAR="build/lib/commons-cli-1.5.0.jar"
if [ ! -f "$COMMONS_CLI_JAR" ]; then
    echo "Downloading commons-cli..."
    curl -o "$COMMONS_CLI_JAR" "https://repo1.maven.org/maven2/commons-cli/commons-cli/1.5.0/commons-cli-1.5.0.jar"
fi

# Compile source files
echo "Compiling source files..."
find src/main/java -name "*.java" > sources.txt
javac -cp "$COMMONS_CLI_JAR" -d build/classes @sources.txt

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    
    # Create JAR file
    echo "Creating JAR file..."
    cd build/classes
    jar -cfm ../apex-compiler.jar ../../manifest.txt com/
    cd ../..
    
    # Copy dependencies
    cd build
    jar -xf lib/commons-cli-1.5.0.jar
    rm -rf META-INF
    jar -uf apex-compiler.jar org/
    
    # Update manifest with Main-Class
    jar -ufm apex-compiler.jar ../manifest.txt
    cd ..
    
    echo "Build complete! JAR file: build/apex-compiler.jar"
    echo ""
    echo "Usage: java -jar build/apex-compiler.jar [OPTIONS] <input-file>"
    echo "Example: java -jar build/apex-compiler.jar examples/Calculator.apex"
else
    echo "Compilation failed!"
    exit 1
fi

# Clean up
rm sources.txt