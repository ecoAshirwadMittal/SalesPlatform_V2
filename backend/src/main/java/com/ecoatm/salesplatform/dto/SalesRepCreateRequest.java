package com.ecoatm.salesplatform.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Create payload for the admin sales-representative CRUD surface.
 *
 * <p>{@code active} is optional (nullable) — a new rep defaults to active,
 * mirroring the {@code buyer_mgmt.sales_representatives.active NOT NULL
 * DEFAULT true} column. Identity (owner) is derived from the JWT at the
 * controller, never from this body.
 */
public record SalesRepCreateRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        Boolean active) {
}
