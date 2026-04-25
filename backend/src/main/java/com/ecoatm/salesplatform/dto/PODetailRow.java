package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public record PODetailRow(
        long id, long purchaseOrderId,
        long buyerCodeId, String buyerCode,
        String productId, String grade, String modelName,
        BigDecimal price, Integer qtyCap,
        BigDecimal priceFulfilled, Integer qtyFulfilled) {}
