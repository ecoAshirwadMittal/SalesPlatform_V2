package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.Round3ReportResponse;
import com.ecoatm.salesplatform.dto.Round3ReportRow;
import com.ecoatm.salesplatform.repository.auctions.Round3BuyerDataReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Read-only admin service for the Round 3 Bid Report by Buyer view.
 *
 * <p>Mendix parity: {@code RoundThreeBidReportPage}. The QA POM only ever
 * filters by week (the dropdown surfaces auctions by week id), so the
 * service exposes a single load-by-week verb. The bulky {@code report_json}
 * blob is omitted from the response — see {@link Round3ReportRow}.
 *
 * <p>Authorisation lives at the controller boundary
 * ({@code @PreAuthorize hasAnyRole('Administrator','SalesOps')}); this
 * service makes no further authorisation checks because the data is
 * non-PII / non-financial-instrument and the entire row set is admin-readable.
 */
@Service
public class Round3ReportService {

    private final Round3BuyerDataReportRepository repository;

    public Round3ReportService(Round3BuyerDataReportRepository repository) {
        this.repository = repository;
    }

    /**
     * Load every Round 3 report row whose parent auction has the given
     * {@code week_id}. Returns an empty list (NOT a 404) when no rows
     * exist for the week — the admin UI uses the empty array to render a
     * "No data" state.
     *
     * @throws IllegalArgumentException when {@code weekId} is null
     */
    @Transactional(readOnly = true, timeout = 10)
    public Round3ReportResponse findByWeek(Long weekId) {
        if (weekId == null) {
            throw new IllegalArgumentException("weekId is required");
        }

        List<Round3ReportRow> rows = repository.findByWeekId(weekId).stream()
                .map(Round3ReportRow::from)
                .toList();

        return Round3ReportResponse.of(weekId, rows);
    }

    /**
     * Drill-down variant — load reports for a single auction id.
     * Useful when the admin already navigated from the Auctions list.
     */
    @Transactional(readOnly = true, timeout = 10)
    public List<Round3ReportRow> findByAuction(Long auctionId) {
        if (auctionId == null) {
            throw new IllegalArgumentException("auctionId is required");
        }
        return repository.findByAuctionIdOrderByBuyerCode(auctionId).stream()
                .map(Round3ReportRow::from)
                .toList();
    }
}
