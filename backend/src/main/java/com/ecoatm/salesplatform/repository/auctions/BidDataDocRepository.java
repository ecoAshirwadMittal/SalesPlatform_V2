package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BidDataDocRepository extends JpaRepository<BidDataDoc, Long> {
    Optional<BidDataDoc> findByUserIdAndBuyerCodeIdAndWeekId(
            long userId, long buyerCodeId, long weekId);
}
