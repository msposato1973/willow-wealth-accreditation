package com.willow.accreditation.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.willow.accreditation.model.CreateAccreditationRequest;
import com.willow.accreditation.service.AccreditationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminAccreditationController.class)
class AdminAccreditationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccreditationService accreditationService;

    private CreateAccreditationRequest validRequest;
    private UUID accreditationId;

    @BeforeEach
    void setUp() {
        accreditationId = UUID.randomUUID();

        validRequest = new CreateAccreditationRequest();
        validRequest.setUserId("test-user-123");
        validRequest.setAccreditationType("BY_INCOME");

        CreateAccreditationRequest.Document document = new CreateAccreditationRequest.Document();
        document.setName("test.pdf");
        document.setMimeType("application/pdf");
        document.setContent("dGVzdCBjb250ZW50");
        validRequest.setDocument(document);
    }

    @Test
    void createAccreditation_ShouldReturnAccreditationId() throws Exception {
        when(accreditationService.createAccreditation(any(), any(), any()))
                .thenReturn(accreditationId);

        mockMvc.perform(post("/user/accreditation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accreditation_id").value(accreditationId.toString()));
    }

    @Test
    void createAccreditation_WithInvalidType_ShouldReturnBadRequest() throws Exception {
        validRequest.setAccreditationType("INVALID_TYPE");

        mockMvc.perform(post("/user/accreditation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAccreditation_WithoutUserId_ShouldReturnBadRequest() throws Exception {
        validRequest.setUserId(null);

        mockMvc.perform(post("/user/accreditation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void finalizeAccreditation_WithValidOutcome_ShouldReturnAccreditationId() throws Exception {
        String requestJson = "{\"outcome\": \"CONFIRMED\"}";

        when(accreditationService.finalizeAccreditation(any(), any(), any()))
                .thenReturn(accreditationId);

        mockMvc.perform(put("/user/accreditation/" + accreditationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accreditation_id").value(accreditationId.toString()));
    }

    @Test
    void finalizeAccreditation_WithInvalidOutcome_ShouldReturnBadRequest() throws Exception {
        String requestJson = "{\"outcome\": \"INVALID\"}";

        mockMvc.perform(put("/user/accreditation/" + accreditationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
