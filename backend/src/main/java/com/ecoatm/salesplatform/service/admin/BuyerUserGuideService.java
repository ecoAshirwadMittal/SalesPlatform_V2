package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.BuyerUserGuideListResponse;
import com.ecoatm.salesplatform.dto.BuyerUserGuideMetadata;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.admin.BuyerUserGuide;
import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.repository.admin.BuyerUserGuideRepository;
import com.ecoatm.salesplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Business logic for the Buyer User Guide admin upload feature.
 *
 * <p>Files are stored on local disk under {@code buyer-guide.upload-dir}
 * (default: {@code uploads/buyer-user-guide} relative to the working directory).
 * S3 is a follow-up — the file_path column stores the relative path so
 * a storage back-end swap is a service-layer change only.
 */
@Service
public class BuyerUserGuideService {

    private static final Logger log = LoggerFactory.getLogger(BuyerUserGuideService.class);

    /** 20 MB hard cap — matches the task spec. */
    static final long MAX_BYTE_SIZE = 20L * 1024 * 1024;

    /** Magic bytes for PDF (%PDF-). */
    private static final byte[] PDF_MAGIC = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D};

    /** History page size — last 10 non-deleted uploads. */
    private static final int HISTORY_PAGE_SIZE = 10;

    private final BuyerUserGuideRepository repository;
    private final UserRepository userRepository;
    private final Path uploadDir;

    public BuyerUserGuideService(
            BuyerUserGuideRepository repository,
            UserRepository userRepository,
            @Value("${buyer-guide.upload-dir:uploads/buyer-user-guide}") String uploadDirPath) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.uploadDir = Paths.get(uploadDirPath);
    }

    // ---------------------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------------------

    /**
     * Returns the active guide metadata (if any) and the last 10 uploads.
     */
    @Transactional(readOnly = true)
    public BuyerUserGuideListResponse list() {
        Optional<BuyerUserGuide> activeOpt = repository.findByIsActiveTrue();
        List<BuyerUserGuide> history = repository.findRecentUploads(
                PageRequest.of(0, HISTORY_PAGE_SIZE));

        BuyerUserGuideMetadata active = activeOpt.map(this::toMetadata).orElse(null);
        List<BuyerUserGuideMetadata> historyDtos = history.stream()
                .map(this::toMetadata)
                .toList();

        return new BuyerUserGuideListResponse(active, historyDtos);
    }

    // ---------------------------------------------------------------------------
    // Upload
    // ---------------------------------------------------------------------------

    /**
     * Validate and persist a new PDF upload.
     *
     * <ol>
     *   <li>Validate Content-Type header.</li>
     *   <li>Validate byte size ≤ 20 MB.</li>
     *   <li>Read the first 5 bytes and verify the PDF magic header.</li>
     *   <li>Write the file to {@link #uploadDir}.</li>
     *   <li>Atomically flip {@code is_active}: deactivate any existing active
     *       row and mark the new row active.</li>
     * </ol>
     *
     * <p>The entire operation runs in a single transaction so a file-write
     * failure before commit will not leave a dangling DB row (and vice versa
     * — the file is written first; if the DB commit fails the orphan file is
     * harmless because the row never becomes active).
     */
    @Transactional
    public BuyerUserGuideMetadata upload(MultipartFile file, long uploadedByUserId) throws IOException {
        validateContentType(file);
        validateByteSize(file);

        byte[] bytes = file.getBytes();
        validateMagicBytes(bytes);

        ensureUploadDir();

        // Store with a UUID-prefixed filename to avoid collisions.
        String storedName = UUID.randomUUID() + "_" + sanitize(file.getOriginalFilename());
        Path dest = uploadDir.resolve(storedName);
        Files.write(dest, bytes);

        // Atomic active-flip: deactivate old, save new.
        repository.deactivateAll();

        BuyerUserGuide guide = new BuyerUserGuide();
        guide.setFileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "buyer-guide.pdf");
        guide.setFilePath(storedName);
        guide.setContentType("application/pdf");
        guide.setByteSize(bytes.length);
        guide.setUploadedBy(uploadedByUserId);
        guide.setUploadedAt(Instant.now());
        guide.setActive(true);
        guide.setDeleted(false);

        BuyerUserGuide saved = repository.save(guide);
        log.info("Buyer user guide uploaded: id={} file={} size={} by={}",
                saved.getId(), storedName, bytes.length, uploadedByUserId);

        return toMetadata(saved);
    }

    // ---------------------------------------------------------------------------
    // Download
    // ---------------------------------------------------------------------------

    /**
     * Returns a stream + metadata for the currently active guide.
     *
     * @throws EntityNotFoundException if no active guide exists.
     */
    @Transactional(readOnly = true)
    public DownloadResult download() throws IOException {
        BuyerUserGuide guide = repository.findByIsActiveTrue()
                .orElseThrow(() -> new EntityNotFoundException("No buyer user guide configured"));
        return toDownloadResult(guide);
    }

    // ---------------------------------------------------------------------------
    // Soft delete
    // ---------------------------------------------------------------------------

    /**
     * Soft-deletes an entry by id.
     *
     * @throws IllegalStateException    if the entry is currently active.
     * @throws EntityNotFoundException  if no non-deleted entry with that id exists.
     */
    @Transactional
    public void delete(long id) {
        BuyerUserGuide guide = repository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("BuyerUserGuide not found: id=" + id));
        if (guide.isActive()) {
            throw new IllegalStateException("Cannot delete the active guide; upload a replacement first.");
        }
        guide.setDeleted(true);
        repository.save(guide);
        log.info("Buyer user guide soft-deleted: id={}", id);
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    private void validateContentType(MultipartFile file) {
        String ct = file.getContentType();
        if (!"application/pdf".equalsIgnoreCase(ct)) {
            throw new IllegalArgumentException(
                    "Invalid content type: expected application/pdf, got " + ct);
        }
    }

    private void validateByteSize(MultipartFile file) {
        if (file.getSize() > MAX_BYTE_SIZE) {
            throw new IllegalArgumentException(
                    "File too large: maximum is 20 MB, got " + file.getSize() + " bytes");
        }
        if (file.getSize() == 0) {
            throw new IllegalArgumentException("File is empty");
        }
    }

    private void validateMagicBytes(byte[] bytes) {
        if (bytes.length < PDF_MAGIC.length) {
            throw new IllegalArgumentException("File is not a valid PDF");
        }
        byte[] header = Arrays.copyOf(bytes, PDF_MAGIC.length);
        if (!Arrays.equals(header, PDF_MAGIC)) {
            throw new IllegalArgumentException("File is not a valid PDF (magic bytes mismatch)");
        }
    }

    private void ensureUploadDir() throws IOException {
        Files.createDirectories(uploadDir);
    }

    private static String sanitize(String name) {
        if (name == null) return "guide.pdf";
        return name.replaceAll("[^a-zA-Z0-9._\\-]", "_");
    }

    private BuyerUserGuideMetadata toMetadata(BuyerUserGuide g) {
        String uploaderName = resolveUploaderName(g.getUploadedBy());
        return new BuyerUserGuideMetadata(
                g.getId(),
                g.getFileName(),
                g.getContentType(),
                g.getByteSize(),
                g.getUploadedBy(),
                uploaderName,
                g.getUploadedAt(),
                g.isActive()
        );
    }

    private String resolveUploaderName(long userId) {
        return userRepository.findById(userId)
                .map(u -> u.getName() != null ? u.getName() : "User #" + userId)
                .orElse("User #" + userId);
    }

    private DownloadResult toDownloadResult(BuyerUserGuide guide) throws IOException {
        Path path = uploadDir.resolve(guide.getFilePath());
        if (!Files.exists(path)) {
            throw new EntityNotFoundException("Guide file not found on disk: " + guide.getFilePath());
        }
        InputStream stream = Files.newInputStream(path);
        return new DownloadResult(guide.getFileName(), guide.getContentType(), guide.getByteSize(), stream);
    }

    /** Carrier for the download stream and its metadata. */
    public record DownloadResult(String fileName, String contentType, long byteSize, InputStream stream) {}
}
