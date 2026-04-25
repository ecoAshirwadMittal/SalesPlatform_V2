package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.Round3ReportResponse;
import com.ecoatm.salesplatform.dto.Round3ReportRow;
import com.ecoatm.salesplatform.service.admin.Round3ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin REST surface for the Round 3 Bid Report by Buyer view.
 *
 * <p>Mendix parity: {@code RoundThreeBidReportPage} — a week dropdown +
 * read-only grid. Class-level {@code @PreAuthorize} grants
 * Administrator/SalesOps; the SecurityConfig path-matcher fallback would
 * be Administrator-only, so the explicit method-level grant is required
 * to admit SalesOps callers (matches QA's {@code userRole.SALES} usage).
 *
 * <p>SecurityConfig still bounces the request at the chain level — if
 * SalesOps needs to reach this endpoint, an explicit {@code requestMatchers}
 * line for {@code /api/v1/admin/round3-reports/**} is required. See
 * {@code SecurityConfig.filterChain} for the matcher patterns.
 */
@RestController
@RequestMapping("/api/v1/admin/round3-reports")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class Round3ReportController {

    private final Round3ReportService service;

    public Round3ReportController(Round3ReportService service) {
        this.service = service;
    }

    /**
     * GET /api/v1/admin/round3-reports?weekId=…
     *
     * <p>Returns every Round 3 report row whose parent auction's
     * {@code week_id} matches. Empty arrays are valid responses for weeks
     * with no submissions — the admin UI uses that to render a "No data"
     * empty state rather than a 404.
     */
    @GetMapping
    public Round3ReportResponse list(@RequestParam Long weekId) {
        return service.findByWeek(weekId);
    }

    /**
     * GET /api/v1/admin/round3-reports/by-auction?auctionId=…
     *
     * <p>Drill-down variant — the admin already resolved a single auction id
     * (e.g. from the Auctions list) and wants only that auction's reports.
     */
    @GetMapping("/by-auction")
    public List<Round3ReportRow> listByAuction(@RequestParam Long auctionId) {
        return service.findByAuction(auctionId);
    }
}
