package com.ecoatm.salesplatform.dto;

import jakarta.validation.constraints.NotNull;

public record PurchaseOrderRequest(
        @NotNull Long weekFromId,
        @NotNull Long weekToId) {}
