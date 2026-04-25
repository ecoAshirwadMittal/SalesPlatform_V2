package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.BidDataAdminListResponse;
import com.ecoatm.salesplatform.model.auctions.BidData;
import com.ecoatm.salesplatform.model.auctions.BidDataAudit;
import com.ecoatm.salesplatform.repository.auctions.BidDataAuditRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataSubmissionException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BidDataAdminService} — the P8 Lane 3A admin
 * Bid Data view + soft-delete service. Six scenarios cover:
 * <ul>
 *   <li>Filter SQL builds with both (round, buyerCode) params and ORDERs by id</li>
 *   <li>{@code submittedBidAmountGt0=true} appends the IS NOT NULL + &gt;0 clause</li>
 *   <li>{@code is_deprecated = false} is always present (soft-deleted rows hidden)</li>
 *   <li>softDelete writes an audit row capturing pre-action state, then flips the flag</li>
 *   <li>softDelete throws BID_DATA_NOT_FOUND for unknown ids</li>
 *   <li>list returns rows mapped from native-query columns in the right order</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class BidDataAdminServiceTest {

    @Mock private BidDataRepository bidDataRepo;
    @Mock private BidDataAuditRepository auditRepo;
    @Mock private EntityManager em;
    @Mock private Query query;

    private BidDataAdminService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new BidDataAdminService(bidDataRepo, auditRepo);
        // EntityManager is @PersistenceContext-injected at runtime; in unit
        // tests we set it via reflection to avoid a Spring context.
        Field f = BidDataAdminService.class.getDeclaredField("em");
        f.setAccessible(true);
        f.set(service, em);
    }

    @Test
    @DisplayName("list filters by (bidRoundId, buyerCodeId) and binds both params in order")
    void list_filtersByRoundAndBuyer() {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.<Object[]>of());

        service.list(200L, 55L, false);

        ArgumentCaptor<String> sqlCap = ArgumentCaptor.forClass(String.class);
        verify(em).createNativeQuery(sqlCap.capture());
        String sql = sqlCap.getValue();
        assertThat(sql)
                .contains("FROM auctions.bid_data")
                .contains("is_deprecated = false")
                .contains("bid_round_id = ?1")
                .contains("buyer_code_id = ?2")
                .contains("ORDER BY id ASC");

        verify(query).setParameter(1, 200L);
        verify(query).setParameter(2, 55L);
    }

    @Test
    @DisplayName("list with submittedBidAmountGt0=true narrows to submitted rows only")
    void list_filtersSubmittedOnly() {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.<Object[]>of());

        service.list(200L, null, true);

        ArgumentCaptor<String> sqlCap = ArgumentCaptor.forClass(String.class);
        verify(em).createNativeQuery(sqlCap.capture());
        assertThat(sqlCap.getValue())
                .contains("submitted_bid_amount IS NOT NULL")
                .contains("submitted_bid_amount > 0");
    }

    @Test
    @DisplayName("list maps native rows to BidDataAdminRow records")
    void list_mapsRows() {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        // Column order matches the SELECT list in BidDataAdminService.list:
        // id, bid_round_id, buyer_code_id, ecoid, merged_grade,
        // bid_quantity, bid_amount, submitted_bid_quantity,
        // submitted_bid_amount, submitted_datetime, changed_date, is_deprecated
        Object[] r = new Object[]{
                100L, 200L, 55L, "ECO-1", "ABC",
                3, new BigDecimal("12.50"), 2, new BigDecimal("10.00"),
                java.sql.Timestamp.from(Instant.parse("2026-04-22T10:00:00Z")),
                java.sql.Timestamp.from(Instant.parse("2026-04-22T11:00:00Z")),
                false
        };
        // varargs of Object[] is a footgun: List.of(r) where r is Object[]
        // unwraps the array — wrap in a typed singleton to keep it as
        // List<Object[]>.
        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        rows.add(r);
        when(query.getResultList()).thenReturn(rows);

        BidDataAdminListResponse resp = service.list(200L, 55L, false);

        assertThat(resp.total()).isEqualTo(1);
        assertThat(resp.rows()).hasSize(1);
        assertThat(resp.rows().get(0).id()).isEqualTo(100L);
        assertThat(resp.rows().get(0).bidAmount()).isEqualByComparingTo("12.50");
        assertThat(resp.rows().get(0).submittedBidAmount()).isEqualByComparingTo("10.00");
        assertThat(resp.rows().get(0).deprecated()).isFalse();
    }

    @Test
    @DisplayName("softDelete writes an audit snapshot then flips is_deprecated to true")
    void softDelete_writesAuditAndFlipsFlag() {
        BidData row = new BidData();
        row.setId(100L);
        row.setBidRoundId(200L);
        row.setBuyerCodeId(55L);
        row.setBidAmount(new BigDecimal("42.50"));
        row.setBidQuantity(3);
        row.setSubmittedBidAmount(new BigDecimal("40.00"));
        row.setSubmittedBidQuantity(2);
        row.setDeprecated(false);

        when(bidDataRepo.findById(100L)).thenReturn(Optional.of(row));

        service.softDelete(100L, 9L);

        ArgumentCaptor<BidDataAudit> auditCap = ArgumentCaptor.forClass(BidDataAudit.class);
        verify(auditRepo).save(auditCap.capture());
        BidDataAudit a = auditCap.getValue();
        // Audit captures pre-action state — the bid_amount is the value
        // BEFORE the soft-delete, not after.
        assertThat(a.getAction()).isEqualTo("SOFT_DELETE");
        assertThat(a.getBidDataId()).isEqualTo(100L);
        assertThat(a.getBidRoundId()).isEqualTo(200L);
        assertThat(a.getBuyerCodeId()).isEqualTo(55L);
        assertThat(a.getBidAmount()).isEqualByComparingTo("42.50");
        assertThat(a.getBidQuantity()).isEqualTo(3);
        assertThat(a.getSubmittedBidAmount()).isEqualByComparingTo("40.00");
        assertThat(a.getSubmittedBidQuantity()).isEqualTo(2);
        assertThat(a.getChangedById()).isEqualTo(9L);

        ArgumentCaptor<BidData> bdCap = ArgumentCaptor.forClass(BidData.class);
        verify(bidDataRepo).save(bdCap.capture());
        BidData saved = bdCap.getValue();
        assertThat(saved.isDeprecated()).isTrue();
        assertThat(saved.getChangedById()).isEqualTo(9L);
        assertThat(saved.getChangedDate()).isNotNull();
    }

    @Test
    @DisplayName("softDelete throws BID_DATA_NOT_FOUND for unknown ids and does not write an audit row")
    void softDelete_throwsWhenMissing() {
        when(bidDataRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.softDelete(999L, 9L))
                .isInstanceOf(BidDataSubmissionException.class)
                .extracting("code").isEqualTo("BID_DATA_NOT_FOUND");

        verify(auditRepo, never()).save(any());
        verify(bidDataRepo, never()).save(any());
    }

    @Test
    @DisplayName("list with no filters still anchors on is_deprecated = false")
    void list_noFilters_stillExcludesSoftDeletes() {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.<Object[]>of());

        service.list(null, null, false);

        ArgumentCaptor<String> sqlCap = ArgumentCaptor.forClass(String.class);
        verify(em).createNativeQuery(sqlCap.capture());
        String sql = sqlCap.getValue();
        assertThat(sql).contains("is_deprecated = false");
        // No filter clauses appended.
        assertThat(sql).doesNotContain("bid_round_id = ?");
        assertThat(sql).doesNotContain("buyer_code_id = ?");
    }
}
