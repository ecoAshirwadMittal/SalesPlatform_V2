package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulingAuctionRepository extends JpaRepository<SchedulingAuction, Long> {

    List<SchedulingAuction> findByAuctionIdOrderByRoundAsc(Long auctionId);
}
