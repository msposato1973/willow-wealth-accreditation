package com.willow.accreditation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class AccreditationException extends RuntimeException {
    private final HttpStatus status;

    public AccreditationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public AccreditationException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}