# Bajaj Finserv Health Challenge - Spring Boot Solution

## Overview

This Spring Boot application solves the Bajaj Finserv Health coding challenge. The application:

1. **Generates a webhook** by sending a POST request on startup
2. **Solves SQL problems** based on the registration number's last two digits
3. **Submits the solution** using JWT token authentication

## Problem Analysis

The challenge involves two SQL problems based on employee database tables:

### Tables Structure:
- **DEPARTMENT**: Contains department information (ID, NAME)
- **EMPLOYEE**: Contains employee details (ID, FIRST_NAME, LAST_NAME, DOB, GENDER, DEPARTMENT)
- **PAYMENTS**: Contains salary payment records (ID, EMP_ID, AMOUNT, PAYMENT_TIME)

### SQL Problems:

#### Problem 1 (Odd registration numbers):
Find the highest salary that was credited to an employee, but only for transactions that were NOT made on the 1st day of any month. Include employee name, age, and department.

**Solution Query:**
```sql
SELECT 
    p.AMOUNT as SALARY,
    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) as NAME,
    TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) as AGE,
    d.DEPARTMENT_NAME
FROM PAYMENTS p
JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE DAY(p.PAYMENT_TIME) != 1
ORDER BY p.AMOUNT DESC
LIMIT 1
```

#### Problem 2 (Even registration numbers):
Calculate the number of employees who are younger than each employee, grouped by their respective departments.

**Solution Query:**
```sql
SELECT 
    e1.EMP_ID,
    e1.FIRST_NAME,
    e1.LAST_NAME,
    d.DEPARTMENT_NAME,
    (SELECT COUNT(*) 
     FROM EMPLOYEE e2 
     WHERE e2.DEPARTMENT = e1.DEPARTMENT 
     AND e2.DOB > e1.DOB) as YOUNGER_EMPLOYEES_COUNT
FROM EMPLOYEE e1
JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID
ORDER BY e1.EMP_ID DESC
```

## Project Structure

```
src/
├── main/
│   ├── java/com/bajaj/finserv/
│   │   ├── FinservHealthChallengeApplication.java
│   │   ├── config/
│   │   │   └── AppConfig.java
│   │   ├── model/
│   │   │   ├── WebhookRequest.java
│   │   │   ├── WebhookResponse.java
│   │   │   └── SolutionRequest.java
│   │   └── service/
│   │       ├── ChallengeService.java
│   │       └── SqlProblemSolver.java
│   └── resources/
│       └── application.properties
└── pom.xml
```

## Key Features

- **Automatic Startup**: Application automatically initiates the challenge flow on startup
- **REST Communication**: Uses RestTemplate for HTTP communications
- **JWT Authentication**: Handles JWT token authentication for solution submission
- **Dynamic Problem Solving**: Automatically determines which SQL problem to solve based on registration number
- **Comprehensive Logging**: Detailed logging for debugging and monitoring

## Dependencies

- Spring Boot Starter Web
- Spring Boot Starter WebFlux
- Jackson for JSON processing
- JWT libraries for token handling

## How to Run

1. **Clone the repository**
2. **Navigate to project directory**
3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   Or build and run the JAR:
   ```bash
   mvn clean package
   java -jar target/finserv-health-challenge-0.0.1-SNAPSHOT.jar
   ```

## API Endpoints Used

- **Webhook Generation**: `POST https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA`
- **Solution Submission**: `POST https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA`

## Sample Input/Output

### Webhook Generation Request:
```json
{
    "name": "John Doe",
    "regNo": "REG12347",
    "email": "john@example.com"
}
```

### Solution Submission:
```json
{
    "finalQuery": "SELECT p.AMOUNT as SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) as NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) as AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE DAY(p.PAYMENT_TIME) != 1 ORDER BY p.AMOUNT DESC LIMIT 1"
}
```

## Requirements Compliance

✅ Uses RestTemplate with Spring Boot  
✅ No controller/endpoint triggers the flow (automatic startup)  
✅ Uses JWT in Authorization header for second API  
✅ Solves SQL problems based on registration number logic  
✅ Generates final JAR output  

## Author

Solution for Bajaj Finserv Health Technical Challenge
