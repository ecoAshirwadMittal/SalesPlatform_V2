package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JdbcPurchaseOrderSnowflakeWriterTest {

    JdbcTemplate snowflakeJdbc;
    JdbcPurchaseOrderSnowflakeWriter writer;

    @BeforeEach
    void init() {
        snowflakeJdbc = mock(JdbcTemplate.class);
        writer = new JdbcPurchaseOrderSnowflakeWriter(snowflakeJdbc);
    }

    @Test
    void upsertCallsStoredProc() {
        var payload = new PurchaseOrderSnowflakePayload(
                42L, 99001L,
                new PurchaseOrderSnowflakePayload.WeekRef(1L, 2025, 1),
                new PurchaseOrderSnowflakePayload.WeekRef(2L, 2025, 4),
                "X", 1, Instant.now(),
                List.of(new PurchaseOrderSnowflakePayload.DetailPayload(
                        100L, 50000L, "12345", "A_YYY", "iPhone",
                        new BigDecimal("10.00"), 100, null, null,
                        "ABC", "ABC")));
        writer.upsert(payload);
        verify(snowflakeJdbc).update(
                contains("CALL AUCTIONS.UPSERT_PURCHASE_ORDER"),
                any(Object[].class));
    }

    @Test
    void deleteCallsStoredProc() {
        writer.delete(42L);
        verify(snowflakeJdbc).update(
                contains("CALL AUCTIONS.DELETE_PURCHASE_ORDER"),
                eq(42L));
    }
}
