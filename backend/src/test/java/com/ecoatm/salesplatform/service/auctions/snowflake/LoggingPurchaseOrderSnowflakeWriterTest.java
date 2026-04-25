package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingPurchaseOrderSnowflakeWriterTest {

    @Test
    void payloadSerializesToExpectedShape() throws Exception {
        var payload = new PurchaseOrderSnowflakePayload(
                42L, 99001L,
                new PurchaseOrderSnowflakePayload.WeekRef(1L, 2025, 1),
                new PurchaseOrderSnowflakePayload.WeekRef(2L, 2025, 4),
                "2025 / Wk1 - 2025 / Wk4",
                1, Instant.parse("2026-04-24T10:00:00Z"),
                List.of(new PurchaseOrderSnowflakePayload.DetailPayload(
                        100L, 50000L, "12345", "A_YYY", "iPhone",
                        new BigDecimal("10.00"), 100,
                        null, null, "ABC", "ABC")));
        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String json = om.writeValueAsString(payload);

        assertThat(json).contains("\"purchaseOrderId\":42");
        assertThat(json).contains("\"weekFrom\"");
        assertThat(json).contains("\"details\":[");
        assertThat(json).contains("\"buyerCode\":\"ABC\"");
    }

    @Test
    void loggingWriterUpsertCallsLogger() {
        LoggingPurchaseOrderSnowflakeWriter writer = new LoggingPurchaseOrderSnowflakeWriter();
        var payload = new PurchaseOrderSnowflakePayload(
                42L, 99001L,
                new PurchaseOrderSnowflakePayload.WeekRef(1L, 2025, 1),
                new PurchaseOrderSnowflakePayload.WeekRef(2L, 2025, 4),
                "X", 0, Instant.now(), List.of());
        writer.upsert(payload);  // should not throw
        writer.delete(42L);      // should not throw
    }
}
