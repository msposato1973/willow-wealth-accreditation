package com.willow.accreditation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
}
