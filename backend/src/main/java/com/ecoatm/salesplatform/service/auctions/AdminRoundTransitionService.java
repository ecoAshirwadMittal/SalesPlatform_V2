package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.RoundTransitionResponse;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.lifecycle.RoundAlreadyTransitionedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

/**
 * Admin-triggered round transitions. Unlike the cron-driven
 * {@link com.ecoatm.salesplatform.service.auctions.lifecycle.RoundTransitionService},
 * this service does NOT enforce wall-clock constraints — an admin can
 * force-start or force-close any round that is in the correct predecessor
 * state (Scheduled → Started, Started → Closed). Time-based guards are
 * intentionally omitted to support manual recovery workflows.
 */
@Service
public class AdminRoundTransitionService {

    private static final String ADMIN_ACTOR_PREFIX = "admin:";

    private final SchedulingAuctionRepository schedulingRepo;
    private final Clock clock;

    public AdminRoundTransitionService(SchedulingAuctionRepository schedulingRepo, Clock clock) {
        this.schedulingRepo = schedulingRepo;
        this.clock = clock;
    }

    @Transactional
    public RoundTransitionResponse startRound(Long roundId, String actor) {
        SchedulingAuction round = schedulingRepo.findByIdForUpdate(roundId)
                .orElseThrow(() -> new IllegalArgumentException("Scheduling auction not found: " + roundId));

        if (round.getRoundStatus() != SchedulingAuctionStatus.Scheduled) {
            throw new RoundAlreadyTransitionedException(roundId);
        }

        applyTransition(round, SchedulingAuctionStatus.Started, actor);
        return toResponse(round);
    }

    @Transactional
    public RoundTransitionResponse closeRound(Long roundId, String actor) {
        SchedulingAuction round = schedulingRepo.findByIdForUpdate(roundId)
                .orElseThrow(() -> new IllegalArgumentException("Scheduling auction not found: " + roundId));

        if (round.getRoundStatus() != SchedulingAuctionStatus.Started) {
            throw new RoundAlreadyTransitionedException(roundId);
        }

        applyTransition(round, SchedulingAuctionStatus.Closed, actor);
        return toResponse(round);
    }

    private void applyTransition(SchedulingAuction round, SchedulingAuctionStatus newStatus, String actor) {
        Instant now = clock.instant();
        round.setRoundStatus(newStatus);
        round.setChangedDate(now);
        round.setUpdatedBy(ADMIN_ACTOR_PREFIX + actor);
        schedulingRepo.save(round);
    }

    private static RoundTransitionResponse toResponse(SchedulingAuction round) {
        return new RoundTransitionResponse(
                round.getId(),
                round.getRound(),
                round.getRoundStatus().name(),
                round.getChangedDate()
        );
    }
}
