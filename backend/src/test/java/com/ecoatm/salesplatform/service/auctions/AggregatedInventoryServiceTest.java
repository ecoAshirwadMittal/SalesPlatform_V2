package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventoryPageResponse;
import com.ecoatm.salesplatform.model.integration.SnowflakeSyncLog;
import com.ecoatm.salesplatform.repository.integration.SnowflakeSyncLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AggregatedInventoryServiceTest {

    @Mock private EntityManager em;
    @Mock private Query nativeQuery;
    @Mock private SnowflakeSyncLogRepository syncLogRepository;

    private AggregatedInventoryService service;

    @BeforeEach
    void setUp() {
        service = new AggregatedInventoryService(em, syncLogRepository);
    }

    @Test
    @DisplayName("search returns page response with mapped rows and total count")
    void search_returnsMappedPage() {
        Object[] row = new Object[] {
                1L, "75", "A_YYY", "Apple", "iPhone 3G",
                "IPHONE 3G 8GB A1241/A1324", "AT&T",
                0, new BigDecimal("0.0000"),
                7, new BigDecimal("2.0700"),
                Boolean.FALSE
        };

        when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(nativeQuery);
        when(nativeQuery.getResultList())
                .thenReturn(List.<Object[]>of(row));
        when(nativeQuery.getSingleResult()).thenReturn(1L);

        AggregatedInventoryPageResponse resp = service.search(100L, null, null, null, null, null, null, 0, 20);

        assertThat(resp.content()).hasSize(1);
        assertThat(resp.content().get(0).ecoid2()).isEqualTo("75");
        assertThat(resp.content().get(0).brand()).isEqualTo("Apple");
        assertThat(resp.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("getTotals computes averages and surfaces helper flags for the selected week")
    void getTotals_withWeekId_returnsKpisAndFlags() {
        Object[] row = new Object[] {
                186020, new BigDecimal("1855306.00"), new BigDecimal("42.1700"),
                57298,  new BigDecimal("5269391.00"), new BigDecimal("214.5400"),
                java.sql.Timestamp.from(Instant.parse("2026-04-17T08:40:00Z"))
        };

        when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(nativeQuery);
        // Order matches the four queries inside getTotals(weekId): totals row,
        // hasInventory, hasAuction, isCurrentWeek.
        when(nativeQuery.getSingleResult())
                .thenReturn(row)
                .thenReturn(Boolean.TRUE)
                .thenReturn(Boolean.FALSE)
                .thenReturn(Boolean.TRUE);

        var log = new SnowflakeSyncLog();
        log.setStatus("COMPLETED");
        when(syncLogRepository.findFirstBySyncTypeAndTargetKeyOrderByStartedAtDesc(
                AggregatedInventorySnowflakeSyncService.SYNC_TYPE, "100"))
                .thenReturn(Optional.of(log));

        var totals = service.getTotals(100L);

        assertThat(totals.totalQuantity()).isEqualTo(186020);
        assertThat(totals.totalPayout()).isEqualByComparingTo(new BigDecimal("1855306.00"));
        assertThat(totals.dwAverageTargetPrice()).isEqualByComparingTo(new BigDecimal("214.5400"));
        assertThat(totals.hasInventory()).isTrue();
        assertThat(totals.hasAuction()).isFalse();
        assertThat(totals.isCurrentWeek()).isTrue();
        assertThat(totals.syncStatus()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("getTotals with null weekId returns safe-default helper flags and no sync lookup")
    void getTotals_withNullWeekId_returnsSafeDefaults() {
        Object[] row = new Object[] {
                0, BigDecimal.ZERO, BigDecimal.ZERO,
                0, BigDecimal.ZERO, BigDecimal.ZERO,
                null
        };

        when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenReturn(row);

        var totals = service.getTotals(null);

        assertThat(totals.hasInventory()).isFalse();
        assertThat(totals.hasAuction()).isFalse();
        assertThat(totals.isCurrentWeek()).isFalse();
        assertThat(totals.syncStatus()).isEqualTo("NONE");
        org.mockito.Mockito.verifyNoInteractions(syncLogRepository);
    }

    @Test
    @DisplayName("updateRow saves mergedGrade, datawipe, totals and flips isTotalQuantityModified")
    void updateRow_persistsAdminEdit() {
        var entity = new com.ecoatm.salesplatform.model.auctions.AggregatedInventory();
        entity.setTotalQuantity(5);
        entity.setDwTotalQuantity(2);
        entity.setMergedGrade("A_YYY");
        when(em.find(com.ecoatm.salesplatform.model.auctions.AggregatedInventory.class, 99L))
                .thenReturn(entity);
        when(em.merge(entity)).thenReturn(entity);

        var req = new com.ecoatm.salesplatform.dto.AggregatedInventoryUpdateRequest(
                "E_YYN", true, 9, 4);

        var updated = service.updateRow(99L, req);

        assertThat(updated.getMergedGrade()).isEqualTo("E_YYN");
        assertThat(updated.isDatawipe()).isTrue();
        assertThat(updated.getTotalQuantity()).isEqualTo(9);
        assertThat(updated.getDwTotalQuantity()).isEqualTo(4);
        assertThat(updated.isTotalQuantityModified()).isTrue();
    }
}
