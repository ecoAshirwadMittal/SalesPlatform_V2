package com.ecoatm.salesplatform.dto;

/**
 * PATCH body for the QBC admin endpoint. The only mutable field exposed via
 * the admin UI is {@code included}; flipping it via this endpoint also forces
 * {@code qualification_type='Manual'} so subsequent auto-recompute jobs know
 * not to overwrite the admin's choice.
 */
public record QualifiedBuyerCodeUpdateRequest(boolean included) {}
