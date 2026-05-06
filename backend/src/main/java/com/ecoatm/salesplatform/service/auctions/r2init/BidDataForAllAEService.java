package com.ecoatm.salesplatform.service.auctions.r2init;

import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataForAllAERepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataDocService;
import com.ecoatm.salesplatform.service.auctions.r1init.SchedulingAuctionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Per-(R2 SA, special-treatment buyer code) {@code bid_data} generator —
 * sub-project 5 Task 10.
 *
 * <p>For each special-treatment {@code buyer_codes.id} supplied by
 * {@code R2BuyerAssignmentService}, this service:
 * <ol>
 *   <li>Get-or-creates the {@code bid_rounds} row for the
 *       (R2 scheduling_auction, buyer_code) tuple — mirrors the inline
 *       pattern in {@link com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationService}.</li>
 *   <li>Get-or-creates the {@code bid_data_docs} row via
 *       {@link BidDataDocService#getOrCreate(long, long, long)}.</li>
 *   <li>Calls {@link BidDataForAllAERepository#insertForSpecialBuyer} to
 *       bulk-INSERT one bid_data row per non-deprecated AE row in the
 *       SA's week.</li>
 * </ol>
 *
 * <p><strong>Idempotency guard:</strong> if the resolved {@code bid_round}
 * already has bid_data rows ({@code countByBidRoundId > 0}), the insert is
 * skipped. Mirrors {@code BidDataCreationService.ensureRowsExist} — protects
 * against a duplicate-fire of {@code R2BuyerAssignmentService}.
 *
 * <p><strong>Known caveat — system user id:</strong> the {@code bid_data_docs}
 * row requires a {@code user_id}. There is no buyer-code → primary user
 * lookup wired up at this stage of sub-project 5; we use the seeded admin
 * user id 9001 (V15 {@code admin@test.com}) as a "system actor" stand-in,
 * mirroring the IT-helper choice in T9. Sub-project 5b/c may revisit this
 * if special-buyer audit attribution becomes a requirement.
 *
 * <p><strong>Transaction:</strong> {@code MANDATORY} — the orchestrating
 * {@code R2BuyerAssignmentService} (Task 11) provides a {@code REQUIRES_NEW}
 * boundary; the underlying repo INSERT also requires a transaction.
 */
@Service
public class BidDataForAllAEService {

    private static final Logger log = LoggerFactory.getLogger(BidDataForAllAEService.class);

    /**
     * V15 admin@test.com user id — used as the system actor for special-buyer
     * bid_data_doc creation. See class javadoc for the caveat.
     */
    static final long SYSTEM_USER_ID = 9001L;

    private final BidDataForAllAERepository bidDataForAllAERepo;
    private final BidRoundRepository bidRoundRepo;
    private final BidDataRepository bidDataRepo;
    private final BidDataDocService bidDataDocService;
    private final SchedulingAuctionRepository saRepo;
    private final AuctionRepository auctionRepo;

    public BidDataForAllAEService(
            BidDataForAllAERepository bidDataForAllAERepo,
            BidRoundRepository bidRoundRepo,
            BidDataRepository bidDataRepo,
            BidDataDocService bidDataDocService,
            SchedulingAuctionRepository saRepo,
            AuctionRepository auctionRepo) {
        this.bidDataForAllAERepo = bidDataForAllAERepo;
        this.bidRoundRepo = bidRoundRepo;
        this.bidDataRepo = bidDataRepo;
        this.bidDataDocService = bidDataDocService;
        this.saRepo = saRepo;
        this.auctionRepo = auctionRepo;
    }

    /**
     * Generate {@code bid_data} rows for every special-treatment buyer code
     * in {@code buyerCodeIds}, scoped to the supplied R2 scheduling auction.
     *
     * @param r2SchedulingAuctionId the R2 {@code scheduling_auctions.id}
     * @param buyerCodeIds          the set of special-treatment
     *                              {@code buyer_codes.id} values
     * @return the total number of {@code bid_data} rows inserted across all
     *         supplied buyer codes (idempotent skips contribute 0)
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public int generateForSpecialBuyers(long r2SchedulingAuctionId, Set<Long> buyerCodeIds) {
        if (buyerCodeIds.isEmpty()) {
            return 0;
        }

        SchedulingAuction sa = saRepo.findById(r2SchedulingAuctionId)
                .orElseThrow(() -> new SchedulingAuctionNotFoundException(r2SchedulingAuctionId));
        Auction auction = auctionRepo.findById(sa.getAuctionId())
                .orElseThrow(() -> new IllegalStateException(
                        "Auction not found for scheduling_auction id=" + r2SchedulingAuctionId
                                + " auctionId=" + sa.getAuctionId()));
        Long weekId = auction.getWeekId();

        int total = 0;
        for (Long codeId : buyerCodeIds) {
            long bidRoundId = getOrCreateBidRound(r2SchedulingAuctionId, codeId, weekId);

            // Idempotency guard — another invocation already populated this
            // (bid_round, buyer_code) slot; skip rather than double-insert.
            if (bidDataRepo.countByBidRoundId(bidRoundId) > 0) {
                log.debug("Skipping special-buyer insert: bid_round_id={} buyer_code_id={} "
                                + "already has bid_data rows",
                        bidRoundId, codeId);
                continue;
            }

            BidDataDoc doc = bidDataDocService.getOrCreate(SYSTEM_USER_ID, codeId, weekId);
            int inserted = bidDataForAllAERepo.insertForSpecialBuyer(
                    r2SchedulingAuctionId, codeId, bidRoundId, doc.getId());
            total += inserted;
        }

        log.info("Special-buyer bid_data generation complete: r2SaId={} buyerCodeCount={} totalRowsInserted={}",
                r2SchedulingAuctionId, buyerCodeIds.size(), total);
        return total;
    }

    /**
     * Get-or-create the {@code bid_rounds} row for (SA, buyer_code).
     * Mirrors {@link com.ecoatm.salesplatform.service.auctions.r1init.Round1InitializationService}'s
     * inline pattern — see CLAUDE.md "no premature abstraction" for why this
     * is not lifted into a shared {@code BidRoundService}.
     */
    private long getOrCreateBidRound(long saId, long buyerCodeId, Long weekId) {
        return bidRoundRepo.findBySchedulingAuctionIdAndBuyerCodeId(saId, buyerCodeId)
                .map(BidRound::getId)
                .orElseGet(() -> {
                    BidRound br = new BidRound();
                    br.setSchedulingAuctionId(saId);
                    br.setBuyerCodeId(buyerCodeId);
                    br.setWeekId(weekId);
                    br.setSubmitted(false);
                    return bidRoundRepo.save(br).getId();
                });
    }
}
