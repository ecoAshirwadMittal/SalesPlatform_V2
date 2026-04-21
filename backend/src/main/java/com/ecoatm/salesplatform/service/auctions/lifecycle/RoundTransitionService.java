package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class RoundTransitionService {

    private static final String SYSTEM_ACTOR = "system:lifecycle-cron";

    private final SchedulingAuctionRepository schedulingRepo;
    private final AuctionRepository auctionRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    public RoundTransitionService(SchedulingAuctionRepository schedulingRepo,
                                  AuctionRepository auctionRepo,
                                  ApplicationEventPublisher eventPublisher,
                                  Clock clock) {
        this.schedulingRepo = schedulingRepo;
        this.auctionRepo = auctionRepo;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RoundClosedEvent closeRound(long roundId) {
        SchedulingAuction round = lockRound(roundId);
        Instant now = clock.instant();
        if (round.getRoundStatus() != SchedulingAuctionStatus.Started
                || round.getEndDatetime() == null
                || !round.getEndDatetime().isBefore(now)) {
            throw new RoundAlreadyTransitionedException(roundId);
        }
        applyTransition(round, SchedulingAuctionStatus.Closed, now);
        long weekId = resolveWeekId(round.getAuctionId());
        RoundClosedEvent event = new RoundClosedEvent(round.getId(), round.getRound(),
                round.getAuctionId(), weekId);
        eventPublisher.publishEvent(event);
        return event;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RoundStartedEvent startRound(long roundId) {
        SchedulingAuction round = lockRound(roundId);
        Instant now = clock.instant();
        if (round.getRoundStatus() != SchedulingAuctionStatus.Scheduled
                || round.getStartDatetime() == null
                || round.getStartDatetime().isAfter(now)) {
            throw new RoundAlreadyTransitionedException(roundId);
        }
        applyTransition(round, SchedulingAuctionStatus.Started, now);
        long weekId = resolveWeekId(round.getAuctionId());
        RoundStartedEvent event = new RoundStartedEvent(round.getId(), round.getRound(),
                round.getAuctionId(), weekId);
        eventPublisher.publishEvent(event);
        return event;
    }

    private SchedulingAuction lockRound(long roundId) {
        return schedulingRepo.findByIdForUpdate(roundId)
                .orElseThrow(() -> new IllegalStateException(
                        "Round disappeared between selector and lock: id=" + roundId));
    }

    private void applyTransition(SchedulingAuction round, SchedulingAuctionStatus newStatus, Instant now) {
        round.setRoundStatus(newStatus);
        round.setChangedDate(now);
        round.setUpdatedBy(SYSTEM_ACTOR);
        schedulingRepo.save(round);
    }

    private long resolveWeekId(Long auctionId) {
        Auction parent = auctionRepo.findById(auctionId)
                .orElseThrow(() -> new IllegalStateException(
                        "Parent auction missing: id=" + auctionId));
        Long weekId = parent.getWeekId();
        return weekId == null ? 0L : weekId;
    }
}
