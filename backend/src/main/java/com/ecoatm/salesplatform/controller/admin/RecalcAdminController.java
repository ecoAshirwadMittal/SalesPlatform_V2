package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.admin.RecalcResponse;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.BidRankingService;
import com.ecoatm.salesplatform.service.auctions.recalc.TargetPriceRecalcService;
import jakarta.persistence.EntityNotFoundException;
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
    private final SchedulingAuctionRepository saRepo;

    public RecalcAdminController(BidRankingService rankingService,
                                 TargetPriceRecalcService targetPriceService,
                                 SchedulingAuctionRepository saRepo) {
        this.rankingService = rankingService;
        this.targetPriceService = targetPriceService;
        this.saRepo = saRepo;
    }

    @PostMapping("/{id}/re-rank")
    @PreAuthorize("hasAnyRole('Administrator', 'SalesOps')")
    public RecalcResponse reRank(@PathVariable long id) {
        long start = System.currentTimeMillis();
        OffsetDateTime startedAt = OffsetDateTime.now(ZoneOffset.UTC);
        rankingService.recalculate(id);
        OffsetDateTime finishedAt = OffsetDateTime.now(ZoneOffset.UTC);

        int round = saRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("not found: " + id))
            .getRound();

        // rowsAffected isn't returned through the service for cleanliness; use 0
        // here and let callers consult the status columns. (Future enhancement:
        // thread the count through.)
        return new RecalcResponse(id, round, "SUCCESS", null,
            startedAt, finishedAt, 0, System.currentTimeMillis() - start);
    }

    @PostMapping("/{id}/recalculate-target-price")
    @PreAuthorize("hasAnyRole('Administrator', 'SalesOps')")
    public RecalcResponse recalculateTargetPrice(@PathVariable long id) {
        long start = System.currentTimeMillis();
        OffsetDateTime startedAt = OffsetDateTime.now(ZoneOffset.UTC);
        targetPriceService.recalculate(id);
        OffsetDateTime finishedAt = OffsetDateTime.now(ZoneOffset.UTC);

        int round = saRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("not found: " + id))
            .getRound();

        return new RecalcResponse(id, round, "SUCCESS", null,
            startedAt, finishedAt, 0, System.currentTimeMillis() - start);
    }
}
