package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataCreationRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BidDataCreationService {

    private static final Logger log = LoggerFactory.getLogger(BidDataCreationService.class);

    // Generous because the advisory lock serializes concurrent callers and the
    // worst-case CTE may insert thousands of rows.
    private static final int GENERATION_TX_TIMEOUT_SECONDS = 30;

    private final BidDataCreationRepository creationRepo;
    private final BidDataRepository bidDataRepo;
    private final BidDataDocService docService;
    private final BidRoundRepository bidRoundRepo;
    private final SchedulingAuctionRepository saRepo;
    private final AuctionRepository auctionRepo;
    private final JdbcTemplate jdbc;

    public BidDataCreationService(BidDataCreationRepository creationRepo,
                                   BidDataRepository bidDataRepo,
                                   BidDataDocService docService,
                                   BidRoundRepository bidRoundRepo,
                                   SchedulingAuctionRepository saRepo,
                                   AuctionRepository auctionRepo,
                                   JdbcTemplate jdbc) {
        this.creationRepo = creationRepo;
        this.bidDataRepo = bidDataRepo;
        this.docService = docService;
        this.bidRoundRepo = bidRoundRepo;
        this.saRepo = saRepo;
        this.auctionRepo = auctionRepo;
        this.jdbc = jdbc;
    }

    /**
     * @implNote The {@code (int) bidRoundId} narrowing cast on the
     * {@code pg_advisory_xact_lock} call is intentional. Postgres exposes two
     * overloads — {@code (bigint)} and {@code (int, int)} — and {@code hashtext()}
     * returns {@code int4}. Binding {@code bidRoundId} as {@code int4} (rather
     * than the default {@code BIGINT} for a Java {@code long}) selects the
     * {@code (int, int)} overload; the {@code (int4, int8)} shape would fail at
     * parse time with "function pg_advisory_xact_lock(integer, bigint) does not
     * exist". Bid round ids are surrogate serial keys that fit in {@code int4}
     * for the lifetime of this app. {@code Object.class} is used because
     * {@code pg_advisory_xact_lock} returns SQL {@code void}.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = GENERATION_TX_TIMEOUT_SECONDS)
    public BidDataCreationResult ensureRowsExist(long buyerCodeId, long bidRoundId, long userId) {
        long started = System.currentTimeMillis();

        if (bidDataRepo.countByBidRoundId(bidRoundId) > 0) {
            return new BidDataCreationResult(0, true, System.currentTimeMillis() - started);
        }

        jdbc.queryForObject(
            "SELECT pg_advisory_xact_lock(hashtext('bid_data_gen'), ?)",
            Object.class, (int) bidRoundId);

        if (bidDataRepo.countByBidRoundId(bidRoundId) > 0) {
            return new BidDataCreationResult(0, true, System.currentTimeMillis() - started);
        }

        var round = bidRoundRepo.findById(bidRoundId)
            .orElseThrow(() -> new java.util.NoSuchElementException("BidRound not found: id=" + bidRoundId));
        // FK-derived from loaded row; unreachable under consistent FKs.
        var sa = saRepo.findById(round.getSchedulingAuctionId()).orElseThrow();
        // FK-derived from loaded row; unreachable under consistent FKs.
        var auction = auctionRepo.findById(sa.getAuctionId()).orElseThrow();

        BidDataDoc doc = docService.getOrCreate(userId, buyerCodeId, auction.getWeekId());
        int inserted = creationRepo.generate(bidRoundId, buyerCodeId, doc.getId());

        long duration = System.currentTimeMillis() - started;
        log.info("bid-data generated bidRoundId={} buyerCodeId={} rows={} durationMs={}",
                 bidRoundId, buyerCodeId, inserted, duration);

        return new BidDataCreationResult(inserted, false, duration);
    }
}
