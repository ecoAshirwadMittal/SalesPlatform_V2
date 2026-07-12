package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.buyermgmt.SalesRepresentative;

/**
 * Response view for the admin sales-representative CRUD surface. Unlike
 * {@link SalesRepSummary} (buyer-detail read model, id + names only) this
 * carries {@code active} so the management grid can render/toggle status.
 */
public record SalesRepResponse(Long id, String firstName, String lastName, boolean active) {

    public static SalesRepResponse from(SalesRepresentative sr) {
        return new SalesRepResponse(sr.getId(), sr.getFirstName(), sr.getLastName(), sr.isActive());
    }
}
