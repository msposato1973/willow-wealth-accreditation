package com.willow.accreditation.exception;

import java.util.UUID;

public class AccreditationNotFoundException extends RuntimeException {

    public AccreditationNotFoundException(String message) {
        super(message);
    }

    public AccreditationNotFoundException(UUID id) {
        super("Accreditation not found with ID: " + id);
    }
}