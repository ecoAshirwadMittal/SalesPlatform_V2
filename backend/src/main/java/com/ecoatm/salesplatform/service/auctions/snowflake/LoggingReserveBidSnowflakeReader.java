package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Default reader for dev/test contexts — returns empty results and logs the call.
 * Active when {@code eb.sync.reader} is absent or set to {@code logging}.
 *
 * <p>Symmetric with {@link LoggingReserveBidSnowflakeWriter} — when neither
 * property is set to {@code jdbc}, the service layer still has concrete bean
 * dependencies to satisfy and no Snowflake DataSource is required.
 */
@Component
@ConditionalOnProperty(value = "eb.sync.reader", havingValue = "logging", matchIfMissing = true)
public class LoggingReserveBidSnowflakeReader implements ReserveBidSnowflakeReader {

    private static final Logger log = LoggerFactory.getLogger(LoggingReserveBidSnowflakeReader.class);

    @Override
    public Optional<Instant> fetchMaxUploadTime() {
        log.debug("[logging-reader] fetchMaxUploadTime — returning empty (dev/test default)");
        return Optional.empty();
    }

    @Override
    public List<ReserveBid> fetchAll() {
        log.debug("[logging-reader] fetchAll — returning empty (dev/test default)");
        return Collections.emptyList();
    }
}
