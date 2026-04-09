package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.mdm.PriceHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record PriceHistoryResponse(
    Long id,
    BigDecimal listPrice,
    BigDecimal minPrice,
    BigDecimal previousListPrice,
    BigDecimal previousMinPrice,
    LocalDateTime expirationDate,
    LocalDateTime createdDate
) {
    public static List<PriceHistoryResponse> fromEntities(List<PriceHistory> entities) {
        List<PriceHistoryResponse> result = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            PriceHistory current = entities.get(i);
            PriceHistory previous = (i + 1 < entities.size()) ? entities.get(i + 1) : null;
            result.add(new PriceHistoryResponse(
                current.getId(),
                current.getListPrice(),
                current.getMinPrice(),
                previous != null ? previous.getListPrice() : null,
                previous != null ? previous.getMinPrice() : null,
                current.getExpirationDate(),
                current.getCreatedDate()
            ));
        }
        return result;
    }
}
