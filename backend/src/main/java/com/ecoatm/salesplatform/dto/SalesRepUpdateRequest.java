package com.ecoatm.salesplatform.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Update payload for the admin sales-representative CRUD surface. The edit form
 * always carries the active toggle, so {@code active} is nullable here only for
 * defensive parsing — the service treats {@code null} as active. Identity
 * (changer) is derived from the JWT at the controller, never from this body.
 */
public record SalesRepUpdateRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        Boolean active) {
}
