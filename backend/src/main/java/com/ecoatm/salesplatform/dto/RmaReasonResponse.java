package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.pws.RmaReason;

public class RmaReasonResponse {

    private Long id;
    private String reason;

    public RmaReasonResponse() {}

    public RmaReasonResponse(Long id, String reason) {
        this.id = id;
        this.reason = reason;
    }

    public static RmaReasonResponse fromEntity(RmaReason entity) {
        return new RmaReasonResponse(entity.getId(), entity.getValidReasons());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
