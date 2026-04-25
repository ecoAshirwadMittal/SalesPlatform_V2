package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.RoundTransitionResponse;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.lifecycle.RoundAlreadyTransitionedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminRoundTransitionServiceTest {

    @Mock
    private SchedulingAuctionRepository schedulingRepo;

    private AdminRoundTransitionService service;

    private final Instant now = Instant.parse("2026-04-25T10:00:00Z");

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);
        service = new AdminRoundTransitionService(schedulingRepo, clock);
    }

    @Test
    void startRound_scheduledRound_flipsToStarted() {
        SchedulingAuction round = fixture(5L, 1, SchedulingAuctionStatus.Scheduled);
        when(schedulingRepo.findByIdForUpdate(5L)).thenReturn(Optional.of(round));

        RoundTransitionResponse resp = service.startRound(5L, "admin@ecoatm.com");

        assertThat(round.getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Started);
        assertThat(round.getUpdatedBy()).isEqualTo("admin:admin@ecoatm.com");
        assertThat(round.getChangedDate()).isEqualTo(now);
        verify(schedulingRepo).save(round);

        assertThat(resp.id()).isEqualTo(5L);
        assertThat(resp.roundStatus()).isEqualTo("Started");
    }

    @Test
    void startRound_notScheduled_throws() {
        SchedulingAuction round = fixture(5L, 1, SchedulingAuctionStatus.Started);
        when(schedulingRepo.findByIdForUpdate(5L)).thenReturn(Optional.of(round));

        assertThatThrownBy(() -> service.startRound(5L, "actor"))
                .isInstanceOf(RoundAlreadyTransitionedException.class);
        verify(schedulingRepo, never()).save(any());
    }

    @Test
    void startRound_doesNotEnforceTimestamp() {
        // Admin can start even before the scheduled time — this is intentional
        SchedulingAuction round = fixture(6L, 2, SchedulingAuctionStatus.Scheduled);
        round.setStartDatetime(now.plusSeconds(3600)); // future start
        when(schedulingRepo.findByIdForUpdate(6L)).thenReturn(Optional.of(round));

        // Should NOT throw — admin bypass of time constraint
        RoundTransitionResponse resp = service.startRound(6L, "ops@ecoatm.com");
        assertThat(resp.roundStatus()).isEqualTo("Started");
    }

    @Test
    void closeRound_startedRound_flipsToClosed() {
        SchedulingAuction round = fixture(7L, 1, SchedulingAuctionStatus.Started);
        when(schedulingRepo.findByIdForUpdate(7L)).thenReturn(Optional.of(round));

        RoundTransitionResponse resp = service.closeRound(7L, "admin@ecoatm.com");

        assertThat(round.getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Closed);
        assertThat(resp.roundStatus()).isEqualTo("Closed");
        verify(schedulingRepo).save(round);
    }

    @Test
    void closeRound_notStarted_throws() {
        SchedulingAuction round = fixture(7L, 1, SchedulingAuctionStatus.Scheduled);
        when(schedulingRepo.findByIdForUpdate(7L)).thenReturn(Optional.of(round));

        assertThatThrownBy(() -> service.closeRound(7L, "actor"))
                .isInstanceOf(RoundAlreadyTransitionedException.class);
    }

    @Test
    void startRound_missingRound_throwsIllegalArgument() {
        when(schedulingRepo.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.startRound(99L, "actor"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    void closeRound_doesNotEnforceEndTimestamp() {
        // Admin can close before the end time — bypass is intentional
        SchedulingAuction round = fixture(8L, 1, SchedulingAuctionStatus.Started);
        round.setEndDatetime(now.plusSeconds(3600)); // not yet ended
        when(schedulingRepo.findByIdForUpdate(8L)).thenReturn(Optional.of(round));

        RoundTransitionResponse resp = service.closeRound(8L, "admin@ecoatm.com");
        assertThat(resp.roundStatus()).isEqualTo("Closed");
    }

    private SchedulingAuction fixture(long id, int round, SchedulingAuctionStatus status) {
        SchedulingAuction sa = new SchedulingAuction();
        try {
            java.lang.reflect.Field f = SchedulingAuction.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(sa, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        sa.setRound(round);
        sa.setRoundStatus(status);
        sa.setCreatedDate(now);
        sa.setChangedDate(now);
        return sa;
    }
}
