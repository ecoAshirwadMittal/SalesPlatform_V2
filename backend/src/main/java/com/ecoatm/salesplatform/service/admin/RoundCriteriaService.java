package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.RoundCriteriaResponse;
import com.ecoatm.salesplatform.dto.RoundCriteriaUpdateRequest;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.BidRoundSelectionFilter;
import com.ecoatm.salesplatform.repository.auctions.BidRoundSelectionFilterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Lane 4 admin R2-criteria service. Backs
 * {@code GET/PUT /api/v1/admin/round-criteria/{round}} with a deliberately
 * narrow surface — only the three settings the QA POM
 * {@code ACC_RoundTwoCriteriaPage.selectRegularBuyerSettings} drives are
 * read or written. The broader admin surface (target_percent, merged_grade*,
 * etc.) lives at {@code /api/v1/admin/auctions/round-filters/{round}}
 * (Phase D).
 *
 * <p><b>Missing-row behavior:</b>
 * <ul>
 *   <li>{@link #get(int)} throws {@link EntityNotFoundException} (→ 404)
 *       when no row exists for the round. The admin page falls back to
 *       defaults locally; this keeps the API honest about persisted state
 *       rather than silently returning defaults that look saved.</li>
 *   <li>{@link #upsert(int, RoundCriteriaUpdateRequest)} creates the row
 *       when missing (true upsert), preserving the entity defaults for
 *       fields not exposed by the Lane-4 request shape.</li>
 * </ul>
 *
 * <p>Round is validated against {@code {2, 3}} — the {@code chk_brsf_round}
 * CHECK constraint in V59 only accepts those two. We surface a 400 from
 * the service layer to avoid a misleading constraint-violation error
 * bubbling out of JPA.
 */
@Service
public class RoundCriteriaService {

    private final BidRoundSelectionFilterRepository repository;

    public RoundCriteriaService(BidRoundSelectionFilterRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public RoundCriteriaResponse get(int round) {
        validateRound(round);
        BidRoundSelectionFilter entity = repository.findByRound(round)
                .orElseThrow(() -> new EntityNotFoundException(
                        "RoundCriteria not found for round=" + round));
        return RoundCriteriaResponse.from(entity);
    }

    @Transactional
    public RoundCriteriaResponse upsert(int round, RoundCriteriaUpdateRequest req) {
        validateRound(round);
        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        // Validate enum strings up-front so we never write a partially-applied
        // row when only one side of the request is malformed.
        var qualification = req.toQualificationEnum();
        var inventory = req.toInventoryEnum();

        BidRoundSelectionFilter entity = repository.findByRound(round)
                .orElseGet(() -> newRowForRound(round));

        entity.setRegularBuyerQualification(qualification);
        entity.setRegularBuyerInventoryOptions(inventory);
        entity.setStbAllowAllBuyersOverride(req.stbAllowAllBuyersOverrideOrDefault());

        Instant now = Instant.now();
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(now);
        }
        entity.setChangedDate(now);

        BidRoundSelectionFilter saved = repository.save(entity);
        return RoundCriteriaResponse.from(saved);
    }

    private static BidRoundSelectionFilter newRowForRound(int round) {
        BidRoundSelectionFilter entity = new BidRoundSelectionFilter();
        entity.setRound(round);
        // Other writable fields (target_percent, merged_grade*, stb_include_all_inventory)
        // intentionally left at the JPA-side defaults so the row passes the
        // schema's NOT NULL constraints without requiring callers to supply them.
        return entity;
    }

    private static void validateRound(int round) {
        if (round != 2 && round != 3) {
            throw new IllegalArgumentException(
                    "round must be 2 or 3 (was " + round + ")");
        }
    }
}
