#!/bin/bash

echo "========================================="
echo "Willow Wealth Accreditation Service"
echo "========================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven."
    exit 1
fi

echo "Building the application..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "Build failed. Please check the errors above."
    exit 1
fi

echo ""
echo "Build successful! Starting the service on port 9999..."
echo ""

# Run the application with properties file
java -jar target/accreditation-1.0.0.jar --spring.config.location=file:./application.properties