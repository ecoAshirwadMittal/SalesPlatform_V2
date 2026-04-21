package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.dto.AuctionDetailResponse;
import com.ecoatm.salesplatform.dto.ScheduleAuctionRequest;
import com.ecoatm.salesplatform.dto.ScheduleDefaultsResponse;
import com.ecoatm.salesplatform.exception.AuctionAlreadyStartedException;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * End-to-end flow integration test for the Phase C auction scheduling
 * service against a real PostgreSQL database (either a Testcontainer or
 * the local dev DB — see {@link PostgresIntegrationTest}).
 *
 * <p>Exercises the full happy path and the {@code Started}-round guard:
 *
 * <ol>
 *   <li>Seed an {@code Unscheduled} auction referencing an existing
 *       {@code mdm.week} row (V65 seed).</li>
 *   <li>Call {@link AuctionScheduleService#loadScheduleDefaults(Long)} —
 *       verifies the 16h/360min/180min math lines up.</li>
 *   <li>Call {@link AuctionScheduleService#saveSchedule} — verifies three
 *       rows are written, parent flips to {@code Scheduled}, and a
 *       second {@code saveSchedule} on the same auction performs a
 *       delete-and-recreate without violating any unique constraint.</li>
 *   <li>Flip one round to {@code Started} via direct repo save and
 *       verify {@link AuctionScheduleService#saveSchedule} fails with
 *       {@link AuctionAlreadyStartedException}.</li>
 * </ol>
 *
 * <p>The test class is {@code @Transactional} so everything rolls back
 * after each test method — no cleanup needed.
 */
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuctionScheduleFlowIT extends PostgresIntegrationTest {

    @Autowired private AuctionScheduleService scheduleService;
    @Autowired private AuctionRepository auctionRepository;
    @Autowired private SchedulingAuctionRepository schedulingAuctionRepository;
    @Autowired private WeekRepository weekRepository;

    @PersistenceContext
    private EntityManager em;

    private Long weekId;
    private Instant weekStart;

    @BeforeEach
    void setUp() {
        // Grab any seeded week — the V65 seed populates weeks across 2024–2030.
        // Using the first week we find keeps the test environment-agnostic.
        var weeks = weekRepository.findAll();
        assertThat(weeks).as("Expected V65 to seed mdm.week rows").isNotEmpty();
        var week = weeks.get(0);
        weekId = week.getId();
        weekStart = week.getWeekStartDateTime();
    }

    @Test
    @DisplayName("IT — full scheduling flow: load defaults, save, reschedule")
    void fullFlow() {
        Auction auction = new Auction();
        auction.setWeekId(weekId);
        auction.setAuctionTitle("Auction IT " + System.nanoTime());
        auction.setAuctionStatus(AuctionStatus.Unscheduled);
        Instant now = Instant.now();
        auction.setCreatedDate(now);
        auction.setChangedDate(now);
        auction.setCreatedBy("it");
        auction.setUpdatedBy("it");
        auction = auctionRepository.saveAndFlush(auction);

        // 1) Load defaults — verify offset math against the real week row.
        ScheduleDefaultsResponse defaults = scheduleService.loadScheduleDefaults(auction.getId());
        assertThat(defaults.round1Start()).isEqualTo(weekStart.plus(Duration.ofHours(16)));
        assertThat(defaults.round1End()).isEqualTo(weekStart.plus(Duration.ofHours(103)));
        assertThat(defaults.round2Active()).isTrue();
        assertThat(defaults.round3Active()).isTrue();

        // 2) Save initial schedule — three rounds written, parent flips.
        ScheduleAuctionRequest req = new ScheduleAuctionRequest(
                defaults.round1Start(), defaults.round1End(),
                defaults.round2Start(), defaults.round2End(), true,
                defaults.round3Start(), defaults.round3End(), true);
        AuctionDetailResponse saved = scheduleService.saveSchedule(auction.getId(), req, "it");

        assertThat(saved.auctionStatus()).isEqualTo(AuctionStatus.Scheduled.name());
        assertThat(saved.rounds()).hasSize(3);
        assertThat(saved.rounds().get(2).name()).isEqualTo("Upsell Round");

        List<SchedulingAuction> rows =
                schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(auction.getId());
        assertThat(rows).hasSize(3);
        assertThat(rows.get(0).getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Scheduled);

        // 3) Reschedule — delete-and-recreate must not leave orphan rows or
        // violate any unique constraint. Shift all rounds forward by 1h.
        ScheduleAuctionRequest reschedule = new ScheduleAuctionRequest(
                req.round1Start().plus(Duration.ofHours(1)), req.round1End().plus(Duration.ofHours(1)),
                req.round2Start().plus(Duration.ofHours(1)), req.round2End().plus(Duration.ofHours(1)), true,
                req.round3Start().plus(Duration.ofHours(1)), req.round3End().plus(Duration.ofHours(1)), true);
        scheduleService.saveSchedule(auction.getId(), reschedule, "it");

        List<SchedulingAuction> after =
                schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(auction.getId());
        assertThat(after).hasSize(3);
        assertThat(after.get(0).getStartDatetime())
                .isEqualTo(req.round1Start().plus(Duration.ofHours(1)));
    }

    @Test
    @DisplayName("IT — Started round blocks saveSchedule and unschedule")
    void startedRoundBlocksDestructiveActions() {
        Auction auction = new Auction();
        auction.setWeekId(weekId);
        auction.setAuctionTitle("Auction IT started " + System.nanoTime());
        auction.setAuctionStatus(AuctionStatus.Unscheduled);
        Instant now = Instant.now();
        auction.setCreatedDate(now);
        auction.setChangedDate(now);
        auction.setCreatedBy("it");
        auction.setUpdatedBy("it");
        auction = auctionRepository.saveAndFlush(auction);

        ScheduleDefaultsResponse defaults = scheduleService.loadScheduleDefaults(auction.getId());
        ScheduleAuctionRequest req = new ScheduleAuctionRequest(
                defaults.round1Start(), defaults.round1End(),
                defaults.round2Start(), defaults.round2End(), true,
                defaults.round3Start(), defaults.round3End(), true);
        scheduleService.saveSchedule(auction.getId(), req, "it");

        // Flip round 1 to Started directly.
        List<SchedulingAuction> rounds =
                schedulingAuctionRepository.findByAuctionIdOrderByRoundAsc(auction.getId());
        rounds.get(0).setRoundStatus(SchedulingAuctionStatus.Started);
        schedulingAuctionRepository.saveAndFlush(rounds.get(0));
        em.clear();

        Long auctionId = auction.getId();
        assertThatThrownBy(() -> scheduleService.saveSchedule(auctionId, req, "it"))
                .isInstanceOf(AuctionAlreadyStartedException.class);
        assertThatThrownBy(() -> scheduleService.unschedule(auctionId, "it"))
                .isInstanceOf(AuctionAlreadyStartedException.class);
        assertThatThrownBy(() -> scheduleService.delete(auctionId))
                .isInstanceOf(AuctionAlreadyStartedException.class);
    }
}
