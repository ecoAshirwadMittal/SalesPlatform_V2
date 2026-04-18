package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.auctions.WeekSyncWatermark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository over {@code auctions.week_sync_watermark}.
 * <p>
 * The composite primary key is {@link WeekSyncWatermark.Key} — this is what
 * {@link JpaRepository#findById(Object)} accepts. Callers that only know the
 * scalar pair should prefer {@link #findByWeekIdAndSource} for readability.
 */
@Repository
public interface WeekSyncWatermarkRepository
        extends JpaRepository<WeekSyncWatermark, WeekSyncWatermark.Key> {

    Optional<WeekSyncWatermark> findByWeekIdAndSource(Long weekId, String source);
}
