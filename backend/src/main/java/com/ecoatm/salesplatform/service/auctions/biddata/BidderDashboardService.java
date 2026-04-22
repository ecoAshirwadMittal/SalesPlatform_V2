package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.dto.BidDataRow;
import com.ecoatm.salesplatform.dto.BidDataTotals;
import com.ecoatm.salesplatform.dto.BidRoundSummary;
import com.ecoatm.salesplatform.dto.BidderDashboardResponse;
import com.ecoatm.salesplatform.dto.RoundTimerState;
import com.ecoatm.salesplatform.dto.SchedulingAuctionSummary;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.BidData;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BidderDashboardService {

    /**
     * Mirrors {@link BidDataSubmissionService#OWNERSHIP_SQL} — the actual
     * schema is {@code user_mgmt.user_buyers (user_id, buyer_id)} +
     * {@code buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)}; a
     * user owns a buyer code iff at least one shared {@code buyer_id}
     * exists. Duplicated here (not extracted to a shared helper) to
     * keep the IDOR fix as a minimal diff — see Task 12 fix notes.
     */
    private static final String OWNERSHIP_SQL = """
            SELECT COUNT(*) FROM user_mgmt.user_buyers ub
            JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_id = ub.buyer_id
            WHERE ub.user_id = ? AND bcb.buyer_code_id = ?
            """;

    private final SchedulingAuctionRepository saRepo;
    private final AuctionRepository auctionRepo;
    private final BidRoundRepository bidRoundRepo;
    private final QualifiedBuyerCodeRepository qbcRepo;
    private final BidDataRepository bidDataRepo;
    private final JdbcTemplate jdbc;
    private final Clock clock;

    public BidderDashboardService(SchedulingAuctionRepository saRepo,
                                  AuctionRepository auctionRepo,
                                  BidRoundRepository bidRoundRepo,
                                  QualifiedBuyerCodeRepository qbcRepo,
                                  BidDataRepository bidDataRepo,
                                  JdbcTemplate jdbc,
                                  Clock clock) {
        this.saRepo = saRepo;
        this.auctionRepo = auctionRepo;
        this.bidRoundRepo = bidRoundRepo;
        this.qbcRepo = qbcRepo;
        this.bidDataRepo = bidDataRepo;
        this.jdbc = jdbc;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public BidderDashboardLandingResult landingRoute(long userId, long buyerCodeId) {
        // IDOR guard: assert before probing any auction/round/QBC state.
        assertOwnership(userId, buyerCodeId);

        Optional<SchedulingAuction> activeOpt =
                saRepo.findFirstByRoundStatusOrderByStartDatetimeDesc(SchedulingAuctionStatus.Started);
        if (activeOpt.isEmpty()) {
            return new BidderDashboardLandingResult.Error("AUCTION_NOT_FOUND");
        }
        SchedulingAuction active = activeOpt.get();

        Optional<QualifiedBuyerCode> activeQbc =
                qbcRepo.findBySchedulingAuctionIdAndBuyerCodeId(active.getId(), buyerCodeId);
        if (activeQbc.isEmpty() || !activeQbc.get().isIncluded()) {
            return new BidderDashboardLandingResult.Download("BUYER_NOT_INCLUDED");
        }

        List<SchedulingAuction> rounds = saRepo.findByAuctionIdOrderByRoundAsc(active.getAuctionId());
        List<Long> saIds = new ArrayList<>(rounds.size());
        for (SchedulingAuction sa : rounds) {
            saIds.add(sa.getId());
        }

        List<QualifiedBuyerCode> buyerQbcs =
                qbcRepo.findBySchedulingAuctionIdInAndBuyerCodeId(saIds, buyerCodeId);

        if (allRoundsSubmitted(buyerQbcs, rounds.size())) {
            return new BidderDashboardLandingResult.AllDone();
        }

        if (shouldRouteToRound2Download(active, rounds, buyerQbcs)) {
            return new BidderDashboardLandingResult.Download("ROUND2_DOWNLOAD");
        }

        Optional<BidRound> bidRoundOpt = bidRoundRepo.findBySchedulingAuctionId(active.getId());
        if (bidRoundOpt.isEmpty()) {
            return new BidderDashboardLandingResult.Error("BIDROUND_MISSING");
        }
        BidRound bidRound = bidRoundOpt.get();
        return new BidderDashboardLandingResult.Grid(bidRound.getId(), active.getId(), active.getRound());
    }

    @Transactional(readOnly = true)
    public BidderDashboardResponse loadGrid(long bidRoundId, long buyerCodeId) {
        BidRound bidRound = bidRoundRepo.findById(bidRoundId)
                .orElseThrow(() -> new IllegalArgumentException("BidRound not found: id=" + bidRoundId));
        SchedulingAuction sa = saRepo.findById(bidRound.getSchedulingAuctionId())
                .orElseThrow(() -> new IllegalStateException(
                        "SchedulingAuction missing for bidRoundId=" + bidRoundId));
        Auction auction = auctionRepo.findById(sa.getAuctionId())
                .orElseThrow(() -> new IllegalStateException(
                        "Auction missing for schedulingAuctionId=" + sa.getId()));

        List<BidData> rows = bidDataRepo
                .findByBidRoundIdAndBuyerCodeIdOrderByEcoidAscMergedGradeAsc(bidRoundId, buyerCodeId);
        List<BidDataRow> rowDtos = new ArrayList<>(rows.size());
        for (BidData r : rows) {
            rowDtos.add(toRow(r));
        }

        SchedulingAuctionSummary auctionSummary = new SchedulingAuctionSummary(
                sa.getId(),
                auction.getId(),
                auction.getAuctionTitle(),
                sa.getRound(),
                sa.getName(),
                sa.getRoundStatus() == null ? null : sa.getRoundStatus().name());

        BidRoundSummary bidRoundSummary = new BidRoundSummary(
                bidRound.getId(),
                bidRound.getSchedulingAuctionId(),
                sa.getRound(),
                sa.getRoundStatus() == null ? null : sa.getRoundStatus().name(),
                sa.getStartDatetime(),
                sa.getEndDatetime(),
                bidRound.getSubmitted(),
                bidRound.getSubmittedDatetime());

        return new BidderDashboardResponse(
                "GRID",
                auctionSummary,
                bidRoundSummary,
                rowDtos,
                computeTotals(rows),
                buildTimer(bidRound));
    }

    private static boolean allRoundsSubmitted(List<QualifiedBuyerCode> buyerQbcs, int expectedRoundCount) {
        if (buyerQbcs.size() < expectedRoundCount) {
            return false;
        }
        for (QualifiedBuyerCode q : buyerQbcs) {
            if (!q.isSubmitted()) {
                return false;
            }
        }
        return true;
    }

    private static boolean shouldRouteToRound2Download(SchedulingAuction active,
                                                       List<SchedulingAuction> rounds,
                                                       List<QualifiedBuyerCode> buyerQbcs) {
        if (active.getRound() == 3) {
            return false;
        }
        SchedulingAuction r2 = findRound(rounds, 2);
        if (r2 == null || !r2.isHasRound()) {
            return false;
        }
        if (r2.getRoundStatus() != SchedulingAuctionStatus.Closed) {
            return false;
        }
        QualifiedBuyerCode r2Qbc = findQbcFor(buyerQbcs, r2.getId());
        // Mendix Round2BidSubmitted=false: buyer hasn't submitted R2 yet but R2 is closed.
        return r2Qbc != null && !r2Qbc.isSubmitted();
    }

    private static SchedulingAuction findRound(List<SchedulingAuction> rounds, int round) {
        for (SchedulingAuction sa : rounds) {
            if (sa.getRound() == round) {
                return sa;
            }
        }
        return null;
    }

    private static QualifiedBuyerCode findQbcFor(List<QualifiedBuyerCode> qbcs, long saId) {
        for (QualifiedBuyerCode q : qbcs) {
            if (q.getSchedulingAuctionId() != null && q.getSchedulingAuctionId() == saId) {
                return q;
            }
        }
        return null;
    }

    private BidDataTotals computeTotals(List<BidData> rows) {
        BigDecimal totalBidAmount = BigDecimal.ZERO;
        BigDecimal totalPayout = BigDecimal.ZERO;
        int totalBidQty = 0;
        for (BidData r : rows) {
            if (r.getBidAmount() != null) totalBidAmount = totalBidAmount.add(r.getBidAmount());
            if (r.getPayout() != null) totalPayout = totalPayout.add(r.getPayout());
            if (r.getBidQuantity() != null) totalBidQty += r.getBidQuantity();
        }
        return new BidDataTotals(rows.size(), totalBidAmount, totalPayout, totalBidQty);
    }

    private RoundTimerState buildTimer(BidRound round) {
        Instant now = clock.instant();
        Instant starts = round.getStartDatetime();
        Instant ends = round.getEndDatetime();
        long secsToStart = starts == null ? -1 : Math.max(0, starts.getEpochSecond() - now.getEpochSecond());
        long secsToEnd = ends == null ? -1 : Math.max(0, ends.getEpochSecond() - now.getEpochSecond());
        boolean active = starts != null && ends != null && !now.isBefore(starts) && now.isBefore(ends);
        return new RoundTimerState(now, starts, ends, secsToStart, secsToEnd, active);
    }

    private void assertOwnership(long userId, long buyerCodeId) {
        if (hasAdministratorRole()) {
            return;
        }
        Long count = jdbc.queryForObject(OWNERSHIP_SQL, Long.class, userId, buyerCodeId);
        if (count == null || count == 0L) {
            throw new BidDataSubmissionException("NOT_YOUR_BID_DATA",
                    "User does not own buyer_code_id=" + buyerCodeId);
        }
    }

    private static boolean hasAdministratorRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        for (var authority : auth.getAuthorities()) {
            if ("ROLE_Administrator".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private static BidDataRow toRow(BidData b) {
        return new BidDataRow(
                b.getId(),
                b.getBidRoundId(),
                b.getEcoid(),
                b.getMergedGrade(),
                b.getBuyerCodeType(),
                b.getBidQuantity(),
                b.getBidAmount(),
                b.getTargetPrice(),
                b.getMaximumQuantity(),
                b.getPayout(),
                b.getSubmittedBidQuantity(),
                b.getSubmittedBidAmount(),
                b.getLastValidBidQuantity(),
                b.getLastValidBidAmount(),
                b.getSubmittedDatetime(),
                b.getChangedDate());
    }
}
