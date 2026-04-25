package com.ecoatm.salesplatform.dto;

import java.util.List;

public record PODetailUploadResult(
        int createdCount,
        int deletedCount,
        int skippedCount,
        List<UploadError> errors) {

    public record UploadError(
            int rowNumber,
            String productId,
            String grade,
            String buyerCode,
            String reason) {}
}
