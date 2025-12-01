package com.bajaj.finserv.service;

import org.springframework.stereotype.Service;

@Service
public class SqlProblemSolver {

    /**
     * Solves the SQL problem based on the registration number's last two digits
     * Odd -> Problem 1: Find highest salary not paid on 1st day of month
     * Even -> Problem 2: Count younger employees in same department
     */
    public String solveProblem(String regNo) {
        String lastTwoDigits = regNo.substring(regNo.length() - 2);
        int lastTwoNum = Integer.parseInt(lastTwoDigits);
        
        if (lastTwoNum % 2 == 1) {
            // Odd - Problem 1: Highest salary not on 1st day
            return getProblem1Solution();
        } else {
            // Even - Problem 2: Count younger employees
            return getProblem2Solution();
        }
    }

    private String getProblem1Solution() {
        return """
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
            """;
    }

    private String getProblem2Solution() {
        return """
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
            """;
    }
}
