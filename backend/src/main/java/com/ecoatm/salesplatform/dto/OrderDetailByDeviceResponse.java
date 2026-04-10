package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public record OrderDetailByDeviceResponse(
        Long imeiDetailId,
        String imei,
        String sku,
        String description,
        BigDecimal unitPrice,
        String serialNumber,
        String boxNumber,
        String trackingNumber,
        String trackingUrl
) {
}
