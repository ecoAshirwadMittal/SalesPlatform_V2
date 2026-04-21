package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
public class AuctionStatusReconciler {

    private static final String SYSTEM_ACTOR = "system:lifecycle-cron";

    private final SchedulingAuctionRepository schedulingRepo;
    private final AuctionRepository auctionRepo;
    private final Clock clock;

    public AuctionStatusReconciler(SchedulingAuctionRepository schedulingRepo,
                                   AuctionRepository auctionRepo,
                                   Clock clock) {
        this.schedulingRepo = schedulingRepo;
        this.auctionRepo = auctionRepo;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reconcile(long auctionId) {
        Auction auction = auctionRepo.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new IllegalStateException(
                        "Auction missing during reconcile: id=" + auctionId));

        List<SchedulingAuction> rounds = schedulingRepo.findByAuctionIdOrderByRoundAsc(auctionId);
        AuctionStatus computed = computeAuctionStatus(rounds, auction.getAuctionStatus());

        if (computed == auction.getAuctionStatus()) {
            return;
        }
        auction.setAuctionStatus(computed);
        auction.setChangedDate(clock.instant());
        auction.setUpdatedBy(SYSTEM_ACTOR);
        auctionRepo.save(auction);
    }

    /**
     * Mendix SUB_SetAuctionStatus rule:
     *   All rounds Closed → Closed; any round Started → Started; otherwise unchanged.
     */
    private AuctionStatus computeAuctionStatus(List<SchedulingAuction> rounds, AuctionStatus current) {
        if (rounds.isEmpty()) return current;
        boolean allClosed = rounds.stream()
                .allMatch(r -> r.getRoundStatus() == SchedulingAuctionStatus.Closed);
        if (allClosed) return AuctionStatus.Closed;
        boolean anyStarted = rounds.stream()
                .anyMatch(r -> r.getRoundStatus() == SchedulingAuctionStatus.Started);
        if (anyStarted) return AuctionStatus.Started;
        return current;
    }
}
