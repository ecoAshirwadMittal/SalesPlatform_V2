package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.CreateAuctionRequest;
import com.ecoatm.salesplatform.dto.CreateAuctionResponse;
import com.ecoatm.salesplatform.exception.AuctionAlreadyExistsException;
import com.ecoatm.salesplatform.exception.DuplicateAuctionTitleException;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Porting target: Mendix {@code ACT_Create_Auction} + {@code VAL_Create_Auction}.
 *
 * <p>Invariants mirrored from legacy:
 * <ul>
 *   <li>One auction per week (gated by {@link AuctionRepository#existsByWeekId}).</li>
 *   <li>Title is case-insensitively unique across {@code auctions.auctions}.</li>
 *   <li>Three scheduling rounds are created in the same transaction with
 *       fixed hour offsets from {@code week_start_datetime}:
 *       R1 +16h→+103h, R2 +104h→+128h, R3 +129h→+156h.</li>
 *   <li>All rounds start in {@link SchedulingAuctionStatus#Scheduled}; the
 *       parent auction itself stays {@link AuctionStatus#Unscheduled} until
 *       the round editor lands (matches legacy default).</li>
 * </ul>
 */
@Service
public class AuctionService {

    static final long ROUND1_START_OFFSET_HOURS = 16;
    static final long ROUND1_END_OFFSET_HOURS = 103;
    static final long ROUND2_START_OFFSET_HOURS = 104;
    static final long ROUND2_END_OFFSET_HOURS = 128;
    static final long ROUND3_START_OFFSET_HOURS = 129;
    static final long ROUND3_END_OFFSET_HOURS = 156;

    private final AuctionRepository auctionRepository;
    private final SchedulingAuctionRepository schedulingAuctionRepository;
    private final WeekRepository weekRepository;

    public AuctionService(AuctionRepository auctionRepository,
                          SchedulingAuctionRepository schedulingAuctionRepository,
                          WeekRepository weekRepository) {
        this.auctionRepository = auctionRepository;
        this.schedulingAuctionRepository = schedulingAuctionRepository;
        this.weekRepository = weekRepository;
    }

    @Transactional
    public CreateAuctionResponse createAuction(CreateAuctionRequest req) {
        if (req == null || req.weekId() == null) {
            throw new IllegalArgumentException("weekId is required");
        }

        Week week = weekRepository.findById(req.weekId())
                .orElseThrow(() -> new EntityNotFoundException("Week", req.weekId()));

        if (auctionRepository.existsByWeekId(week.getId())) {
            throw new AuctionAlreadyExistsException(week.getId());
        }

        String title = buildAuctionTitle(week.getWeekDisplay(), req.customSuffix());
        if (auctionRepository.existsByAuctionTitleIgnoreCase(title)) {
            throw new DuplicateAuctionTitleException(title);
        }

        Instant now = Instant.now();
        String actor = currentUsername();

        Auction auction = new Auction();
        auction.setAuctionTitle(title);
        auction.setAuctionStatus(AuctionStatus.Unscheduled);
        auction.setWeekId(week.getId());
        auction.setCreatedBy(actor);
        auction.setUpdatedBy(actor);
        auction.setCreatedDate(now);
        auction.setChangedDate(now);
        Auction saved = auctionRepository.save(auction);

        Instant weekStart = week.getWeekStartDateTime();
        List<SchedulingAuction> rounds = List.of(
                buildRound(saved.getId(), 1, weekStart, ROUND1_START_OFFSET_HOURS, ROUND1_END_OFFSET_HOURS, week, actor, now),
                buildRound(saved.getId(), 2, weekStart, ROUND2_START_OFFSET_HOURS, ROUND2_END_OFFSET_HOURS, week, actor, now),
                buildRound(saved.getId(), 3, weekStart, ROUND3_START_OFFSET_HOURS, ROUND3_END_OFFSET_HOURS, week, actor, now));
        List<SchedulingAuction> savedRounds = schedulingAuctionRepository.saveAll(rounds);

        List<CreateAuctionResponse.Round> roundDtos = savedRounds.stream()
                .map(r -> new CreateAuctionResponse.Round(
                        r.getId(),
                        r.getRound(),
                        r.getStartDatetime(),
                        r.getEndDatetime(),
                        r.getRoundStatus().name()))
                .toList();

        return new CreateAuctionResponse(
                saved.getId(),
                saved.getAuctionTitle(),
                saved.getAuctionStatus().name(),
                week.getId(),
                week.getWeekDisplay(),
                roundDtos);
    }

    /**
     * Mirrors Mendix title composition: "Auction " + weekDisplay, optionally
     * suffixed by a trimmed custom label separated by a single space.
     */
    static String buildAuctionTitle(String weekDisplay, String customSuffix) {
        String base = "Auction " + (weekDisplay == null ? "" : weekDisplay.trim());
        if (customSuffix == null) {
            return base;
        }
        String trimmed = customSuffix.trim();
        if (trimmed.isEmpty()) {
            return base;
        }
        return base + " " + trimmed;
    }

    private SchedulingAuction buildRound(Long auctionId, int round, Instant weekStart,
                                         long startOffsetHours, long endOffsetHours,
                                         Week week, String actor, Instant now) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setAuctionId(auctionId);
        sa.setName("Round " + round);
        sa.setRound(round);
        sa.setAuctionWeekYear(week.getWeekDisplay());
        sa.setStartDatetime(weekStart.plus(Duration.ofHours(startOffsetHours)));
        sa.setEndDatetime(weekStart.plus(Duration.ofHours(endOffsetHours)));
        sa.setRoundStatus(SchedulingAuctionStatus.Scheduled);
        sa.setHasRound(true);
        sa.setNotificationsEnabled(true);
        sa.setCreatedBy(actor);
        sa.setUpdatedBy(actor);
        sa.setCreatedDate(now);
        sa.setChangedDate(now);
        return sa;
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
