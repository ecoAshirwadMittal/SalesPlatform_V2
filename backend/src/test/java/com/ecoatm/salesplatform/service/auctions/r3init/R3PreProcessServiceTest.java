package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.R3PreProcessCompletedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.R3BuyerQualificationRepository;
import com.ecoatm.salesplatform.repository.auctions.R3PreProcessSupportRepository;
import com.ecoatm.salesplatform.repository.auctions.R3SpecialBuyerRepository;
import com.ecoatm.salesplatform.repository.auctions.Round3BuyerDataReportRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R3PreProcessServiceTest {

    @Mock SchedulingAuctionRepository saRepo;
    @Mock R3PreProcessSupportRepository supportRepo;
    @Mock R3BuyerQualificationRepository qualRepo;
    @Mock R3SpecialBuyerRepository       specialRepo;
    @Mock QualifiedBuyerCodeRepository   qbcRepo;
    @Mock BidRoundRepository             bidRoundRepo;
    @Mock Round3BuyerDataReportRepository reportRepo;
    @Mock RecalcStatusUpdater            statusUpdater;
    @Mock ApplicationEventPublisher      events;

    @InjectMocks R3PreProcessService service;

    private SchedulingAuction r2Sa(long id, long auctionId) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(id); sa.setAuctionId(auctionId); sa.setRound(2); sa.setHasRound(true);
        return sa;
    }

    private SchedulingAuction r3Sa(long id, long auctionId, boolean hasRound) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(id); sa.setAuctionId(auctionId); sa.setRound(3); sa.setHasRound(hasRound);
        return sa;
    }

    @Test
    @DisplayName("happy path — all 5 phases run, status flip RUNNING→SUCCESS, event published")
    void happy_path() {
        long r2 = 6002L, r3 = 6003L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));
        when(statusUpdater.tryFlipToRunning(r3, "R3_PREPROCESS")).thenReturn(true);
        when(supportRepo.deleteUnsubmittedBids(r2)).thenReturn(2);
        when(qualRepo.qualifiedBuyerCodes(r3)).thenReturn(Set.of(60001L, 60002L));
        when(specialRepo.specialTreatmentBuyerCodes(r3)).thenReturn(Set.of(60005L));
        when(qbcRepo.bulkInsertForRound(eq(r3), any(Long[].class), any(Long[].class))).thenReturn(8);
        // Phase 4.5: read-back the included QBCs (3 — union of qualified + special).
        when(saRepo.findWeekIdById(r3)).thenReturn(601L);
        when(qbcRepo.findBySchedulingAuctionId(r3)).thenReturn(includedQbcs(
            r3, 60001L, 60002L, 60005L));
        when(bidRoundRepo.findBySchedulingAuctionIdAndBuyerCodeId(eq(r3), anyLong()))
            .thenReturn(Optional.empty());
        when(reportRepo.bulkInsertForSchedulingAuction(r3)).thenReturn(2);

        R3PreProcessResult result = service.run(r2, r3);

        assertThat(result.qualifiedCount()).isEqualTo(3);  // 2 qual + 1 special
        assertThat(result.specialTreatmentCount()).isEqualTo(1);
        assertThat(result.notQualifiedCount()).isEqualTo(5);  // 8 total - 3 qualified
        assertThat(result.reportRowCount()).isEqualTo(2);
        assertThat(result.deletedBidsCount()).isEqualTo(2);
        assertThat(result.skipped()).isFalse();

        // Phase 4.5: bid_rounds saved for each of the 3 included QBCs.
        ArgumentCaptor<List<BidRound>> savedRoundsCaptor = ArgumentCaptor.forClass(List.class);
        verify(bidRoundRepo).saveAll(savedRoundsCaptor.capture());
        assertThat(savedRoundsCaptor.getValue()).hasSize(3);
        assertThat(savedRoundsCaptor.getValue())
            .allMatch(br -> br.getSchedulingAuctionId() != null
                && br.getSchedulingAuctionId() == r3
                && br.getWeekId() != null
                && br.getWeekId() == 601L
                && Boolean.FALSE.equals(br.getSubmitted()));

        verify(statusUpdater).markSuccess(r3, "R3_PREPROCESS");
        verify(events).publishEvent(any(R3PreProcessCompletedEvent.class));
    }

    /**
     * Mirrors the helper in {@code R2BuyerAssignmentServiceTest}: builds the
     * stand-in {@link QualifiedBuyerCode} rows that
     * {@code qbcRepo.findBySchedulingAuctionId} returns in Phase 4.5 — the
     * read-back of just-inserted included QBCs that the bid_round seed loop
     * iterates.
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

    @Test
    @DisplayName("has_round=false on R3 SA → SKIPPED, no QBC writes, no event")
    void has_round_false_skips() {
        long r2 = 6022L, r3 = 6023L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 602L)));
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 602L, false)));

        R3PreProcessResult result = service.run(r2, r3);

        assertThat(result.skipped()).isTrue();
        assertThat(result.qualifiedCount()).isZero();
        verify(statusUpdater).markSkipped(r3, "R3_PREPROCESS");
        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), eq("R3_PREPROCESS"));
        verify(qbcRepo, never()).bulkInsertForRound(anyLong(), any(), any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    @DisplayName("R2 SA round != 2 → IllegalArgumentException pre-flip")
    void r2_round_mismatch_throws() {
        long r2 = 6001L, r3 = 6003L;
        SchedulingAuction wrongRound = new SchedulingAuction();
        wrongRound.setId(r2); wrongRound.setAuctionId(600L); wrongRound.setRound(1);
        when(saRepo.findById(r2)).thenReturn(Optional.of(wrongRound));
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));

        assertThatThrownBy(() -> service.run(r2, r3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("round-2");
        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), any());
    }

    @Test
    @DisplayName("R3 SA round != 3 → IllegalArgumentException pre-flip")
    void r3_round_mismatch_throws() {
        long r2 = 6002L, r3 = 6002L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        // r3 same id as r2 → round=2
        assertThatThrownBy(() -> service.run(r2, r3))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("R2/R3 SAs from different auctions → IllegalArgumentException")
    void sibling_mismatch_throws() {
        long r2 = 6002L, r3 = 6023L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));   // auction 600
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 602L, true)));  // auction 602

        assertThatThrownBy(() -> service.run(r2, r3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("siblings");
    }

    @Test
    @DisplayName("status already RUNNING → RecalcAlreadyRunningException")
    void already_running_throws() {
        long r2 = 6002L, r3 = 6003L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));
        when(statusUpdater.tryFlipToRunning(r3, "R3_PREPROCESS")).thenReturn(false);

        assertThatThrownBy(() -> service.run(r2, r3))
            .isInstanceOf(RecalcAlreadyRunningException.class);
    }

    @Test
    @DisplayName("repo throw → status flip RUNNING→FAILED with truncated error, exception propagates")
    void repo_throw_marks_failed() {
        long r2 = 6002L, r3 = 6003L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));
        when(statusUpdater.tryFlipToRunning(r3, "R3_PREPROCESS")).thenReturn(true);
        when(supportRepo.deleteUnsubmittedBids(r2))
            .thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> service.run(r2, r3))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("boom");

        verify(statusUpdater).markFailed(eq(r3), eq("R3_PREPROCESS"),
            org.mockito.ArgumentMatchers.contains("boom"));
        verify(events, never()).publishEvent(any());
    }

    @Test
    @DisplayName("recalculate(r3SaId) resolves R2 SA via findByAuctionIdAndRound")
    void recalculate_resolves_r2_sibling() {
        long r3 = 6003L, r2 = 6002L;
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));
        when(saRepo.findByAuctionIdAndRound(600L, 2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(statusUpdater.tryFlipToRunning(r3, "R3_PREPROCESS")).thenReturn(true);
        when(qbcRepo.bulkInsertForRound(eq(r3), any(), any())).thenReturn(0);

        service.recalculate(r3);

        verify(supportRepo).deleteUnsubmittedBids(r2);
    }

    @Test
    @DisplayName("recalculate throws when no R2 sibling exists")
    void recalculate_no_r2_sibling_throws() {
        long r3 = 6003L;
        when(saRepo.findById(r3)).thenReturn(Optional.of(r3Sa(r3, 600L, true)));
        when(saRepo.findByAuctionIdAndRound(600L, 2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recalculate(r3))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("R2 SA");
    }

    @Test
    @DisplayName("unknown R3 SA id → EntityNotFoundException")
    void unknown_id_throws() {
        long r2 = 6002L;
        when(saRepo.findById(r2)).thenReturn(Optional.of(r2Sa(r2, 600L)));
        when(saRepo.findById(99999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.run(r2, 99999L))
            .isInstanceOf(EntityNotFoundException.class);
    }
}
