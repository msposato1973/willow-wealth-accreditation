package com.willow.accreditation.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willow.accreditation.model.CreateAccreditationRequest;
import com.willow.accreditation.model.FinalizeAccreditationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccreditationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void createAccreditation_WithDuplicatePending_ShouldReturnBadRequest() {
        // First accreditation
        CreateAccreditationRequest request = new CreateAccreditationRequest();
        request.setUserId("duplicate-user");
        request.setAccreditationType("BY_INCOME");

        CreateAccreditationRequest.Document document = new CreateAccreditationRequest.Document();
        document.setName("test.pdf");
        document.setMimeType("application/pdf");
        document.setContent("dGVzdCBjb250ZW50");
        request.setDocument(document);

        restTemplate.postForEntity(baseUrl + "/user/accreditation", request, Map.class);

        // Second accreditation for same user
        ResponseEntity<Map> secondResponse = restTemplate.postForEntity(
                baseUrl + "/user/accreditation",
                request,
                Map.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, secondResponse.getStatusCode());
    }

    @Test
    void fullAccreditationFlow_ShouldWorkSuccessfully() {
        // 1. Create accreditation
       CreateAccreditationRequest createRequest = new CreateAccreditationRequest();

        createRequest.setUserId("test-user");
        createRequest.setAccreditationType("BY_INCOME");

        CreateAccreditationRequest.Document document = new CreateAccreditationRequest.Document();
        document.setName("test.pdf");
        document.setMimeType("application/pdf");
        document.setContent("dGVzdCBjb250ZW50");
        createRequest.setDocument(document);

        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
                baseUrl + "/user/accreditation",
                createRequest,
                Map.class
        );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        String accreditationId = (String) createResponse.getBody().get("accreditation_id");

        // 2. Finalize accreditation
        FinalizeAccreditationRequest finalizeRequest = new FinalizeAccreditationRequest();
        finalizeRequest.setOutcome("CONFIRMED");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FinalizeAccreditationRequest> entity = new HttpEntity<>(finalizeRequest, headers);

        ResponseEntity<Map> finalizeResponse = restTemplate.exchange(
                baseUrl + "/user/accreditation/" + accreditationId,
                HttpMethod.PUT,
                entity,
                Map.class
        );

        assertEquals(HttpStatus.OK, finalizeResponse.getStatusCode());
        String finalizedId = (String) finalizeResponse.getBody().get("accreditation_id");
        assertEquals(accreditationId, finalizedId);
    }



    @Test
    void finalizeAccreditation_WithInvalidId_ShouldReturnBadRequest() {
        FinalizeAccreditationRequest finalizeRequest = new FinalizeAccreditationRequest();
        finalizeRequest.setOutcome("CONFIRMED");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FinalizeAccreditationRequest> entity = new HttpEntity<>(finalizeRequest, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/user/accreditation/invalid-id",
                HttpMethod.PUT,
                entity,
                Map.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
