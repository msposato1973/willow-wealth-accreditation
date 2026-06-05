package com.willow.accreditation.controller;

import com.willow.accreditation.model.Accreditation;
import com.willow.accreditation.util.*;

import com.willow.accreditation.service.AccreditationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAccreditationController.class)
class UserAccreditationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccreditationService accreditationService;

    private String userId;
    private List<Accreditation> accreditations;

    @BeforeEach
    void setUp() {
        userId = "test-user-123";

        Accreditation acc1 = new Accreditation();
        acc1.setId(UUID.randomUUID());
        acc1.setUserId(userId);
        acc1.setType(AccreditationType.BY_INCOME);
        acc1.setStatus(AccreditationStatus.CONFIRMED);

        Accreditation acc2 = new Accreditation();
        acc2.setId(UUID.randomUUID());
        acc2.setUserId(userId);
        acc2.setType(AccreditationType.BY_NET_WORTH);
        acc2.setStatus(AccreditationStatus.PENDING);

        accreditations = List.of(acc1, acc2);
    }

    @Test
    void getUserAccreditations_ShouldReturnAccreditations() throws Exception {
        when(accreditationService.getAccreditationsForUser(anyString()))
                .thenReturn(new com.willow.accreditation.model.UserAccreditationsResponse(userId, accreditations));

        mockMvc.perform(get("/user/" + userId + "/accreditation"))
                .andExpect(status().isOk());
    }
    @Test
    void getUserAccreditations_ReturnsEmptyStatusesWhenNoAccreditations() throws Exception {
        when(accreditationService.getAccreditationsForUser(anyString()))
                .thenReturn(new com.willow.accreditation.model.UserAccreditationsResponse(userId, List.of()));

        mockMvc.perform(get("/user/" + userId + "/accreditation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.accreditationStatuses").isMap())
                .andExpect(jsonPath("$.accreditationStatuses.length()").value(0));
    }

    @Test
    void getUserAccreditations_ReturnsInternalServerErrorWhenServiceFails() throws Exception {
        when(accreditationService.getAccreditationsForUser(anyString()))
                .thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(get("/user/" + userId + "/accreditation"))
                .andExpect(status().isInternalServerError());
    }


}