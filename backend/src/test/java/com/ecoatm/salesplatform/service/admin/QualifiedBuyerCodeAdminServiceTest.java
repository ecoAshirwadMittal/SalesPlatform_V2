package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeAdminListResponse;
import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeAdminRow;
import com.ecoatm.salesplatform.event.buyermgmt.QualificationOverriddenEvent;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCodeAudit;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeAuditRepository;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
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
 * Unit tests for {@link QualifiedBuyerCodeAdminService} — the P8 Lane 3B
 * admin Qualified Buyer Codes service, extended by gap-analysis 2.4
 * sub-feature 2 with a round-status guard + override event.
 *
 * <ul>
 *   <li>list filters by schedulingAuctionId + JOINs buyer_codes for human code</li>
 *   <li>updateIncluded flips included=false and forces qualification_type=Manual</li>
 *   <li>updateIncluded flips included=true and forces qualification_type=Manual</li>
 *   <li>updateIncluded writes an audit row capturing old vs new values</li>
 *   <li>updateIncluded throws QualifiedBuyerCodeNotFoundException for unknown ids</li>
 *   <li>updateIncluded persists changedDate + changedById on the parent row</li>
 *   <li>updateIncluded on a Closed round rejects (no persist, no audit, no event)</li>
 *   <li>updateIncluded on a Started round publishes {@link QualificationOverriddenEvent}
 *       with the full fact shape</li>
 *   <li>updateIncluded still publishes the event when included=false (Task 4
 *       owns the email decision)</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class QualifiedBuyerCodeAdminServiceTest {

    @Mock private QualifiedBuyerCodeRepository qbcRepo;
    @Mock private QualifiedBuyerCodeAuditRepository auditRepo;
    @Mock private SchedulingAuctionRepository saRepo;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private EntityManager em;
    @Mock private Query query;

    private QualifiedBuyerCodeAdminService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new QualifiedBuyerCodeAdminService(qbcRepo, auditRepo, saRepo, eventPublisher);
        Field f = QualifiedBuyerCodeAdminService.class.getDeclaredField("em");
        f.setAccessible(true);
        f.set(service, em);
    }

    @Test
    @DisplayName("list filters by schedulingAuctionId and JOINs buyer_codes for human code")
    void list_filtersBySchedulingAuctionAndJoinsBuyerCodes() {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        Object[] r = new Object[]{
                1L, 200L, 55L, "AA600WHL", "Qualified", true, false
        };
        // varargs of Object[] is a footgun: List.of(r) where r is Object[]
        // unwraps the array.
        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        rows.add(r);
        when(query.getResultList()).thenReturn(rows);

        QualifiedBuyerCodeAdminListResponse resp = service.list(200L);

        ArgumentCaptor<String> sqlCap = ArgumentCaptor.forClass(String.class);
        verify(em).createNativeQuery(sqlCap.capture());
        String sql = sqlCap.getValue();
        assertThat(sql)
                .contains("FROM buyer_mgmt.qualified_buyer_codes qbc")
                .contains("JOIN buyer_mgmt.buyer_codes bc")
                .contains("scheduling_auction_id = ?1")
                .contains("ORDER BY bc.code ASC");

        assertThat(resp.total()).isEqualTo(1);
        assertThat(resp.rows().get(0).buyerCode()).isEqualTo("AA600WHL");
        assertThat(resp.rows().get(0).qualificationType()).isEqualTo("Qualified");
        assertThat(resp.rows().get(0).included()).isTrue();
    }

    @Test
    @DisplayName("updateIncluded(false) flips flag to false and forces qualification_type=Manual")
    void updateIncluded_uncheck_setsManualAndFalse() {
        QualifiedBuyerCode qbc = qbc(1L, 200L, 55L, true, QualificationType.Qualified);
        when(qbcRepo.findById(1L)).thenReturn(Optional.of(qbc));
        when(saRepo.findById(200L)).thenReturn(Optional.of(sa(SchedulingAuctionStatus.Started)));
        when(qbcRepo.save(any(QualifiedBuyerCode.class))).thenAnswer(inv -> inv.getArgument(0));
        // Stub the followup list() invocation that the service uses to build
        // the response shape.
        stubListReturning(new Object[]{1L, 200L, 55L, "AA600WHL", "Manual", false, false});

        QualifiedBuyerCodeAdminRow row = service.updateIncluded(1L, false, 9L);

        ArgumentCaptor<QualifiedBuyerCode> cap = ArgumentCaptor.forClass(QualifiedBuyerCode.class);
        verify(qbcRepo).save(cap.capture());
        assertThat(cap.getValue().isIncluded()).isFalse();
        assertThat(cap.getValue().getQualificationType()).isEqualTo(QualificationType.Manual);
        assertThat(row.included()).isFalse();
        assertThat(row.qualificationType()).isEqualTo("Manual");
    }

    @Test
    @DisplayName("updateIncluded(true) flips flag to true and forces qualification_type=Manual")
    void updateIncluded_check_setsManualAndTrue() {
        QualifiedBuyerCode qbc = qbc(1L, 200L, 55L, false, QualificationType.Not_Qualified);
        when(qbcRepo.findById(1L)).thenReturn(Optional.of(qbc));
        when(saRepo.findById(200L)).thenReturn(Optional.of(sa(SchedulingAuctionStatus.Started)));
        when(qbcRepo.save(any(QualifiedBuyerCode.class))).thenAnswer(inv -> inv.getArgument(0));
        stubListReturning(new Object[]{1L, 200L, 55L, "AA600WHL", "Manual", true, false});

        QualifiedBuyerCodeAdminRow row = service.updateIncluded(1L, true, 9L);

        ArgumentCaptor<QualifiedBuyerCode> cap = ArgumentCaptor.forClass(QualifiedBuyerCode.class);
        verify(qbcRepo).save(cap.capture());
        assertThat(cap.getValue().isIncluded()).isTrue();
        assertThat(cap.getValue().getQualificationType()).isEqualTo(QualificationType.Manual);
        assertThat(row.included()).isTrue();
        assertThat(row.qualificationType()).isEqualTo("Manual");
    }

    @Test
    @DisplayName("updateIncluded writes an audit row capturing old + new included/qualification_type")
    void updateIncluded_writesAuditWithDelta() {
        QualifiedBuyerCode qbc = qbc(1L, 200L, 55L, true, QualificationType.Qualified);
        when(qbcRepo.findById(1L)).thenReturn(Optional.of(qbc));
        when(saRepo.findById(200L)).thenReturn(Optional.of(sa(SchedulingAuctionStatus.Started)));
        when(qbcRepo.save(any(QualifiedBuyerCode.class))).thenAnswer(inv -> inv.getArgument(0));
        stubListReturning(new Object[]{1L, 200L, 55L, "AA600WHL", "Manual", false, false});

        service.updateIncluded(1L, false, 9L);

        ArgumentCaptor<QualifiedBuyerCodeAudit> cap = ArgumentCaptor.forClass(QualifiedBuyerCodeAudit.class);
        verify(auditRepo).save(cap.capture());
        QualifiedBuyerCodeAudit a = cap.getValue();
        assertThat(a.getQualifiedBuyerCodeId()).isEqualTo(1L);
        assertThat(a.getSchedulingAuctionId()).isEqualTo(200L);
        assertThat(a.getBuyerCodeId()).isEqualTo(55L);
        assertThat(a.getOldIncluded()).isTrue();
        assertThat(a.getNewIncluded()).isFalse();
        assertThat(a.getOldQualificationType()).isEqualTo("Qualified");
        assertThat(a.getNewQualificationType()).isEqualTo("Manual");
        assertThat(a.getChangedById()).isEqualTo(9L);
    }

    @Test
    @DisplayName("updateIncluded throws QualifiedBuyerCodeNotFoundException for unknown ids (no event)")
    void updateIncluded_throwsWhenMissing() {
        when(qbcRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateIncluded(999L, false, 9L))
                .isInstanceOf(QualifiedBuyerCodeNotFoundException.class);

        verify(qbcRepo, never()).save(any());
        verify(auditRepo, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("updateIncluded stamps changedDate and changedById on the parent row")
    void updateIncluded_stampsChangedDateAndById() {
        QualifiedBuyerCode qbc = qbc(1L, 200L, 55L, true, QualificationType.Qualified);
        when(qbcRepo.findById(1L)).thenReturn(Optional.of(qbc));
        when(saRepo.findById(200L)).thenReturn(Optional.of(sa(SchedulingAuctionStatus.Started)));
        when(qbcRepo.save(any(QualifiedBuyerCode.class))).thenAnswer(inv -> inv.getArgument(0));
        stubListReturning(new Object[]{1L, 200L, 55L, "AA600WHL", "Manual", false, false});

        service.updateIncluded(1L, false, 9L);

        ArgumentCaptor<QualifiedBuyerCode> cap = ArgumentCaptor.forClass(QualifiedBuyerCode.class);
        verify(qbcRepo).save(cap.capture());
        assertThat(cap.getValue().getChangedById()).isEqualTo(9L);
        assertThat(cap.getValue().getChangedDate()).isNotNull();
    }

    // -----------------------------------------------------------------------
    // gap-analysis 2.4 sub-feature 2 — round-status guard + override event
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateIncluded on a Closed round throws RoundClosedException and persists nothing (no save, no audit, no event)")
    void updateIncluded_closedRound_rejectsAndPublishesNoEvent() {
        QualifiedBuyerCode qbc = qbc(1L, 200L, 55L, true, QualificationType.Qualified);
        when(qbcRepo.findById(1L)).thenReturn(Optional.of(qbc));
        when(saRepo.findById(200L)).thenReturn(Optional.of(sa(SchedulingAuctionStatus.Closed)));

        assertThatThrownBy(() -> service.updateIncluded(1L, false, 9L))
                .isInstanceOf(RoundClosedException.class)
                .hasMessageContaining("closed");

        verify(qbcRepo, never()).save(any());
        verify(auditRepo, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("updateIncluded on a Started round with included=true publishes QualificationOverriddenEvent with the full fact shape")
    void updateIncluded_startedIncludedTrue_publishesEvent() {
        QualifiedBuyerCode qbc = qbc(1L, 200L, 55L, false, QualificationType.Not_Qualified);
        when(qbcRepo.findById(1L)).thenReturn(Optional.of(qbc));
        when(saRepo.findById(200L)).thenReturn(Optional.of(sa(SchedulingAuctionStatus.Started)));
        when(qbcRepo.save(any(QualifiedBuyerCode.class))).thenAnswer(inv -> inv.getArgument(0));
        stubListReturning(new Object[]{1L, 200L, 55L, "AA600WHL", "Manual", true, false});

        service.updateIncluded(1L, true, 9L);

        ArgumentCaptor<QualificationOverriddenEvent> cap =
                ArgumentCaptor.forClass(QualificationOverriddenEvent.class);
        verify(eventPublisher).publishEvent(cap.capture());
        QualificationOverriddenEvent ev = cap.getValue();
        assertThat(ev.qualifiedBuyerCodeId()).isEqualTo(1L);
        assertThat(ev.buyerCodeId()).isEqualTo(55L);
        assertThat(ev.schedulingAuctionId()).isEqualTo(200L);
        assertThat(ev.included()).isTrue();
        assertThat(ev.roundStatus()).isEqualTo(SchedulingAuctionStatus.Started);
        assertThat(ev.changedByUserId()).isEqualTo(9L);
        assertThat(ev.occurredAt()).isNotNull();
    }

    @Test
    @DisplayName("updateIncluded with included=false on a non-closed round still persists and still publishes the event (Task 4 owns the email decision)")
    void updateIncluded_includedFalse_stillPublishesEvent() {
        QualifiedBuyerCode qbc = qbc(1L, 200L, 55L, true, QualificationType.Qualified);
        when(qbcRepo.findById(1L)).thenReturn(Optional.of(qbc));
        when(saRepo.findById(200L)).thenReturn(Optional.of(sa(SchedulingAuctionStatus.Started)));
        when(qbcRepo.save(any(QualifiedBuyerCode.class))).thenAnswer(inv -> inv.getArgument(0));
        stubListReturning(new Object[]{1L, 200L, 55L, "AA600WHL", "Manual", false, false});

        service.updateIncluded(1L, false, 9L);

        verify(qbcRepo).save(any(QualifiedBuyerCode.class));
        ArgumentCaptor<QualificationOverriddenEvent> cap =
                ArgumentCaptor.forClass(QualificationOverriddenEvent.class);
        verify(eventPublisher).publishEvent(cap.capture());
        assertThat(cap.getValue().included()).isFalse();
        assertThat(cap.getValue().roundStatus()).isEqualTo(SchedulingAuctionStatus.Started);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void stubListReturning(Object[] row) {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        rows.add(row);
        when(query.getResultList()).thenReturn(rows);
    }

    private static QualifiedBuyerCode qbc(long id, long saId, long bcId,
                                          boolean included, QualificationType type) {
        QualifiedBuyerCode q = new QualifiedBuyerCode();
        q.setId(id);
        q.setSchedulingAuctionId(saId);
        q.setBuyerCodeId(bcId);
        q.setIncluded(included);
        q.setQualificationType(type);
        q.setSpecialTreatment(false);
        return q;
    }

    private static SchedulingAuction sa(SchedulingAuctionStatus status) {
        SchedulingAuction s = new SchedulingAuction();
        s.setId(200L);
        s.setRoundStatus(status);
        return s;
    }
}
