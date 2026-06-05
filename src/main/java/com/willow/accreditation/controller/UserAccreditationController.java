package com.willow.accreditation.controller;

import com.willow.accreditation.model.UserAccreditationsResponse;
import com.willow.accreditation.service.AccreditationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Client API", description = "Endpoints for client applications")
public class UserAccreditationController {

    @Autowired
    private AccreditationService accreditationService;

    @GetMapping("/user/{userId}/accreditation")
    @Operation(summary = "Get user accreditations",
            description = "Returns all accreditation statuses for a user")
    public ResponseEntity<UserAccreditationsResponse> getUserAccreditations(
            @PathVariable String userId) {

        UserAccreditationsResponse response = accreditationService.getAccreditationsForUser(userId);
        return ResponseEntity.ok(response);
    }
}