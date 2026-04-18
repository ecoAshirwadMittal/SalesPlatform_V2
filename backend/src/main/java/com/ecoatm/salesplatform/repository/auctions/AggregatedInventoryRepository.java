package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AggregatedInventoryRepository extends JpaRepository<AggregatedInventory, Long> {
}
