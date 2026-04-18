package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventoryPageResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AggregatedInventoryServiceTest {

    @Mock private EntityManager em;
    @Mock private Query nativeQuery;

    private AggregatedInventoryService service;

    @BeforeEach
    void setUp() {
        service = new AggregatedInventoryService(em);
    }

    @Test
    @DisplayName("search returns page response with mapped rows and total count")
    void search_returnsMappedPage() {
        Object[] row = new Object[] {
                1L, "75", "A_YYY", "Apple", "iPhone 3G",
                "IPHONE 3G 8GB A1241/A1324", "AT&T",
                0, new BigDecimal("0.0000"),
                7, new BigDecimal("2.0700")
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
    @DisplayName("getTotals computes averages from totals row + aggregated inventory")
    void getTotals_withTotalsRow_returnsKpis() {
        Object[] row = new Object[] {
                186020, new BigDecimal("1855306.00"), new BigDecimal("42.1700"),
                57298,  new BigDecimal("5269391.00"), new BigDecimal("214.5400"),
                java.sql.Timestamp.from(Instant.parse("2026-04-17T08:40:00Z"))
        };

        when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenReturn(row);

        var totals = service.getTotals(100L);

        assertThat(totals.totalQuantity()).isEqualTo(186020);
        assertThat(totals.totalPayout()).isEqualByComparingTo(new BigDecimal("1855306.00"));
        assertThat(totals.dwAverageTargetPrice()).isEqualByComparingTo(new BigDecimal("214.5400"));
    }
}
