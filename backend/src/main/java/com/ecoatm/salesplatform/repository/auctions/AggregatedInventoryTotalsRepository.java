package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.AggregatedInventoryTotals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AggregatedInventoryTotalsRepository extends JpaRepository<AggregatedInventoryTotals, Long> {
    Optional<AggregatedInventoryTotals> findByWeekId(Long weekId);
}
