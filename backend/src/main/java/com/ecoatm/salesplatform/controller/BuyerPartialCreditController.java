package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.partialcredit.CreateDraftRequest;
import com.ecoatm.salesplatform.dto.partialcredit.CreditRequestDetail;
import com.ecoatm.salesplatform.dto.partialcredit.CreditRequestSummary;
import com.ecoatm.salesplatform.dto.partialcredit.LineReplacementResponse;
import com.ecoatm.salesplatform.dto.partialcredit.PhotoMetadataView;
import com.ecoatm.salesplatform.dto.partialcredit.SetLinesRequest;
import com.ecoatm.salesplatform.dto.partialcredit.UpdateDraftRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestPhoto;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.security.UploadRateLimiter;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestFileDropParser;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestPhotoRepository;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestPhotoService;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestService;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestValidationException;
import com.ecoatm.salesplatform.service.partialcredit.PhotoUploadException;
import com.ecoatm.salesplatform.service.partialcredit.TooManyRowsException;
import com.ecoatm.salesplatform.service.partialcredit.ValidationIssue;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Buyer-facing REST surface for the Sprint-2 wizard. Five verbs:
 *
 * <ul>
 *   <li>{@code POST /draft} — create a {@link CreditRequest} in DRAFT,
 *       enriched from Snowflake when available</li>
 *   <li>{@code PATCH /{id}} — wizard step-to-step state save (reasons +
 *       damage Q/A)</li>
 *   <li>{@code POST /{id}/submit} — run validator, denormalise manifest
 *       fields onto each line entity, flip status to PENDING_APPROVAL</li>
 *   <li>{@code GET /{id}} — detail view</li>
 *   <li>{@code GET / ?buyerCodeId=&status=} — buyer's own request list</li>
 * </ul>
 *
 * <p><b>Role gating:</b> Class-level {@code @PreAuthorize} accepts the
 * future {@code PartialCredit_Buyer} role and the existing {@code Bidder}
 * role so today's buyer accounts can hit the surface before SPKB-3659 wires
 * the new role mapping. Administrators are admitted for diagnostic /
 * support flows. Per-buyer-code scoping happens in
 * {@link CreditRequestService} — class-level role gating is necessary but
 * not sufficient.
 */
@RestController
@RequestMapping("/api/v1/buyer/partial-credit")
// Sprint 4 §7.1 — drop the orphaned PartialCredit_Buyer role name.
// The V89-seeded user_roles row (1101) stays in the DB but is never
// assigned to a user; the allowlist references only the existing
// global roles. Phase 2 can re-introduce the role tier if/when
// wholesale-eligibility gating becomes wanted.
@PreAuthorize("hasAnyRole('Bidder','SalesRep','Administrator')")
public class BuyerPartialCreditController {

    private final CreditRequestService service;
    private final CreditRequestPhotoService photoService;
    private final CreditRequestFileDropParser fileDropParser;
    private final CreditRequestPhotoRepository photoRepository;
    private final UploadRateLimiter uploadRateLimiter;

    public BuyerPartialCreditController(
            CreditRequestService service,
            CreditRequestPhotoService photoService,
            CreditRequestFileDropParser fileDropParser,
            CreditRequestPhotoRepository photoRepository,
            UploadRateLimiter uploadRateLimiter) {
        this.service = service;
        this.photoService = photoService;
        this.fileDropParser = fileDropParser;
        this.photoRepository = photoRepository;
        this.uploadRateLimiter = uploadRateLimiter;
    }

    @PostMapping("/draft")
    public ResponseEntity<CreditRequestDetail> createDraft(
            @RequestBody CreateDraftRequest body, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        CreditRequest cr = service.createDraft(body.orderNumber(), body.buyerCodeId(), userId, admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDetail(cr));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CreditRequestDetail> update(
            @PathVariable Long id,
            @RequestBody UpdateDraftRequest body,
            Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        CreditRequest cr = service.update(
                id, userId, admin,
                body.hasMissingDevice(), body.hasWrongDevice(), body.hasEncumberedDevice(),
                body.shipmentDamaged());
        return ResponseEntity.ok(toDetail(cr));
    }

    @PostMapping("/{id}/missing-lines")
    public ResponseEntity<LineReplacementResponse> setMissingLines(
            @PathVariable Long id, @RequestBody SetLinesRequest body, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        var outcome = service.replaceMissingLines(id, userId, admin,
                body.barcodes() == null ? List.of() : body.barcodes());
        return ResponseEntity.ok(new LineReplacementResponse(
                toDetail(service.getById(id, userId, admin)),
                outcome.reconciliation()));
    }

    @PostMapping("/{id}/wrong-lines")
    public ResponseEntity<LineReplacementResponse> setWrongLines(
            @PathVariable Long id, @RequestBody SetLinesRequest body, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        var outcome = service.replaceWrongLines(id, userId, admin,
                body.wrongLines() == null ? List.of() : body.wrongLines());
        return ResponseEntity.ok(new LineReplacementResponse(
                toDetail(service.getById(id, userId, admin)),
                outcome.reconciliation()));
    }

    @PostMapping("/{id}/encumbered-lines")
    public ResponseEntity<LineReplacementResponse> setEncumberedLines(
            @PathVariable Long id, @RequestBody SetLinesRequest body, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        var outcome = service.replaceEncumberedLines(id, userId, admin,
                body.barcodes() == null ? List.of() : body.barcodes());
        return ResponseEntity.ok(new LineReplacementResponse(
                toDetail(service.getById(id, userId, admin)),
                outcome.reconciliation()));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<CreditRequestDetail> submit(@PathVariable Long id, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        CreditRequest cr = service.submit(id, userId, admin);
        return ResponseEntity.ok(toDetail(cr));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditRequestDetail> getById(@PathVariable Long id, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        CreditRequest cr = service.getById(id, userId, admin);
        return ResponseEntity.ok(toDetail(cr));
    }

    @GetMapping
    public ResponseEntity<List<CreditRequestSummary>> list(
            @RequestParam Long buyerCodeId,
            @RequestParam(required = false) SystemStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        Pageable pageable = PageRequest.of(page, Math.min(size, 200));
        Page<CreditRequest> rows = service.listForBuyerCode(buyerCodeId, userId, admin, pageable);
        // Status filter is applied in-memory for now — the typical buyer
        // landing has at most a few dozen rows. If list sizes grow we
        // push the filter into the repo via a dedicated finder.
        List<CreditRequestSummary> body = rows.stream()
                .filter(cr -> status == null || matchesStatus(cr, status))
                .map(this::toSummary)
                .toList();
        return ResponseEntity.ok(body);
    }

    // ─── file-drop barcode parser (Sprint 4 chunk 8) ────────────────────

    /**
     * Multipart endpoint for the wizard Step 2 file-drop. Accepts xlsx,
     * csv, or docx; returns the parsed barcodes + any warnings (skipped
     * cells, dedupe, etc.). The wizard merges the result with whatever
     * the buyer already typed into the textarea — no DB write happens
     * here, so this endpoint deliberately stays buyer-code-agnostic.
     */
    @PostMapping(value = "/parse-barcodes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> parseBarcodes(
            @RequestPart("file") MultipartFile file,
            HttpServletRequest request) {
        if (!uploadRateLimiter.tryAcquire(UploadRateLimiter.clientIp(request))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        var outcome = fileDropParser.parse(file);
        return ResponseEntity.ok(Map.of(
                "barcodes", outcome.barcodes(),
                "warnings", outcome.warnings()));
    }

    // ─── photo upload / list / download / delete (Sprint 4 chunk 4) ────

    /**
     * Multipart upload of one photo. {@code wrongDeviceLineId} is
     * optional — when null, the row is stored as DAMAGE (request-scoped
     * evidence); when present, as WRONG_DEVICE (per-line evidence).
     *
     * <p>Spring rejects 10 MB+ requests at the multipart-parser layer
     * (per application.yml); the service enforces a stricter 5 MB
     * cap inside the request body via {@link PhotoUploadException}.
     */
    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoMetadataView> uploadPhoto(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "wrongDeviceLineId", required = false) Long wrongDeviceLineId,
            Authentication auth,
            HttpServletRequest request) {
        if (!uploadRateLimiter.tryAcquire(UploadRateLimiter.clientIp(request))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        CreditRequestPhoto saved = photoService.uploadPhoto(id, wrongDeviceLineId, file, userId, admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(PhotoMetadataView.from(saved));
    }

    /** List metadata for every photo on the request. The bytea blobs
     *  are fetched separately via the {@code /photos/{photoId}/blob}
     *  endpoint so the gallery can stream them lazily. */
    @GetMapping("/{id}/photos")
    public ResponseEntity<List<PhotoMetadataView>> listPhotos(
            @PathVariable Long id, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        List<PhotoMetadataView> photos = photoService.listPhotos(id, userId, admin).stream()
                .map(PhotoMetadataView::from)
                .toList();
        return ResponseEntity.ok(photos);
    }

    /** Stream a photo blob. Served as {@code attachment} with {@code nosniff} so a
     *  stored blob can never execute as script in the app origin if opened directly
     *  (H-11); {@code <img src>} embedding still renders it (browsers ignore
     *  Content-Disposition for subresource loads). Upload-side magic-byte
     *  validation already guarantees the bytes are a real image. */
    @GetMapping("/photos/{photoId}/blob")
    public ResponseEntity<byte[]> downloadPhoto(
            @PathVariable Long photoId, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        CreditRequestPhoto photo = photoService.downloadPhoto(photoId, userId, admin);
        String safeName = sanitizeHeaderFilename(photo.getOriginalFilename());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        org.springframework.http.ContentDisposition.builder("attachment")
                                .filename(safeName)
                                .build().toString())
                .body(photo.getBlob());
    }

    /** Strip characters that could break the quoted Content-Disposition value. */
    private static String sanitizeHeaderFilename(String name) {
        if (name == null || name.isBlank()) {
            return "photo";
        }
        return name.replaceAll("[\"\\r\\n\\\\]", "_");
    }

    /** Delete a photo. Buyers can only delete their own uploads; admins
     *  can delete any. Both are blocked once the parent request is
     *  APPROVED / DECLINED — the evidence trail freezes with review. */
    @DeleteMapping("/photos/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long photoId, Authentication auth) {
        Long userId = principalUserId(auth);
        boolean admin = isAdmin(auth);
        photoService.deletePhoto(photoId, userId, admin);
        return ResponseEntity.noContent().build();
    }

    // ─── exception handlers ────────────────────────────────────────────

    @ExceptionHandler(CreditRequestValidationException.class)
    public ResponseEntity<Map<String, Object>> onValidationFailure(CreditRequestValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "VALIDATION_FAILED",
                "issues", ex.getIssues().stream()
                        .map(i -> Map.of("code", i.code(), "message", i.message()))
                        .toList()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> onNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "NOT_FOUND",
                "message", ex.getMessage() == null ? "Resource not found" : ex.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> onForbidden(SecurityException ex) {
        // Don't leak buyer_code_id collisions across tenants in the body.
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", "FORBIDDEN"));
    }

    @ExceptionHandler(PhotoUploadException.class)
    public ResponseEntity<Map<String, Object>> onPhotoUploadRejected(PhotoUploadException ex) {
        return ResponseEntity.status(ex.reason().status()).body(Map.of(
                "error", ex.reason().name(),
                "message", ex.getMessage() == null ? "Upload rejected" : ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Map<String, Object>> onUnsupportedFileType(UnsupportedOperationException ex) {
        // The file-drop parser raises this for non-xlsx/csv/docx
        // uploads; surface as 415 so the UI can render an inline hint.
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(Map.of(
                "error", "UNSUPPORTED_FILE_TYPE",
                "message", ex.getMessage() == null ? "Unsupported file type" : ex.getMessage()));
    }

    @ExceptionHandler(TooManyRowsException.class)
    public ResponseEntity<Map<String, Object>> onTooManyRows(TooManyRowsException ex) {
        // The file-drop parser raises this once a hostile upload crosses the
        // row/entry cap (H-9); surface as 413 so the UI can tell the buyer to
        // shrink the file rather than silently truncating.
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of(
                "error", "TOO_MANY_ROWS",
                "limit", ex.limit(),
                "matched", ex.matched(),
                "message", ex.getMessage() == null ? "File too large" : ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> onConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "INVALID_STATE",
                "message", ex.getMessage() == null ? "Invalid state transition" : ex.getMessage()));
    }

    // ─── helpers ───────────────────────────────────────────────────────

    private static Long principalUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof Long l) return l;
        if (principal instanceof Number n) return n.longValue();
        throw new IllegalStateException("Expected Long principal, got " + principal.getClass());
    }

    private static boolean isAdmin(Authentication auth) {
        // Sprint 4 chunk 6 (SPKB-3659) — SalesReps act as admin for the
        // buyer surface. The OnBehalfSubmissionService validates they
        // can act for the buyer code; once the draft exists, the wizard
        // endpoints trust submitted_by_id ownership at the service
        // layer instead of the buyer-code junction check.
        for (GrantedAuthority a : auth.getAuthorities()) {
            String authority = a.getAuthority();
            if ("ROLE_Administrator".equals(authority) || "ROLE_SalesRep".equals(authority)) {
                return true;
            }
        }
        return false;
    }

    private CreditRequestDetail toDetail(CreditRequest cr) {
        CreditRequestStatus statusRow = service.findStatusRow(cr.getStatusId())
                .orElseThrow(() -> new IllegalStateException("Status row missing for " + cr.getId()));
        // V91 — pre-resolve wrong-device photo counts so the buyer
        // detail Wrong table's Photos column renders real values
        // instead of "—" placeholders. Per-line cap of 5 photos
        // keeps total volume tiny, so a list + group-in-memory is
        // fine; the ownership check is already enforced by the
        // class-level @PreAuthorize + service-layer guards above.
        java.util.Map<Long, Integer> wrongPhotoCounts = new java.util.HashMap<>();
        for (var photo : photoRepository.findByCreditRequestIdOrderById(cr.getId())) {
            Long lineId = photo.getWrongDeviceLineId();
            if (lineId != null) {
                wrongPhotoCounts.merge(lineId, 1, Integer::sum);
            }
        }
        return CreditRequestDetail.from(
                cr,
                statusRow.getSystemStatus(),
                statusRow.getExternalStatusText(),
                statusRow.getInternalStatusText(),
                statusRow.getColorHex(),
                service.getMissingLines(cr.getId()),
                service.getWrongLines(cr.getId()),
                service.getEncumberedLines(cr.getId()),
                wrongPhotoCounts);
    }

    private CreditRequestSummary toSummary(CreditRequest cr) {
        CreditRequestStatus statusRow = service.findStatusRow(cr.getStatusId())
                .orElseThrow(() -> new IllegalStateException("Status row missing for " + cr.getId()));
        return CreditRequestSummary.from(cr, statusRow.getSystemStatus(), statusRow.getExternalStatusText());
    }

    private boolean matchesStatus(CreditRequest cr, SystemStatus target) {
        return service.findStatusRow(cr.getStatusId())
                .map(row -> row.getSystemStatus() == target)
                .orElse(false);
    }
}
