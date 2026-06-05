package com.willow.accreditation.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAccreditationsResponse {
    private String userId;
    private Map<String, AccreditationStatusInfo> accreditationStatuses;

    public UserAccreditationsResponse(String userId, List<Accreditation> accreditations) {
        this.userId = userId;
        this.accreditationStatuses = new HashMap<>();

        for (Accreditation acc : accreditations) {
            accreditationStatuses.put(
                    acc.getId().toString(),
                    new AccreditationStatusInfo(acc.getType().toString(), acc.getStatus().toString())
            );
        }
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Map<String, AccreditationStatusInfo> getAccreditationStatuses() { return accreditationStatuses; }
    public void setAccreditationStatuses(Map<String, AccreditationStatusInfo> accreditationStatuses) {
        this.accreditationStatuses = accreditationStatuses;
    }

    public static class AccreditationStatusInfo {
        private String accreditationType;
        private String status;

        public AccreditationStatusInfo(String accreditationType, String status) {
            this.accreditationType = accreditationType;
            this.status = status;
        }

        public String getAccreditationType() { return accreditationType; }
        public void setAccreditationType(String accreditationType) { this.accreditationType = accreditationType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}