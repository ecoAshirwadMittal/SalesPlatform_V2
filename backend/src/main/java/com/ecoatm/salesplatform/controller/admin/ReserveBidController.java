package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.ReserveBidAuditResponse;
import com.ecoatm.salesplatform.dto.ReserveBidListResponse;
import com.ecoatm.salesplatform.dto.ReserveBidRequest;
import com.ecoatm.salesplatform.dto.ReserveBidRow;
import com.ecoatm.salesplatform.dto.ReserveBidSyncStatus;
import com.ecoatm.salesplatform.dto.ReserveBidUploadResult;
import com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidException;
import com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidService;
import com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidValidationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/reserve-bids")
public class ReserveBidController {

    private final ReserveBidService service;

    public ReserveBidController(ReserveBidService service) {
        this.service = service;
    }

    @GetMapping
    public ReserveBidListResponse list(
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) BigDecimal minBid,
            @RequestParam(required = false) BigDecimal maxBid,
            @RequestParam(required = false) Instant updatedSince,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.search(productId, grade, minBid, maxBid, updatedSince, page, size);
    }

    @GetMapping("/{id}")
    public ReserveBidRow get(@PathVariable long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<ReserveBidRow> create(@RequestBody @Valid ReserveBidRequest req) {
        ReserveBidRow created = service.create(userId(), req);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ReserveBidRow update(@PathVariable long id, @RequestBody @Valid ReserveBidRequest req) {
        return service.update(userId(), id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ReserveBidUploadResult upload(@RequestParam("file") MultipartFile file) {
        return service.upload(userId(), file);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.downloadAll(out);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"reserve-bids-" + Instant.now() + ".xlsx\"")
                .body(out.toByteArray());
    }

    @GetMapping("/{id}/audit")
    public ReserveBidAuditResponse audit(
            @PathVariable long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.findAudit(id, page, size);
    }

    @GetMapping("/sync")
    public ReserveBidSyncStatus syncStatus() {
        return service.syncStatus();
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> triggerSync() {
        int rows = service.runScheduledSync();
        return ResponseEntity.accepted().body(Map.of("rowsFetched", rows));
    }

    @ExceptionHandler(ReserveBidValidationException.class)
    public ResponseEntity<Map<String, String>> onValidation(ReserveBidValidationException ex) {
        return ResponseEntity.badRequest().body(Map.of("code", ex.code(), "message", ex.getMessage()));
    }

    @ExceptionHandler(ReserveBidException.class)
    public ResponseEntity<Map<String, String>> onDomain(ReserveBidException ex) {
        int status = switch (ex.code()) {
            case "RESERVE_BID_NOT_FOUND" -> 404;
            case "DUPLICATE_PRODUCT_GRADE" -> 409;
            case "SYNC_NOT_INITIALIZED" -> 503;
            default -> 400;
        };
        return ResponseEntity.status(status).body(Map.of("code", ex.code(), "message", ex.getMessage()));
    }

    /**
     * Extracts the authenticated user's ID from the security context.
     * JwtAuthenticationFilter stores the userId (Long) as the principal directly.
     * Returns 0L as a safe fallback for test contexts using @WithMockUser,
     * which sets a String principal rather than a Long.
     */
    private static long userId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return 0L;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Long l) {
            return l;
        }
        return 0L;
    }
}
