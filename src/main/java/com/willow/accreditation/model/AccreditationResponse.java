package com.willow.accreditation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccreditationResponse {
    @JsonProperty("accreditation_id")
    private String accreditationId;
}