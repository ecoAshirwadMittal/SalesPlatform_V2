package com.ecoatm.salesplatform.service.rma;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Prod RMA Snowflake writer: serialises the snapshot and calls the
 * {@code AUCTIONS.UPSERT_RMA_DATA(?)} stored procedure through the
 * {@code snowflakeJdbcTemplate}. Selected when {@code rma.sync.writer = jdbc};
 * mirrors {@code JdbcPurchaseOrderSnowflakeWriter}.
 *
 * <p>The single {@code ?} bind is the JSON payload — exactly the
 * {@code JSON_CONTENT} argument the legacy {@code SUB_SendRMADetailsToSnowflake}
 * bound before invoking {@code PWS_UpsertRMAStoredProc} (constant value
 * {@code AUCTIONS.UPSERT_RMA_DATA(?)}). The Snowflake environment database
 * (legacy {@code SnowflakeEnvironmentDB}, e.g. {@code ECO_QA}) is supplied by
 * the connection's default database, so it is not concatenated into the call
 * here — the same convention the PO / recalc JDBC writers follow.
 */
@Component
@ConditionalOnProperty(name = "rma.sync.writer", havingValue = "jdbc")
public class JdbcRmaSnowflakeWriter implements RmaSnowflakeWriter {

    private final JdbcTemplate snowflakeJdbc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public JdbcRmaSnowflakeWriter(
            @Qualifier("snowflakeJdbcTemplate") JdbcTemplate snowflakeJdbc) {
        this.snowflakeJdbc = snowflakeJdbc;
    }

    @Override
    public void push(RmaSnowflakePayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            snowflakeJdbc.update("CALL AUCTIONS.UPSERT_RMA_DATA(?)", json);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to call AUCTIONS.UPSERT_RMA_DATA", ex);
        }
    }
}
