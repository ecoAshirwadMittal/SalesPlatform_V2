package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

    /**
     * R1 init floor clamp — non-DW target price. Lifts any below-floor
     * {@code avg_target_price} up to {@code minimumAllowedBid} for rows in
     * the target week with a non-zero non-DW quantity. Parity with Mendix
     * {@code SUB_LoopForRound1TargetPrice}.
     */
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE auctions.aggregated_inventory
               SET avg_target_price = :min
             WHERE week_id = :weekId
               AND avg_target_price < :min
               AND total_quantity > 0
            """, nativeQuery = true)
    int clampNonDwTargetPrice(@Param("weekId") Long weekId,
                              @Param("min") BigDecimal min);

    /**
     * R1 init floor clamp — DW target price. Same semantics as
     * {@link #clampNonDwTargetPrice} but against the DW-variant columns.
     * Parity with Mendix {@code SUB_LoopForRound1TargetPrice_DW}.
     */
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE auctions.aggregated_inventory
               SET dw_avg_target_price = :min
             WHERE week_id = :weekId
               AND dw_avg_target_price < :min
               AND dw_total_quantity > 0
            """, nativeQuery = true)
    int clampDwTargetPrice(@Param("weekId") Long weekId,
                           @Param("min") BigDecimal min);
}
