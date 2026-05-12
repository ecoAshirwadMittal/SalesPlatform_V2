package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.dto.partialcredit.StatusConfigPatch;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link StatusConfigService}. The service is pure
 * (regex + null-coalescing) on top of a JpaRepository — mocking the repo
 * is sufficient.
 */
@ExtendWith(MockitoExtension.class)
class StatusConfigServiceTest {

    @Mock CreditRequestStatusRepository repository;
    private StatusConfigService service;

    @BeforeEach
    void setUp() {
        service = new StatusConfigService(repository);
    }

    /**
     * Builds a status row with sane defaults. Per-test overrides happen
     * through the returned setters to keep individual tests focused on
     * the field they're asserting against.
     */
    private static CreditRequestStatus row(Long id, SystemStatus status, int sortOrder) {
        CreditRequestStatus r = new CreditRequestStatus();
        r.setId(id);
        r.setSystemStatus(status);
        r.setInternalStatusText(status.name());
        r.setExternalStatusText(status.name());
        r.setColorHex("#888888");
        r.setSortOrder(sortOrder);
        r.setShowInUserCounters(Boolean.TRUE);
        r.setIsDefault(Boolean.FALSE);
        return r;
    }

    @Test
    @DisplayName("listAll returns rows ordered by sort_order ascending")
    void listAll_returns_sort_order_ascending() {
        // Insert intentionally out of order to prove the sort kicks in.
        when(repository.findAll()).thenReturn(List.of(
                row(3L, SystemStatus.UNDER_REVIEW, 30),
                row(1L, SystemStatus.DRAFT, 10),
                row(5L, SystemStatus.DECLINED, 50),
                row(2L, SystemStatus.PENDING_APPROVAL, 20),
                row(4L, SystemStatus.APPROVED, 40)));

        List<CreditRequestStatus> result = service.listAll();

        assertThat(result).hasSize(5);
        assertThat(result).extracting(CreditRequestStatus::getSortOrder)
                .containsExactly(10, 20, 30, 40, 50);
        assertThat(result).extracting(CreditRequestStatus::getSystemStatus)
                .containsExactly(
                        SystemStatus.DRAFT,
                        SystemStatus.PENDING_APPROVAL,
                        SystemStatus.UNDER_REVIEW,
                        SystemStatus.APPROVED,
                        SystemStatus.DECLINED);
    }

    @Test
    @DisplayName("update applies every non-null editable field")
    void update_applies_all_editable_fields() {
        CreditRequestStatus existing = row(2L, SystemStatus.PENDING_APPROVAL, 20);
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(CreditRequestStatus.class))).thenAnswer(inv -> inv.getArgument(0));

        StatusConfigPatch patch = new StatusConfigPatch(
                "Awaiting review (internal)",
                "Pending Approval (external)",
                "#FF8800",
                99,
                Boolean.FALSE);

        CreditRequestStatus result = service.update(2L, patch, 42L);

        assertThat(result.getInternalStatusText()).isEqualTo("Awaiting review (internal)");
        assertThat(result.getExternalStatusText()).isEqualTo("Pending Approval (external)");
        assertThat(result.getColorHex()).isEqualTo("#FF8800");
        assertThat(result.getSortOrder()).isEqualTo(99);
        assertThat(result.getShowInUserCounters()).isFalse();

        ArgumentCaptor<CreditRequestStatus> captor = ArgumentCaptor.forClass(CreditRequestStatus.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getColorHex()).isEqualTo("#FF8800");
    }

    @Test
    @DisplayName("update with all-null fields leaves the row untouched")
    void update_with_null_fields_leaves_row_alone() {
        CreditRequestStatus existing = row(2L, SystemStatus.PENDING_APPROVAL, 20);
        existing.setColorHex("#AABBCC");
        existing.setInternalStatusText("KEEP_ME");
        existing.setExternalStatusText("KEEP_ME_TOO");
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(CreditRequestStatus.class))).thenAnswer(inv -> inv.getArgument(0));

        StatusConfigPatch noop = new StatusConfigPatch(null, null, null, null, null);

        CreditRequestStatus result = service.update(2L, noop, 42L);

        assertThat(result.getInternalStatusText()).isEqualTo("KEEP_ME");
        assertThat(result.getExternalStatusText()).isEqualTo("KEEP_ME_TOO");
        assertThat(result.getColorHex()).isEqualTo("#AABBCC");
        assertThat(result.getSortOrder()).isEqualTo(20);
        assertThat(result.getShowInUserCounters()).isTrue();
    }

    @Test
    @DisplayName("update rejects invalid colorHex with a clear message")
    void update_rejects_invalid_color_hex() {
        // The repository should never be hit because validation runs after
        // the lookup but before any mutation/save. We still stub findById
        // so the lookup succeeds and we exercise the validate() path.
        CreditRequestStatus existing = row(2L, SystemStatus.PENDING_APPROVAL, 20);
        when(repository.findById(2L)).thenReturn(Optional.of(existing));

        StatusConfigPatch badColor = new StatusConfigPatch(
                null, null, "not-a-color", null, null);

        assertThatThrownBy(() -> service.update(2L, badColor, 42L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("colorHex")
                .hasMessageContaining("not-a-color");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update rejects oversize internal + external text")
    void update_rejects_oversize_text() {
        CreditRequestStatus existing = row(2L, SystemStatus.PENDING_APPROVAL, 20);
        when(repository.findById(2L)).thenReturn(Optional.of(existing));

        String oversize = "x".repeat(101);

        StatusConfigPatch internalTooLong = new StatusConfigPatch(
                oversize, null, null, null, null);
        assertThatThrownBy(() -> service.update(2L, internalTooLong, 42L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("internalStatusText");

        StatusConfigPatch externalTooLong = new StatusConfigPatch(
                null, oversize, null, null, null);
        assertThatThrownBy(() -> service.update(2L, externalTooLong, 42L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("externalStatusText");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update of non-existent id throws EntityNotFoundException")
    void update_throws_when_id_missing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        StatusConfigPatch patch = new StatusConfigPatch("x", null, null, null, null);

        assertThatThrownBy(() -> service.update(999L, patch, 42L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update preserves system_status (immutable enum key)")
    void update_preserves_system_status() {
        CreditRequestStatus existing = row(2L, SystemStatus.PENDING_APPROVAL, 20);
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(CreditRequestStatus.class))).thenAnswer(inv -> inv.getArgument(0));

        // Patch hits every editable field except system_status — there is
        // no patch path for the enum key. Confirm the entity stays pinned
        // to PENDING_APPROVAL throughout.
        StatusConfigPatch patch = new StatusConfigPatch(
                "new internal", "new external", "#112233", 42, Boolean.FALSE);

        CreditRequestStatus result = service.update(2L, patch, 42L);

        assertThat(result.getSystemStatus()).isEqualTo(SystemStatus.PENDING_APPROVAL);
    }

    @Test
    @DisplayName("update preserves id (immutable primary key)")
    void update_preserves_id() {
        CreditRequestStatus existing = row(2L, SystemStatus.PENDING_APPROVAL, 20);
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(CreditRequestStatus.class))).thenAnswer(inv -> inv.getArgument(0));

        StatusConfigPatch patch = new StatusConfigPatch(
                "new internal", null, null, null, null);

        CreditRequestStatus result = service.update(2L, patch, 42L);

        assertThat(result.getId()).isEqualTo(2L);
    }
}
