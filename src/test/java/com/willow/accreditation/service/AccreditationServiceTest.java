package com.willow.accreditation.service;


import com.willow.accreditation.exception.InvalidRequestException;
import com.willow.accreditation.model.*;
import com.willow.accreditation.util.*;
import com.willow.accreditation.repository.InMemoryAccreditationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccreditationServiceTest {

    @Mock
    private InMemoryAccreditationRepository repository;

    @InjectMocks
    private AccreditationService accreditationService;

    private String userId;
    private CreateAccreditationRequest.Document document;
    private Accreditation accreditation;

    @BeforeEach
    void setUp() {
        userId = "test-user-123";

        document = new CreateAccreditationRequest.Document();
        document.setName("test.pdf");
        document.setMimeType("application/pdf");
        document.setContent("dGVzdCBjb250ZW50");

        accreditation = new Accreditation();
        accreditation.setId(UUID.randomUUID());
        accreditation.setUserId(userId);
        accreditation.setType(AccreditationType.BY_INCOME);
        accreditation.setStatus(AccreditationStatus.PENDING);
    }


    @Test
    void createAccreditation_WithExistingPending_ShouldThrowException() {
        when(repository.existsPendingForUser(userId)).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> {
            accreditationService.createAccreditation(userId, "BY_INCOME", document);
        });
    }

    @Test
    void finalizeAccreditation_WithFailedStatus_ShouldThrowException() {
        accreditation.setStatus(AccreditationStatus.FAILED);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(accreditation));

        assertThrows(InvalidRequestException.class, () -> {
            accreditationService.finalizeAccreditation(accreditation.getId(), "CONFIRMED", null);
        });
    }

    @Test
    void finalizeAccreditation_WithConfirmedToExpired_ShouldWork() {
        accreditation.setStatus(AccreditationStatus.CONFIRMED);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(accreditation));

        UUID id = accreditationService.finalizeAccreditation(accreditation.getId(), "EXPIRED", null);

        assertEquals(AccreditationStatus.EXPIRED, accreditation.getStatus());
    }

    @Test
    void finalizeAccreditation_WithConfirmedToFailed_ShouldThrowException() {
        accreditation.setStatus(AccreditationStatus.CONFIRMED);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(accreditation));

        assertThrows(InvalidRequestException.class, () -> {
            accreditationService.finalizeAccreditation(accreditation.getId(), "FAILED", null);
        });
    }

    @Test
    void finalizeAccreditation_ShouldUpdateStatus() {
        accreditation.setStatus(AccreditationStatus.PENDING);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(accreditation));

        UUID id = accreditationService.finalizeAccreditation(accreditation.getId(), "CONFIRMED", null);

        assertEquals(AccreditationStatus.CONFIRMED, accreditation.getStatus());
    }

}
