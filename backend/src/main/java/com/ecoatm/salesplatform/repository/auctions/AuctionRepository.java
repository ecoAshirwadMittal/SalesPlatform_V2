package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
