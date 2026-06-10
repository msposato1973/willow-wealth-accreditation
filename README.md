# Willow Wealth Accreditation Service

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation & Running](#installation--running)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Configuration](#configuration)
- [Business Rules](#business-rules)
- [Audit Logging](#audit-logging)
- [Scaling & Performance](#scaling--performance)
- [Troubleshooting](#troubleshooting)
- [Future Enhancements](#future-enhancements)

## Copilot
 I **"Used GitHub Copilot for :
 - Some code  generation and writing unit tests"**,
 - "Used ChatGPT for writing documentation and code comments"**.
 - Generating some comment useful for README formatting style.**

## Overview

The **Willow Wealth Accreditation Service** is a RESTful HTTP service that manages user accreditation status for investment compliance. 
It enables administrators to track accreditation requests and serves client-facing applications with real-time accreditation status.

### Problem Statement
As per SEC regulations, Willow Wealth's investments are only open to accredited investors. 
This service manages the verification workflow ensuring compliance while maintaining a seamless user experience.

## Features

### ✅ Admin Functionality
- Create accreditation requests with document upload
- Finalize accreditation status (CONFIRMED, EXPIRED, FAILED)
- Automatic expiry of CONFIRMED statuses after 30 days
- Business rule enforcement (no duplicate PENDING requests)

### ✅ Client Functionality
- Retrieve all accreditation statuses for a user
- Real-time status updates
- Clean RESTful API design

### ✅ Technical Features
- Input validation with meaningful error messages
- In-memory storage (ConcurrentHashMap for thread safety)
- Background scheduler for automated expiry
- Comprehensive error handling
- Full test coverage (unit + integration)

## Architecture

### High-Level Architecture
```
+------------------+       +------------------+       +------------------+
|  Client Apps     | <---> |  Accreditation   | <---> |  In-Memory Storage  |
|  (Web/Mobile)    |       |  Service         |       |       |  (ConcurrentHashMap) |
+------------------+       +------------------+       +------------------+
|  Admin Interface | <---> |  Accreditation   |
|  (Web Portal)    |       |  Service         |
+------------------+       +------------------+
``` 
### Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.1.5
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito
- **Documentation**: Swagger/OpenAPI  
- **Scheduling**: Spring's @Scheduled for background tasks
- **Storage**: In-memory ConcurrentHashMap
- **Logging**: SLF4J with Logback
- **Version Control**: Git
- **License**: MIT License
- **CI/CD**: GitHub Actions (optional for future enhancements)
## Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git (for cloning the repository)
- IDE (optional but recommended: IntelliJ IDEA, Eclipse)
- Postman or similar tool for API testing
- Swagger UI for API documentation (available at /swagger-ui.html when running the service)


### Design Patterns Applied

| Pattern | Application | Benefit |
|---------|-------------|---------|
| **Layered Architecture** | Controller → Service → Repository | Separation of concerns, testability |
| **Dependency Injection** | Spring `@Autowired` | Loose coupling, easier testing |
| **Repository Pattern** | `InMemoryAccreditationRepository` | Abstraction of data access |
| **Singleton Pattern** | Spring Beans (controllers, services) | Efficient resource usage |
| **Factory Pattern** | Response object creation | Consistent API responses |
| **Strategy Pattern** | Status transition logic | Encapsulated business rules |

### Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.1.5 |
| Build Tool | Maven | 3.6+ |
| Testing | JUnit 5, Mockito | Latest |
| Storage | ConcurrentHashMap (in-memory) | - |

## Prerequisites

Before running the service, ensure you have the following installed:

```bash
# Check Java version (must be 17 or higher)
java -version

# Check Maven version (must be 3.6 or higher)
mvn -version

# Check Git (for cloning)
git --version
```



## Installation & Running
1. **Clone the Repository**
   git clone https://github.com/willow-wealth/accreditation-service.git
   cd willow-wealth-accreditation-service

```bash
git clone
cd willow-wealth-accreditation-service
``` 
2. **Build the Project**
```bash
mvn clean install
```
3. **Run the Service**
```bash
mvn spring-boot:run 
```
The service will start on `http://localhost:8080`.
## API Documentation

## Expected Output
### Create Accreditation Request
=========================================
Willow Wealth Accreditation Service
=========================================

Building the application...
[INFO] Scanning for projects...
[INFO] Building jar: target/accreditation-1.0.0.jar

Build successful! Starting the service on port 9999...



2024-01-15 10:30:45 - Started WillowAccreditationApplication in 2.345 seconds



## Verify the Service
# Health check (if actuator enabled)
curl http://localhost:9999/actuator/health

# Or test an endpoint
curl http://localhost:9999/user/test-user/accreditation
{
  "userId": "test-user",
  "statuses": []
}
```
Admin Endpoints
- **Create Accreditation Request**
  - `POST /admin/accreditation` 
  - Request Body:
    ```json
    
  {
  "user_id": "g8NlYJnk7zK9BlB1J2Ebjs0AkhCTpE1V",
  "accreditation_type": "BY_INCOME",
  "document": {
    "name": "2018_tax_return.pdf",
    "mime_type": "application/pdf",
    "content": "ICAiQC8qIjogWyJzcmMvKiJdCiAgICB9CiAgfQp9Cg=="
  }
}
    ```


Error Responses:

400 Bad Request - Validation failed or duplicate PENDING request

400 Bad Request - Invalid accreditation_type (must be BY_INCOME or BY_NET_WORTH)

400 Bad Request - Invalid mime_type (must be application/pdf, image/jpeg, or image/png)


curl -X POST http://localhost:8080/user/accreditation \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "user123",
    "accreditation_type": "BY_INCOME",
    "document": {
      "name": "income_statement.pdf",
      "mime_type": "application/pdf",
      "content": "base64encodedcontenthere"
    }
  }'


2. Finalize Accreditation
Updates the status of an accreditation request.

Endpoint: PUT /user/accreditation/:accreditationId

Request Body:

```json
{
  "outcome": "CONFIRMED",
  "adminNotes": "Verified tax documents - meets income threshold"
}      
```
Expected Response:
```json
{
"accreditation_id": "87bb6030-458e-11ed-b023-039b275a916a"
}
```
200 OK - Accreditation status updated successfully
400 Bad Request - Invalid outcome (must be CONFIRMED, EXPIRED, or FAILED)
404 Not Found - Accreditation request not found

## Valid Outcomes:

CONFIRMED - Document verified and approved

EXPIRED - Document verification expired (30 days)

FAILED - Document verification failed

## cURL Example:

curl -X PUT http://localhost:8080/user/accreditation/87bb6030-458e-11ed-b023-039b275a916a \
-H "Content-Type: application/json" \
-d '{"outcome": "CONFIRMED", "adminNotes": "All documents verified"}'


Client Endpoint
Get User Accreditations
Retrieves all accreditation statuses for a specific user.

Endpoint: GET /user/:userId/accreditation

CURL Example:
curl http://localhost:8080/user/user123/accreditation

Response:
```json
{
  "user_id": "g8NlYJnk7zK9BlB1J2Ebjs0AkhCTpE1V",
  "accreditation_statuses": {
    "87bb6030-458e-11ed-b023-039b275a916a": {
      "accreditation_type": "BY_INCOME",
      "status": "FAILED"
    },
    "c031a5da-d59a-4d35-b1f0-0f8324dcc156": {
      "accreditation_type": "BY_NET_WORTH",
      "status": "CONFIRMED"
    }
  }
}
```
200 OK - Accreditation statuses retrieved successfully
404 Not Found - User not found (if no accreditations exist for the user)

## Testing
### Unit Tests
- Test accreditation request creation with valid and invalid inputs
- Test accreditation finalization with valid and invalid outcomes
- Test retrieval of user accreditations
- Test business rule enforcement (no duplicate PENDING requests)
  Run All Tests
```bash
mvn test
```

# Unit tests
mvn test -Dtest=AccreditationServiceTest

# Controller tests
mvn test -Dtest=AdminAccreditationControllerTest

# Integration tests
mvn test -Dtest=AccreditationIntegrationTest

### Integration Tests
- Test end-to-end accreditation workflow (create → finalize → retrieve)
- Test background scheduler for automatic expiry of CONFIRMED statuses
- Test error handling for invalid inputs and edge cases
Run Integration Tests
- Integration tests can be run with a specific profile or command:
```bashmvn verify -Pintegration-tests
``` 
## Configuration
- Application properties (e.g., server port, scheduler settings) can be configured in `src/main/resources/application.properties`.
- Example:
```properties
server.port=8080
scheduler.expiry-cron=0 0 0 * * ? # Runs daily at midnight
``` 
## Business Rules
- A user cannot have more than one PENDING accreditation request at a time.
- CONFIRMED accreditation statuses automatically expire after 30 days.
- Only valid accreditation types (BY_INCOME, BY_NET_WORTH) are accepted.
- Only valid document MIME types (application/pdf, image/jpeg, image/png) are accepted.
  - Finalization outcomes must be one of CONFIRMED, EXPIRED, or FAILED.
  - All endpoints must return appropriate HTTP status codes and error messages for invalid requests.
  - The service must be thread-safe to handle concurrent requests without data corruption.
