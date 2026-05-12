package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequestPhoto;
import com.ecoatm.salesplatform.model.partialcredit.enums.PhotoKind;

import java.time.Instant;

/**
 * Metadata-only projection of a {@link CreditRequestPhoto} row — used
 * by the list endpoint where we deliberately do <i>not</i> ship the
 * bytea blob (the gallery component pulls each blob through the
 * dedicated download URL).
 */
public record PhotoMetadataView(
        Long id,
        Long creditRequestId,
        Long wrongDeviceLineId,
        PhotoKind kind,
        String originalFilename,
        String contentType,
        Integer sizeBytes,
        Instant uploadedDate,
        Long uploadedByUserId) {

    public static PhotoMetadataView from(CreditRequestPhoto p) {
        return new PhotoMetadataView(
                p.getId(),
                p.getCreditRequestId(),
                p.getWrongDeviceLineId(),
                p.getKind(),
                p.getOriginalFilename(),
                p.getContentType(),
                p.getSizeBytes(),
                p.getCreatedDate(),
                p.getCreatedById());
    }
}
