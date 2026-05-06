package com.ecoatm.salesplatform.service.auctions.recalc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecalcOrchestratorTest {

    @Mock BidRankingService rankingService;
    @Mock TargetPriceRecalcService targetPriceService;

    @InjectMocks RecalcOrchestrator orchestrator;

    @Test
    void runs_both_in_happy_path() {
        orchestrator.runForClosedRound(9001L);

        verify(rankingService).run(9001L);
        verify(targetPriceService).run(9001L);
    }

    @Test
    void target_price_still_runs_when_ranking_throws() {
        doThrow(new RuntimeException("ranking exploded"))
            .when(rankingService).run(9001L);

        orchestrator.runForClosedRound(9001L);

        verify(targetPriceService).run(9001L);  // still called
    }

    @Test
    void ranking_runs_first_even_if_target_price_throws() {
        doThrow(new RuntimeException("tp exploded"))
            .when(targetPriceService).run(9001L);

        orchestrator.runForClosedRound(9001L);

        verify(rankingService).run(9001L);
    }

    @Test
    void no_throw_propagates_when_both_fail() {
        doThrow(new RuntimeException("a")).when(rankingService).run(9001L);
        doThrow(new RuntimeException("b")).when(targetPriceService).run(9001L);

        // Must not throw — orchestrator swallows both.
        orchestrator.runForClosedRound(9001L);
    }
}
