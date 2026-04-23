package com.ecoatm.salesplatform.dto;

import java.util.List;

public record ReserveBidUploadResult(
        int created,
        int updated,
        int unchanged,
        int auditsGenerated,
        List<UploadError> errors) {

    public record UploadError(int rowNumber, String productId, String grade, String reason) {}
}
