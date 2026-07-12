package com.ecoatm.salesplatform.service.rma;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Unit test for {@link LoggingRmaSnowflakeWriter} — the default (dev / test)
 * no-op writer. Mirrors {@code LoggingPurchaseOrderSnowflakeWriterTest}: proves
 * the snapshot serialises to a Snowflake-friendly shape carrying the RMA
 * business fields, and that the writer's log path never throws.
 */
class LoggingRmaSnowflakeWriterTest {

    static RmaSnowflakePayload samplePayload() {
        return new RmaSnowflakePayload(
                42L, "RMA-000042", 11L, "ABC",
                "Approved", "Success", "ORA-9001", "ORA-ID-1", 200, true,
                3, 5, new BigDecimal("1250.00"),
                2, 3, new BigDecimal("800.00"),
                2, 1,
                7L, 3L,
                LocalDateTime.parse("2026-07-01T09:00:00"),
                LocalDateTime.parse("2026-07-02T10:30:00"),
                LocalDateTime.parse("2026-07-02T10:30:00"),
                Instant.parse("2026-07-02T10:30:05Z"),
                List.of(new RmaSnowflakePayload.ItemPayload(
                        100L, 555L, "356938035643809", "SO-12345",
                        new BigDecimal("400.00"), "DOA", "Approve", "Approved", null)));
    }

    @Test
    @DisplayName("snapshot serialises to a shape carrying the RMA header + items")
    void payloadSerializesToExpectedShape() throws Exception {
        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String json = om.writeValueAsString(samplePayload());

        assertThat(json).contains("\"rmaId\":42");
        assertThat(json).contains("\"rmaNumber\":\"RMA-000042\"");
        assertThat(json).contains("\"buyerCode\":\"ABC\"");
        assertThat(json).contains("\"systemStatus\":\"Approved\"");
        assertThat(json).contains("\"items\":[");
        assertThat(json).contains("\"imei\":\"356938035643809\"");
    }

    @Test
    @DisplayName("push logs the would-be-pushed row and never throws")
    void pushLogsAndNeverThrows() {
        LoggingRmaSnowflakeWriter writer = new LoggingRmaSnowflakeWriter();
        assertThatCode(() -> writer.push(samplePayload())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("push tolerates an empty item list")
    void pushToleratesEmptyItems() {
        LoggingRmaSnowflakeWriter writer = new LoggingRmaSnowflakeWriter();
        RmaSnowflakePayload noItems = new RmaSnowflakePayload(
                7L, "RMA-7", 1L, "X",
                "Declined", null, null, null, null, false,
                0, 0, BigDecimal.ZERO, 0, 0, BigDecimal.ZERO, 0, 1,
                7L, 3L, null, null, null, Instant.now(), List.of());
        assertThatCode(() -> writer.push(noItems)).doesNotThrowAnyException();
    }
}
