# Bajaj Finserv Health Challenge - JAVA Qualifier 1

## 🎯 Overview

This Spring Boot application (Java 21) solves **Question 1 (Odd)** of the Bajaj Finserv Health coding challenge.

### What it does:
1. ✅ Sends POST request on startup to generate webhook
2. ✅ Receives webhook URL and JWT access token
3. ✅ Determines question based on last 2 digits of regNo (Odd → Question 1)
4. ✅ Solves SQL Problem 1: **Highest salaried employee per department** (excluding 1st day payments)
5. ✅ Submits solution using JWT authentication

---

## 📊 Database Schema

### Tables:

**DEPARTMENT**
| Column | Type | Description |
|--------|------|-------------|
| DEPARTMENT_ID | INT (PK) | Department identifier |
| DEPARTMENT_NAME | VARCHAR | Department name |

**EMPLOYEE**
| Column | Type | Description |
|--------|------|-------------|
| EMP_ID | INT (PK) | Employee identifier |
| FIRST_NAME | VARCHAR | First name |
| LAST_NAME | VARCHAR | Last name |
| DOB | DATE | Date of birth |
| GENDER | VARCHAR | Gender |
| DEPARTMENT | INT (FK) | References DEPARTMENT_ID |

**PAYMENTS**
| Column | Type | Description |
|--------|------|-------------|
| PAYMENT_ID | INT (PK) | Payment identifier |
| EMP_ID | INT (FK) | References EMP_ID |
| AMOUNT | DECIMAL | Salary amount |
| PAYMENT_TIME | DATETIME | Payment timestamp |

---

## 💡 SQL Problem 1 (Odd Registration Numbers)

**Problem Statement:**
> Find the highest salaried employee, per department, but do not include payments that were made on the 1st day of the month.

**Output Format:**
- `DEPARTMENT_NAME`: Name of the department
- `SALARY`: Total highest salary (excluding 1st day payments)
- `EMPLOYEE_NAME`: Combined FIRST_NAME and LAST_NAME (format: "John Doe")
- `AGE`: Age of the employee

### 🔧 SQL Solution:

```sql
WITH RankedSalaries AS (
    SELECT 
        d.DEPARTMENT_NAME,
        SUM(p.AMOUNT) as SALARY,
        CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) as EMPLOYEE_NAME,
        TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) as AGE,
        ROW_NUMBER() OVER (PARTITION BY d.DEPARTMENT_NAME ORDER BY SUM(p.AMOUNT) DESC) as rank_num
    FROM PAYMENTS p
    JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
    JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
    WHERE DAY(p.PAYMENT_TIME) != 1
    GROUP BY d.DEPARTMENT_NAME, e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, e.DOB
)
SELECT 
    DEPARTMENT_NAME,
    SALARY,
    EMPLOYEE_NAME,
    AGE
FROM RankedSalaries
WHERE rank_num = 1
ORDER BY DEPARTMENT_NAME
```

**Key Logic:**
1. ✅ Excludes payments on 1st day: `WHERE DAY(p.PAYMENT_TIME) != 1`
2. ✅ Groups by department: `PARTITION BY d.DEPARTMENT_NAME`
3. ✅ Calculates total salary per employee: `SUM(p.AMOUNT)`
4. ✅ Ranks employees by salary: `ROW_NUMBER() OVER (...ORDER BY SUM(p.AMOUNT) DESC)`
5. ✅ Returns top employee per department: `WHERE rank_num = 1`

---

## 🚀 How to Run

### Prerequisites:
- Java 21 (LTS)
- Maven 3.6+

### Using Maven:
```bash
# Clean and build
mvn clean package

# Run the application
mvn spring-boot:run
```

### Using JAR:
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/finserv-health-challenge-0.0.1-SNAPSHOT.jar
```

### Using Batch File (Windows):
```bash
run.bat
```

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/bajaj/finserv/
│   │   ├── FinservHealthChallengeApplication.java  # Main entry point
│   │   ├── config/
│   │   │   └── AppConfig.java                      # RestTemplate config
│   │   ├── model/
│   │   │   ├── WebhookRequest.java                 # POST body for webhook generation
│   │   │   ├── WebhookResponse.java                # Response from webhook API
│   │   │   └── SolutionRequest.java                # Solution submission body
│   │   └── service/
│   │       ├── ChallengeService.java               # Main orchestrator
│   │       └── SqlProblemSolver.java               # SQL solution generator
│   └── resources/
│       └── application.properties                  # Spring Boot config
└── test/
    └── java/com/bajaj/finserv/
        └── FinservHealthChallengeApplicationTests.java
```

---

## 🔄 Application Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Startup                       │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 1: Generate Webhook                                    │
│  POST https://bfhldevapigw.healthrx.co.in/hiring/          │
│       generateWebhook/JAVA                                   │
│  Body: { "name", "regNo", "email" }                         │
└─────────────────────────────────────────────────────────────┘
                            ↓
                    Receive Response:
                    • webhookUrl
                    • accessToken (JWT)
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 2: Solve SQL Problem                                   │
│  • Check last 2 digits of regNo                             │
│  • Odd (47) → Question 1                                    │
│  • Generate SQL solution                                     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  STEP 3: Submit Solution                                     │
│  POST {webhookUrl}                                           │
│  Headers:                                                    │
│    • Authorization: {accessToken}                           │
│    • Content-Type: application/json                         │
│  Body: { "finalQuery": "SQL_QUERY_HERE" }                  │
└─────────────────────────────────────────────────────────────┘
                            ↓
                      ✅ Success!
```

---

## 🔑 Key Implementation Details

### 1. No Controllers/Endpoints
The flow is triggered automatically on startup using `@EventListener(ApplicationReadyEvent.class)` - no manual endpoints needed.

### 2. RestTemplate Usage
Uses Spring's `RestTemplate` for HTTP communication (as per requirements).

### 3. JWT Authentication
The `accessToken` from webhook generation is used as-is in the `Authorization` header when submitting the solution.

### 4. Registration Number Logic
```java
String lastTwoDigits = regNo.substring(regNo.length() - 2);
int lastTwoNum = Integer.parseInt(lastTwoDigits);

if (lastTwoNum % 2 == 1) {
    // Odd → Question 1
    return getProblem1Solution();
}
```

---

## 📝 Configuration

Update these values in `ChallengeService.java`:

```java
// Your registration details
WebhookRequest request = new WebhookRequest(
    "John Doe",           // Your name
    "REG12347",          // Your registration number (must be ODD)
    "john@example.com"   // Your email
);
```

---

## ✅ Requirements Compliance

| Requirement | Status |
|-------------|--------|
| Use Spring Boot | ✅ |
| Use RestTemplate/WebClient | ✅ RestTemplate |
| No controller/endpoint triggers flow | ✅ Auto-startup |
| Generate webhook on startup | ✅ |
| Solve SQL based on regNo | ✅ |
| JWT Authorization | ✅ |
| Question 1 (Odd) implemented | ✅ |

---

## 🎓 Technologies Used

- **Java 21** (LTS)
- **Spring Boot 3.2.0**
- **Spring Web** (RestTemplate)
- **Jackson** (JSON processing)
- **JJWT** (JWT support)
- **Maven**

---

## 📦 Build Output

The build generates:
- `target/finserv-health-challenge-0.0.1-SNAPSHOT.jar` - Executable JAR file

---

## 🐛 Troubleshooting

### Common Issues:

1. **Connection refused**: Check if the API endpoint is accessible
2. **401 Unauthorized**: Verify JWT token is included in Authorization header
3. **SQL syntax error**: Verify the database dialect matches (MySQL syntax used)

---

## 📄 License

This project is created for the Bajaj Finserv Health coding challenge.

---

## 👤 Author

GitHub: [@RandomSummer](https://github.com/RandomSummer)

---

**Good Luck! 🚀**
