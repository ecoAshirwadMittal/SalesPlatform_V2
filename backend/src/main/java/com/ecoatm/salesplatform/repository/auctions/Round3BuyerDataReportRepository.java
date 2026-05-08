package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.Round3BuyerDataReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
     * {@code @ManyToOne Auction}, only FK columns, so we join via the
     * raw {@code SchedulingAuction} + {@code Auction} entities and filter
     * on the auction's {@code weekId} field. Ordered by company name so
     * the admin grid renders deterministically (the legacy {@code buyerCode}
     * column is NULL for V85+ rows — Mendix legacy populated it, the
     * port writes {@code companyName} + {@code buyerCodes} (plural) instead).
     *
     * <p>The join must go through {@code scheduling_auction_id}, NOT the
     * legacy {@code auction_id} column. {@link #bulkInsertForSchedulingAuction}
     * (V85+) only sets {@code scheduling_auction_id}; {@code auction_id}
     * stays NULL on every newly-inserted row, so a join on it would silently
     * return zero rows even when reports exist for the requested week.
     */
    @Query("""
            SELECT r
              FROM Round3BuyerDataReport r,
                   com.ecoatm.salesplatform.model.auctions.SchedulingAuction sa,
                   com.ecoatm.salesplatform.model.auctions.Auction a
             WHERE r.schedulingAuctionId = sa.id
               AND sa.auctionId = a.id
               AND a.weekId = :weekId
             ORDER BY r.companyName
            """)
    List<Round3BuyerDataReport> findByWeekId(@Param("weekId") Long weekId);

    /**
     * Reports for a single auction. Useful when the admin already drilled
     * down from the Auctions list and wants reports tied to a specific
     * auction id rather than the entire week.
     */
    List<Round3BuyerDataReport> findByAuctionIdOrderByBuyerCode(Long auctionId);

    /**
     * Reports for a specific R3 scheduling auction. Used by R3PreProcessService
     * for idempotent delete-then-reinsert cycles, and by integration tests to
     * verify bulk-insert results.
     *
     * @param schedulingAuctionId the R3 scheduling auction id
     * @return list of reports for that R3 SA (empty if none exist)
     * @since V84 (sub-project 6)
     */
    List<Round3BuyerDataReport> findBySchedulingAuctionId(Long schedulingAuctionId);

    /**
     * Bulk insert Round 3 buyer data reports from qualified buyer codes.
     * Populates one row per company (with comma-joined buyer codes) by reading
     * qualified buyer codes and aggregating by company name.
     *
     * <p>Uses a native PostgreSQL query that groups by company and performs
     * string_agg on buyer code values. Only includes rows where
     * qualification_type='Qualified' AND included=TRUE.
     *
     * <p>Must run inside a parent transaction (propagation=MANDATORY).
     *
     * @param saId the R3 scheduling auction id
     * @return number of rows inserted
     * @since V84 (sub-project 6)
     */
    @Modifying
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.MANDATORY)
    @Query(value = """
            INSERT INTO auctions.round3_buyer_data_reports (
                scheduling_auction_id, company_name, buyer_codes, created_date, changed_date
            )
            SELECT :saId,
                   b.company_name,
                   string_agg(bc.code, ',' ORDER BY bc.code),
                   NOW(), NOW()
              FROM buyer_mgmt.qualified_buyer_codes qbc
              JOIN buyer_mgmt.buyer_codes bc        ON bc.id = qbc.buyer_code_id
              JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
              JOIN buyer_mgmt.buyers b              ON b.id = bcb.buyer_id
             WHERE qbc.scheduling_auction_id = :saId
               AND qbc.qualification_type    = 'Qualified'
               AND qbc.included              = TRUE
             GROUP BY b.company_name
            """, nativeQuery = true)
    int bulkInsertForSchedulingAuction(@Param("saId") Long saId);

    /**
     * Delete all Round 3 buyer data reports for a specific R3 scheduling auction.
     * Used by R3PreProcessService for idempotent rerun (delete-then-insert pattern).
     *
     * <p>Must run inside a parent transaction (propagation=MANDATORY).
     *
     * @param saId the R3 scheduling auction id
     * @return number of rows deleted
     * @since V84 (sub-project 6)
     */
    @Modifying
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.MANDATORY)
    @Query(value = "DELETE FROM auctions.round3_buyer_data_reports WHERE scheduling_auction_id = :saId",
           nativeQuery = true)
    int deleteBySchedulingAuctionId(@Param("saId") Long saId);
}
