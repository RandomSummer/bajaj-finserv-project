package com.bajaj.finserv.service;

import org.springframework.stereotype.Service;

@Service
public class SqlProblemSolver {

    /**
     * Solves the SQL problem based on the registration number's last two digits
     * Odd -> Problem 1: Find highest salaried employee per department, excluding 1st day of month payments
     */
    public String solveProblem(String regNo) {
        String lastTwoDigits = regNo.substring(regNo.length() - 2);
        int lastTwoNum = Integer.parseInt(lastTwoDigits);
        
        if (lastTwoNum % 2 == 1) {
            // Odd - Problem 1: Highest salaried employee per department (excluding 1st day payments)
            return getProblem1Solution();
        } else {
            // Even - Problem 2: Not implemented as per requirements
            throw new UnsupportedOperationException("Even registration numbers not supported in this implementation");
        }
    }

    private String getProblem1Solution() {
        // Problem: Find the highest salaried employee per department,
        // but do not include payments made on the 1st day of the month
        return """
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
            """;
    }
}
