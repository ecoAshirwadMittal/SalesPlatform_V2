package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PurchaseOrderValidator {

    private final WeekRepository weekRepo;
    private final BuyerCodeRepository buyerCodeRepo;

    public PurchaseOrderValidator(WeekRepository weekRepo,
                                  BuyerCodeRepository buyerCodeRepo) {
        this.weekRepo = weekRepo;
        this.buyerCodeRepo = buyerCodeRepo;
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
}
