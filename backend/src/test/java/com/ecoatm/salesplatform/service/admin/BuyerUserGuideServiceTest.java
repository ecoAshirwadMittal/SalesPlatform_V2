package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.BuyerUserGuideListResponse;
import com.ecoatm.salesplatform.dto.BuyerUserGuideMetadata;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.admin.BuyerUserGuide;
import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.repository.admin.BuyerUserGuideRepository;
import com.ecoatm.salesplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyerUserGuideServiceTest {

    @Mock
    private BuyerUserGuideRepository repository;

    @Mock
    private UserRepository userRepository;

    @TempDir
    Path tempDir;

    private BuyerUserGuideService service;

    // Minimal valid PDF content (starts with %PDF-)
    private static final byte[] VALID_PDF = new byte[]{
            0x25, 0x50, 0x44, 0x46, 0x2D, // %PDF-
            0x31, 0x2E, 0x34              // 1.4
    };

    @BeforeEach
    void setUp() {
        service = new BuyerUserGuideService(repository, userRepository, tempDir.toString());
    }

    // ---------------------------------------------------------------------------
    // list
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("list returns active=null and empty history when no uploads exist")
    void list_noUploads_returnsEmptyState() {
        when(repository.findByIsActiveTrue()).thenReturn(Optional.empty());
        when(repository.findRecentUploads(any())).thenReturn(List.of());

        BuyerUserGuideListResponse result = service.list();

        assertThat(result.active()).isNull();
        assertThat(result.history()).isEmpty();
    }

    @Test
    @DisplayName("list returns active guide and history when uploads exist")
    void list_withActive_returnsMetadata() {
        BuyerUserGuide guide = makeGuide(1L, "guide.pdf", true);
        when(repository.findByIsActiveTrue()).thenReturn(Optional.of(guide));
        when(repository.findRecentUploads(any())).thenReturn(List.of(guide));
        stubUploader(99L, "Admin User");

        BuyerUserGuideListResponse result = service.list();

        assertThat(result.active()).isNotNull();
        assertThat(result.active().fileName()).isEqualTo("guide.pdf");
        assertThat(result.active().isActive()).isTrue();
        assertThat(result.history()).hasSize(1);
    }

    // ---------------------------------------------------------------------------
    // upload — validation
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("upload rejects wrong content-type")
    void upload_wrongContentType_throws() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "text/plain", VALID_PDF);

        assertThatThrownBy(() -> service.upload(file, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("application/pdf");
    }

    @Test
    @DisplayName("upload rejects file exceeding 20 MB")
    void upload_tooLarge_throws() {
        // Just over 20 MB
        byte[] large = new byte[(int) BuyerUserGuideService.MAX_BYTE_SIZE + 1];
        large[0] = 0x25; large[1] = 0x50; large[2] = 0x44; large[3] = 0x46; large[4] = 0x2D;
        MockMultipartFile file = new MockMultipartFile(
                "file", "big.pdf", "application/pdf", large);

        assertThatThrownBy(() -> service.upload(file, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too large");
    }

    @Test
    @DisplayName("upload rejects empty file")
    void upload_emptyFile_throws() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]);

        assertThatThrownBy(() -> service.upload(file, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    @DisplayName("upload rejects file with wrong magic bytes")
    void upload_wrongMagicBytes_throws() {
        byte[] notPdf = new byte[]{0x50, 0x4B, 0x03, 0x04, 0x14}; // ZIP signature
        MockMultipartFile file = new MockMultipartFile(
                "file", "trick.pdf", "application/pdf", notPdf);

        assertThatThrownBy(() -> service.upload(file, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a valid PDF");
    }

    // ---------------------------------------------------------------------------
    // upload — happy path
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("upload deactivates existing guide and marks new guide active")
    void upload_valid_flipsActiveFlag() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "guide-v2.pdf", "application/pdf", VALID_PDF);

        when(repository.save(any(BuyerUserGuide.class))).thenAnswer(inv -> {
            BuyerUserGuide g = inv.getArgument(0);
            setId(g, 42L);
            g.setActive(true);
            return g;
        });
        stubUploader(7L, "Admin");

        BuyerUserGuideMetadata result = service.upload(file, 7L);

        verify(repository).deactivateAll();
        verify(repository).save(any(BuyerUserGuide.class));
        assertThat(result.isActive()).isTrue();
        assertThat(result.fileName()).isEqualTo("guide-v2.pdf");
        assertThat(result.byteSize()).isEqualTo(VALID_PDF.length);
    }

    @Test
    @DisplayName("upload writes file to disk")
    void upload_valid_writesFileToDisk() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "written.pdf", "application/pdf", VALID_PDF);

        when(repository.save(any(BuyerUserGuide.class))).thenAnswer(inv -> {
            BuyerUserGuide g = inv.getArgument(0);
            setId(g, 1L);
            return g;
        });
        stubUploader(1L, "Admin");

        service.upload(file, 1L);

        // At least one file with a .pdf extension should exist in tempDir
        long count = java.nio.file.Files.list(tempDir)
                .filter(p -> p.getFileName().toString().endsWith("_written.pdf"))
                .count();
        assertThat(count).isEqualTo(1);
    }

    // ---------------------------------------------------------------------------
    // download
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("download throws EntityNotFoundException when no active guide")
    void download_noActive_throws() {
        when(repository.findByIsActiveTrue()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.download())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No buyer user guide configured");
    }

    @Test
    @DisplayName("download throws EntityNotFoundException when file missing from disk")
    void download_fileMissingOnDisk_throws() {
        BuyerUserGuide guide = makeGuide(1L, "missing.pdf", true);
        guide.setFilePath("nonexistent_uuid_missing.pdf");
        when(repository.findByIsActiveTrue()).thenReturn(Optional.of(guide));

        assertThatThrownBy(() -> service.download())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Guide file not found on disk");
    }

    // ---------------------------------------------------------------------------
    // delete
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("delete soft-deletes inactive guide")
    void delete_inactiveGuide_marksDeleted() {
        BuyerUserGuide guide = makeGuide(5L, "old.pdf", false);
        when(repository.findActiveById(5L)).thenReturn(Optional.of(guide));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.delete(5L);

        assertThat(guide.isDeleted()).isTrue();
        verify(repository).save(guide);
    }

    @Test
    @DisplayName("delete throws IllegalStateException when guide is active")
    void delete_activeGuide_throwsConflict() {
        BuyerUserGuide guide = makeGuide(3L, "active.pdf", true);
        when(repository.findActiveById(3L)).thenReturn(Optional.of(guide));

        assertThatThrownBy(() -> service.delete(3L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete the active guide");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("delete throws EntityNotFoundException for unknown id")
    void delete_unknownId_throws() {
        when(repository.findActiveById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private static void setId(BuyerUserGuide g, Long id) {
        try {
            var idField = BuyerUserGuide.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(g, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BuyerUserGuide makeGuide(Long id, String name, boolean active) {
        BuyerUserGuide g = new BuyerUserGuide();
        setId(g, id);
        g.setFileName(name);
        g.setFilePath("uuid_" + name);
        g.setContentType("application/pdf");
        g.setByteSize(1024L);
        g.setUploadedBy(99L);
        g.setUploadedAt(Instant.now());
        g.setActive(active);
        g.setDeleted(false);
        return g;
    }

    private void stubUploader(long userId, String name) {
        User user = new User();
        user.setName(name);
        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    }
}
