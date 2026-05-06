package com.ecoatm.salesplatform.service.auctions.recalc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RecalcOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(RecalcOrchestrator.class);

    private final BidRankingService rankingService;
    private final TargetPriceRecalcService targetPriceService;

    public RecalcOrchestrator(BidRankingService rankingService,
                              TargetPriceRecalcService targetPriceService) {
        this.rankingService = rankingService;
        this.targetPriceService = targetPriceService;
    }

    /**
     * Runs both processes for a closed round. Each is wrapped in try/catch so
     * one failing does NOT prevent the other from running. Per design §4 each
     * service is itself REQUIRES_NEW so the rollback boundaries are
     * independent.
     */
    public void runForClosedRound(long schedulingAuctionId) {
        try {
            rankingService.run(schedulingAuctionId);
        } catch (RuntimeException ex) {
            log.error("RANKING failed in orchestrator schedulingAuctionId={}",
                schedulingAuctionId, ex);
        }

        try {
            targetPriceService.run(schedulingAuctionId);
        } catch (RuntimeException ex) {
            log.error("TARGET_PRICE failed in orchestrator schedulingAuctionId={}",
                schedulingAuctionId, ex);
        }
    }
}
