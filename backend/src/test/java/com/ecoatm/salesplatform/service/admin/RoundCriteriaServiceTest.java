package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.RoundCriteriaResponse;
import com.ecoatm.salesplatform.dto.RoundCriteriaUpdateRequest;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.BidRoundSelectionFilter;
import com.ecoatm.salesplatform.model.auctions.RegularBuyerInventoryOption;
import com.ecoatm.salesplatform.model.auctions.RegularBuyerQualification;
import com.ecoatm.salesplatform.repository.auctions.BidRoundSelectionFilterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RoundCriteriaService}. Covers the four load-bearing
 * branches called out in the Lane 4 plan:
 *
 * <ol>
 *   <li>{@code GET} on a missing row → 404 (caller renders defaults locally).</li>
 *   <li>{@code PUT} when no row exists → INSERT a new row with the chosen settings.</li>
 *   <li>{@code PUT} when a row exists → UPDATE in place, preserving any
 *       columns the Lane-4 request shape doesn't expose
 *       (target_percent, merged_grade*, stb_include_all_inventory).</li>
 *   <li>Friendly enum strings ({@code Bid_Buyers_Only}, {@code Full_Inventory})
 *       are translated to the DB-side legacy enums on both read and write.</li>
 * </ol>
 *
 * <p>The non-admin / 403 case is verified at the controller layer
 * ({@code RoundCriteriaControllerTest}) — {@code @PreAuthorize} runs in the
 * Spring Security AOP proxy, not inside the service, so a service-layer
 * unit test cannot exercise it without a full MVC slice.
 */
@ExtendWith(MockitoExtension.class)
class RoundCriteriaServiceTest {

    @Mock private BidRoundSelectionFilterRepository repository;

    private RoundCriteriaService service;

    @BeforeEach
    void setUp() {
        service = new RoundCriteriaService(repository);
    }

    // -----------------------------------------------------------------------
    // GET — missing-row → 404
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("get(2) — missing row throws EntityNotFoundException (→ 404)")
    void get_missingRow_throws404() {
        when(repository.findByRound(2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(2))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("RoundCriteria")
                .hasMessageContaining("round=2");
    }

    @Test
    @DisplayName("get(2) — existing row projects friendly enum strings")
    void get_existingRow_translatesEnums() {
        BidRoundSelectionFilter row = sampleRow(2,
                RegularBuyerQualification.Only_Qualified,
                RegularBuyerInventoryOption.InventoryRound1QualifiedBids,
                Boolean.FALSE);
        when(repository.findByRound(2)).thenReturn(Optional.of(row));

        RoundCriteriaResponse result = service.get(2);

        assertThat(result.round()).isEqualTo(2);
        assertThat(result.regularBuyerQualification()).isEqualTo("Bid_Buyers_Only");
        assertThat(result.regularBuyerInventoryOptions()).isEqualTo("Inventory_With_Bids");
        assertThat(result.stbAllowAllBuyersOverride()).isFalse();
    }

    @Test
    @DisplayName("get(1) — invalid round short-circuits before repository call")
    void get_invalidRound_throws400() {
        assertThatThrownBy(() -> service.get(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("round must be 2 or 3");

        verify(repository, never()).findByRound(any(Integer.class));
    }

    // -----------------------------------------------------------------------
    // PUT — creates a new row when missing
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("upsert(2) — creates a new row when none exists, sets createdDate")
    void upsert_missingRow_inserts() {
        when(repository.findByRound(2)).thenReturn(Optional.empty());
        when(repository.save(any(BidRoundSelectionFilter.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RoundCriteriaUpdateRequest req = new RoundCriteriaUpdateRequest(
                "All_Buyers", "Full_Inventory", Boolean.TRUE);

        RoundCriteriaResponse out = service.upsert(2, req);

        ArgumentCaptor<BidRoundSelectionFilter> captor =
                ArgumentCaptor.forClass(BidRoundSelectionFilter.class);
        verify(repository).save(captor.capture());
        BidRoundSelectionFilter saved = captor.getValue();

        assertThat(saved.getRound()).isEqualTo(2);
        assertThat(saved.getRegularBuyerQualification())
                .isEqualTo(RegularBuyerQualification.All_Buyers);
        assertThat(saved.getRegularBuyerInventoryOptions())
                .isEqualTo(RegularBuyerInventoryOption.ShowAllInventory);
        assertThat(saved.getStbAllowAllBuyersOverride()).isTrue();
        // First insert sets both timestamps so the NOT NULL columns are populated.
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getChangedDate()).isNotNull();

        assertThat(out.regularBuyerQualification()).isEqualTo("All_Buyers");
        assertThat(out.regularBuyerInventoryOptions()).isEqualTo("Full_Inventory");
        assertThat(out.stbAllowAllBuyersOverride()).isTrue();
    }

    // -----------------------------------------------------------------------
    // PUT — updates an existing row in place
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("upsert(2) — updates existing row, bumps changedDate, preserves createdDate")
    void upsert_existingRow_updatesInPlace() {
        BidRoundSelectionFilter existing = sampleRow(2,
                RegularBuyerQualification.Only_Qualified,
                RegularBuyerInventoryOption.InventoryRound1QualifiedBids,
                Boolean.FALSE);
        Instant originalCreated = existing.getCreatedDate();
        Instant originalChanged = existing.getChangedDate();

        when(repository.findByRound(2)).thenReturn(Optional.of(existing));
        when(repository.save(any(BidRoundSelectionFilter.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RoundCriteriaUpdateRequest req = new RoundCriteriaUpdateRequest(
                "All_Buyers", "Full_Inventory", Boolean.TRUE);

        RoundCriteriaResponse out = service.upsert(2, req);

        ArgumentCaptor<BidRoundSelectionFilter> captor =
                ArgumentCaptor.forClass(BidRoundSelectionFilter.class);
        verify(repository).save(captor.capture());
        BidRoundSelectionFilter saved = captor.getValue();

        assertThat(saved.getRegularBuyerQualification())
                .isEqualTo(RegularBuyerQualification.All_Buyers);
        assertThat(saved.getRegularBuyerInventoryOptions())
                .isEqualTo(RegularBuyerInventoryOption.ShowAllInventory);
        assertThat(saved.getStbAllowAllBuyersOverride()).isTrue();
        assertThat(saved.getCreatedDate()).isEqualTo(originalCreated);
        assertThat(saved.getChangedDate()).isAfter(originalChanged);

        assertThat(out.stbAllowAllBuyersOverride()).isTrue();
    }

    // -----------------------------------------------------------------------
    // PUT — input validation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("upsert(2) — unknown qualification string throws IllegalArgumentException (→ 400)")
    void upsert_unknownEnum_throws400() {
        RoundCriteriaUpdateRequest req = new RoundCriteriaUpdateRequest(
                "Unknown", "Full_Inventory", Boolean.TRUE);

        assertThatThrownBy(() -> service.upsert(2, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("regularBuyerQualification");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("upsert(5) — invalid round short-circuits before repository call")
    void upsert_invalidRound_throws400() {
        RoundCriteriaUpdateRequest req = new RoundCriteriaUpdateRequest(
                "All_Buyers", "Full_Inventory", Boolean.TRUE);

        assertThatThrownBy(() -> service.upsert(5, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("round must be 2 or 3");

        verify(repository, never()).findByRound(any(Integer.class));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("upsert(2) — null override flag falls back to false")
    void upsert_nullOverride_defaultsToFalse() {
        when(repository.findByRound(2)).thenReturn(Optional.empty());
        when(repository.save(any(BidRoundSelectionFilter.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RoundCriteriaUpdateRequest req = new RoundCriteriaUpdateRequest(
                "Bid_Buyers_Only", "Inventory_With_Bids", null);

        RoundCriteriaResponse out = service.upsert(2, req);

        assertThat(out.stbAllowAllBuyersOverride()).isFalse();
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static BidRoundSelectionFilter sampleRow(int round,
                                                     RegularBuyerQualification qual,
                                                     RegularBuyerInventoryOption inv,
                                                     Boolean override) {
        BidRoundSelectionFilter row = new BidRoundSelectionFilter();
        row.setRound(round);
        row.setRegularBuyerQualification(qual);
        row.setRegularBuyerInventoryOptions(inv);
        row.setStbAllowAllBuyersOverride(override);
        row.setStbIncludeAllInventory(Boolean.FALSE);
        row.setCreatedDate(Instant.parse("2026-01-01T00:00:00Z"));
        row.setChangedDate(Instant.parse("2026-01-01T00:00:00Z"));
        return row;
    }
}
