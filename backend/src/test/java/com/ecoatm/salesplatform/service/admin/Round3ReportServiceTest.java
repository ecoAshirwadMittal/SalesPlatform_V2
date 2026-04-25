package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.Round3ReportResponse;
import com.ecoatm.salesplatform.dto.Round3ReportRow;
import com.ecoatm.salesplatform.model.auctions.Round3BuyerDataReport;
import com.ecoatm.salesplatform.repository.auctions.Round3BuyerDataReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link Round3ReportService}.
 *
 * <p>Coverage focus is on the service's two contracts:
 * <ul>
 *   <li>findByWeek — empty week returns empty rows + count=0 (NOT 404)</li>
 *   <li>findByWeek/findByAuction — null id rejected with IllegalArgumentException</li>
 *   <li>Row mapping faithfully reproduces every entity column</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class Round3ReportServiceTest {

    private static final Long WEEK_ID = 544L;
    private static final Long AUCTION_ID = 12345L;

    @Mock
    private Round3BuyerDataReportRepository repository;

    private Round3ReportService service;

    @BeforeEach
    void setUp() {
        service = new Round3ReportService(repository);
    }

    // -----------------------------------------------------------------------
    // findByWeek
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findByWeek returns rows in repository order with count denormalised")
    void findByWeek_populatedWeek_returnsRows() {
        Round3BuyerDataReport r1 = report(1L, "AA600WHL", "Andrei Aliasiuk", 100, "1500.00");
        Round3BuyerDataReport r2 = report(2L, "DS2WHSL", "Damon Storage 2", 50, "750.50");
        when(repository.findByWeekId(WEEK_ID)).thenReturn(List.of(r1, r2));

        Round3ReportResponse response = service.findByWeek(WEEK_ID);

        assertThat(response.weekId()).isEqualTo(WEEK_ID);
        assertThat(response.count()).isEqualTo(2);
        assertThat(response.rows()).hasSize(2);
        assertThat(response.rows().get(0).buyerCode()).isEqualTo("AA600WHL");
        assertThat(response.rows().get(0).companyName()).isEqualTo("Andrei Aliasiuk");
        assertThat(response.rows().get(0).totalQuantity()).isEqualTo(100);
        assertThat(response.rows().get(0).totalPayout()).isEqualByComparingTo("1500.00");
        assertThat(response.rows().get(1).buyerCode()).isEqualTo("DS2WHSL");

        verify(repository).findByWeekId(WEEK_ID);
    }

    @Test
    @DisplayName("findByWeek returns empty rows + count=0 when no reports exist for the week")
    void findByWeek_emptyWeek_returnsEmptyRows() {
        when(repository.findByWeekId(WEEK_ID)).thenReturn(List.of());

        Round3ReportResponse response = service.findByWeek(WEEK_ID);

        // Empty week is a valid response — admin UI renders a "No data" state.
        // We must NOT throw EntityNotFoundException here; that would 404 the
        // dropdown selection and force the user to refresh the picker.
        assertThat(response.weekId()).isEqualTo(WEEK_ID);
        assertThat(response.count()).isZero();
        assertThat(response.rows()).isEmpty();
    }

    @Test
    @DisplayName("findByWeek rejects null weekId before touching the repository")
    void findByWeek_nullWeekId_throws() {
        assertThatThrownBy(() -> service.findByWeek(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("weekId");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("findByWeek maps every entity column into the row DTO")
    void findByWeek_mapsAllColumns() {
        Round3BuyerDataReport r = report(7L, "HN", "Nadia Boonnayanont", 200, "3200.75");
        r.setSubmittedDatetime(Instant.parse("2026-04-25T12:00:00Z"));
        r.setAuctionId(99L);
        when(repository.findByWeekId(eq(WEEK_ID))).thenReturn(List.of(r));

        Round3ReportResponse response = service.findByWeek(WEEK_ID);

        Round3ReportRow row = response.rows().get(0);
        assertThat(row.id()).isEqualTo(7L);
        assertThat(row.auctionId()).isEqualTo(99L);
        assertThat(row.buyerCode()).isEqualTo("HN");
        assertThat(row.companyName()).isEqualTo("Nadia Boonnayanont");
        assertThat(row.totalQuantity()).isEqualTo(200);
        assertThat(row.totalPayout()).isEqualByComparingTo("3200.75");
        assertThat(row.submittedDatetime()).isEqualTo(Instant.parse("2026-04-25T12:00:00Z"));
    }

    // -----------------------------------------------------------------------
    // findByAuction
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findByAuction returns rows for a single auction id")
    void findByAuction_returnsRows() {
        Round3BuyerDataReport r1 = report(1L, "HN", "Nadia Boonnayanont", 50, "500.00");
        when(repository.findByAuctionIdOrderByBuyerCode(AUCTION_ID))
                .thenReturn(List.of(r1));

        List<Round3ReportRow> rows = service.findByAuction(AUCTION_ID);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).buyerCode()).isEqualTo("HN");
        verify(repository).findByAuctionIdOrderByBuyerCode(AUCTION_ID);
    }

    @Test
    @DisplayName("findByAuction returns empty list for an auction with no reports")
    void findByAuction_emptyAuction_returnsEmptyList() {
        when(repository.findByAuctionIdOrderByBuyerCode(AUCTION_ID)).thenReturn(List.of());

        List<Round3ReportRow> rows = service.findByAuction(AUCTION_ID);

        assertThat(rows).isEmpty();
    }

    @Test
    @DisplayName("findByAuction rejects null auctionId before touching the repository")
    void findByAuction_nullAuctionId_throws() {
        assertThatThrownBy(() -> service.findByAuction(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("auctionId");

        verifyNoInteractions(repository);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static Round3BuyerDataReport report(
            Long id,
            String buyerCode,
            String company,
            Integer qty,
            String payout
    ) {
        Round3BuyerDataReport r = new Round3BuyerDataReport();
        r.setId(id);
        r.setBuyerCode(buyerCode);
        r.setCompanyName(company);
        r.setTotalQuantity(qty);
        r.setTotalPayout(new BigDecimal(payout));
        return r;
    }
}
