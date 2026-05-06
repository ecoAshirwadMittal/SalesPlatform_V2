package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.admin.RecalcResponse;
import com.ecoatm.salesplatform.service.auctions.recalc.BidRankingService;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcResult;
import com.ecoatm.salesplatform.service.auctions.recalc.TargetPriceRecalcService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/v1/admin/auctions/scheduling-auctions")
public class RecalcAdminController {

    private final BidRankingService rankingService;
    private final TargetPriceRecalcService targetPriceService;

    public RecalcAdminController(BidRankingService rankingService,
                                 TargetPriceRecalcService targetPriceService) {
        this.rankingService = rankingService;
        this.targetPriceService = targetPriceService;
    }

    @PostMapping("/{id}/re-rank")
    @PreAuthorize("hasAnyRole('Administrator', 'SalesOps')")
    public RecalcResponse reRank(@PathVariable long id) {
        long start = System.currentTimeMillis();
        OffsetDateTime startedAt = OffsetDateTime.now(ZoneOffset.UTC);
        RecalcResult result = rankingService.recalculate(id);
        OffsetDateTime finishedAt = OffsetDateTime.now(ZoneOffset.UTC);

        return new RecalcResponse(id, result.round(), "SUCCESS", null,
            startedAt, finishedAt, result.rowsAffected(), System.currentTimeMillis() - start);
    }

    @PostMapping("/{id}/recalculate-target-price")
    @PreAuthorize("hasAnyRole('Administrator', 'SalesOps')")
    public RecalcResponse recalculateTargetPrice(@PathVariable long id) {
        long start = System.currentTimeMillis();
        OffsetDateTime startedAt = OffsetDateTime.now(ZoneOffset.UTC);
        RecalcResult result = targetPriceService.recalculate(id);
        OffsetDateTime finishedAt = OffsetDateTime.now(ZoneOffset.UTC);

        return new RecalcResponse(id, result.round(), "SUCCESS", null,
            startedAt, finishedAt, result.rowsAffected(), System.currentTimeMillis() - start);
    }
}
