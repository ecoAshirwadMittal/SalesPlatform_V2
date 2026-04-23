package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BuyerUserGuideListResponse;
import com.ecoatm.salesplatform.dto.BuyerUserGuideMetadata;
import com.ecoatm.salesplatform.service.admin.BuyerUserGuideService;
import com.ecoatm.salesplatform.service.admin.BuyerUserGuideService.DownloadResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * REST surface for the Buyer User Guide feature (Phase 12).
 *
 * <ul>
 *   <li>{@code GET  /api/v1/admin/buyer-user-guide}       — Administrator: list active + history.</li>
 *   <li>{@code POST /api/v1/admin/buyer-user-guide}       — Administrator: upload a new PDF.</li>
 *   <li>{@code DELETE /api/v1/admin/buyer-user-guide/{id}} — Administrator: soft-delete by id.</li>
 *   <li>{@code GET  /api/v1/bidder/docs/buyer-guide}      — Bidder/Administrator: stream active PDF.</li>
 * </ul>
 *
 * <p>The admin routes are gated at the {@link com.ecoatm.salesplatform.security.SecurityConfig}
 * filter chain via {@code /api/v1/admin/**} (Administrator only). The bidder route
 * sits under {@code /api/v1/bidder/**} which already allows Bidder + Administrator.
 * Both tiers are further restricted by the {@code @PreAuthorize} annotations here.
 */
@RestController
public class BuyerUserGuideController {

    private final BuyerUserGuideService service;

    public BuyerUserGuideController(BuyerUserGuideService service) {
        this.service = service;
    }

    // ---------------------------------------------------------------------------
    // Admin endpoints
    // ---------------------------------------------------------------------------

    @GetMapping("/api/v1/admin/buyer-user-guide")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<BuyerUserGuideListResponse> list() {
        return ResponseEntity.ok(service.list());
    }

    @PostMapping(value = "/api/v1/admin/buyer-user-guide",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<BuyerUserGuideMetadata> upload(
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {
        long userId = (Long) auth.getPrincipal();
        BuyerUserGuideMetadata meta = service.upload(file, userId);
        return ResponseEntity.ok(meta);
    }

    @DeleteMapping("/api/v1/admin/buyer-user-guide/{id}")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------------------------------------------------------------------
    // Bidder endpoint — streams active PDF inline
    // ---------------------------------------------------------------------------

    /**
     * Streams the currently active Buyer User Guide PDF to the caller.
     *
     * <p>Returns {@code 404} with a JSON body when no guide has been uploaded yet.
     * Content-Disposition is {@code inline} so the browser renders the PDF rather
     * than triggering a download dialog.
     */
    @GetMapping("/api/v1/bidder/docs/buyer-guide")
    @PreAuthorize("hasAnyRole('Bidder','Administrator')")
    public void download(HttpServletResponse response) throws IOException {
        DownloadResult result = service.download();

        String encoded = URLEncoder.encode(result.fileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        response.setContentType(result.contentType());
        response.setHeader("Content-Disposition",
                "inline; filename=\"" + result.fileName()
                        + "\"; filename*=UTF-8''" + encoded);
        response.setContentLengthLong(result.byteSize());

        try (var in = result.stream(); OutputStream out = response.getOutputStream()) {
            in.transferTo(out);
        }
    }
}
