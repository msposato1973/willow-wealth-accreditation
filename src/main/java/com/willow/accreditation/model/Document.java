package com.willow.accreditation.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @NotBlank(message = "Document name is required")
    private String name;

    @NotBlank(message = "MIME type is required")
    private String mimeType;

    @NotBlank(message = "Document content is required")
    private String content;
}
