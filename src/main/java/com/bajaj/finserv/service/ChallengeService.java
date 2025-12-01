package com.bajaj.finserv.service;

import com.bajaj.finserv.model.SolutionRequest;
import com.bajaj.finserv.model.WebhookRequest;
import com.bajaj.finserv.model.WebhookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChallengeService {

    @Autowired
    private SqlProblemSolver sqlProblemSolver;

    private final RestTemplate restTemplate;
    private final String WEBHOOK_GENERATION_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private final String TEST_WEBHOOK_BASE_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    public ChallengeService() {
        this.restTemplate = new RestTemplate();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStartup() {
        try {
            System.out.println("==================================================");
            System.out.println("Bajaj Finserv Health Challenge - JAVA Qualifier 1");
            System.out.println("==================================================");
            
            // Step 1: Generate webhook
            System.out.println("\n[STEP 1] Generating Webhook...");
            WebhookResponse webhookResponse = generateWebhook();
            
            if (webhookResponse != null) {
                System.out.println("✓ Webhook generated successfully");
                System.out.println("  Webhook URL: " + webhookResponse.getWebhookUrl());
                System.out.println("  Access Token: " + webhookResponse.getAccessToken().substring(0, 30) + "...");
                
                // Step 2: Solve the SQL problem (Question 1 - Odd)
                System.out.println("\n[STEP 2] Solving SQL Problem...");
                String regNo = "22BCE10153"; // Odd registration number for Question 1
                System.out.println("  Registration: " + regNo);
                System.out.println("  Last 2 digits: 53 (Odd) -> Question 1");
                
                String sqlSolution = sqlProblemSolver.solveProblem(regNo);
                
                System.out.println("  ✓ SQL Solution generated:");
                System.out.println("\n" + sqlSolution);
                
                // Step 3: Submit the solution with JWT token
                System.out.println("\n[STEP 3] Submitting Solution...");
                submitSolution(sqlSolution, webhookResponse.getWebhookUrl(), webhookResponse.getAccessToken());
            } else {
                System.err.println("✗ Failed to generate webhook");
            }
            
        } catch (Exception e) {
            System.err.println("Error during challenge execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private WebhookResponse generateWebhook() {
        try {
            WebhookRequest request = new WebhookRequest(
                "Sk Sofiquee Fiaz", 
                "22BCE10153", 
                "sksofiqueefiaz2015@gmail.com"
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                WEBHOOK_GENERATION_URL, entity, WebhookResponse.class);
            
            WebhookResponse webhookResponse = response.getBody();
            
            // Fallback to base URL if webhook URL is still null
            if (webhookResponse != null && webhookResponse.getWebhookUrl() == null) {
                System.out.println("  Warning: webhook URL is null, using fallback");
                webhookResponse.setWebhookUrl(TEST_WEBHOOK_BASE_URL);
            }
            
            return webhookResponse;
            
        } catch (Exception e) {
            System.err.println("Error generating webhook: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void submitSolution(String sqlQuery, String webhookUrl, String accessToken) {
        try {
            SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken); // JWT token as received
            
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);
            
            System.out.println("  Target URL: " + webhookUrl);
            System.out.println("  Authorization: Bearer Token (JWT)");
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                webhookUrl, entity, String.class);
            
            System.out.println("\n✓ Solution submitted successfully!");
            System.out.println("  Response Status: " + response.getStatusCode());
            System.out.println("  Response Body: " + response.getBody());
            System.out.println("\n==================================================");
            System.out.println("Challenge Completed Successfully!");
            System.out.println("==================================================");
            
        } catch (Exception e) {
            System.err.println("Error submitting solution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
