package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.SalesRepCreateRequest;
import com.ecoatm.salesplatform.dto.SalesRepResponse;
import com.ecoatm.salesplatform.dto.SalesRepUpdateRequest;
import com.ecoatm.salesplatform.exception.DuplicateSalesRepException;
import com.ecoatm.salesplatform.exception.SalesRepHasOffersException;
import com.ecoatm.salesplatform.model.buyermgmt.SalesRepresentative;
import com.ecoatm.salesplatform.repository.SalesRepresentativeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SalesRepService} — the legacy {@code Act_SaveSaleRep}
 * (trim + case-insensitive dup guard, owner stamping) and
 * {@code ACT_DeleteSalesRep} (offer-reference guard) rules, fully mocked.
 */
@ExtendWith(MockitoExtension.class)
class SalesRepServiceTest {

    private static final long CALLER_ID = 9001L;

    @Mock
    private SalesRepresentativeRepository repository;

    private SalesRepService service;

    @BeforeEach
    void setUp() {
        service = new SalesRepService(repository);
    }

    // ── create ────────────────────────────────────────────────────────

    @Test
    @DisplayName("create trims names, stamps owner + dates, and persists")
    void create_trimsAndStampsOwner() {
        when(repository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("Julie", "Spellman"))
                .thenReturn(false);
        when(repository.nextId()).thenReturn(4L);
        when(repository.save(any(SalesRepresentative.class))).thenAnswer(inv -> inv.getArgument(0));

        SalesRepResponse resp = service.create(
                new SalesRepCreateRequest("  Julie  ", "  Spellman  ", null), CALLER_ID);

        ArgumentCaptor<SalesRepresentative> captor = ArgumentCaptor.forClass(SalesRepresentative.class);
        verify(repository).save(captor.capture());
        SalesRepresentative saved = captor.getValue();

        assertThat(saved.getFirstName()).isEqualTo("Julie");
        assertThat(saved.getLastName()).isEqualTo("Spellman");
        assertThat(saved.getId()).isEqualTo(4L);
        assertThat(saved.isActive()).isTrue();          // null active defaults to true
        assertThat(saved.getOwnerId()).isEqualTo(CALLER_ID);
        assertThat(saved.getChangedById()).isEqualTo(CALLER_ID);
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getChangedDate()).isNotNull();

        assertThat(resp.firstName()).isEqualTo("Julie");
        assertThat(resp.active()).isTrue();
    }

    @Test
    @DisplayName("create rejects a case-insensitive duplicate name and never saves")
    void create_rejectsCaseInsensitiveDuplicate() {
        when(repository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("julie", "spellman"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(
                new SalesRepCreateRequest("julie", "spellman", true), CALLER_ID))
                .isInstanceOf(DuplicateSalesRepException.class)
                .hasMessageContaining("julie spellman");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create honours an explicit active=false")
    void create_honoursExplicitInactive() {
        when(repository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(any(), any()))
                .thenReturn(false);
        when(repository.nextId()).thenReturn(5L);
        when(repository.save(any(SalesRepresentative.class))).thenAnswer(inv -> inv.getArgument(0));

        SalesRepResponse resp = service.create(
                new SalesRepCreateRequest("New", "Rep", false), CALLER_ID);

        assertThat(resp.active()).isFalse();
    }

    // ── update ────────────────────────────────────────────────────────

    @Test
    @DisplayName("update excludes self in the duplicate check and stamps changer")
    void update_excludesSelfInDuplicateCheck() {
        SalesRepresentative existing = rep(2L, "Nathan", "Mount", true);
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndIdNot("Nathan", "Mount", 2L))
                .thenReturn(false);
        when(repository.save(any(SalesRepresentative.class))).thenAnswer(inv -> inv.getArgument(0));

        service.update(2L, new SalesRepUpdateRequest("  Nathan  ", "  Mount  ", true), CALLER_ID);

        verify(repository).existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndIdNot("Nathan", "Mount", 2L);
        assertThat(existing.getChangedById()).isEqualTo(CALLER_ID);
        assertThat(existing.getChangedDate()).isNotNull();
        assertThat(existing.getFirstName()).isEqualTo("Nathan");
    }

    @Test
    @DisplayName("update rejects a collision with a different rep")
    void update_rejectsCollisionWithOtherRep() {
        SalesRepresentative existing = rep(2L, "Nathan", "Mount", true);
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndIdNot("Nick", "Prodzenko", 2L))
                .thenReturn(true);

        assertThatThrownBy(() -> service.update(
                2L, new SalesRepUpdateRequest("Nick", "Prodzenko", true), CALLER_ID))
                .isInstanceOf(DuplicateSalesRepException.class);

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update throws EntityNotFoundException for a missing id")
    void update_missingId_throwsNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(
                999L, new SalesRepUpdateRequest("A", "B", true), CALLER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── delete ────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete throws when an offer references the rep and never deletes")
    void delete_throwsWhenOfferReferencesRep() {
        SalesRepresentative existing = rep(1L, "Julie", "Spellman", true);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.countOffersReferencing(1L)).thenReturn(3L);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(SalesRepHasOffersException.class)
                .hasMessageContaining("Cannot be Deleted");

        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("delete succeeds when no offer references the rep")
    void delete_succeedsWhenNoOffers() {
        SalesRepresentative existing = rep(1L, "Julie", "Spellman", true);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.countOffersReferencing(1L)).thenReturn(0L);

        service.delete(1L);

        verify(repository).delete(existing);
    }

    @Test
    @DisplayName("delete throws EntityNotFoundException for a missing id and never checks offers")
    void delete_missingId_throwsNotFound() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(42L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("42");

        verify(repository, never()).countOffersReferencing(anyLong());
        verify(repository, never()).delete(any());
    }

    // ── helper ────────────────────────────────────────────────────────

    private static SalesRepresentative rep(Long id, String first, String last, boolean active) {
        SalesRepresentative r = new SalesRepresentative();
        r.setId(id);
        r.setFirstName(first);
        r.setLastName(last);
        r.setActive(active);
        return r;
    }
}
