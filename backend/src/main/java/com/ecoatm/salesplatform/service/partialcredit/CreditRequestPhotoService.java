package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestPhoto;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.PhotoKind;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestPhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Buyer photo-upload service for partial-credit requests (Sprint 4
 * chunk 4 — SPKB-3662). Supports four operations: upload, list, fetch
 * the bytea blob, delete.
 *
 * <p>Auth + ownership lean on {@link CreditRequestService#getById} so
 * the same {@code user → buyer_code} junction guard the wizard already
 * enforces also gates photo access. Photo-row "uploadedById" ownership
 * is enforced inline on delete: a buyer can only delete their own
 * uploads; an admin can delete any.
 *
 * <p>Hard limits — all configurable, defaults documented in
 * application.yml:
 * <ul>
 *   <li>{@code partial-credit.photo.max-size-mb} (default 5 MB) — file
 *       byte cap. Larger files fail with {@link PhotoUploadException}
 *       (TOO_LARGE → 413).</li>
 *   <li>{@code partial-credit.photo.max-per-line} (default 5) — per-line
 *       cap on {@link PhotoKind#WRONG_DEVICE} photos. Exceeded → 409.</li>
 *   <li>{@code partial-credit.photo.allowed-content-types} — MIME
 *       allowlist (default image/jpeg, png, heic, webp). Others → 415.</li>
 * </ul>
 *
 * <p>Upload + delete are blocked once the parent request is in
 * {@code APPROVED} or {@code DECLINED} — review is final and the
 * evidence trail freezes (matches the Sprint 4 §11.Q1 decision).
 */
@Service
public class CreditRequestPhotoService {

    private final CreditRequestPhotoRepository photoRepository;
    private final CreditRequestService creditRequestService;
    private final long maxSizeBytes;
    private final int maxPhotosPerLine;
    private final Set<String> allowedContentTypes;

    public CreditRequestPhotoService(
            CreditRequestPhotoRepository photoRepository,
            CreditRequestService creditRequestService,
            @Value("${partial-credit.photo.max-size-mb:5}") int maxSizeMb,
            @Value("${partial-credit.photo.max-per-line:5}") int maxPhotosPerLine,
            @Value("${partial-credit.photo.allowed-content-types:image/jpeg,image/png,image/heic,image/webp}")
                    String allowedContentTypesCsv) {
        this.photoRepository = photoRepository;
        this.creditRequestService = creditRequestService;
        this.maxSizeBytes = (long) maxSizeMb * 1024L * 1024L;
        this.maxPhotosPerLine = maxPhotosPerLine;
        this.allowedContentTypes = Arrays.stream(allowedContentTypesCsv.split(","))
                .map(String::trim)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Persist one photo. Validates size + MIME + per-line count + parent
     * request state before reading the bytes into memory.
     *
     * <p>Photos uploaded against a non-null {@code wrongDeviceLineId}
     * are stored as {@link PhotoKind#WRONG_DEVICE}; uploads against
     * {@code null} (request-scoped damage evidence) are
     * {@link PhotoKind#DAMAGE}.
     */
    @Transactional
    public CreditRequestPhoto uploadPhoto(
            Long creditRequestId,
            Long wrongDeviceLineId,
            MultipartFile file,
            Long uploaderUserId,
            boolean isAdmin) {
        // Ownership check — throws SecurityException (→ 403) or
        // EntityNotFoundException (→ 404) before we touch the bytes.
        CreditRequest cr = creditRequestService.getById(creditRequestId, uploaderUserId, isAdmin);
        requireRequestOpen(cr);

        if (file == null || file.isEmpty()) {
            throw new PhotoUploadException(
                    PhotoUploadException.Reason.UNSUPPORTED_TYPE,
                    "Empty upload — pick a file before submitting");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new PhotoUploadException(
                    PhotoUploadException.Reason.TOO_LARGE,
                    "File exceeds " + (maxSizeBytes / (1024L * 1024L)) + " MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null
                || !allowedContentTypes.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new PhotoUploadException(
                    PhotoUploadException.Reason.UNSUPPORTED_TYPE,
                    "Content type not allowed: " + contentType);
        }
        PhotoKind kind = wrongDeviceLineId == null ? PhotoKind.DAMAGE : PhotoKind.WRONG_DEVICE;
        if (kind == PhotoKind.WRONG_DEVICE) {
            long existing = photoRepository.findByWrongDeviceLineIdOrderById(wrongDeviceLineId).size();
            if (existing >= maxPhotosPerLine) {
                throw new PhotoUploadException(
                        PhotoUploadException.Reason.TOO_MANY_PER_LINE,
                        "Line " + wrongDeviceLineId + " already has " + existing + " photos");
            }
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            // Should be unreachable for the multipart-resolver path; if
            // it does happen, surface as 500 (RuntimeException) so the
            // operator sees the real cause rather than a bespoke 4xx.
            throw new RuntimeException("Failed to read upload bytes", ex);
        }

        // H-11: verify the actual bytes are a real image, not a script payload
        // uploaded with a spoofed image/* content-type — defense in depth even if
        // the content-type allowlist is later widened (e.g. to image/svg+xml).
        if (!looksLikeAllowedImage(bytes)) {
            throw new PhotoUploadException(
                    PhotoUploadException.Reason.UNSUPPORTED_TYPE,
                    "File content is not a valid image");
        }

        CreditRequestPhoto photo = new CreditRequestPhoto();
        photo.setCreditRequestId(creditRequestId);
        photo.setWrongDeviceLineId(wrongDeviceLineId);
        photo.setKind(kind);
        photo.setOriginalFilename(safeFilename(file.getOriginalFilename()));
        photo.setContentType(contentType);
        photo.setSizeBytes((int) Math.min(bytes.length, Integer.MAX_VALUE));
        photo.setBlob(bytes);
        photo.setCreatedDate(Instant.now());
        photo.setCreatedById(uploaderUserId);
        return photoRepository.save(photo);
    }

    /** List metadata (no bytea) for every photo on a request. */
    @Transactional(readOnly = true)
    public List<CreditRequestPhoto> listPhotos(Long creditRequestId, Long viewerUserId, boolean isAdmin) {
        creditRequestService.getById(creditRequestId, viewerUserId, isAdmin);
        return photoRepository.findByCreditRequestIdOrderById(creditRequestId);
    }

    /** Return the blob row (content-type + bytes). Ownership is checked
     *  against the parent request — the caller must be able to see the
     *  request to fetch its photos. */
    @Transactional(readOnly = true)
    public CreditRequestPhoto downloadPhoto(Long photoId, Long viewerUserId, boolean isAdmin) {
        CreditRequestPhoto photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Photo " + photoId));
        creditRequestService.getById(photo.getCreditRequestId(), viewerUserId, isAdmin);
        return photo;
    }

    private static final java.util.Set<String> HEIF_BRANDS = java.util.Set.of(
            "heic", "heix", "hevc", "hevx", "mif1", "msf1", "heim", "heis", "hevm", "hevs");

    /**
     * Verifies the bytes match an allowed image format (JPEG / PNG / WebP / HEIF)
     * rather than trusting the client-declared MIME (security review 2026-07-10,
     * H-11). SVG deliberately fails — it has no binary signature.
     */
    private static boolean looksLikeAllowedImage(byte[] b) {
        if (b == null || b.length < 12) {
            return false;
        }
        if ((b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return true; // JPEG
        }
        if ((b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return true; // PNG
        }
        if (b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return true; // WebP
        }
        if (b[4] == 'f' && b[5] == 't' && b[6] == 'y' && b[7] == 'p') { // HEIF / HEIC
            String brand = new String(b, 8, 4, java.nio.charset.StandardCharsets.US_ASCII)
                    .toLowerCase(java.util.Locale.ROOT);
            return HEIF_BRANDS.contains(brand);
        }
        return false;
    }

    /**
     * Delete one photo. Buyers can delete their own uploads; admins can
     * delete any photo. Blocked once the parent request is in a final
     * state — the evidence trail freezes alongside the review outcome.
     */
    @Transactional
    public void deletePhoto(Long photoId, Long viewerUserId, boolean isAdmin) {
        CreditRequestPhoto photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Photo " + photoId));
        CreditRequest cr = creditRequestService.getById(
                photo.getCreditRequestId(), viewerUserId, isAdmin);
        requireRequestOpen(cr);

        if (!isAdmin) {
            Long owner = photo.getCreatedById();
            if (owner == null || !owner.equals(viewerUserId)) {
                throw new SecurityException(
                        "User " + viewerUserId + " cannot delete a photo uploaded by " + owner);
            }
        }
        photoRepository.delete(photo);
    }

    private void requireRequestOpen(CreditRequest cr) {
        CreditRequestStatus row = creditRequestService.findStatusRow(cr.getStatusId())
                .orElseThrow(() -> new IllegalStateException("Status row missing for " + cr.getId()));
        SystemStatus status = row.getSystemStatus();
        if (status == SystemStatus.APPROVED || status == SystemStatus.DECLINED) {
            throw new PhotoUploadException(
                    PhotoUploadException.Reason.REQUEST_FINALIZED,
                    "Request " + cr.getId() + " is " + status + " — photo edits are frozen");
        }
    }

    private static String safeFilename(String original) {
        if (original == null || original.isBlank()) return "upload";
        // Strip any path components a buggy client might leak.
        int slash = Math.max(original.lastIndexOf('/'), original.lastIndexOf('\\'));
        String base = slash >= 0 ? original.substring(slash + 1) : original;
        // Cap to the column length (400 chars). Truncate from the front
        // so the extension stays intact.
        if (base.length() <= 400) return base;
        return base.substring(base.length() - 400);
    }
}
