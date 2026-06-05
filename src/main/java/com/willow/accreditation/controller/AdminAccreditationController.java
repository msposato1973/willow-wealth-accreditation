package com.willow.accreditation.controller;

import com.willow.accreditation.model.CreateAccreditationRequest;
import com.willow.accreditation.model.FinalizeAccreditationRequest;
import com.willow.accreditation.service.AccreditationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Tag(name = "Admin API", description = "Endpoints for administrators")
public class AdminAccreditationController {

    @Autowired
    private AccreditationService accreditationService;

    @PostMapping("/user/accreditation")
    @Operation(summary = "Create a new accreditation request",
            description = "Submits a new accreditation request with document")
    public ResponseEntity<Map<String, String>> createAccreditation(
            @Valid @RequestBody CreateAccreditationRequest request) {

        UUID accreditationId = accreditationService.createAccreditation(
                request.getUserId(),
                request.getAccreditationType(),
                request.getDocument()
        );

        Map<String, String> response = new HashMap<>();
        response.put("accreditation_id", accreditationId.toString());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/accreditation/{accreditationId}")
    @Operation(summary = "Finalize an accreditation request",
            description = "Confirm, expire, or fail an accreditation")
    public ResponseEntity<Map<String, String>> finalizeAccreditation(
            @PathVariable UUID accreditationId,
            @Valid @RequestBody FinalizeAccreditationRequest request) {

        UUID id = accreditationService.finalizeAccreditation(
                accreditationId,
                request.getOutcome(),
                request.getAdminNotes()
        );

        Map<String, String> response = new HashMap<>();
        response.put("accreditation_id", id.toString());

        return ResponseEntity.ok(response);
    }
}