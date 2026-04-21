package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AuctionDetailResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuctionScheduleService}. All collaborators are
 * mocked; no Spring context, no database. Covers the key invariants from
 * Phase C §1.5 of {@code docs/tasks/auction-scheduling-plan.md}:
 *
 * <ul>
 *   <li>Fresh schedule-defaults are computed from {@code week.week_start_datetime}
 *       using the documented hour + minute offsets.</li>
 *   <li>Reschedule-defaults come from the stored rows when rounds already exist.</li>
 *   <li>Save schedule atomically deletes and recreates three rounds, flips
 *       the parent status, and publishes {@link AuctionScheduledEvent}.</li>
 *   <li>Save is blocked by {@code Started} rounds (409) and by existing
 *       bid rows (409).</li>
 *   <li>Round end-before-start fails with {@link RoundValidationException}.</li>
 *   <li>Unschedule flips every round to {@code Unscheduled} and publishes
 *       {@link AuctionUnscheduledEvent}.</li>
 *   <li>Delete relies on DB cascade — only the parent row is deleted.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class AuctionScheduleServiceTest {

    private static final Long AUCTION_ID = 42L;
    private static final Long WEEK_ID = 100L;
    private static final String WEEK_DISPLAY = "2026 / Wk17";
    private static final Instant WEEK_START = Instant.parse("2026-04-20T00:00:00Z");
    private static final String ACTOR = "admin@ecoatm.com";

    @Mock private AuctionRepository auctionRepository;
    @Mock private SchedulingAuctionRepository schedulingAuctionRepository;
    @Mock private WeekRepository weekRepository;
    @Mock private BidRoundRepository bidRoundRepository;
    @Mock private AuctionsFeatureConfigRepository featureConfigRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private AuctionScheduleService service;

    @BeforeEach
    void setUp() {
        service = new AuctionScheduleService(
                auctionRepository,
                schedulingAuctionRepository,
                weekRepository,
                bidRoundRepository,
                featureConfigRepository,
                eventPublisher);
    }

    // --- loadScheduleDefaults ---

    @Test
    @DisplayName("loadScheduleDefaults — fresh schedule uses week + offsets (16h, 360min, 180min)")
    void loadScheduleDefaults_freshSchedule_computesFromWeek() {
        when(auctionRepository.findById(AUCTION_ID))
                .thenReturn(Optional.of(auction(AUCTION_ID, AuctionStatus.Unscheduled)));
        when(weekRepository.findById(WEEK_ID))
                .thenReturn(Optional.of(week(WEEK_ID, WEEK_DISPLAY, WEEK_START)));
        when(featureConfigRepository.findSingleton())
                .thenReturn(Optional.of(config(360, 180)));
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(List.of());

        ScheduleDefaultsResponse defaults = service.loadScheduleDefaults(AUCTION_ID);

        assertThat(defaults.round1Start()).isEqualTo(WEEK_START.plus(Duration.ofHours(16)));
        assertThat(defaults.round1End()).isEqualTo(WEEK_START.plus(Duration.ofHours(103)));
        assertThat(defaults.round2Start())
                .isEqualTo(WEEK_START.plus(Duration.ofHours(103)).plus(Duration.ofMinutes(360)));
        assertThat(defaults.round2End()).isEqualTo(WEEK_START.plus(Duration.ofHours(128)));
        assertThat(defaults.round3Start())
                .isEqualTo(WEEK_START.plus(Duration.ofHours(128)).plus(Duration.ofMinutes(180)));
        assertThat(defaults.round3End()).isEqualTo(WEEK_START.plus(Duration.ofHours(156)));
        assertThat(defaults.round2Active()).isTrue();
        assertThat(defaults.round3Active()).isTrue();
        assertThat(defaults.round2MinutesOffset()).isEqualTo(360);
        assertThat(defaults.round3MinutesOffset()).isEqualTo(180);
    }

    @Test
    @DisplayName("loadScheduleDefaults — reschedule branch uses stored rows' times")
    void loadScheduleDefaults_reschedule_usesStoredRows() {
        when(auctionRepository.findById(AUCTION_ID))
                .thenReturn(Optional.of(auction(AUCTION_ID, AuctionStatus.Scheduled)));
        when(weekRepository.findById(WEEK_ID))
                .thenReturn(Optional.of(week(WEEK_ID, WEEK_DISPLAY, WEEK_START)));
        when(featureConfigRepository.findSingleton())
                .thenReturn(Optional.of(config(360, 180)));

        Instant r1s = Instant.parse("2026-04-21T01:00:00Z");
        Instant r1e = Instant.parse("2026-04-24T01:00:00Z");
        Instant r2s = Instant.parse("2026-04-24T02:00:00Z");
        Instant r2e = Instant.parse("2026-04-25T01:00:00Z");
        Instant r3s = Instant.parse("2026-04-25T02:00:00Z");
        Instant r3e = Instant.parse("2026-04-26T01:00:00Z");
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(List.of(
                        round(11L, 1, r1s, r1e, SchedulingAuctionStatus.Scheduled, true),
                        round(12L, 2, r2s, r2e, SchedulingAuctionStatus.Scheduled, true),
                        round(13L, 3, r3s, r3e, SchedulingAuctionStatus.Unscheduled, false)));

        ScheduleDefaultsResponse defaults = service.loadScheduleDefaults(AUCTION_ID);

        assertThat(defaults.round1Start()).isEqualTo(r1s);
        assertThat(defaults.round1End()).isEqualTo(r1e);
        assertThat(defaults.round2Start()).isEqualTo(r2s);
        assertThat(defaults.round2End()).isEqualTo(r2e);
        assertThat(defaults.round3Start()).isEqualTo(r3s);
        assertThat(defaults.round3End()).isEqualTo(r3e);
        assertThat(defaults.round2Active()).isTrue();
        // Stored round 3 is Unscheduled + hasRound=false -> inactive.
        assertThat(defaults.round3Active()).isFalse();
    }

    // --- saveSchedule ---

    @Test
    @DisplayName("saveSchedule — happy path creates three rounds, flips status, publishes event")
    void saveSchedule_happyPath() {
        Auction auction = auction(AUCTION_ID, AuctionStatus.Unscheduled);
        when(auctionRepository.findByIdForUpdate(AUCTION_ID)).thenReturn(Optional.of(auction));
        when(weekRepository.findById(WEEK_ID))
                .thenReturn(Optional.of(week(WEEK_ID, WEEK_DISPLAY, WEEK_START)));
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(List.of());
        when(schedulingAuctionRepository.saveAll(anyCollection()))
                .thenAnswer(inv -> inv.<List<SchedulingAuction>>getArgument(0));

        ScheduleAuctionRequest req = validRequest();

        AuctionDetailResponse response = service.saveSchedule(AUCTION_ID, req, ACTOR);

        assertThat(response.auctionStatus()).isEqualTo(AuctionStatus.Scheduled.name());
        assertThat(response.rounds()).hasSize(3);
        assertThat(response.rounds().get(2).name()).isEqualTo("Upsell Round");

        // No prior rounds → no delete.
        verify(schedulingAuctionRepository, never()).deleteByAuctionId(anyLong());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SchedulingAuction>> captor = ArgumentCaptor.forClass(List.class);
        verify(schedulingAuctionRepository).saveAll(captor.capture());
        List<SchedulingAuction> saved = captor.getValue();
        assertThat(saved).hasSize(3);
        assertThat(saved.get(0).getRound()).isEqualTo(1);
        assertThat(saved.get(0).getName()).isEqualTo("Round 1");
        assertThat(saved.get(1).getName()).isEqualTo("Round 2");
        assertThat(saved.get(2).getName()).isEqualTo("Upsell Round");
        assertThat(saved.get(0).getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Scheduled);
        assertThat(auction.getAuctionStatus()).isEqualTo(AuctionStatus.Scheduled);
        assertThat(auction.getUpdatedBy()).isEqualTo(ACTOR);

        verify(eventPublisher).publishEvent(any(AuctionScheduledEvent.class));
    }

    @Test
    @DisplayName("saveSchedule — reschedule deletes existing rows then recreates")
    void saveSchedule_reschedule_deletesAndRecreates() {
        Auction auction = auction(AUCTION_ID, AuctionStatus.Scheduled);
        when(auctionRepository.findByIdForUpdate(AUCTION_ID)).thenReturn(Optional.of(auction));
        when(weekRepository.findById(WEEK_ID))
                .thenReturn(Optional.of(week(WEEK_ID, WEEK_DISPLAY, WEEK_START)));
        List<SchedulingAuction> existing = List.of(
                round(11L, 1, WEEK_START, WEEK_START.plus(Duration.ofHours(1)),
                        SchedulingAuctionStatus.Scheduled, true),
                round(12L, 2, WEEK_START, WEEK_START.plus(Duration.ofHours(2)),
                        SchedulingAuctionStatus.Scheduled, true),
                round(13L, 3, WEEK_START, WEEK_START.plus(Duration.ofHours(3)),
                        SchedulingAuctionStatus.Scheduled, true));
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(existing);
        when(bidRoundRepository.existsBySchedulingAuctionIdIn(anyCollection())).thenReturn(false);
        when(schedulingAuctionRepository.saveAll(anyCollection()))
                .thenAnswer(inv -> inv.<List<SchedulingAuction>>getArgument(0));

        service.saveSchedule(AUCTION_ID, validRequest(), ACTOR);

        verify(schedulingAuctionRepository).deleteByAuctionId(AUCTION_ID);
        verify(schedulingAuctionRepository).flush();
        verify(schedulingAuctionRepository).saveAll(anyCollection());
        verify(eventPublisher).publishEvent(any(AuctionScheduledEvent.class));
    }

    @Test
    @DisplayName("saveSchedule — existing Started round → AuctionAlreadyStartedException (409)")
    void saveSchedule_startedRound_blocks() {
        Auction auction = auction(AUCTION_ID, AuctionStatus.Scheduled);
        when(auctionRepository.findByIdForUpdate(AUCTION_ID)).thenReturn(Optional.of(auction));
        when(weekRepository.findById(WEEK_ID))
                .thenReturn(Optional.of(week(WEEK_ID, WEEK_DISPLAY, WEEK_START)));
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(List.of(round(11L, 1,
                        WEEK_START, WEEK_START.plus(Duration.ofHours(1)),
                        SchedulingAuctionStatus.Started, true)));

        assertThatThrownBy(() -> service.saveSchedule(AUCTION_ID, validRequest(), ACTOR))
                .isInstanceOf(AuctionAlreadyStartedException.class);

        verify(schedulingAuctionRepository, never()).deleteByAuctionId(anyLong());
        verify(schedulingAuctionRepository, never()).saveAll(anyCollection());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("saveSchedule — bids already submitted → AuctionHasBidsException (409)")
    void saveSchedule_withBids_blocks() {
        Auction auction = auction(AUCTION_ID, AuctionStatus.Scheduled);
        when(auctionRepository.findByIdForUpdate(AUCTION_ID)).thenReturn(Optional.of(auction));
        when(weekRepository.findById(WEEK_ID))
                .thenReturn(Optional.of(week(WEEK_ID, WEEK_DISPLAY, WEEK_START)));
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(List.of(round(11L, 1,
                        WEEK_START, WEEK_START.plus(Duration.ofHours(1)),
                        SchedulingAuctionStatus.Scheduled, true)));
        when(bidRoundRepository.existsBySchedulingAuctionIdIn(anyCollection())).thenReturn(true);

        assertThatThrownBy(() -> service.saveSchedule(AUCTION_ID, validRequest(), ACTOR))
                .isInstanceOf(AuctionHasBidsException.class);

        verify(schedulingAuctionRepository, never()).deleteByAuctionId(anyLong());
        verify(schedulingAuctionRepository, never()).saveAll(anyCollection());
    }

    @Test
    @DisplayName("saveSchedule — R1 end <= start → RoundValidationException (400)")
    void saveSchedule_invalidRound1_throws() {
        Instant start = WEEK_START.plus(Duration.ofHours(16));
        // end equal to start — not after.
        ScheduleAuctionRequest bad = new ScheduleAuctionRequest(
                start, start,
                start.plus(Duration.ofHours(2)), start.plus(Duration.ofHours(3)), true,
                start.plus(Duration.ofHours(4)), start.plus(Duration.ofHours(5)), true);

        assertThatThrownBy(() -> service.saveSchedule(AUCTION_ID, bad, ACTOR))
                .isInstanceOf(RoundValidationException.class)
                .hasMessageContaining("Round 1");

        // Validation runs before DB access.
        verify(auctionRepository, never()).findByIdForUpdate(anyLong());
    }

    @Test
    @DisplayName("saveSchedule — inactive R3 skips its validation")
    void saveSchedule_inactiveRound3_skipsValidation() {
        Auction auction = auction(AUCTION_ID, AuctionStatus.Unscheduled);
        when(auctionRepository.findByIdForUpdate(AUCTION_ID)).thenReturn(Optional.of(auction));
        when(weekRepository.findById(WEEK_ID))
                .thenReturn(Optional.of(week(WEEK_ID, WEEK_DISPLAY, WEEK_START)));
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(List.of());
        when(schedulingAuctionRepository.saveAll(anyCollection()))
                .thenAnswer(inv -> inv.<List<SchedulingAuction>>getArgument(0));

        Instant r1s = WEEK_START.plus(Duration.ofHours(16));
        Instant r1e = WEEK_START.plus(Duration.ofHours(103));
        Instant r2s = WEEK_START.plus(Duration.ofHours(109));
        Instant r2e = WEEK_START.plus(Duration.ofHours(128));
        // Round 3 is inactive AND has an invalid end<=start — should be skipped.
        Instant r3s = WEEK_START.plus(Duration.ofHours(131));
        Instant r3e = WEEK_START.plus(Duration.ofHours(130));
        ScheduleAuctionRequest req = new ScheduleAuctionRequest(r1s, r1e, r2s, r2e, true, r3s, r3e, false);

        AuctionDetailResponse response = service.saveSchedule(AUCTION_ID, req, ACTOR);
        assertThat(response.rounds()).hasSize(3);
        assertThat(response.rounds().get(2).roundStatus())
                .isEqualTo(SchedulingAuctionStatus.Unscheduled.name());
        assertThat(response.rounds().get(2).hasRound()).isFalse();
    }

    // --- unschedule ---

    @Test
    @DisplayName("unschedule — flips auction + every round to Unscheduled, publishes event")
    void unschedule_happyPath() {
        Auction auction = auction(AUCTION_ID, AuctionStatus.Scheduled);
        when(auctionRepository.findByIdForUpdate(AUCTION_ID)).thenReturn(Optional.of(auction));
        when(weekRepository.findById(WEEK_ID))
                .thenReturn(Optional.of(week(WEEK_ID, WEEK_DISPLAY, WEEK_START)));
        List<SchedulingAuction> rounds = List.of(
                round(11L, 1, WEEK_START, WEEK_START.plus(Duration.ofHours(1)),
                        SchedulingAuctionStatus.Scheduled, true),
                round(12L, 2, WEEK_START, WEEK_START.plus(Duration.ofHours(2)),
                        SchedulingAuctionStatus.Scheduled, true),
                round(13L, 3, WEEK_START, WEEK_START.plus(Duration.ofHours(3)),
                        SchedulingAuctionStatus.Scheduled, true));
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(rounds);

        service.unschedule(AUCTION_ID, ACTOR);

        assertThat(auction.getAuctionStatus()).isEqualTo(AuctionStatus.Unscheduled);
        assertThat(rounds).allSatisfy(r ->
                assertThat(r.getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Unscheduled));
        verify(eventPublisher).publishEvent(any(AuctionUnscheduledEvent.class));
    }

    @Test
    @DisplayName("unschedule — Started round → AuctionAlreadyStartedException (409)")
    void unschedule_startedRound_blocks() {
        Auction auction = auction(AUCTION_ID, AuctionStatus.Scheduled);
        when(auctionRepository.findByIdForUpdate(AUCTION_ID)).thenReturn(Optional.of(auction));
        when(weekRepository.findById(WEEK_ID))
                .thenReturn(Optional.of(week(WEEK_ID, WEEK_DISPLAY, WEEK_START)));
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(List.of(round(11L, 1,
                        WEEK_START, WEEK_START.plus(Duration.ofHours(1)),
                        SchedulingAuctionStatus.Started, true)));

        assertThatThrownBy(() -> service.unschedule(AUCTION_ID, ACTOR))
                .isInstanceOf(AuctionAlreadyStartedException.class);

        verify(eventPublisher, never()).publishEvent(any());
    }

    // --- delete ---

    @Test
    @DisplayName("delete — happy path deletes parent only (DB cascade handles the rest)")
    void delete_happyPath() {
        Auction auction = auction(AUCTION_ID, AuctionStatus.Scheduled);
        when(auctionRepository.findByIdForUpdate(AUCTION_ID)).thenReturn(Optional.of(auction));
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(List.of(round(11L, 1,
                        WEEK_START, WEEK_START.plus(Duration.ofHours(1)),
                        SchedulingAuctionStatus.Scheduled, true)));

        service.delete(AUCTION_ID);

        verify(auctionRepository).delete(auction);
        verify(schedulingAuctionRepository, never()).deleteByAuctionId(anyLong());
    }

    @Test
    @DisplayName("delete — Started round → AuctionAlreadyStartedException (409)")
    void delete_startedRound_blocks() {
        Auction auction = auction(AUCTION_ID, AuctionStatus.Scheduled);
        when(auctionRepository.findByIdForUpdate(AUCTION_ID)).thenReturn(Optional.of(auction));
        when(schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(AUCTION_ID))
                .thenReturn(List.of(round(11L, 1,
                        WEEK_START, WEEK_START.plus(Duration.ofHours(1)),
                        SchedulingAuctionStatus.Started, true)));

        assertThatThrownBy(() -> service.delete(AUCTION_ID))
                .isInstanceOf(AuctionAlreadyStartedException.class);

        verify(auctionRepository, never()).delete(any(Auction.class));
    }

    @Test
    @DisplayName("getAuctionDetail — missing auction throws EntityNotFoundException (404)")
    void getAuctionDetail_missing_throws() {
        when(auctionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAuctionDetail(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ---------------- helpers ----------------

    private static ScheduleAuctionRequest validRequest() {
        Instant r1s = WEEK_START.plus(Duration.ofHours(16));
        Instant r1e = WEEK_START.plus(Duration.ofHours(103));
        Instant r2s = WEEK_START.plus(Duration.ofHours(109));
        Instant r2e = WEEK_START.plus(Duration.ofHours(128));
        Instant r3s = WEEK_START.plus(Duration.ofHours(131));
        Instant r3e = WEEK_START.plus(Duration.ofHours(156));
        return new ScheduleAuctionRequest(r1s, r1e, r2s, r2e, true, r3s, r3e, true);
    }

    private static Auction auction(Long id, AuctionStatus status) {
        Auction a = new Auction();
        setField(a, "id", id);
        setField(a, "weekId", WEEK_ID);
        a.setAuctionStatus(status);
        a.setAuctionTitle("Auction " + WEEK_DISPLAY);
        return a;
    }

    private static Week week(Long id, String display, Instant start) {
        Week w = new Week();
        setField(w, "id", id);
        setField(w, "weekDisplay", display);
        setField(w, "weekStartDateTime", start);
        return w;
    }

    private static SchedulingAuction round(Long id,
                                           int number,
                                           Instant start,
                                           Instant end,
                                           SchedulingAuctionStatus status,
                                           boolean hasRound) {
        SchedulingAuction sa = new SchedulingAuction();
        setField(sa, "id", id);
        sa.setAuctionId(AUCTION_ID);
        sa.setRound(number);
        sa.setStartDatetime(start);
        sa.setEndDatetime(end);
        sa.setRoundStatus(status);
        sa.setHasRound(hasRound);
        return sa;
    }

    private static AuctionsFeatureConfig config(int r2, int r3) {
        AuctionsFeatureConfig c = new AuctionsFeatureConfig();
        setField(c, "auctionRound2MinutesOffset", r2);
        setField(c, "auctionRound3MinutesOffset", r3);
        return c;
    }

    private static void setField(Object target, String name, Object value) {
        try {
            Field f = findField(target.getClass(), name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
}
