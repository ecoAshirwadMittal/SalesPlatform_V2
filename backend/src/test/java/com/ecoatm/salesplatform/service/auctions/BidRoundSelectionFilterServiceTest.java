package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.BidRoundSelectionFilterRequest;
import com.ecoatm.salesplatform.dto.BidRoundSelectionFilterResponse;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BidRoundSelectionFilterService}. Covers both read
 * (get) and write (update) flows, the round validation guard, and the
 * {@code changedDate} bump on save. Repository is mocked; no database.
 */
@ExtendWith(MockitoExtension.class)
class BidRoundSelectionFilterServiceTest {

    @Mock private BidRoundSelectionFilterRepository repository;

    private BidRoundSelectionFilterService service;

    @BeforeEach
    void setUp() {
        service = new BidRoundSelectionFilterService(repository);
    }

    private static BidRoundSelectionFilter sampleRound2Entity() {
        BidRoundSelectionFilter f = new BidRoundSelectionFilter();
        f.setLegacyId(42L);
        f.setRound(2);
        f.setTargetPercent(new BigDecimal("85.0000"));
        f.setTargetValue(new BigDecimal("150000.00"));
        f.setTotalValueFloor(new BigDecimal("5000.00"));
        f.setMergedGrade1("A");
        f.setMergedGrade2("B");
        f.setMergedGrade3("C");
        f.setStbAllowAllBuyersOverride(Boolean.FALSE);
        f.setStbIncludeAllInventory(Boolean.TRUE);
        f.setRegularBuyerQualification(RegularBuyerQualification.Only_Qualified);
        f.setRegularBuyerInventoryOptions(RegularBuyerInventoryOption.InventoryRound1QualifiedBids);
        f.setCreatedDate(Instant.parse("2024-01-01T00:00:00Z"));
        f.setChangedDate(Instant.parse("2024-01-01T00:00:00Z"));
        return f;
    }

    @Test
    @DisplayName("get(2) — returns projection with enum names as strings")
    void get_round2_returnsFilter() {
        when(repository.findByRound(2)).thenReturn(Optional.of(sampleRound2Entity()));

        BidRoundSelectionFilterResponse result = service.get(2);

        assertThat(result.round()).isEqualTo(2);
        assertThat(result.targetPercent()).isEqualByComparingTo("85.0000");
        assertThat(result.mergedGrade1()).isEqualTo("A");
        assertThat(result.stbIncludeAllInventory()).isTrue();
        assertThat(result.regularBuyerQualification()).isEqualTo("Only_Qualified");
        assertThat(result.regularBuyerInventoryOptions()).isEqualTo("InventoryRound1QualifiedBids");
        verify(repository).findByRound(2);
    }

    @Test
    @DisplayName("get(1) — invalid round throws IllegalArgumentException (→ 400)")
    void get_invalidRound_throws() {
        assertThatThrownBy(() -> service.get(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("round must be 2 or 3");

        verify(repository, never()).findByRound(any(Integer.class));
    }

    @Test
    @DisplayName("get(3) — missing row throws EntityNotFoundException (→ 404)")
    void get_missing_throws() {
        when(repository.findByRound(3)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(3))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("BidRoundSelectionFilter");
    }

    @Test
    @DisplayName("update(2) — persists writable fields and bumps changedDate")
    void update_happyPath_persistsFieldsAndBumpsChangedDate() {
        BidRoundSelectionFilter stored = sampleRound2Entity();
        Instant originalChanged = stored.getChangedDate();
        when(repository.findByRound(2)).thenReturn(Optional.of(stored));
        when(repository.save(any(BidRoundSelectionFilter.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BidRoundSelectionFilterRequest req = new BidRoundSelectionFilterRequest(
                new BigDecimal("90.5000"),
                new BigDecimal("200000.00"),
                new BigDecimal("7500.00"),
                "AA",
                "BB",
                "CC",
                Boolean.TRUE,
                null, // omitted → service defaults to FALSE
                RegularBuyerQualification.All_Buyers,
                RegularBuyerInventoryOption.ShowAllInventory);

        BidRoundSelectionFilterResponse out = service.update(2, req);

        ArgumentCaptor<BidRoundSelectionFilter> captor =
                ArgumentCaptor.forClass(BidRoundSelectionFilter.class);
        verify(repository).save(captor.capture());
        BidRoundSelectionFilter saved = captor.getValue();

        assertThat(saved.getTargetPercent()).isEqualByComparingTo("90.5000");
        assertThat(saved.getTargetValue()).isEqualByComparingTo("200000.00");
        assertThat(saved.getTotalValueFloor()).isEqualByComparingTo("7500.00");
        assertThat(saved.getMergedGrade1()).isEqualTo("AA");
        assertThat(saved.getMergedGrade2()).isEqualTo("BB");
        assertThat(saved.getMergedGrade3()).isEqualTo("CC");
        assertThat(saved.getStbAllowAllBuyersOverride()).isTrue();
        assertThat(saved.getStbIncludeAllInventory()).isFalse(); // null → FALSE default
        assertThat(saved.getRegularBuyerQualification())
                .isEqualTo(RegularBuyerQualification.All_Buyers);
        assertThat(saved.getRegularBuyerInventoryOptions())
                .isEqualTo(RegularBuyerInventoryOption.ShowAllInventory);
        assertThat(saved.getChangedDate()).isAfter(originalChanged);

        assertThat(out.regularBuyerQualification()).isEqualTo("All_Buyers");
        assertThat(out.regularBuyerInventoryOptions()).isEqualTo("ShowAllInventory");
    }

    @Test
    @DisplayName("update(5) — invalid round short-circuits before repository call")
    void update_invalidRound_throws() {
        BidRoundSelectionFilterRequest req = new BidRoundSelectionFilterRequest(
                null, null, null, null, null, null, null, null, null, null);

        assertThatThrownBy(() -> service.update(5, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("round must be 2 or 3");

        verify(repository, never()).findByRound(any(Integer.class));
        verify(repository, never()).save(any());
    }
}
