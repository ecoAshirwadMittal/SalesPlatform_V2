package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerStatus;

import java.util.List;

public record BuyerDetailResponse(
    Long id,
    String companyName,
    BuyerStatus status,
    boolean isSpecialBuyer,
    List<SalesRepSummary> salesReps,
    List<BuyerCodeDetail> buyerCodes,
    BuyerPermissions permissions
) {}
