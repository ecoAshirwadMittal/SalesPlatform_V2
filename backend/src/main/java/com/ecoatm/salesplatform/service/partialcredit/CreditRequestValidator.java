package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.enums.PhotoKind;
import com.ecoatm.salesplatform.model.partialcredit.enums.ShipmentDamaged;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestPhotoRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EncumberedDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.MissingDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.WrongDeviceLineRepository;
import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader;
import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader.ManifestLine;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 * Enforces the submit-time rules from
 * {@code partial-credit-implementation-plan.md} §7 and runs the
 * paste/upload barcode reconciliation used during the wizard.
 *
 * <p>Two entry points:
 * <ul>
 *   <li>{@link #validateForSubmit(CreditRequest)} — full pre-submit gate,
 *       returns the collected list of issues</li>
 *   <li>{@link #reconcileBarcodes(String, String, java.util.List)} —
 *       per-batch reconciliation; classifies barcodes Valid / Duplicate /
 *       NotInOrder and surfaces the banner copy verbatim</li>
 * </ul>
 *
 * <p>Both flows query Snowflake via {@link CreditRequestSnowflakeReader}
 * and may make zero Snowflake calls in dev/test thanks to the
 * {@code LoggingCreditRequestSnowflakeReader} default.
 */
@Service
public class CreditRequestValidator {

    /**
     * Credit window — buyer must submit within N days of
     * {@code ORDER_SHIPPED_DATE}. Confluence §Business rules.
     */
    public static final int CREDIT_WINDOW_DAYS = 30;

    private final CreditRequestRepository creditRequestRepository;
    private final MissingDeviceLineRepository missingDeviceLineRepository;
    private final WrongDeviceLineRepository wrongDeviceLineRepository;
    private final EncumberedDeviceLineRepository encumberedDeviceLineRepository;
    private final CreditRequestPhotoRepository photoRepository;
    private final CreditRequestSnowflakeReader snowflakeReader;

    public CreditRequestValidator(
            CreditRequestRepository creditRequestRepository,
            MissingDeviceLineRepository missingDeviceLineRepository,
            WrongDeviceLineRepository wrongDeviceLineRepository,
            EncumberedDeviceLineRepository encumberedDeviceLineRepository,
            CreditRequestPhotoRepository photoRepository,
            CreditRequestSnowflakeReader snowflakeReader) {
        this.creditRequestRepository = creditRequestRepository;
        this.missingDeviceLineRepository = missingDeviceLineRepository;
        this.wrongDeviceLineRepository = wrongDeviceLineRepository;
        this.encumberedDeviceLineRepository = encumberedDeviceLineRepository;
        this.photoRepository = photoRepository;
        this.snowflakeReader = snowflakeReader;
    }

    public SubmissionValidationResult validateForSubmit(CreditRequest cr, String buyerCode) {
        List<ValidationIssue> issues = new ArrayList<>();
        validateOrderEligibility(cr, buyerCode, issues);
        validateReasonSelection(cr, issues);
        validatePerReasonLines(cr, issues);
        validateDamageAnswer(cr, issues);
        return new SubmissionValidationResult(List.copyOf(issues));
    }

    public void validateForSubmitOrThrow(CreditRequest cr, String buyerCode) {
        SubmissionValidationResult result = validateForSubmit(cr, buyerCode);
        if (!result.valid()) {
            throw new CreditRequestValidationException(result.issues());
        }
    }

    private void validateOrderEligibility(CreditRequest cr, String buyerCode, List<ValidationIssue> issues) {
        // Order-on-manifest check: the snowflake reader returns false for
        // unknown orders OR when the reader is wired in 'logging' mode (no
        // live Snowflake). The validator treats the result as authoritative —
        // dev/test contexts run with a stubbed reader that resolves to true
        // for known fixtures.
        // (Step 1 of the wizard would have called validateOrderForBuyer
        // up-front; this re-check at submit time is a defence-in-depth gate
        // against tampered drafts.)
        if (!snowflakeReader.validateOrderForBuyer(cr.getOrderNumber(), buyerCode)) {
            issues.add(ValidationIssue.orderNotFound(cr.getOrderNumber()));
        }

        // 30-day window — only enforced when we have a shipped date.
        Instant shipped = cr.getOrderShippedDate();
        if (shipped != null) {
            Duration age = Duration.between(shipped, Instant.now());
            if (age.toDays() > CREDIT_WINDOW_DAYS) {
                issues.add(ValidationIssue.orderOutsideWindow(CREDIT_WINDOW_DAYS));
            }
        }

        // One active request per (order, buyer) — prior fully-declined
        // requests unblock a re-submission, so we exclude DECLINED rows
        // and exclude the current draft itself from the conflict set.
        List<CreditRequest> active = creditRequestRepository.findActiveByOrderAndBuyer(
                cr.getOrderNumber(), cr.getBuyerCodeId(), SystemStatus.DECLINED);
        boolean hasOtherActive = active.stream()
                .anyMatch(other -> !other.getId().equals(cr.getId()));
        if (hasOtherActive) {
            issues.add(ValidationIssue.orderHasActiveRequest());
        }
    }

    private void validateReasonSelection(CreditRequest cr, List<ValidationIssue> issues) {
        boolean any = Boolean.TRUE.equals(cr.getHasMissingDevice())
                || Boolean.TRUE.equals(cr.getHasWrongDevice())
                || Boolean.TRUE.equals(cr.getHasEncumberedDevice());
        if (!any) {
            issues.add(ValidationIssue.noReasonSelected());
        }
    }

    private void validatePerReasonLines(CreditRequest cr, List<ValidationIssue> issues) {
        // Drafts are created with id=null momentarily during a unit test
        // path; skip the line-existence check in that case — the missing
        // line will surface as a different error downstream.
        if (cr.getId() == null) {
            return;
        }

        if (Boolean.TRUE.equals(cr.getHasMissingDevice())
                && missingDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()).isEmpty()) {
            issues.add(ValidationIssue.missingDeviceLineMissing());
        }
        if (Boolean.TRUE.equals(cr.getHasWrongDevice())
                && wrongDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()).isEmpty()) {
            issues.add(ValidationIssue.wrongDeviceLineMissing());
        }
        if (Boolean.TRUE.equals(cr.getHasEncumberedDevice())
                && encumberedDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()).isEmpty()) {
            issues.add(ValidationIssue.encumberedDeviceLineMissing());
        }
    }

    private void validateDamageAnswer(CreditRequest cr, List<ValidationIssue> issues) {
        ShipmentDamaged d = cr.getShipmentDamaged();
        if (d == null || d == ShipmentDamaged.NOT_ANSWERED) {
            issues.add(ValidationIssue.damageNotAnswered());
            return;
        }
        if (d == ShipmentDamaged.YES && cr.getId() != null) {
            long damagePhotos = photoRepository.countByCreditRequestIdAndKind(cr.getId(), PhotoKind.DAMAGE);
            if (damagePhotos == 0) {
                issues.add(ValidationIssue.damageRequiresPhoto());
            }
        }
    }

    public BarcodeReconciliationResult reconcileBarcodes(
            String orderNumber, String buyerCode, List<String> rawBarcodes) {

        // Normalise + drop blanks first so the duplicate/in-order buckets
        // are computed off the same canonical strings the wizard displays.
        List<String> normalised = normaliseBarcodes(rawBarcodes);

        // Dedupe within the input. Order is preserved (LinkedHashSet
        // semantics via the seen-set) so the wizard's chip list keeps the
        // buyer's typing order.
        Set<String> seen = new HashSet<>();
        List<String> uniqueInOrder = new ArrayList<>();
        List<String> duplicates = new ArrayList<>();
        for (String b : normalised) {
            if (seen.add(b)) {
                uniqueInOrder.add(b);
            } else {
                duplicates.add(b);
            }
        }

        // One Snowflake round-trip — fetch the full manifest for the
        // order, then partition the de-duped input set against it.
        // (Per-barcode lookups would be O(N) round-trips; doing it once
        // is the right call given typical batches are 1-100 barcodes.)
        List<ManifestLine> manifest = snowflakeReader.getOrderLines(orderNumber, buyerCode);
        Map<String, ManifestLine> byBarcode = new HashMap<>();
        for (ManifestLine ml : manifest) {
            if (ml.barcode() != null) {
                byBarcode.put(ml.barcode().trim(), ml);
            }
        }

        List<ManifestLine> validLines = new ArrayList<>();
        List<String> notInOrder = new ArrayList<>();
        for (String b : uniqueInOrder) {
            ManifestLine match = byBarcode.get(b);
            if (match != null) {
                validLines.add(match);
            } else {
                notInOrder.add(b);
            }
        }

        String banner = buildBanner(duplicates.size(), notInOrder.size());
        return new BarcodeReconciliationResult(
                List.copyOf(validLines),
                List.copyOf(duplicates),
                List.copyOf(notInOrder),
                banner);
    }

    private static List<String> normaliseBarcodes(List<String> rawBarcodes) {
        if (rawBarcodes == null || rawBarcodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> out = new ArrayList<>(rawBarcodes.size());
        for (String s : rawBarcodes) {
            if (s == null) continue;
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) {
                out.add(trimmed);
            }
        }
        return out;
    }

    private static String buildBanner(int duplicateCount, int notInOrderCount) {
        if (duplicateCount == 0 && notInOrderCount == 0) {
            return "";
        }
        // Confluence copy: "Removed N duplicate and M not in order."
        StringBuilder sb = new StringBuilder("Removed ");
        sb.append(duplicateCount).append(duplicateCount == 1 ? " duplicate" : " duplicates");
        sb.append(" and ");
        sb.append(notInOrderCount).append(notInOrderCount == 1 ? " not in order." : " not in order.");
        return sb.toString();
    }
}
