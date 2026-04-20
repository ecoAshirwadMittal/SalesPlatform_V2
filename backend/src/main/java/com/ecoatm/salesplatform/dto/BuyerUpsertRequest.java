package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BuyerUpsertRequest(
    @NotBlank @Size(max = 200) String companyName,
    BuyerStatus status,
    boolean isSpecialBuyer,
    List<Long> salesRepIds,
    @Valid List<BuyerCodeUpsertRequest> buyerCodes
) {}
