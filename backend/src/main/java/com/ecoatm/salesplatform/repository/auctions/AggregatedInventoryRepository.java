package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AggregatedInventoryRepository extends JpaRepository<AggregatedInventory, Long> {

    /**
     * Lock guard for the Snowflake sync service. An auction row for the week
     * means the bid engine is already using these numbers — the sync must
     * skip so admin edits and live bid data are not stomped. Parity with the
     * Mendix guard in {@code AggregatedInventoryHelper}.
     *
     * <p>Kept as a native query against {@code auctions.auctions} rather than
     * depending on an {@code Auction} JPA entity that does not yet exist.
     */
    @Query(value = """
            SELECT EXISTS (SELECT 1 FROM auctions.auctions WHERE week_id = :weekId)
            """, nativeQuery = true)
    boolean existsAuctionForWeek(@Param("weekId") long weekId);
}
