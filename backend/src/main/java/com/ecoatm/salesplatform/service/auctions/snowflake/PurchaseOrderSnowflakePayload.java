package com.ecoatm.salesplatform.service.auctions.snowflake;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PurchaseOrderSnowflakePayload(
        long purchaseOrderId, long legacyId,
        WeekRef weekFrom, WeekRef weekTo,
        String weekRangeLabel,
        int totalRecords,
        Instant pushTimestamp,
        List<DetailPayload> details) {

    public record WeekRef(long id, int year, int weekNumber) {}

    public record DetailPayload(
            long detailId, long legacyId,
            String productId, String grade, String modelName,
            BigDecimal price, Integer qtyCap,
            BigDecimal priceFulfilled, Integer qtyFulfilled,
            String buyerCode, String tempBuyerCode) {}
}
