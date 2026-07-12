package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.Auction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    /**
     * Mendix invariant: one auction row per week. Callers gate the Create
     * Auction flow on this check so duplicates can't be created from either
     * the admin UI or the REST endpoint.
     */
    boolean existsByWeekId(Long weekId);

    /**
     * Case-insensitive uniqueness check mirroring Mendix
     * {@code VAL_Create_Auction}.
     */
    boolean existsByAuctionTitleIgnoreCase(String auctionTitle);

    /**
     * Pessimistic lock on the {@code auctions.auctions} row for the Save
     * Schedule / Unschedule / Delete transactions. Two admins racing to
     * save the same auction serialize behind this lock so the
     * delete-and-recreate path never interleaves.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    Optional<Auction> findByIdForUpdate(Long id);

    /**
     * Bidder dashboard ended-state selection: the single most-recent auction.
     * Mirrors Mendix {@code ACT_GetMostRecentAuction} (a bare top-1 retrieve of
     * {@code AuctionUI.Auction}); ordered by {@code created_date DESC} so the
     * newest auction wins when more than one row survives the
     * {@code KEEP_LATEST_AUCTIONS} prune.
     */
    Optional<Auction> findFirstByOrderByCreatedDateDesc();
}
