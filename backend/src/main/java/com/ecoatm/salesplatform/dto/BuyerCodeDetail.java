package com.ecoatm.salesplatform.dto;

public record BuyerCodeDetail(
    Long id,
    String code,
    String buyerCodeType,
    Integer budget,
    boolean softDelete,
    boolean codeUniqueValid
) {}
