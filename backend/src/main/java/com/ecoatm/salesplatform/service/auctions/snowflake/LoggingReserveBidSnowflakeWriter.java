package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "eb.sync.writer", havingValue = "logging", matchIfMissing = true)
public class LoggingReserveBidSnowflakeWriter implements ReserveBidSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(LoggingReserveBidSnowflakeWriter.class);

    @Override
    public int upsert(ReserveBidSnowflakePayload payload) {
        log.info("[logging-writer] UPSERT rowCount={} user={}", payload.rows().size(), payload.actingUser());
        return payload.rows().size();
    }

    @Override
    public int delete(ReserveBidSnowflakePayload payload) {
        log.info("[logging-writer] DELETE rowCount={} user={}", payload.rows().size(), payload.actingUser());
        return payload.rows().size();
    }
}
