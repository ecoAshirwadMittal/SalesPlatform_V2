package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.ShipStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.ShipmentDamaged;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EncumberedDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.MissingDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.WrongDeviceLineRepository;
import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader;
import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader.ManifestLine;
import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader.OrderHeader;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Buyer-side orchestration for the partial-credit wizard. Drafts, updates,
 * submits, and reads requests on behalf of the calling user.
 *
 * <p>Scope-checks are inlined (not extracted to a shared helper) to match
 * the deliberate IDOR pattern in {@code BidderDashboardService} — see that
 * class's {@code OWNERSHIP_SQL} comment. Admins bypass the check.
 */
@Service
public class CreditRequestService {

    /**
     * Resolves whether a user has access to a buyer code via the
     * {@code user_mgmt.user_buyers} → {@code buyer_mgmt.buyer_code_buyers}
     * join. Same shape as {@code BidderDashboardService.OWNERSHIP_SQL}.
     */
    private static final String OWNERSHIP_SQL = """
            SELECT COUNT(*) FROM user_mgmt.user_buyers ub
            JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_id = ub.buyer_id
            WHERE ub.user_id = ? AND bcb.buyer_code_id = ?
            """;

    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestStatusRepository statusRepository;
    private final MissingDeviceLineRepository missingDeviceLineRepository;
    private final WrongDeviceLineRepository wrongDeviceLineRepository;
    private final EncumberedDeviceLineRepository encumberedDeviceLineRepository;
    private final BuyerCodeRepository buyerCodeRepository;
    private final CreditRequestSnowflakeReader snowflakeReader;
    private final CreditRequestValidator validator;
    private final JdbcTemplate jdbcTemplate;

    public CreditRequestService(
            CreditRequestRepository creditRequestRepository,
            CreditRequestStatusRepository statusRepository,
            MissingDeviceLineRepository missingDeviceLineRepository,
            WrongDeviceLineRepository wrongDeviceLineRepository,
            EncumberedDeviceLineRepository encumberedDeviceLineRepository,
            BuyerCodeRepository buyerCodeRepository,
            CreditRequestSnowflakeReader snowflakeReader,
            CreditRequestValidator validator,
            JdbcTemplate jdbcTemplate) {
        this.creditRequestRepository = creditRequestRepository;
        this.statusRepository = statusRepository;
        this.missingDeviceLineRepository = missingDeviceLineRepository;
        this.wrongDeviceLineRepository = wrongDeviceLineRepository;
        this.encumberedDeviceLineRepository = encumberedDeviceLineRepository;
        this.buyerCodeRepository = buyerCodeRepository;
        this.snowflakeReader = snowflakeReader;
        this.validator = validator;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public CreditRequest createDraft(String orderNumber, Long buyerCodeId, Long userId, boolean isAdmin) {
        ensureBuyerCodeOwnership(userId, buyerCodeId, isAdmin);
        BuyerCode buyerCode = buyerCodeRepository.findById(buyerCodeId)
                .orElseThrow(() -> new EntityNotFoundException("BuyerCode " + buyerCodeId));

        CreditRequestStatus draftStatus = statusRepository.findBySystemStatus(SystemStatus.DRAFT)
                .orElseThrow(() -> new IllegalStateException("DRAFT status row missing — V89 seed not applied"));

        CreditRequest cr = new CreditRequest();
        cr.setRequestNumber(generateRequestNumber());
        cr.setRequestDate(Instant.now());
        cr.setStatusId(draftStatus.getId());
        cr.setOrderNumber(orderNumber);
        cr.setBuyerCodeId(buyerCodeId);
        cr.setShipmentDamaged(ShipmentDamaged.NOT_ANSWERED);
        cr.setCreatedById(userId);
        cr.setChangedById(userId);

        // Best-effort enrichment from Snowflake. If the reader is in
        // 'logging' mode the call returns empty and the row is created
        // without party_name/dates — the wizard renders fine and submit
        // re-fetches when the JDBC reader is wired in prod.
        Optional<OrderHeader> header = snowflakeReader.getOrderHeader(orderNumber, buyerCode.getCode());
        header.ifPresent(h -> {
            cr.setPartyName(h.partyName());
            cr.setOrderCreatedDate(h.orderCreatedDate());
            cr.setOrderShippedDate(h.orderShippedDate());
        });

        return creditRequestRepository.save(cr);
    }

    @Transactional
    public CreditRequest update(
            Long id, Long userId, boolean isAdmin,
            Boolean hasMissing, Boolean hasWrong, Boolean hasEncumbered,
            ShipmentDamaged shipmentDamaged) {
        CreditRequest cr = loadForUser(id, userId, isAdmin);
        ensureDraft(cr);
        if (hasMissing != null)     cr.setHasMissingDevice(hasMissing);
        if (hasWrong != null)       cr.setHasWrongDevice(hasWrong);
        if (hasEncumbered != null)  cr.setHasEncumberedDevice(hasEncumbered);
        if (shipmentDamaged != null) cr.setShipmentDamaged(shipmentDamaged);
        cr.setChangedDate(Instant.now());
        cr.setChangedById(userId);
        return creditRequestRepository.save(cr);
    }

    @Transactional
    public CreditRequest submit(Long id, Long userId, boolean isAdmin) {
        CreditRequest cr = loadForUser(id, userId, isAdmin);
        ensureDraft(cr);

        BuyerCode buyerCode = buyerCodeRepository.findById(cr.getBuyerCodeId())
                .orElseThrow(() -> new EntityNotFoundException("BuyerCode " + cr.getBuyerCodeId()));

        // Run the full submit-time rule set. Throws when any rule fails;
        // the controller advice layer maps it to HTTP 400 with the issue
        // list on the body.
        validator.validateForSubmitOrThrow(cr, buyerCode.getCode());

        // Denormalise manifest fields onto each line so the request is
        // self-contained post-submit (no Snowflake re-queries during
        // admin review). In 'logging' mode the manifest is empty, so the
        // line fields are left as the buyer entered them — that's fine
        // for dev/test.
        denormaliseLines(cr, buyerCode.getCode());

        // Flip status to PENDING_APPROVAL and stamp submission audit.
        CreditRequestStatus pending = statusRepository.findBySystemStatus(SystemStatus.PENDING_APPROVAL)
                .orElseThrow(() -> new IllegalStateException("PENDING_APPROVAL status row missing"));
        cr.setStatusId(pending.getId());
        cr.setSubmittedDate(Instant.now());
        cr.setSubmittedById(userId);
        cr.setChangedDate(Instant.now());
        cr.setChangedById(userId);
        return creditRequestRepository.save(cr);
    }

    @Transactional(readOnly = true)
    public CreditRequest getById(Long id, Long userId, boolean isAdmin) {
        return loadForUser(id, userId, isAdmin);
    }

    @Transactional(readOnly = true)
    public List<MissingDeviceLine> getMissingLines(Long id) {
        return missingDeviceLineRepository.findByCreditRequestIdOrderById(id);
    }

    @Transactional(readOnly = true)
    public List<WrongDeviceLine> getWrongLines(Long id) {
        return wrongDeviceLineRepository.findByCreditRequestIdOrderById(id);
    }

    @Transactional(readOnly = true)
    public List<EncumberedDeviceLine> getEncumberedLines(Long id) {
        return encumberedDeviceLineRepository.findByCreditRequestIdOrderById(id);
    }

    @Transactional
    public LineReplacementOutcome<MissingDeviceLine> replaceMissingLines(
            Long id, Long userId, boolean isAdmin, List<String> barcodes) {
        CreditRequest cr = loadForUser(id, userId, isAdmin);
        ensureDraft(cr);
        BuyerCode buyerCode = buyerCodeRepository.findById(cr.getBuyerCodeId())
                .orElseThrow(() -> new EntityNotFoundException("BuyerCode " + cr.getBuyerCodeId()));

        // Reconcile the pasted barcodes against the Snowflake manifest
        // before persisting so duplicates + non-manifest entries never
        // hit the line table. The banner is surfaced back to the wizard
        // verbatim (controller wraps this outcome in LineReplacementResponse).
        ReplacementPlan plan = planReplacement(cr, buyerCode.getCode(), barcodes);

        missingDeviceLineRepository.deleteAll(
                missingDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()));
        List<MissingDeviceLine> rows = new ArrayList<>();
        for (ManifestLine ml : plan.persistable()) {
            MissingDeviceLine row = new MissingDeviceLine();
            row.setCreditRequestId(cr.getId());
            row.setBarcodeSubmitted(ml.barcode());
            row.setCreatedById(userId);
            row.setChangedById(userId);
            rows.add(missingDeviceLineRepository.save(row));
        }
        return new LineReplacementOutcome<>(rows, plan.reconciliation());
    }

    @Transactional
    public LineReplacementOutcome<WrongDeviceLine> replaceWrongLines(
            Long id, Long userId, boolean isAdmin,
            List<com.ecoatm.salesplatform.dto.partialcredit.SetLinesRequest.WrongLineInput> wrongLines) {
        CreditRequest cr = loadForUser(id, userId, isAdmin);
        ensureDraft(cr);
        BuyerCode buyerCode = buyerCodeRepository.findById(cr.getBuyerCodeId())
                .orElseThrow(() -> new EntityNotFoundException("BuyerCode " + cr.getBuyerCodeId()));

        // Reconcile only the expected barcodes — the user-supplied
        // actualImeiOrModel hangs off each row and is preserved verbatim
        // for the surviving expectedBarcode entries.
        List<String> expectedBarcodes = new ArrayList<>();
        Map<String, String> actualByExpected = new LinkedHashMap<>();
        if (wrongLines != null) {
            for (var input : wrongLines) {
                if (input == null || input.expectedBarcode() == null || input.expectedBarcode().isBlank()) continue;
                String trimmedExpected = input.expectedBarcode().trim();
                String trimmedActual = input.actualImeiOrModel() == null ? null : input.actualImeiOrModel().trim();
                expectedBarcodes.add(trimmedExpected);
                // First-occurrence wins for actualImeiOrModel — duplicates
                // are dropped by reconciliation, so the surviving entry
                // keeps the buyer's first-typed answer.
                actualByExpected.putIfAbsent(trimmedExpected, trimmedActual);
            }
        }

        ReplacementPlan plan = planReplacement(cr, buyerCode.getCode(), expectedBarcodes);

        wrongDeviceLineRepository.deleteAll(
                wrongDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()));
        List<WrongDeviceLine> rows = new ArrayList<>();
        for (ManifestLine ml : plan.persistable()) {
            WrongDeviceLine row = new WrongDeviceLine();
            row.setCreditRequestId(cr.getId());
            row.setExpectedBarcode(ml.barcode());
            row.setActualImeiOrModel(actualByExpected.get(ml.barcode()));
            row.setCreatedById(userId);
            row.setChangedById(userId);
            rows.add(wrongDeviceLineRepository.save(row));
        }
        return new LineReplacementOutcome<>(rows, plan.reconciliation());
    }

    @Transactional
    public LineReplacementOutcome<EncumberedDeviceLine> replaceEncumberedLines(
            Long id, Long userId, boolean isAdmin, List<String> barcodes) {
        CreditRequest cr = loadForUser(id, userId, isAdmin);
        ensureDraft(cr);
        BuyerCode buyerCode = buyerCodeRepository.findById(cr.getBuyerCodeId())
                .orElseThrow(() -> new EntityNotFoundException("BuyerCode " + cr.getBuyerCodeId()));

        ReplacementPlan plan = planReplacement(cr, buyerCode.getCode(), barcodes);

        encumberedDeviceLineRepository.deleteAll(
                encumberedDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId()));
        List<EncumberedDeviceLine> rows = new ArrayList<>();
        for (ManifestLine ml : plan.persistable()) {
            EncumberedDeviceLine row = new EncumberedDeviceLine();
            row.setCreditRequestId(cr.getId());
            row.setBarcodeSubmitted(ml.barcode());
            row.setCreatedById(userId);
            row.setChangedById(userId);
            rows.add(encumberedDeviceLineRepository.save(row));
        }
        return new LineReplacementOutcome<>(rows, plan.reconciliation());
    }

    /**
     * Runs reconciliation against the Snowflake manifest and produces the
     * pair (lines-to-persist, reconciliation-result-for-wizard).
     *
     * <p>Two paths:
     * <ol>
     *   <li><b>Manifest present</b> — reconcile, persist only
     *       {@code validLines}, return the reconciliation verbatim so the
     *       wizard banner reflects true dedup + not-in-order counts.</li>
     *   <li><b>Manifest empty</b> (LOGGING reader OR unknown order in
     *       prod) — treat as "no Snowflake configured / no manifest to
     *       compare against": persist the deduped raw input as synthetic
     *       {@link ManifestLine} rows so the wizard stays usable in
     *       dev/test, and rebuild the reconciliation so the banner only
     *       reports actual duplicates (rather than flagging every input
     *       as "not in order").</li>
     * </ol>
     *
     * <p>The pre-check uses {@code snowflakeReader.getOrderLines}
     * directly (rather than parsing the reconciler output) so the two
     * modes stay unambiguous regardless of whether inputs happen to
     * match the manifest.
     */
    private ReplacementPlan planReplacement(
            CreditRequest cr, String buyerCode, List<String> rawBarcodes) {
        boolean manifestPopulated =
                !snowflakeReader.getOrderLines(cr.getOrderNumber(), buyerCode).isEmpty();

        if (manifestPopulated) {
            BarcodeReconciliationResult reconciliation =
                    validator.reconcileBarcodes(cr.getOrderNumber(), buyerCode, rawBarcodes);
            return new ReplacementPlan(reconciliation.validLines(), reconciliation);
        }

        java.util.Set<String> seen = new java.util.LinkedHashSet<>();
        List<ManifestLine> fallback = new ArrayList<>();
        List<String> duplicates = new ArrayList<>();
        if (rawBarcodes != null) {
            for (String b : rawBarcodes) {
                if (b == null) continue;
                String trimmed = b.trim();
                if (trimmed.isEmpty()) continue;
                if (seen.add(trimmed)) {
                    fallback.add(new ManifestLine(trimmed, null, null, null, null, null, null, null, null));
                } else {
                    duplicates.add(trimmed);
                }
            }
        }
        String banner = duplicates.isEmpty()
                ? ""
                : "Removed " + duplicates.size() + (duplicates.size() == 1 ? " duplicate" : " duplicates")
                        + " and 0 not in order.";
        BarcodeReconciliationResult fallbackReconciliation = new BarcodeReconciliationResult(
                List.copyOf(fallback), List.copyOf(duplicates), List.of(), banner);
        return new ReplacementPlan(fallback, fallbackReconciliation);
    }

    /**
     * Internal pair returned by {@link #planReplacement} — the manifest
     * lines to persist, plus the (possibly rewritten) reconciliation
     * result to ship back to the wizard.
     */
    private record ReplacementPlan(
            List<ManifestLine> persistable,
            BarcodeReconciliationResult reconciliation) {}

    /**
     * Returned by the three {@code replaceXxxLines} entry points so the
     * controller can hand back the persisted lines + the reconciliation
     * banner in a single response without re-running reconciliation.
     */
    public record LineReplacementOutcome<T>(
            List<T> persistedLines,
            BarcodeReconciliationResult reconciliation) {}

    @Transactional(readOnly = true)
    public Page<CreditRequest> listForBuyerCode(Long buyerCodeId, Long userId, boolean isAdmin, Pageable pageable) {
        ensureBuyerCodeOwnership(userId, buyerCodeId, isAdmin);
        return creditRequestRepository.findByBuyerCodeIdOrderByRequestDateDesc(buyerCodeId, pageable);
    }

    public Optional<CreditRequestStatus> findStatusRow(Long statusId) {
        return statusRepository.findById(statusId);
    }

    // ─── private ───────────────────────────────────────────────────────

    private CreditRequest loadForUser(Long id, Long userId, boolean isAdmin) {
        CreditRequest cr = creditRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CreditRequest " + id));
        ensureBuyerCodeOwnership(userId, cr.getBuyerCodeId(), isAdmin);
        return cr;
    }

    private void ensureBuyerCodeOwnership(Long userId, Long buyerCodeId, boolean isAdmin) {
        if (isAdmin) {
            return;
        }
        Long count = jdbcTemplate.queryForObject(OWNERSHIP_SQL, Long.class, userId, buyerCodeId);
        if (count == null || count == 0L) {
            throw new SecurityException(
                    "User " + userId + " does not own buyer_code_id=" + buyerCodeId);
        }
    }

    private void ensureDraft(CreditRequest cr) {
        CreditRequestStatus row = statusRepository.findById(cr.getStatusId())
                .orElseThrow(() -> new IllegalStateException("Status row missing for credit request " + cr.getId()));
        if (row.getSystemStatus() != SystemStatus.DRAFT) {
            throw new IllegalStateException(
                    "Credit request " + cr.getId() + " is not in DRAFT — current status: " + row.getSystemStatus());
        }
    }

    /**
     * Pulls the full order manifest once, then patches each line entity
     * with the matching manifest row's brand/model/grade/amount/etc. so
     * the credit request is self-contained after submit.
     */
    private void denormaliseLines(CreditRequest cr, String buyerCode) {
        List<ManifestLine> manifest = snowflakeReader.getOrderLines(cr.getOrderNumber(), buyerCode);
        if (manifest.isEmpty()) {
            return;
        }
        Map<String, ManifestLine> byBarcode = new HashMap<>();
        for (ManifestLine ml : manifest) {
            if (ml.barcode() != null) {
                byBarcode.put(ml.barcode(), ml);
            }
        }

        for (MissingDeviceLine line : missingDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId())) {
            ManifestLine match = byBarcode.get(line.getBarcodeSubmitted());
            if (match != null) {
                line.setBrand(match.deviceBrand());
                line.setModel(match.deviceModel());
                line.setGrade(match.grade());
                line.setBoxNumber(match.boxNumber());
                line.setAmountPaid(match.amount());
                line.setShipStatus(match.trackingNumber() != null && !match.trackingNumber().isBlank()
                        ? ShipStatus.SHIPPED
                        : ShipStatus.NOT_SHIPPED);
                missingDeviceLineRepository.save(line);
            }
        }

        for (WrongDeviceLine line : wrongDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId())) {
            ManifestLine match = byBarcode.get(line.getExpectedBarcode());
            if (match != null) {
                line.setExpectedBrand(match.deviceBrand());
                line.setExpectedModel(match.deviceModel());
                line.setExpectedGrade(match.grade());
                line.setExpectedBoxNumber(match.boxNumber());
                line.setExpectedAmountPaid(match.amount());
                line.setExpectedEcoatmCode(match.ecoatmCode());
                wrongDeviceLineRepository.save(line);
            }
        }

        for (EncumberedDeviceLine line : encumberedDeviceLineRepository.findByCreditRequestIdOrderById(cr.getId())) {
            ManifestLine match = byBarcode.get(line.getBarcodeSubmitted());
            if (match != null) {
                line.setBrand(match.deviceBrand());
                line.setModel(match.deviceModel());
                line.setGrade(match.grade());
                line.setBoxNumber(match.boxNumber());
                line.setAmountPaid(match.amount());
                encumberedDeviceLineRepository.save(line);
            }
        }
    }

    /**
     * Generates a stable, human-readable request number. Format is
     * {@code PCR-yyyyMMdd-XXXXXX} where the suffix is the current epoch
     * millis modulo 1M — sufficient for uniqueness during Sprint 2
     * testing; Sprint 4 can replace with a proper sequence + day-anchor.
     */
    private static String generateRequestNumber() {
        long suffix = System.currentTimeMillis() % 1_000_000L;
        java.time.LocalDate today = java.time.LocalDate.now();
        return String.format("PCR-%04d%02d%02d-%06d",
                today.getYear(), today.getMonthValue(), today.getDayOfMonth(), suffix);
    }
}
