package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PurchaseOrderValidator {

    private final WeekRepository weekRepo;
    private final BuyerCodeRepository buyerCodeRepo;
    private final PurchaseOrderRepository purchaseOrderRepo;

    public PurchaseOrderValidator(WeekRepository weekRepo,
                                  BuyerCodeRepository buyerCodeRepo,
                                  PurchaseOrderRepository purchaseOrderRepo) {
        this.weekRepo = weekRepo;
        this.buyerCodeRepo = buyerCodeRepo;
        this.purchaseOrderRepo = purchaseOrderRepo;
    }

    public record WeekRange(Week from, Week to) {}

    public WeekRange resolveWeekRange(Long weekFromId, Long weekToId) {
        Week from = weekRepo.findById(weekFromId).orElseThrow(() ->
                new PurchaseOrderValidationException("INVALID_WEEK_RANGE",
                        "week_from id not found: " + weekFromId, List.of()));
        Week to = weekRepo.findById(weekToId).orElseThrow(() ->
                new PurchaseOrderValidationException("INVALID_WEEK_RANGE",
                        "week_to id not found: " + weekToId, List.of()));
        if (from.getWeekId() > to.getWeekId()) {
            throw new PurchaseOrderValidationException("INVALID_WEEK_RANGE",
                    "week_from must be <= week_to (got "
                            + from.getWeekId() + " > " + to.getWeekId() + ")",
                    List.of());
        }
        return new WeekRange(from, to);
    }

    public void requireBuyerCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) return;
        Set<String> found = new HashSet<>(buyerCodeRepo.findCodesIn(codes));
        List<String> missing = codes.stream().distinct()
                .filter(c -> !found.contains(c))
                .toList();
        if (!missing.isEmpty()) {
            throw new PurchaseOrderValidationException("MISSING_BUYER_CODE",
                    "Unknown buyer codes referenced: " + String.join(", ", missing),
                    missing);
        }
    }

    /**
     * VAL_WeekRange_PO (gap 0.1) — reject a candidate week range that overlaps
     * ANY existing PO's range. Scope is GLOBAL (no buyer/product/grade
     * filter); see {@link PurchaseOrderRepository#findOverlappingWeekRange} for
     * the rationale (exact legacy parity + 4C floor-collision prevention).
     *
     * <p>Call AFTER {@link #resolveWeekRange} on both create and update. On
     * update, pass the edited PO's id as {@code excludePoId} so it is not
     * flagged as overlapping itself; pass {@code null} on create.
     *
     * @throws PurchaseOrderValidationException with code
     *         {@code OVERLAPPING_WEEK_RANGE} naming the first conflicting PO
     *         and its weeks.
     */
    public void requireNonOverlappingWeekRange(WeekRange range, Long excludePoId) {
        // Compare by the business weekId (chronological), NOT the surrogate
        // week id — see PurchaseOrderRepository#findOverlappingWeekRange.
        List<PurchaseOrder> overlaps = purchaseOrderRepo.findOverlappingWeekRange(
                range.from().getWeekId(), range.to().getWeekId(), excludePoId);
        if (overlaps.isEmpty()) {
            return;
        }
        PurchaseOrder conflict = overlaps.get(0);
        String candidate = range.from().getWeekDisplay() + " - " + range.to().getWeekDisplay();
        String existing = conflict.getWeekFrom().getWeekDisplay() + " - "
                + conflict.getWeekTo().getWeekDisplay();
        throw new PurchaseOrderValidationException("OVERLAPPING_WEEK_RANGE",
                "Week range " + candidate + " overlaps existing purchase order "
                        + conflict.getId() + " (" + existing + "). A given week may be "
                        + "covered by at most one purchase order.",
                List.of(String.valueOf(conflict.getId())));
    }
}
