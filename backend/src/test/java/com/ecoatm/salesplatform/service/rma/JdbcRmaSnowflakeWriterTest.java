package com.ecoatm.salesplatform.service.rma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link JdbcRmaSnowflakeWriter} — the prod writer. Mirrors
 * {@code JdbcPurchaseOrderSnowflakeWriterTest}: proves it calls the legacy
 * {@code AUCTIONS.UPSERT_RMA_DATA(?)} stored procedure with the serialised
 * snapshot as the single {@code JSON_CONTENT} argument (the shape
 * {@code SUB_SendRMADetailsToSnowflake} used), and that a JDBC failure surfaces
 * as an exception for the listener to swallow.
 */
class JdbcRmaSnowflakeWriterTest {

    JdbcTemplate snowflakeJdbc;
    JdbcRmaSnowflakeWriter writer;

    @BeforeEach
    void init() {
        snowflakeJdbc = mock(JdbcTemplate.class);
        writer = new JdbcRmaSnowflakeWriter(snowflakeJdbc);
    }

    @Test
    @DisplayName("push calls AUCTIONS.UPSERT_RMA_DATA with the JSON snapshot arg")
    void pushCallsStoredProc() {
        writer.push(LoggingRmaSnowflakeWriterTest.samplePayload());

        verify(snowflakeJdbc).update(
                contains("CALL AUCTIONS.UPSERT_RMA_DATA"),
                contains("\"rmaNumber\":\"RMA-000042\""));
    }

    @Test
    @DisplayName("a JDBC failure is wrapped with the proc name and rethrown")
    void jdbcFailureRethrown() {
        doThrow(new RuntimeException("Snowflake down"))
                .when(snowflakeJdbc).update(anyString(), anyString());

        assertThatThrownBy(() -> writer.push(LoggingRmaSnowflakeWriterTest.samplePayload()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("AUCTIONS.UPSERT_RMA_DATA");
    }
}
