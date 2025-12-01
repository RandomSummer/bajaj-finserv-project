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
            System.out.println("Application started. Initiating Bajaj Finserv Health Challenge...");
            
            // Step 1: Generate webhook
            WebhookResponse webhookResponse = generateWebhook();
            
            if (webhookResponse != null) {
                System.out.println("Webhook generated successfully");
                System.out.println("Webhook URL: " + webhookResponse.getWebhookUrl());
                System.out.println("Access Token: " + webhookResponse.getAccessToken());
                
                // Step 2: Solve the problem
                String regNo = "REG12347"; // Using sample registration number
                String sqlSolution = sqlProblemSolver.solveProblem(regNo);
                
                System.out.println("SQL Solution generated:");
                System.out.println(sqlSolution);
                
                // Step 3: Submit the solution
                submitSolution(sqlSolution, webhookResponse.getAccessToken());
            }
            
        } catch (Exception e) {
            System.err.println("Error during challenge execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private WebhookResponse generateWebhook() {
        try {
            WebhookRequest request = new WebhookRequest("John Doe", "REG12347", "john@example.com");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                WEBHOOK_GENERATION_URL, entity, WebhookResponse.class);
            
            return response.getBody();
            
        } catch (Exception e) {
            System.err.println("Error generating webhook: " + e.getMessage());
            return null;
        }
    }

    private void submitSolution(String sqlQuery, String accessToken) {
        try {
            SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken); // Direct token without "Bearer " prefix
            
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);
            
            System.out.println("Submitting solution to: " + TEST_WEBHOOK_BASE_URL);
            System.out.println("Authorization token: " + accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
            System.out.println("Request body: " + solutionRequest.getFinalQuery());
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                TEST_WEBHOOK_BASE_URL, entity, String.class);
            
            System.out.println("Solution submitted successfully!");
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response: " + response.getBody());
            
        } catch (Exception e) {
            System.err.println("Error submitting solution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
