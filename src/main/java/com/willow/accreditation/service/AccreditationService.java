package com.willow.accreditation.service;

import com.willow.accreditation.exception.InvalidRequestException;
import com.willow.accreditation.model.*;
import com.willow.accreditation.repository.InMemoryAccreditationRepository;
import com.willow.accreditation.util.AccreditationStatus;
import com.willow.accreditation.util.AccreditationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccreditationService {

    @Autowired
    private InMemoryAccreditationRepository repository;

    public UUID createAccreditation(String userId, String accreditationTypeStr,
                                    CreateAccreditationRequest.Document document) {

        if (repository.existsPendingForUser(userId)) {
            throw new InvalidRequestException("User already has a PENDING accreditation request");
        }

        AccreditationType type;
        try {
            type = AccreditationType.valueOf(accreditationTypeStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid accreditation_type: " + accreditationTypeStr);
        }

        Accreditation accreditation = new Accreditation();
        accreditation.setUserId(userId);
        accreditation.setType(type);
        accreditation.setStatus(AccreditationStatus.PENDING);
        accreditation.setDocumentName(document.getName());
        accreditation.setDocumentMimeType(document.getMimeType());
        accreditation.setDocumentContentBase64(document.getContent());
        accreditation.setCreatedAt(LocalDateTime.now());
        accreditation.setLastUpdatedAt(LocalDateTime.now());

        repository.save(accreditation);

        return accreditation.getId();
    }

    public UUID finalizeAccreditation(UUID accreditationId, String outcome, String adminNotes) {
        Accreditation accreditation = repository.findById(accreditationId)
                .orElseThrow(() -> new InvalidRequestException("Accreditation not found with ID: " + accreditationId));

        if (accreditation.getStatus() == AccreditationStatus.FAILED) {
            throw new InvalidRequestException("Cannot update a FAILED accreditation");
        }

        if (accreditation.getStatus() == AccreditationStatus.CONFIRMED &&
                !"EXPIRED".equals(outcome)) {
            throw new InvalidRequestException("CONFIRMED accreditation can only be changed to EXPIRED");
        }

        AccreditationStatus newStatus;
        try {
            newStatus = AccreditationStatus.valueOf(outcome);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid outcome: " + outcome);
        }

        accreditation.setStatus(newStatus);
        accreditation.setLastUpdatedAt(LocalDateTime.now());
        accreditation.setAdminNotes(adminNotes);

        repository.save(accreditation);

        return accreditation.getId();
    }

    public UserAccreditationsResponse getAccreditationsForUser(String userId) {
        return new UserAccreditationsResponse(userId, repository.findByUserId(userId));
    }
}