package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Minimal stub — full JDBC implementation lands in Task 12.
 * Returns empty/no-op results so Task 9's sync status logic can be implemented and tested.
 * NOT marked @Component here — the JDBC impl will carry @Component + @ConditionalOnProperty in Task 12.
 */
public class ReserveBidSnowflakeReader {

    public Optional<Instant> fetchMaxUploadTime() {
        return Optional.empty();
    }

    public List<ReserveBid> fetchAll() {
        return Collections.emptyList();
    }
}
