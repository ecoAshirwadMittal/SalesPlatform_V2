package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.Round3BuyerDataReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * JPA repository for {@link Round3BuyerDataReport}.
 *
 * <p>Reports filter by week — the table itself stores {@code auction_id},
 * so the week filter joins through {@code auctions.auctions.week_id}. The
 * fallback path filters directly by {@code auction_id} for callers that
 * already have the auction id resolved.
 */
public interface Round3BuyerDataReportRepository extends JpaRepository<Round3BuyerDataReport, Long> {

    /**
     * Reports for every auction whose {@code week_id} matches.
     *
     * <p>Why JPQL + native enum: {@link Round3BuyerDataReport} has no
     * {@code @ManyToOne Auction}, only the FK column, so we join via the
     * raw {@code Auction} entity and filter on its {@code weekId} field.
     * Ordered by buyer code so the admin grid renders deterministically.
     */
    @Query("""
            SELECT r
              FROM Round3BuyerDataReport r,
                   com.ecoatm.salesplatform.model.auctions.Auction a
             WHERE r.auctionId = a.id
               AND a.weekId = :weekId
             ORDER BY r.buyerCode
            """)
    List<Round3BuyerDataReport> findByWeekId(@Param("weekId") Long weekId);

    /**
     * Reports for a single auction. Useful when the admin already drilled
     * down from the Auctions list and wants reports tied to a specific
     * auction id rather than the entire week.
     */
    List<Round3BuyerDataReport> findByAuctionIdOrderByBuyerCode(Long auctionId);
}
