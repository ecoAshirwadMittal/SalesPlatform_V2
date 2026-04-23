package com.ecoatm.salesplatform.service.auctions.snowflake;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ReserveBidSnowflakePayload(List<Row> rows, String actingUser, String action) {

    public record Row(String productId, String grade, String brand, String model,
                      BigDecimal bid, Instant lastUpdateDatetime,
                      BigDecimal lastAwardedMinPrice, String lastAwardedWeek) {}
}
