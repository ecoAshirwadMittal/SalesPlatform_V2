package com.ecoatm.salesplatform.service.auctions.r2init;

import com.ecoatm.salesplatform.event.R2BuyerAssignmentCompletedEvent;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.AuctionsFeatureConfigRepository;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.R2BuyerQualificationRepository;
import com.ecoatm.salesplatform.repository.auctions.R2SpecialBuyerRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R2BuyerAssignmentServiceTest {

    @Mock SchedulingAuctionRepository saRepo;
    @Mock AuctionsFeatureConfigRepository cfgRepo;
    @Mock R2BuyerQualificationRepository qualRepo;
    @Mock R2SpecialBuyerRepository specialRepo;
    @Mock QualifiedBuyerCodeRepository qbcRepo;
    @Mock BidRoundRepository bidRoundRepo;
    @Mock BidDataForAllAEService specialBidDataService;
    @Mock RecalcStatusUpdater statusUpdater;
    @Mock ApplicationEventPublisher events;

    @InjectMocks R2BuyerAssignmentService service;

    private SchedulingAuction sa;
    private AuctionsFeatureConfig cfg;

    @BeforeEach
    void setUp() {
        sa = new SchedulingAuction();
        sa.setId(502L);
        sa.setRound(2);
        sa.setAuctionId(800L);

        cfg = new AuctionsFeatureConfig();
        cfg.setId(1L);
        cfg.setCalculateRound2BuyerParticipation(true);
    }

    @Test
    @DisplayName("happy path — flips RUNNING, computes sets, writes QBCs, seeds special bid_data, marks SUCCESS, publishes event")
    void happy_path() {
        when(saRepo.findById(502L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(502L)).thenReturn(501L);
        when(cfgRepo.findById(1L)).thenReturn(Optional.of(cfg));
        when(statusUpdater.tryFlipToRunning(502L, "R2_INIT")).thenReturn(true);

        Set<Long> qualified = Set.of(1001L, 1002L, 1003L);
        Set<Long> special   = Set.of(1003L, 1004L);   // 1003 in both; union = 4
        when(qualRepo.qualifiedBuyerCodes(502L)).thenReturn(qualified);
        when(specialRepo.specialTreatmentBuyerCodes(502L)).thenReturn(special);
        when(qbcRepo.deleteBySchedulingAuctionId(502L)).thenReturn(0);
        when(qbcRepo.bulkInsertForRound(eq(502L), any(Long[].class), any(Long[].class)))
            .thenReturn(8);   // 4 qualified, 4 not_qualified
        // Phase 4.5: read-back the just-inserted included QBCs (the 4-row union).
        // bid_round seeding loop iterates these and saves a BidRound per row.
        when(qbcRepo.findBySchedulingAuctionId(502L)).thenReturn(includedQbcs(
            502L, 1001L, 1002L, 1003L, 1004L));
        when(bidRoundRepo.findBySchedulingAuctionIdAndBuyerCodeId(eq(502L), anyLong()))
            .thenReturn(Optional.empty());
        when(specialBidDataService.generateForSpecialBuyers(eq(502L), eq(special))).thenReturn(20);

        R2BuyerAssignmentResult result = service.run(502L);

        verify(statusUpdater).tryFlipToRunning(502L, "R2_INIT");
        verify(qbcRepo).deleteBySchedulingAuctionId(502L);
        verify(qbcRepo).bulkInsertForRound(eq(502L), any(Long[].class), any(Long[].class));
        // Phase 4.5: bid_rounds saved for each of the 4 included QBCs.
        ArgumentCaptor<List<BidRound>> savedRoundsCaptor = ArgumentCaptor.forClass(List.class);
        verify(bidRoundRepo).saveAll(savedRoundsCaptor.capture());
        assertThat(savedRoundsCaptor.getValue()).hasSize(4);
        assertThat(savedRoundsCaptor.getValue())
            .allMatch(br -> br.getSchedulingAuctionId() != null
                && br.getSchedulingAuctionId() == 502L
                && br.getWeekId() != null
                && br.getWeekId() == 501L
                && Boolean.FALSE.equals(br.getSubmitted()));
        verify(specialBidDataService).generateForSpecialBuyers(502L, special);
        verify(statusUpdater).markSuccess(502L, "R2_INIT");

        assertThat(result.qualifiedCount()).isEqualTo(4);   // union(1001,1002,1003,1004)
        assertThat(result.specialTreatmentCount()).isEqualTo(2);
        assertThat(result.notQualifiedCount()).isEqualTo(4); // 8 - 4
        assertThat(result.specialBidDataCount()).isEqualTo(20);
        assertThat(result.skipped()).isFalse();

        ArgumentCaptor<R2BuyerAssignmentCompletedEvent> captor =
            ArgumentCaptor.forClass(R2BuyerAssignmentCompletedEvent.class);
        verify(events).publishEvent(captor.capture());
        R2BuyerAssignmentCompletedEvent e = captor.getValue();
        assertThat(e.schedulingAuctionId()).isEqualTo(502L);
        assertThat(e.auctionId()).isEqualTo(800L);
        assertThat(e.weekId()).isEqualTo(501L);
        assertThat(e.qualifiedCount()).isEqualTo(4);
        assertThat(e.specialTreatmentCount()).isEqualTo(2);

        verify(statusUpdater, never()).markFailed(anyLong(), anyString(), anyString());
        verify(statusUpdater, never()).markSkipped(anyLong(), anyString());
    }

    @Test
    @DisplayName("calculate_round2_buyer_participation = FALSE → SKIPPED, no QBC writes, no event")
    void config_gate_false_short_circuits_to_skipped() {
        cfg.setCalculateRound2BuyerParticipation(false);
        when(saRepo.findById(502L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(502L)).thenReturn(501L);
        when(cfgRepo.findById(1L)).thenReturn(Optional.of(cfg));

        R2BuyerAssignmentResult result = service.run(502L);

        assertThat(result.skipped()).isTrue();
        assertThat(result.qualifiedCount()).isEqualTo(0);
        assertThat(result.specialTreatmentCount()).isEqualTo(0);
        assertThat(result.notQualifiedCount()).isEqualTo(0);
        assertThat(result.specialBidDataCount()).isEqualTo(0);

        verify(statusUpdater).markSkipped(502L, "R2_INIT");
        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), anyString());
        verifyNoInteractions(qualRepo, specialRepo, qbcRepo, bidRoundRepo, specialBidDataService, events);
        verify(statusUpdater, never()).markSuccess(anyLong(), anyString());
        verify(statusUpdater, never()).markFailed(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("round != 2 throws IllegalArgumentException BEFORE status flip")
    void rejects_wrong_round() {
        sa.setRound(1);
        when(saRepo.findById(502L)).thenReturn(Optional.of(sa));

        assertThatThrownBy(() -> service.run(502L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("R2 buyer assignment only valid for round 2");

        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), anyString());
        verify(statusUpdater, never()).markSkipped(anyLong(), anyString());
        verifyNoInteractions(qualRepo, specialRepo, qbcRepo, bidRoundRepo, specialBidDataService, events);
    }

    @Test
    @DisplayName("scheduling auction not found throws EntityNotFoundException BEFORE status flip")
    void unknown_id_throws_entity_not_found() {
        when(saRepo.findById(99999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.run(99999L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("scheduling_auction not found");

        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), anyString());
        verify(statusUpdater, never()).markSkipped(anyLong(), anyString());
        verifyNoInteractions(qualRepo, specialRepo, qbcRepo, bidRoundRepo, specialBidDataService, events);
    }

    @Test
    @DisplayName("status already RUNNING → RecalcAlreadyRunningException(R2_INIT)")
    void already_running_throws() {
        when(saRepo.findById(502L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(502L)).thenReturn(501L);
        when(cfgRepo.findById(1L)).thenReturn(Optional.of(cfg));
        when(statusUpdater.tryFlipToRunning(502L, "R2_INIT")).thenReturn(false);

        assertThatThrownBy(() -> service.run(502L))
            .isInstanceOf(RecalcAlreadyRunningException.class);

        verifyNoInteractions(qualRepo, specialRepo, qbcRepo, bidRoundRepo, specialBidDataService);
        verify(events, never()).publishEvent(any());
        verify(statusUpdater, never()).markSuccess(anyLong(), anyString());
        verify(statusUpdater, never()).markFailed(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("repo throw during phase 3-5 → markFailed with 'RuntimeException: ...' + propagate")
    void repo_throw_marks_failed() {
        when(saRepo.findById(502L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(502L)).thenReturn(501L);
        when(cfgRepo.findById(1L)).thenReturn(Optional.of(cfg));
        when(statusUpdater.tryFlipToRunning(502L, "R2_INIT")).thenReturn(true);
        RuntimeException boom = new RuntimeException("DB exploded");
        when(qualRepo.qualifiedBuyerCodes(502L)).thenThrow(boom);

        assertThatThrownBy(() -> service.run(502L)).isSameAs(boom);

        ArgumentCaptor<String> errCaptor = ArgumentCaptor.forClass(String.class);
        verify(statusUpdater).markFailed(eq(502L), eq("R2_INIT"), errCaptor.capture());
        assertThat(errCaptor.getValue())
            .startsWith("RuntimeException: ")
            .contains("DB exploded");

        verify(statusUpdater, never()).markSuccess(anyLong(), anyString());
        verify(events, never()).publishEvent(any());
        // Phase 4.5 never reached because qualRepo (Phase 3) threw.
        verifyNoInteractions(bidRoundRepo);
    }

    @Test
    @DisplayName("week id null → IllegalStateException BEFORE status flip")
    void missing_week_throws_illegal_state() {
        when(saRepo.findById(502L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(502L)).thenReturn(null);

        assertThatThrownBy(() -> service.run(502L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot resolve week_id");

        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), anyString());
        verifyNoInteractions(qualRepo, specialRepo, qbcRepo, bidRoundRepo, specialBidDataService, events);
    }

    /**
     * Builds a stand-in for the QBC rows that {@code qbcRepo.bulkInsertForRound}
     * just persisted, returned by the read-back call in Phase 4.5. All rows are
     * marked {@code included = true} (the only ones the bid_round seed loop
     * picks up). The bulk-INSERT also writes Not_Qualified rows with
     * {@code included = false}; we omit them here to keep the assertion on
     * {@code saveAll} size focused on the included subset.
     */
    private static List<QualifiedBuyerCode> includedQbcs(long saId, long... buyerCodeIds) {
        List<QualifiedBuyerCode> out = new ArrayList<>(buyerCodeIds.length);
        for (long id : buyerCodeIds) {
            QualifiedBuyerCode q = new QualifiedBuyerCode();
            q.setSchedulingAuctionId(saId);
            q.setBuyerCodeId(id);
            q.setIncluded(true);
            out.add(q);
        }
        return out;
    }
}
