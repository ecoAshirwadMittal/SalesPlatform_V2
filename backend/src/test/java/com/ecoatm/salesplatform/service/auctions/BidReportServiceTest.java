package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.BidReportPageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidReportServiceTest {

    @Mock
    private EntityManager em;

    private BidReportService service;

    @BeforeEach
    void setUp() {
        service = new BidReportService(em);
    }

    @Test
    void getR3Report_noFilter_returnsAllRows() {
        Query selectQ = mock(Query.class);
        Query countQ = mock(Query.class);

        Object[] sampleRow = buildRow(1L, 10L, 100L, 200L, 55L);
        when(em.createNativeQuery(anyString()))
                .thenReturn(selectQ)
                .thenReturn(countQ);
        when(selectQ.setParameter(anyString(), any())).thenReturn(selectQ);
        when(countQ.setParameter(anyString(), any())).thenReturn(countQ);
        when(selectQ.getResultList()).thenReturn(Collections.singletonList(sampleRow));
        when(countQ.getSingleResult()).thenReturn(1L);

        BidReportPageResponse response = service.getR3Report(null, 0, 20);

        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).id()).isEqualTo(1L);
        assertThat(response.content().get(0).bidRoundId()).isEqualTo(10L);
    }

    @Test
    void getR3Report_withAuctionId_passesFilterParam() {
        Query selectQ = mock(Query.class);
        Query countQ = mock(Query.class);

        when(em.createNativeQuery(anyString()))
                .thenReturn(selectQ)
                .thenReturn(countQ);
        when(selectQ.setParameter(anyString(), any())).thenReturn(selectQ);
        when(countQ.setParameter(anyString(), any())).thenReturn(countQ);
        when(selectQ.getResultList()).thenReturn(List.of());
        when(countQ.getSingleResult()).thenReturn(0L);

        service.getR3Report(42L, 0, 20);

        verify(selectQ).setParameter("auctionId", 42L);
        verify(countQ).setParameter("auctionId", 42L);
    }

    @Test
    void getR3Report_emptyResult_returnsZeroTotalPages() {
        Query selectQ = mock(Query.class);
        Query countQ = mock(Query.class);

        when(em.createNativeQuery(anyString()))
                .thenReturn(selectQ)
                .thenReturn(countQ);
        when(selectQ.setParameter(anyString(), any())).thenReturn(selectQ);
        when(countQ.setParameter(anyString(), any())).thenReturn(countQ);
        when(selectQ.getResultList()).thenReturn(List.of());
        when(countQ.getSingleResult()).thenReturn(0L);

        BidReportPageResponse response = service.getR3Report(null, 0, 20);

        assertThat(response.totalElements()).isZero();
        assertThat(response.content()).isEmpty();
        assertThat(response.totalPages()).isZero();
    }

    private static Object[] buildRow(long id, long bidRoundId, long auctionId,
                                      long saId, long buyerCodeId) {
        return new Object[]{
                id,                          // 0 id
                bidRoundId,                  // 1 bid_round_id
                auctionId,                   // 2 auction_id
                saId,                        // 3 scheduling_auction_id
                buyerCodeId,                 // 4 buyer_code_id
                "ECO-1",                     // 5 ecoid
                "A",                         // 6 merged_grade
                "Wholesale",                 // 7 buyer_code_type
                5,                           // 8 bid_quantity
                new BigDecimal("10.00"),     // 9 bid_amount
                4,                           // 10 submitted_bid_quantity
                new BigDecimal("9.50"),      // 11 submitted_bid_amount
                new BigDecimal("8.00"),      // 12 target_price
                10,                          // 13 maximum_quantity
                null,                        // 14 submitted_datetime
                null                         // 15 changed_date
        };
    }
}
