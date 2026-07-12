package com.ecoatm.salesplatform.service.rma;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Default (dev / test) RMA Snowflake writer: a no-op that logs the row it
 * <em>would</em> push instead of touching a live Snowflake DataSource. Selected
 * when {@code rma.sync.writer} is {@code logging} or unset
 * ({@code matchIfMissing = true}), mirroring {@code LoggingPurchaseOrderSnowflakeWriter}.
 *
 * <p>Only business identifiers are logged (RMA number, item count, and the
 * serialised snapshot of business data) — no secrets, tokens, or the Snowflake
 * connection string ever appear here.
 */
@Component
@ConditionalOnProperty(name = "rma.sync.writer", havingValue = "logging", matchIfMissing = true)
public class LoggingRmaSnowflakeWriter implements RmaSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(LoggingRmaSnowflakeWriter.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void push(RmaSnowflakePayload payload) {
        try {
            log.info("[RMA SNOWFLAKE PUSH] would call AUCTIONS.UPSERT_RMA_DATA "
                   + "for RMA {} ({} items): {}",
                    payload.rmaNumber(), payload.items().size(),
                    objectMapper.writeValueAsString(payload));
        } catch (Exception ex) {
            // Never let a serialisation hiccup escape — this writer is a no-op.
            log.warn("Failed to serialize RMA payload for logging (rmaId={})",
                    payload.rmaId(), ex);
        }
    }
}
