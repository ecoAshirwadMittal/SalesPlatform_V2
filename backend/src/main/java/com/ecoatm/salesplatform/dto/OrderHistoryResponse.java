package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.pws.OrderHistoryView;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderHistoryResponse(
        Long id,
        String orderNumber,
        LocalDateTime offerDate,
        LocalDateTime orderDate,
        String orderStatus,
        LocalDateTime shipDate,
        String shipMethod,
        Integer skuCount,
        Integer totalQuantity,
        BigDecimal totalPrice,
        String buyer,
        String company,
        LocalDateTime lastUpdateDate,
        String offerOrderType,
        Long offerId
) {
    public static OrderHistoryResponse from(OrderHistoryView v) {
        return new OrderHistoryResponse(
                v.getId(),
                v.getOrderNumber(),
                v.getOfferDate(),
                v.getOrderDate(),
                v.getOrderStatus(),
                v.getShipDate(),
                v.getShipMethod(),
                v.getSkuCount(),
                v.getTotalQuantity(),
                v.getTotalPrice(),
                v.getBuyer(),
                v.getCompany(),
                v.getLastUpdateDate(),
                v.getOfferOrderType(),
                v.getOfferId()
        );
    }
}
