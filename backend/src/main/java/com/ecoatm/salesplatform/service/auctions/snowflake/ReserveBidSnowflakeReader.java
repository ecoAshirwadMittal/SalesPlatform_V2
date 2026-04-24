package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Reads reserve bid watermark + rows from the authoritative Snowflake source.
 *
 * <p>Interface so dev/test contexts can use {@link LoggingReserveBidSnowflakeReader}
 * (no Snowflake DataSource required) and prod contexts can switch to the JDBC
 * implementation via {@code eb.sync.reader=jdbc}. Mirrors the writer pattern
 * exactly.
 */
public interface ReserveBidSnowflakeReader {

    Optional<Instant> fetchMaxUploadTime();

    List<ReserveBid> fetchAll();
}
