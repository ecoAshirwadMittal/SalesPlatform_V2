package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ReserveBidAuditRow(
        long id,
        long reserveBidId,
        String productId,
        String grade,
        BigDecimal oldPrice,
        BigDecimal newPrice,
        Instant createdDate,
        String changedByUsername) {}
