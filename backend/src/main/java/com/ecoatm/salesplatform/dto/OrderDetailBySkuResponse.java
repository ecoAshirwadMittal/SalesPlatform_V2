package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public record OrderDetailBySkuResponse(
        Long offerItemId,
        String sku,
        String description,
        Integer orderedQty,
        Integer shippedQty,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
