package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoundTransitionServiceTest {

    @Mock private SchedulingAuctionRepository schedulingRepo;
    @Mock private AuctionRepository auctionRepo;
    @Mock private ApplicationEventPublisher eventPublisher;

    private RoundTransitionService service;

    private final Instant now = Instant.parse("2026-04-20T12:00:00Z");

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);
        service = new RoundTransitionService(schedulingRepo, auctionRepo, eventPublisher, clock);
    }

    @Test
    void closeRound_happyPath_flipsStatusAndPublishesEvent() {
        SchedulingAuction round = roundFixture(11L, 1, SchedulingAuctionStatus.Started,
                now.minusSeconds(60), 100L);
        Auction parent = auctionFixture(100L, 999L);
        when(schedulingRepo.findByIdForUpdate(11L)).thenReturn(Optional.of(round));
        when(auctionRepo.findById(100L)).thenReturn(Optional.of(parent));

        RoundClosedEvent event = service.closeRound(11L);

        assertThat(round.getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Closed);
        assertThat(round.getChangedDate()).isEqualTo(now);
        assertThat(round.getUpdatedBy()).isEqualTo("system:lifecycle-cron");
        verify(schedulingRepo).save(round);
        assertThat(event).isEqualTo(new RoundClosedEvent(11L, 1, 100L, 999L));
        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void closeRound_whenNotStarted_throwsRoundAlreadyTransitioned() {
        SchedulingAuction round = roundFixture(11L, 1, SchedulingAuctionStatus.Closed,
                now.minusSeconds(60), 100L);
        when(schedulingRepo.findByIdForUpdate(11L)).thenReturn(Optional.of(round));

        assertThatThrownBy(() -> service.closeRound(11L))
                .isInstanceOf(RoundAlreadyTransitionedException.class)
                .hasMessageContaining("11");
        verify(schedulingRepo, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void closeRound_whenEndStillFuture_throwsRoundAlreadyTransitioned() {
        SchedulingAuction round = roundFixture(11L, 1, SchedulingAuctionStatus.Started,
                now.plusSeconds(60), 100L);
        when(schedulingRepo.findByIdForUpdate(11L)).thenReturn(Optional.of(round));

        assertThatThrownBy(() -> service.closeRound(11L))
                .isInstanceOf(RoundAlreadyTransitionedException.class);
    }

    @Test
    void startRound_happyPath_flipsStatusAndPublishesEvent() {
        SchedulingAuction round = roundFixture(22L, 2, SchedulingAuctionStatus.Scheduled,
                now.plusSeconds(3600), 200L);
        round.setStartDatetime(now.minusSeconds(1));
        Auction parent = auctionFixture(200L, 555L);
        when(schedulingRepo.findByIdForUpdate(22L)).thenReturn(Optional.of(round));
        when(auctionRepo.findById(200L)).thenReturn(Optional.of(parent));

        RoundStartedEvent event = service.startRound(22L);

        assertThat(round.getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Started);
        verify(schedulingRepo).save(round);
        assertThat(event).isEqualTo(new RoundStartedEvent(22L, 2, 200L, 555L));
        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void startRound_whenStartFuture_throws() {
        SchedulingAuction round = roundFixture(22L, 2, SchedulingAuctionStatus.Scheduled,
                now.plusSeconds(3600), 200L);
        round.setStartDatetime(now.plusSeconds(60));
        when(schedulingRepo.findByIdForUpdate(22L)).thenReturn(Optional.of(round));

        assertThatThrownBy(() -> service.startRound(22L))
                .isInstanceOf(RoundAlreadyTransitionedException.class);
    }

    @Test
    void closeRound_whenRowMissing_throwsIllegalState() {
        when(schedulingRepo.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.closeRound(99L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("99");
    }

    private SchedulingAuction roundFixture(long id, int round, SchedulingAuctionStatus status,
                                           Instant endDt, long auctionId) {
        SchedulingAuction r = new SchedulingAuction();
        try {
            java.lang.reflect.Field f = SchedulingAuction.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(r, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        r.setRound(round);
        r.setRoundStatus(status);
        r.setEndDatetime(endDt);
        r.setStartDatetime(now.minusSeconds(7200));
        r.setAuctionId(auctionId);
        return r;
    }

    private Auction auctionFixture(long id, long weekId) {
        Auction a = new Auction();
        try {
            java.lang.reflect.Field f = Auction.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(a, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        a.setWeekId(weekId);
        a.setAuctionStatus(AuctionStatus.Started);
        return a;
    }
}
