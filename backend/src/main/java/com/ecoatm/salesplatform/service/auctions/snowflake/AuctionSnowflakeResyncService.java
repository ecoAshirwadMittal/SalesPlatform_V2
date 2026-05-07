package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.SyncLogWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin recovery service: re-pushes Snowflake bid ranks + target prices for every
 * closed round of an auction. Per-round failures are isolated and logged via
 * {@link SyncLogWriter#writeFailed}.
 *
 * <p>Ports Mendix {@code ACT_Auction_SendAllBidsToSnowflake_Admin} (loop over
 * submitted bid rounds → push each). Our slice writers operate at (weekId, targetRound)
 * granularity, so for each closed round R we call both writers with targetRound = R+1.
 * R3 close is skipped (no R4 to push).
 */
@Service
public class AuctionSnowflakeResyncService {

    private static final Logger log = LoggerFactory.getLogger(AuctionSnowflakeResyncService.class);

    private final AuctionRepository auctionRepo;
    private final SchedulingAuctionRepository saRepo;
    private final BidRankingSnowflakeWriter bidRankingWriter;
    private final TargetPriceSnowflakeWriter targetPriceWriter;
    private final SyncLogWriter syncLogWriter;

    public AuctionSnowflakeResyncService(AuctionRepository auctionRepo,
                                         SchedulingAuctionRepository saRepo,
                                         BidRankingSnowflakeWriter bidRankingWriter,
                                         TargetPriceSnowflakeWriter targetPriceWriter,
                                         SyncLogWriter syncLogWriter) {
        this.auctionRepo = auctionRepo;
        this.saRepo = saRepo;
        this.bidRankingWriter = bidRankingWriter;
        this.targetPriceWriter = targetPriceWriter;
        this.syncLogWriter = syncLogWriter;
    }

    public AuctionSnowflakeResyncResult resync(long auctionId) {
        long start = System.currentTimeMillis();

        Auction auction = auctionRepo.findById(auctionId)
            .orElseThrow(() -> new EntityNotFoundException("Auction", auctionId));

        Long weekId = auction.getWeekId();
        if (weekId == null) {
            throw new IllegalStateException(
                "auction " + auctionId + " has no weekId; cannot resync to Snowflake");
        }

        // For each closed round R in {1, 2}, push the slice for targetRound = R + 1.
        // R3 close has no targetRound 4 to push, so skip it.
        List<SchedulingAuction> sas = saRepo.findByAuctionIdOrderByRoundAsc((long) auctionId);
        List<RoundOutcome> outcomes = new ArrayList<>();
        int totalSuccesses = 0;
        int totalFailures = 0;

        for (SchedulingAuction sa : sas) {
            if (sa.getRoundStatus() != SchedulingAuctionStatus.Closed) continue;
            int targetRound = sa.getRound() + 1;
            if (targetRound > 3) continue; // R3 close has no R4 to push

            boolean rankingOk = pushOne("BID_RANKING", auctionId, weekId, targetRound,
                () -> bidRankingWriter.pushBidRankings(weekId, targetRound));
            boolean targetPriceOk = pushOne("TARGET_PRICE", auctionId, weekId, targetRound,
                () -> targetPriceWriter.pushTargetPrices(weekId, targetRound));

            outcomes.add(new RoundOutcome(sa.getRound(), targetRound, rankingOk, targetPriceOk));
            if (rankingOk && targetPriceOk) {
                totalSuccesses++;
            } else {
                totalFailures++;
            }
        }

        long durationMs = System.currentTimeMillis() - start;
        log.info("Snowflake resync auctionId={} weekId={} closedRounds={} successes={} failures={} ms={}",
                auctionId, weekId, outcomes.size(), totalSuccesses, totalFailures, durationMs);

        return new AuctionSnowflakeResyncResult(auctionId, weekId,
                outcomes, totalSuccesses, totalFailures, durationMs);
    }

    private boolean pushOne(String syncType, long auctionId, long weekId, int targetRound,
                            Runnable writer) {
        try {
            writer.run();
            return true;
        } catch (RuntimeException ex) {
            log.error("[snowflake-resync] {} failed auctionId={} weekId={} targetRound={}",
                syncType, auctionId, weekId, targetRound, ex);
            syncLogWriter.writeFailed(syncType,
                "auctionId=" + auctionId + ",weekId=" + weekId + ",targetRound=" + targetRound,
                ex.toString());
            return false;
        }
    }

    public record AuctionSnowflakeResyncResult(
            long auctionId,
            long weekId,
            List<RoundOutcome> outcomes,
            int totalSuccesses,
            int totalFailures,
            long durationMs) {}

    public record RoundOutcome(int closedRound, int targetRound,
                               boolean bidRankingPushed, boolean targetPricePushed) {}
}
