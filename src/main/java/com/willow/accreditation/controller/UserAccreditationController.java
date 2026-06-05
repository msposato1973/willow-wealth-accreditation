package com.willow.accreditation.controller;

import com.willow.accreditation.model.UserAccreditationsResponse;
import com.willow.accreditation.service.AccreditationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserAccreditationController {

    @Autowired
    private AccreditationService accreditationService;

    @GetMapping("/user/{userId}/accreditation")
    public ResponseEntity<UserAccreditationsResponse> getUserAccreditations(
            @PathVariable String userId) {

        UserAccreditationsResponse response = accreditationService.getAccreditationsForUser(userId);
        return ResponseEntity.ok(response);
    }
}