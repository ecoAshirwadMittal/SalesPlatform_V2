package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SchedulingAuctionRepository extends JpaRepository<SchedulingAuction, Long> {

    List<SchedulingAuction> findByAuctionIdOrderByRoundAsc(Long auctionId);

    /**
     * Derived lookup for a single round of an auction. Used by the admin
     * R1 init endpoint to resolve the SchedulingAuction row for
     * {@code (auctionId, 1)} before handing off to
     * {@code Round1InitializationService}.
     */
    Optional<SchedulingAuction> findByAuctionIdAndRound(Long auctionId, int round);

    /**
     * Delete-and-recreate pathway for the Save Schedule flow. Used only from
     * inside a {@code @Transactional} service method that has already taken
     * a pessimistic lock on the parent {@code Auction} row, so we can rely
     * on the tx boundary rather than flushing here.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SchedulingAuction s WHERE s.auctionId = :auctionId")
    void deleteByAuctionId(Long auctionId);

    /**
     * Returns {@code true} iff any round of the auction has advanced to the
     * given status. Used for the Started-round guard on Save, Unschedule,
     * and Delete.
     */
    boolean existsByAuctionIdAndRoundStatus(Long auctionId, SchedulingAuctionStatus status);

    /**
     * Lifecycle cron — Phase 1 selector. Returns ids of rounds that should
     * transition Started -> Closed (end_datetime in the past).
     */
    @Query("""
        select s.id from SchedulingAuction s
        where s.roundStatus = com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus.Started
          and s.endDatetime < :now
    """)
    List<Long> findIdsToClose(@Param("now") Instant now);

    /**
     * Lifecycle cron — Phase 2 selector. Returns ids of rounds that should
     * transition Scheduled -> Started (start_datetime <= now).
     */
    @Query("""
        select s.id from SchedulingAuction s
        where s.roundStatus = com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus.Scheduled
          and s.startDatetime <= :now
    """)
    List<Long> findIdsToStart(@Param("now") Instant now);

    /**
     * Acquire a pessimistic write lock on a single SchedulingAuction row.
     * Used by RoundTransitionService to guard the status re-check + update
     * against concurrent ticks.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SchedulingAuction s where s.id = :id")
    Optional<SchedulingAuction> findByIdForUpdate(@Param("id") Long id);
}
