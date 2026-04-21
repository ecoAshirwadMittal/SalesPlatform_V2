package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.BidRoundSelectionFilterRequest;
import com.ecoatm.salesplatform.dto.BidRoundSelectionFilterResponse;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.BidRoundSelectionFilter;
import com.ecoatm.salesplatform.repository.auctions.BidRoundSelectionFilterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Round-qualification criteria service. Ports Mendix
 * {@code DS_BidRoundSelectionFilter_Round2} / {@code _Round3} (reads)
 * and the implicit "save" action attached to the Round 2/3 criteria
 * DataView (writes).
 *
 * <p>Round is validated against {@code {2, 3}} — matches the
 * {@code chk_brsf_round} CHECK constraint in V59. Any other value
 * throws {@link IllegalArgumentException} (→ 400) before hitting the
 * repository, to avoid a misleading "row not found" from the database.
 */
@Service
public class BidRoundSelectionFilterService {

    private final BidRoundSelectionFilterRepository repository;

    public BidRoundSelectionFilterService(BidRoundSelectionFilterRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public BidRoundSelectionFilterResponse get(int round) {
        validateRound(round);
        BidRoundSelectionFilter entity = repository.findByRound(round)
                .orElseThrow(() -> new EntityNotFoundException("BidRoundSelectionFilter", round));
        return BidRoundSelectionFilterResponse.from(entity);
    }

    @Transactional
    public BidRoundSelectionFilterResponse update(int round, BidRoundSelectionFilterRequest req) {
        validateRound(round);
        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        BidRoundSelectionFilter entity = repository.findByRound(round)
                .orElseThrow(() -> new EntityNotFoundException("BidRoundSelectionFilter", round));

        entity.setTargetPercent(req.targetPercent());
        entity.setTargetValue(req.targetValue());
        entity.setTotalValueFloor(req.totalValueFloor());
        entity.setMergedGrade1(req.mergedGrade1());
        entity.setMergedGrade2(req.mergedGrade2());
        entity.setMergedGrade3(req.mergedGrade3());
        // Boolean fields default to false when the caller omits them — the
        // DB columns are NOT NULL, so a null here would violate the schema.
        entity.setStbAllowAllBuyersOverride(
                req.stbAllowAllBuyersOverride() != null ? req.stbAllowAllBuyersOverride() : Boolean.FALSE);
        entity.setStbIncludeAllInventory(
                req.stbIncludeAllInventory() != null ? req.stbIncludeAllInventory() : Boolean.FALSE);
        if (req.regularBuyerQualification() != null) {
            entity.setRegularBuyerQualification(req.regularBuyerQualification());
        }
        if (req.regularBuyerInventoryOptions() != null) {
            entity.setRegularBuyerInventoryOptions(req.regularBuyerInventoryOptions());
        }
        entity.setChangedDate(Instant.now());

        BidRoundSelectionFilter saved = repository.save(entity);
        return BidRoundSelectionFilterResponse.from(saved);
    }

    private static void validateRound(int round) {
        if (round != 2 && round != 3) {
            throw new IllegalArgumentException(
                    "round must be 2 or 3 (was " + round + ")");
        }
    }
}
