package com.willow.accreditation.model;

import com.willow.accreditation.util.AccreditationStatus;
import com.willow.accreditation.util.AccreditationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Accreditation {
    private UUID id;
    private String userId;
    private AccreditationType type;
    private AccreditationStatus status;
    private String documentName;
    private String documentMimeType;
    private String documentContentBase64;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private String adminNotes;

    public Accreditation() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = this.createdAt;
    }


}