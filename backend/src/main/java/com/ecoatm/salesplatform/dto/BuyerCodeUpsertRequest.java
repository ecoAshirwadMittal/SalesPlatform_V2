package com.ecoatm.salesplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record BuyerCodeUpsertRequest(
    Long id,
    @NotBlank String code,
    String buyerCodeType,
    @PositiveOrZero Integer budget,
    boolean softDelete
) {}
