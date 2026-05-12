package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.dto.partialcredit.StatusConfigPatch;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SPKB-3664: cosmetic-field editor for the five seeded rows on
 * {@code partial_credit.credit_request_statuses}.
 *
 * <p>Editable fields: {@code internal_status_text}, {@code external_status_text},
 * {@code color_hex}, {@code sort_order}, {@code show_in_user_counters}.
 *
 * <p>Immutable: {@code system_status} (enum key consumed by both buyer
 * landing rendering and admin business logic), {@code id}, {@code is_default}
 * (lifecycle-managed), {@code status_grouped_to} (config-time).
 *
 * <p>Why no audit-stamp: the V89 seed schema of {@link CreditRequestStatus}
 * does not expose {@code changed_by_id}/{@code changed_date} columns — only
 * {@code credit_requests} carries those. A V90 follow-up could add them
 * if SPKB-3664 ever needs a per-status audit trail.
 */
@Service
public class StatusConfigService {

    /** Six-hex color spec (matches the V89 CHECK constraint pattern). */
    private static final Pattern COLOR_HEX_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    /** Mirrors the column constraint (VARCHAR(100)). */
    private static final int MAX_STATUS_TEXT_LENGTH = 100;

    private final CreditRequestStatusRepository repository;

    public StatusConfigService(CreditRequestStatusRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns all five seeded rows ordered by {@code sort_order} ascending.
     * Used to render the admin grid in the same order the buyer landing
     * counter strip uses.
     */
    @Transactional(readOnly = true)
    public List<CreditRequestStatus> listAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(
                        CreditRequestStatus::getSortOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    /**
     * Applies {@code patch} to the row identified by {@code id}, leaving
     * fields with a {@code null} patch value untouched. Validates color hex
     * + text length before persisting so the DB CHECK constraint never
     * surfaces as a 500.
     *
     * <p>{@code actorUserId} is accepted today for forward compatibility with
     * a V90 audit-stamp column; it is intentionally unused because the
     * entity has no audit fields yet (see class javadoc).
     *
     * @throws EntityNotFoundException if {@code id} doesn't resolve to one
     *     of the five seeded rows.
     * @throws IllegalArgumentException on invalid color hex or oversize text.
     */
    @Transactional
    public CreditRequestStatus update(Long id, StatusConfigPatch patch, Long actorUserId) {
        CreditRequestStatus row = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("credit_request_status", id));

        validate(patch);

        if (patch.internalStatusText() != null) {
            row.setInternalStatusText(patch.internalStatusText());
        }
        if (patch.externalStatusText() != null) {
            row.setExternalStatusText(patch.externalStatusText());
        }
        if (patch.colorHex() != null) {
            row.setColorHex(patch.colorHex());
        }
        if (patch.sortOrder() != null) {
            row.setSortOrder(patch.sortOrder());
        }
        if (patch.showInUserCounters() != null) {
            row.setShowInUserCounters(patch.showInUserCounters());
        }

        // Unused today (see class javadoc on audit columns). Kept in the
        // signature so callers don't have to change when V90 lands.
        if (actorUserId != null) {
            // no-op placeholder
        }

        return repository.save(row);
    }

    private static void validate(StatusConfigPatch patch) {
        if (patch.colorHex() != null && !COLOR_HEX_PATTERN.matcher(patch.colorHex()).matches()) {
            throw new IllegalArgumentException(
                    "colorHex must match #RRGGBB (six hex digits), got: " + patch.colorHex());
        }
        if (patch.internalStatusText() != null
                && patch.internalStatusText().length() > MAX_STATUS_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                    "internalStatusText exceeds " + MAX_STATUS_TEXT_LENGTH + " characters");
        }
        if (patch.externalStatusText() != null
                && patch.externalStatusText().length() > MAX_STATUS_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                    "externalStatusText exceeds " + MAX_STATUS_TEXT_LENGTH + " characters");
        }
    }
}
