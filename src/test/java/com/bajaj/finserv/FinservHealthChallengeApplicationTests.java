package com.bajaj.finserv;

import com.bajaj.finserv.service.SqlProblemSolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FinservHealthChallengeApplicationTests {

    @Autowired
    private SqlProblemSolver sqlProblemSolver;

    @Test
    void contextLoads() {
        assertNotNull(sqlProblemSolver);
    }

    @Test
    void testOddRegistrationNumber() {
        String query = sqlProblemSolver.solveProblem("REG12347");
        assertTrue(query.contains("WHERE DAY(p.PAYMENT_TIME) != 1"));
        assertTrue(query.contains("ORDER BY p.AMOUNT DESC"));
    }

    @Test
    void testEvenRegistrationNumber() {
        String query = sqlProblemSolver.solveProblem("REG12348");
        assertTrue(query.contains("YOUNGER_EMPLOYEES_COUNT"));
        assertTrue(query.contains("ORDER BY e1.EMP_ID DESC"));
    }
}
