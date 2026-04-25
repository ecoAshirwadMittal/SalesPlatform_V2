package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "po.sync.writer", havingValue = "logging", matchIfMissing = true)
public class LoggingPurchaseOrderSnowflakeWriter implements PurchaseOrderSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(
            LoggingPurchaseOrderSnowflakeWriter.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void upsert(PurchaseOrderSnowflakePayload payload) {
        try {
            log.info("[PO SNOWFLAKE PUSH] would call AUCTIONS.UPSERT_PURCHASE_ORDER "
                   + "for PO {} ({} details): {}",
                    payload.purchaseOrderId(), payload.details().size(),
                    objectMapper.writeValueAsString(payload));
        } catch (Exception ex) {
            log.warn("Failed to serialize PO payload for logging", ex);
        }
    }

    @Override
    public void delete(long purchaseOrderId) {
        log.info("[PO SNOWFLAKE PUSH] would call AUCTIONS.DELETE_PURCHASE_ORDER for PO {}",
                purchaseOrderId);
    }
}
