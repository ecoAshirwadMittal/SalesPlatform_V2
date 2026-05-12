package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.ActionRecommendation;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * Pure-logic action recommender for Wrong-Device lines (SPKB-3661).
 *
 * <p>Drives the <em>default</em> selection in the admin Action dropdown — the
 * reviewer can always override. Output is binary {@link ActionRecommendation}:
 * {@code ACCEPT} or {@code DECLINE}.
 *
 * <p>This service is intentionally a pure function: no DB writes, no
 * transactional context, no fetched dependencies. The caller computes the
 * received device's latest submitted bid (via {@code MaxSubmittedBidLookup}
 * from Sprint 3 Chunk 1) and passes it in as {@code receivedLatestPrice}.
 * Decoupling the lookup keeps this class trivially unit-testable and side-
 * effect-free.
 *
 * <p>Decision tree (§9 of the Mendix-targeted impl plan):
 * <ol>
 *   <li>Expected brand NOT in {Apple, Samsung, Motorola} -> {@code DECLINE}
 *   <li>Expected device is "no power" -> {@code DECLINE}
 *   <li>Diff is capacity-or-model (same allowed brand) -> {@code ACCEPT}
 *   <li>Diff is grade-or-color -> {@code DECLINE}
 *   <li>Received latest price &gt; expected paid -> {@code DECLINE}
 *   <li>Default -> {@code DECLINE} (safe fallback for unclassified diffs)
 * </ol>
 */
@Service
public class ActionRecommendationService {

    /**
     * Whitelisted brands for the Accept-by-default branch. Comparison is
     * case-insensitive — store the canonical lowercase forms so the hot
     * path is a single {@code contains} call on a normalised key.
     */
    private static final Set<String> ACCEPT_BRANDS_LOWER =
            Set.of("apple", "samsung", "motorola");

    /**
     * Substrings that flag an "Expected: device with no power" line. Over-
     * matching here is safer than under-matching — false positives only
     * change the default from ACCEPT to DECLINE, which the reviewer can
     * override.
     */
    private static final String[] NO_POWER_TOKENS = {
        "no power", "nopower", "no_power"
    };

    /**
     * Returns the recommended action for a Wrong-Device line.
     *
     * @param line the wrong-device line — Expected* fields are read; the
     *             diff between Expected* and Actual* drives classification
     * @param receivedLatestPrice the most-recent submitted bid for the
     *             {@code (actualEcoatmCode, actualGrade)} tuple in the
     *             line's expected week. May be {@code null} when no bid
     *             data exists — caller does not have to pre-resolve this.
     * @return {@code ACCEPT} or {@code DECLINE}; never {@code null}
     */
    public ActionRecommendation recommend(WrongDeviceLine line, BigDecimal receivedLatestPrice) {
        Objects.requireNonNull(line, "line must not be null");

        // 1. Null-safety on expected brand — without it the allowlist
        //    check below would NPE; treat missing brand as unclassifiable.
        String expectedBrand = line.getExpectedBrand();
        if (expectedBrand == null) {
            return ActionRecommendation.DECLINE;
        }

        // 2. Brand allowlist gate.
        if (!isBrandAllowed(expectedBrand)) {
            return ActionRecommendation.DECLINE;
        }

        // 3. "No power" override — buyers should not get auto-Accept on
        //    a device that came in dead, even if the rest of the line
        //    looks like a clean capacity diff.
        if (isNoPower(line)) {
            return ActionRecommendation.DECLINE;
        }

        // 4-5. Classify the diff and route on the kind.
        DiffKind diff = classifyDiff(line);
        if (diff == DiffKind.CAPACITY_OR_MODEL) {
            return ActionRecommendation.ACCEPT;
        }
        if (diff == DiffKind.GRADE_OR_COLOR) {
            return ActionRecommendation.DECLINE;
        }

        // 6. Price floor — if the buyer received something worth MORE
        //    than they paid, decline (no credit owed).
        BigDecimal expectedAmountPaid = line.getExpectedAmountPaid();
        if (receivedLatestPrice != null
                && expectedAmountPaid != null
                && receivedLatestPrice.compareTo(expectedAmountPaid) > 0) {
            return ActionRecommendation.DECLINE;
        }

        // 7. Safe default for any unclassified diff.
        return ActionRecommendation.DECLINE;
    }

    /**
     * @return {@code true} when {@code brand} matches any entry in
     *         {@link #ACCEPT_BRANDS_LOWER} (case-insensitive)
     */
    private boolean isBrandAllowed(String brand) {
        return ACCEPT_BRANDS_LOWER.contains(brand.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns {@code true} when {@code expectedModel} or
     * {@code expectedDeviceDescription} contains any "no power" variant.
     * Case-insensitive on both inputs.
     */
    private boolean isNoPower(WrongDeviceLine line) {
        return containsNoPowerToken(line.getExpectedModel())
                || containsNoPowerToken(line.getExpectedDeviceDescription());
    }

    private boolean containsNoPowerToken(String haystack) {
        if (haystack == null) {
            return false;
        }
        String normalized = haystack.toLowerCase(Locale.ROOT);
        for (String token : NO_POWER_TOKENS) {
            if (normalized.contains(token)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Classifies the Expected vs. Actual diff into one of three buckets.
     *
     * <p>Grade differences are checked first because they win in the
     * decision tree even when the model also differs — a buyer who
     * received the right model but the wrong grade should NOT get a
     * capacity-or-model Accept.
     *
     * <p>Capacity diffs are encoded inside the model string by Mendix
     * (e.g. {@code "iPhone 12 128GB"} vs. {@code "iPhone 12 256GB"}),
     * so a model-string mismatch within the same brand collapses to
     * {@link DiffKind#CAPACITY_OR_MODEL}.
     */
    private DiffKind classifyDiff(WrongDeviceLine line) {
        if (notEqualNullSafe(line.getExpectedGrade(), line.getActualGrade())) {
            return DiffKind.GRADE_OR_COLOR;
        }
        if (sameBrand(line) && notEqualNullSafe(line.getExpectedModel(), line.getActualModel())) {
            return DiffKind.CAPACITY_OR_MODEL;
        }
        return DiffKind.UNCLASSIFIED;
    }

    private boolean sameBrand(WrongDeviceLine line) {
        // expectedBrand is guaranteed non-null here — the early null check
        // in recommend() short-circuits the DECLINE path before classifyDiff
        // runs. Only actualBrand can be null.
        String actual = line.getActualBrand();
        if (actual == null) {
            return false;
        }
        return line.getExpectedBrand().equalsIgnoreCase(actual);
    }

    /**
     * Two strings differ when both are non-null and not equal. Null on
     * either side does not trip a diff — Sprint 2 may have left actuals
     * unresolved, and we'd rather fall through to {@code UNCLASSIFIED}
     * than misroute a missing-data line into a Decline branch.
     */
    private boolean notEqualNullSafe(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return !a.equals(b);
    }

    /**
     * Internal classification buckets for the Expected vs. Actual diff.
     * Package-private for test visibility; not part of the public API.
     */
    enum DiffKind {
        CAPACITY_OR_MODEL,
        GRADE_OR_COLOR,
        UNCLASSIFIED
    }
}
