package com.willow.accreditation.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateAccreditationRequest {

    @NotBlank(message = "user_id is required")
    private String userId;

    @NotBlank(message = "accreditation_type is required")
    @Pattern(regexp = "^(BY_INCOME|BY_NET_WORTH)$",
            message = "accreditation_type must be BY_INCOME or BY_NET_WORTH")
    private String accreditationType;

    @NotNull(message = "document is required")
    @Valid
    private Document document;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAccreditationType() { return accreditationType; }
    public void setAccreditationType(String accreditationType) { this.accreditationType = accreditationType; }
    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public static class Document {
        @NotBlank(message = "document name is required")
        private String name;

        @NotBlank(message = "mime_type is required")
        @Pattern(regexp = "^(application/pdf|image/jpeg|image/png)$",
                message = "mime_type must be application/pdf, image/jpeg, or image/png")
        private String mimeType;

        @NotBlank(message = "document content is required")
        private String content;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}