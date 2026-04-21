package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AuctionDetailResponse;
import com.ecoatm.salesplatform.dto.RoundView;
import com.ecoatm.salesplatform.dto.ScheduleAuctionRequest;
import com.ecoatm.salesplatform.dto.ScheduleDefaultsResponse;
import com.ecoatm.salesplatform.event.AuctionScheduledEvent;
import com.ecoatm.salesplatform.event.AuctionUnscheduledEvent;
import com.ecoatm.salesplatform.exception.AuctionAlreadyStartedException;
import com.ecoatm.salesplatform.exception.AuctionHasBidsException;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.exception.RoundValidationException;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.AuctionsFeatureConfigRepository;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Ports the Mendix auction-scheduling microflows:
 * <ul>
 *   <li>{@code ACT_LoadScheduleAuction_Helper} + {@code ACT_Create_SchedulingAuction_Helper_Default}
 *       → {@link #loadScheduleDefaults(Long)}.</li>
 *   <li>{@code ACT_SaveScheduleAuction} + {@code VAL_Schedule_Auction}
 *       → {@link #saveSchedule(Long, ScheduleAuctionRequest, String)}.</li>
 *   <li>{@code ACT_UnscheduleAuction}
 *       → {@link #unschedule(Long, String)}.</li>
 *   <li>{@code ACT_Delete_AuctionFromAdmin}
 *       → {@link #delete(Long)}.</li>
 * </ul>
 *
 * <p>Core invariants preserved from the plan's §1.5:
 * <ul>
 *   <li>An auction in status {@link AuctionStatus#Unscheduled} has <strong>zero</strong>
 *       rounds; save flips to {@link AuctionStatus#Scheduled} and persists exactly three
 *       rounds atomically.</li>
 *   <li>Save is a delete-and-recreate; reschedule is blocked when any round already has
 *       {@code bid_rounds} rows to prevent silent bid-history deletion.</li>
 *   <li>Any round in {@link SchedulingAuctionStatus#Started} blocks Save, Unschedule, and
 *       Delete — mirrors Mendix's Unschedule gate, extended defensively to the other
 *       destructive actions.</li>
 *   <li>Round 3's display name is the customer-facing literal {@code "Upsell Round"} —
 *       see plan §2.</li>
 * </ul>
 */
@Service
public class AuctionScheduleService {

    /** Mendix {@code StartHourOffset} = 16 (the {@code +TimezoneOffset} delta is zero on UTC-stored weeks). */
    private static final int START_HOUR_OFFSET_HOURS = 16;

    /** Round 1 end = weekStart + 103h (Mendix: {@code StartHourOffset + 87}). */
    private static final int ROUND1_END_HOURS_FROM_WEEK_START = START_HOUR_OFFSET_HOURS + 87;

    /** Round 2 end = weekStart + 128h (Mendix: {@code StartHourOffset + 112}). */
    private static final int ROUND2_END_HOURS_FROM_WEEK_START = START_HOUR_OFFSET_HOURS + 112;

    /** Round 3 end = weekStart + 156h (Mendix: {@code StartHourOffset + 140}). */
    private static final int ROUND3_END_HOURS_FROM_WEEK_START = START_HOUR_OFFSET_HOURS + 140;

    /** Fallback defaults used only when the config singleton is missing (defensive — V63 seeds it). */
    private static final int DEFAULT_ROUND2_MINUTES_OFFSET = 360;
    private static final int DEFAULT_ROUND3_MINUTES_OFFSET = 180;

    private static final String ROUND_1_NAME = "Round 1";
    private static final String ROUND_2_NAME = "Round 2";
    // Customer-facing name — do NOT shorten to "Round 3"; appears in the admin UI and emails.
    private static final String ROUND_3_NAME = "Upsell Round";

    private final AuctionRepository auctionRepository;
    private final SchedulingAuctionRepository schedulingAuctionRepository;
    private final WeekRepository weekRepository;
    private final BidRoundRepository bidRoundRepository;
    private final AuctionsFeatureConfigRepository featureConfigRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AuctionScheduleService(AuctionRepository auctionRepository,
                                  SchedulingAuctionRepository schedulingAuctionRepository,
                                  WeekRepository weekRepository,
                                  BidRoundRepository bidRoundRepository,
                                  AuctionsFeatureConfigRepository featureConfigRepository,
                                  ApplicationEventPublisher eventPublisher) {
        this.auctionRepository = auctionRepository;
        this.schedulingAuctionRepository = schedulingAuctionRepository;
        this.weekRepository = weekRepository;
        this.bidRoundRepository = bidRoundRepository;
        this.featureConfigRepository = featureConfigRepository;
        this.eventPublisher = eventPublisher;
    }

    // ---------------------------------------------------------------------
    // Reads
    // ---------------------------------------------------------------------

    @Transactional(readOnly = true)
    public AuctionDetailResponse getAuctionDetail(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction", auctionId));
        Week week = weekRepository.findById(auction.getWeekId())
                .orElseThrow(() -> new EntityNotFoundException("Week", auction.getWeekId()));
        List<SchedulingAuction> rounds =
                schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(auctionId);
        return toDetailResponse(auction, week, rounds);
    }

    @Transactional(readOnly = true)
    public ScheduleDefaultsResponse loadScheduleDefaults(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction", auctionId));
        Week week = weekRepository.findById(auction.getWeekId())
                .orElseThrow(() -> new EntityNotFoundException("Week", auction.getWeekId()));

        AuctionsFeatureConfig config = featureConfigRepository.findSingleton().orElse(null);
        int r2Offset = config != null ? config.getAuctionRound2MinutesOffset() : DEFAULT_ROUND2_MINUTES_OFFSET;
        int r3Offset = config != null ? config.getAuctionRound3MinutesOffset() : DEFAULT_ROUND3_MINUTES_OFFSET;

        List<SchedulingAuction> existing =
                schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(auctionId);

        if (!existing.isEmpty()) {
            // Rescheduling branch — populate from stored rows (ACT_LoadScheduleAuction_Helper).
            Instant r1Start = pickStart(existing, 1);
            Instant r1End = pickEnd(existing, 1);
            Instant r2Start = pickStart(existing, 2);
            Instant r2End = pickEnd(existing, 2);
            Instant r3Start = pickStart(existing, 3);
            Instant r3End = pickEnd(existing, 3);
            // Mendix: Round{2,3}_isActive = hasRound && status != Unscheduled — but the UI
            // primarily flips based on hasRound, and an Unscheduled round always has hasRound=false
            // in our write path. We mirror that: active iff hasRound and not Unscheduled.
            boolean r2Active = isRoundActive(existing, 2);
            boolean r3Active = isRoundActive(existing, 3);
            return new ScheduleDefaultsResponse(
                    r1Start, r1End, r2Start, r2End, r3Start, r3End,
                    r2Active, r3Active, r2Offset, r3Offset);
        }

        // Fresh schedule — ACT_Create_SchedulingAuction_Helper_Default math.
        Instant weekStart = week.getWeekStartDateTime();
        Instant r1Start = weekStart.plus(Duration.ofHours(START_HOUR_OFFSET_HOURS));
        Instant r1End = weekStart.plus(Duration.ofHours(ROUND1_END_HOURS_FROM_WEEK_START));
        Instant r2Start = weekStart
                .plus(Duration.ofHours(ROUND1_END_HOURS_FROM_WEEK_START))
                .plus(Duration.ofMinutes(r2Offset));
        Instant r2End = weekStart.plus(Duration.ofHours(ROUND2_END_HOURS_FROM_WEEK_START));
        Instant r3Start = weekStart
                .plus(Duration.ofHours(ROUND2_END_HOURS_FROM_WEEK_START))
                .plus(Duration.ofMinutes(r3Offset));
        Instant r3End = weekStart.plus(Duration.ofHours(ROUND3_END_HOURS_FROM_WEEK_START));
        return new ScheduleDefaultsResponse(
                r1Start, r1End, r2Start, r2End, r3Start, r3End,
                true, true, r2Offset, r3Offset);
    }

    // ---------------------------------------------------------------------
    // Save Schedule (Confirm)
    // ---------------------------------------------------------------------

    @Transactional
    public AuctionDetailResponse saveSchedule(Long auctionId,
                                              ScheduleAuctionRequest req,
                                              String actor) {
        if (req == null) {
            throw new IllegalArgumentException("Schedule request body is required");
        }
        validate(req);

        // Pessimistic lock — serializes two admins hitting Confirm on the same auction.
        Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction", auctionId));
        Week week = weekRepository.findById(auction.getWeekId())
                .orElseThrow(() -> new EntityNotFoundException("Week", auction.getWeekId()));

        List<SchedulingAuction> existing =
                schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(auctionId);

        if (hasStartedRound(existing)) {
            throw new AuctionAlreadyStartedException();
        }

        if (!existing.isEmpty()) {
            List<Long> existingIds = existing.stream().map(SchedulingAuction::getId).toList();
            if (bidRoundRepository.existsBySchedulingAuctionIdIn(existingIds)) {
                throw new AuctionHasBidsException();
            }
            // Delete-and-recreate — flush so the inserts below don't collide on any
            // downstream unique indexes (none today, but the flush is cheap and defends
            // against future constraint adds).
            schedulingAuctionRepository.deleteByAuctionId(auctionId);
            schedulingAuctionRepository.flush();
        }

        Instant now = Instant.now();
        String weekDisplay = week.getWeekDisplay();

        SchedulingAuction r1 = newRound(auction.getId(), 1, ROUND_1_NAME, weekDisplay,
                req.round1Start(), req.round1End(),
                SchedulingAuctionStatus.Scheduled, true, actor, now);
        SchedulingAuction r2 = newRound(auction.getId(), 2, ROUND_2_NAME, weekDisplay,
                req.round2Start(), req.round2End(),
                req.round2Active() ? SchedulingAuctionStatus.Scheduled : SchedulingAuctionStatus.Unscheduled,
                req.round2Active(), actor, now);
        SchedulingAuction r3 = newRound(auction.getId(), 3, ROUND_3_NAME, weekDisplay,
                req.round3Start(), req.round3End(),
                req.round3Active() ? SchedulingAuctionStatus.Scheduled : SchedulingAuctionStatus.Unscheduled,
                req.round3Active(), actor, now);
        schedulingAuctionRepository.saveAll(List.of(r1, r2, r3));

        auction.setAuctionStatus(AuctionStatus.Scheduled);
        auction.setChangedDate(now);
        auction.setUpdatedBy(actor);
        auctionRepository.save(auction);

        // Post-commit no-op audit hook — actual Snowflake push deferred to Phase F.
        eventPublisher.publishEvent(new AuctionScheduledEvent(auction.getId()));

        return toDetailResponse(auction, week, List.of(r1, r2, r3));
    }

    // ---------------------------------------------------------------------
    // Unschedule
    // ---------------------------------------------------------------------

    @Transactional
    public AuctionDetailResponse unschedule(Long auctionId, String actor) {
        Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction", auctionId));
        Week week = weekRepository.findById(auction.getWeekId())
                .orElseThrow(() -> new EntityNotFoundException("Week", auction.getWeekId()));

        List<SchedulingAuction> rounds =
                schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(auctionId);

        if (hasStartedRound(rounds)) {
            throw new AuctionAlreadyStartedException(
                    "Auction has started. Unscheduling the auction is not available.");
        }

        Instant now = Instant.now();
        auction.setAuctionStatus(AuctionStatus.Unscheduled);
        auction.setChangedDate(now);
        auction.setUpdatedBy(actor);
        auctionRepository.save(auction);

        for (SchedulingAuction r : rounds) {
            r.setRoundStatus(SchedulingAuctionStatus.Unscheduled);
            r.setChangedDate(now);
            r.setUpdatedBy(actor);
        }
        schedulingAuctionRepository.saveAll(rounds);

        eventPublisher.publishEvent(new AuctionUnscheduledEvent(auction.getId()));

        return toDetailResponse(auction, week, rounds);
    }

    // ---------------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------------

    @Transactional
    public void delete(Long auctionId) {
        Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction", auctionId));

        List<SchedulingAuction> rounds =
                schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(auctionId);
        if (hasStartedRound(rounds)) {
            throw new AuctionAlreadyStartedException(
                    "Auction has started. Deleting the auction is not available.");
        }

        // V58 declares ON DELETE CASCADE on scheduling_auctions.auction_id, and V59 cascades
        // bid_rounds.scheduling_auction_id — so a single delete on the parent row is sufficient.
        auctionRepository.delete(auction);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void validate(ScheduleAuctionRequest req) {
        List<String> errors = new ArrayList<>();
        // Round 1 is always active (Mendix: no toggle).
        if (req.round1Start() == null || req.round1End() == null
                || !req.round1End().isAfter(req.round1Start())) {
            errors.add("Round 1 end date must be later than start date");
        }
        if (req.round2Active()) {
            if (req.round2Start() == null || req.round2End() == null
                    || !req.round2End().isAfter(req.round2Start())) {
                errors.add("Round 2 end date must be later than start date");
            }
        }
        if (req.round3Active()) {
            if (req.round3Start() == null || req.round3End() == null
                    || !req.round3End().isAfter(req.round3Start())) {
                errors.add("Round 3 end date must be later than start date");
            }
        }
        if (!errors.isEmpty()) {
            throw new RoundValidationException(errors);
        }
    }

    private SchedulingAuction newRound(Long auctionId,
                                       int round,
                                       String name,
                                       String weekDisplay,
                                       Instant start,
                                       Instant end,
                                       SchedulingAuctionStatus status,
                                       boolean hasRound,
                                       String actor,
                                       Instant now) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setAuctionId(auctionId);
        sa.setRound(round);
        sa.setName(name);
        sa.setAuctionWeekYear(weekDisplay);
        sa.setStartDatetime(start);
        sa.setEndDatetime(end);
        sa.setRoundStatus(status);
        sa.setHasRound(hasRound);
        sa.setNotificationsEnabled(true);
        sa.setCreatedBy(actor);
        sa.setUpdatedBy(actor);
        sa.setCreatedDate(now);
        sa.setChangedDate(now);
        return sa;
    }

    private static boolean hasStartedRound(List<SchedulingAuction> rounds) {
        return rounds.stream().anyMatch(r -> r.getRoundStatus() == SchedulingAuctionStatus.Started);
    }

    private static Optional<SchedulingAuction> pick(List<SchedulingAuction> rounds, int round) {
        return rounds.stream().filter(r -> r.getRound() == round).findFirst();
    }

    private static Instant pickStart(List<SchedulingAuction> rounds, int round) {
        return pick(rounds, round).map(SchedulingAuction::getStartDatetime).orElse(null);
    }

    private static Instant pickEnd(List<SchedulingAuction> rounds, int round) {
        return pick(rounds, round).map(SchedulingAuction::getEndDatetime).orElse(null);
    }

    private static boolean isRoundActive(List<SchedulingAuction> rounds, int round) {
        return pick(rounds, round)
                .map(r -> r.isHasRound() && r.getRoundStatus() != SchedulingAuctionStatus.Unscheduled)
                .orElse(false);
    }

    private AuctionDetailResponse toDetailResponse(Auction auction,
                                                   Week week,
                                                   List<SchedulingAuction> rounds) {
        List<RoundView> roundViews = rounds.stream()
                .sorted(Comparator.comparingInt(SchedulingAuction::getRound))
                .map(r -> new RoundView(
                        r.getId(),
                        r.getRound(),
                        r.getName(),
                        r.getStartDatetime(),
                        r.getEndDatetime(),
                        r.getRoundStatus() != null ? r.getRoundStatus().name() : null,
                        r.isHasRound()))
                .toList();
        return new AuctionDetailResponse(
                auction.getId(),
                auction.getAuctionTitle(),
                auction.getAuctionStatus() != null ? auction.getAuctionStatus().name() : null,
                week.getId(),
                week.getWeekDisplay(),
                roundViews);
    }
}
