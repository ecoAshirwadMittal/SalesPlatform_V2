package com.ecoatm.salesplatform.repository.integration;

import com.ecoatm.salesplatform.model.integration.SnowflakeSyncLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository over {@code integration.snowflake_sync_log}.
 * <p>
 * Phase 4 owns the write paths ({@code save}). Phase 6's sync-status endpoint
 * will consume {@link #findFirstBySyncTypeAndTargetKeyOrderByStartedAtDesc}
 * to render the "latest run" state for the admin inventory page.
 */
@Repository
public interface SnowflakeSyncLogRepository extends JpaRepository<SnowflakeSyncLog, Long> {

    /**
     * Return the most recent log row for the given sync type + target key.
     * Useful to answer "what is the status of the latest sync for week X?".
     */
    Optional<SnowflakeSyncLog> findFirstBySyncTypeAndTargetKeyOrderByStartedAtDesc(
            String syncType, String targetKey);
}
