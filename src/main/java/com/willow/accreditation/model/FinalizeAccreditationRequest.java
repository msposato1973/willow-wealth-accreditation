package com.willow.accreditation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class FinalizeAccreditationRequest {

    @NotBlank(message = "outcome is required")
    @Pattern(regexp = "^(CONFIRMED|EXPIRED|FAILED)$",
            message = "outcome must be CONFIRMED, EXPIRED, or FAILED")
    private String outcome;

    private String adminNotes;

    // Getters and Setters
    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}