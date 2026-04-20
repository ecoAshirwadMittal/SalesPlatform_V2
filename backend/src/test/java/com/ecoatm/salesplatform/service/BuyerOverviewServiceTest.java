package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.BuyerOverviewPageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyerOverviewServiceTest {

    @Mock private EntityManager em;
    @Mock private Query listQuery;
    @Mock private Query countQuery;

    private BuyerOverviewService service;

    @BeforeEach
    void setUp() {
        service = new BuyerOverviewService(em);
    }

    /** First call returns the list query, second returns the count query. */
    private void stubTwoQueries() {
        when(em.createNativeQuery(anyString())).thenReturn(listQuery, countQuery);
        when(listQuery.setParameter(anyString(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(listQuery);
        when(countQuery.setParameter(anyString(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(countQuery);
    }

    private static Object[] row(long id, String name, String status, String codes) {
        return new Object[]{id, name, status, codes};
    }

    @Test
    @DisplayName("maps rows, computes totalPages, and normalizes status")
    void search_mapsRows() {
        stubTwoQueries();
        List<Object[]> rows = new ArrayList<>();
        rows.add(row(1L, "Acme Corp", "Active", "AC001, AC002"));
        rows.add(row(2L, "Beta LLC", "Disabled", "BL100"));
        rows.add(row(3L, "Gamma Inc", null, ""));
        when(listQuery.getResultList()).thenReturn(rows);
        when(countQuery.getSingleResult()).thenReturn(3L);

        BuyerOverviewPageResponse page = service.search(null, null, null, 0, 20);

        assertThat(page.content()).hasSize(3);
        assertThat(page.content().get(0).id()).isEqualTo(1L);
        assertThat(page.content().get(0).companyName()).isEqualTo("Acme Corp");
        assertThat(page.content().get(0).status()).isEqualTo("Active");
        assertThat(page.content().get(0).buyerCodesDisplay()).isEqualTo("AC001, AC002");
        // Null/blank status normalizes to Disabled (Mendix parity)
        assertThat(page.content().get(2).status()).isEqualTo("Disabled");
        assertThat(page.content().get(2).buyerCodesDisplay()).isEqualTo("");

        assertThat(page.totalElements()).isEqualTo(3L);
        assertThat(page.totalPages()).isEqualTo(1);
        assertThat(page.page()).isZero();
        assertThat(page.pageSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("empty result returns 0 totalElements and 0 totalPages")
    void search_emptyResult() {
        stubTwoQueries();
        when(listQuery.getResultList()).thenReturn(List.of());
        when(countQuery.getSingleResult()).thenReturn(0L);

        BuyerOverviewPageResponse page = service.search("NoMatch", null, null, 0, 20);

        assertThat(page.content()).isEmpty();
        assertThat(page.totalElements()).isZero();
        assertThat(page.totalPages()).isZero();
    }

    @Test
    @DisplayName("computes totalPages via ceiling division")
    void search_totalPages_ceilingDivision() {
        stubTwoQueries();
        List<Object[]> oneRow = new ArrayList<>();
        oneRow.add(row(1L, "Acme", "Active", "A1"));
        when(listQuery.getResultList()).thenReturn(oneRow);
        when(countQuery.getSingleResult()).thenReturn(41L);

        BuyerOverviewPageResponse page = service.search(null, null, null, 0, 20);

        // 41 elements / 20 per page = 3 pages (ceil)
        assertThat(page.totalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("blank filters are passed as null to the query")
    void search_blankFiltersBoundAsNull() {
        stubTwoQueries();
        when(listQuery.getResultList()).thenReturn(List.of());
        when(countQuery.getSingleResult()).thenReturn(0L);

        service.search("   ", "", null, 0, 10);

        verify(listQuery, atLeastOnce()).setParameter("name", (Object) null);
        verify(listQuery, atLeastOnce()).setParameter("codes", (Object) null);
        verify(listQuery, atLeastOnce()).setParameter("status", (Object) null);
    }

    @Test
    @DisplayName("non-blank filters are trimmed and forwarded")
    void search_nonBlankFiltersForwarded() {
        stubTwoQueries();
        when(listQuery.getResultList()).thenReturn(List.of());
        when(countQuery.getSingleResult()).thenReturn(0L);

        service.search("Acme", "AC", "Active", 2, 25);

        verify(listQuery, atLeastOnce()).setParameter("name", "Acme");
        verify(listQuery, atLeastOnce()).setParameter("codes", "AC");
        verify(listQuery, atLeastOnce()).setParameter("status", "Active");
        verify(listQuery, atLeastOnce()).setParameter("limit", 25);
        verify(listQuery, atLeastOnce()).setParameter("offset", 50);
    }
}
