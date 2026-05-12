package com.ecoatm.salesplatform.service.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestPhoto;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.PhotoKind;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestPhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for {@link CreditRequestPhotoService}.
 *
 * <p>Twelve cases covering upload validation (size / MIME / per-line cap
 * / parent-status freeze), list/download pass-throughs, and the
 * buyer-vs-admin delete authorization split.
 */
@ExtendWith(MockitoExtension.class)
class CreditRequestPhotoServiceTest {

    @Mock private CreditRequestPhotoRepository photoRepository;
    @Mock private CreditRequestService creditRequestService;

    private CreditRequestPhotoService service;

    @BeforeEach
    void setUp() {
        service = new CreditRequestPhotoService(
                photoRepository,
                creditRequestService,
                /* maxSizeMb */ 5,
                /* maxPhotosPerLine */ 5,
                "image/jpeg,image/png,image/heic,image/webp");
    }

    @Test
    @DisplayName("uploadPhoto — happy path persists row + returns saved entity")
    void uploadPhoto_happyPath() {
        CreditRequest cr = openRequest(100L);
        when(creditRequestService.getById(100L, 7L, false)).thenReturn(cr);
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));
        when(photoRepository.findByWrongDeviceLineIdOrderById(500L)).thenReturn(List.of());
        when(photoRepository.save(any(CreditRequestPhoto.class))).thenAnswer(inv -> inv.getArgument(0));

        MultipartFile file = jpeg("a.jpg", 1024);

        CreditRequestPhoto saved = service.uploadPhoto(100L, 500L, file, 7L, false);

        assertThat(saved.getCreditRequestId()).isEqualTo(100L);
        assertThat(saved.getWrongDeviceLineId()).isEqualTo(500L);
        assertThat(saved.getKind()).isEqualTo(PhotoKind.WRONG_DEVICE);
        assertThat(saved.getContentType()).isEqualTo("image/jpeg");
        assertThat(saved.getCreatedById()).isEqualTo(7L);
        assertThat(saved.getSizeBytes()).isEqualTo(1024);
    }

    @Test
    @DisplayName("uploadPhoto — null wrongDeviceLineId stores as DAMAGE kind")
    void uploadPhoto_nullLineMeansDamageKind() {
        when(creditRequestService.getById(anyLong(), anyLong(), anyBoolean())).thenReturn(openRequest(100L));
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));
        when(photoRepository.save(any(CreditRequestPhoto.class))).thenAnswer(inv -> inv.getArgument(0));

        CreditRequestPhoto saved = service.uploadPhoto(100L, null, jpeg("d.jpg", 500), 7L, false);

        assertThat(saved.getKind()).isEqualTo(PhotoKind.DAMAGE);
        assertThat(saved.getWrongDeviceLineId()).isNull();
    }

    @Test
    @DisplayName("uploadPhoto — > 5 MB throws TOO_LARGE")
    void uploadPhoto_oversize_rejected() {
        when(creditRequestService.getById(anyLong(), anyLong(), anyBoolean())).thenReturn(openRequest(100L));
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));

        MultipartFile file = jpeg("big.jpg", 6 * 1024 * 1024);

        assertThatThrownBy(() -> service.uploadPhoto(100L, 500L, file, 7L, false))
                .isInstanceOf(PhotoUploadException.class)
                .extracting("reason")
                .isEqualTo(PhotoUploadException.Reason.TOO_LARGE);
        verify(photoRepository, never()).save(any());
    }

    @Test
    @DisplayName("uploadPhoto — disallowed MIME (image/gif) throws UNSUPPORTED_TYPE")
    void uploadPhoto_unsupportedMime_rejected() {
        when(creditRequestService.getById(anyLong(), anyLong(), anyBoolean())).thenReturn(openRequest(100L));
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));

        MultipartFile file = new MockMultipartFile("file", "anim.gif", "image/gif", new byte[1024]);

        assertThatThrownBy(() -> service.uploadPhoto(100L, 500L, file, 7L, false))
                .isInstanceOf(PhotoUploadException.class)
                .extracting("reason")
                .isEqualTo(PhotoUploadException.Reason.UNSUPPORTED_TYPE);
    }

    @Test
    @DisplayName("uploadPhoto — empty upload throws UNSUPPORTED_TYPE (not silently saves an empty row)")
    void uploadPhoto_empty_rejected() {
        when(creditRequestService.getById(anyLong(), anyLong(), anyBoolean())).thenReturn(openRequest(100L));
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));

        MultipartFile file = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> service.uploadPhoto(100L, 500L, file, 7L, false))
                .isInstanceOf(PhotoUploadException.class)
                .extracting("reason")
                .isEqualTo(PhotoUploadException.Reason.UNSUPPORTED_TYPE);
    }

    @Test
    @DisplayName("uploadPhoto — APPROVED parent freezes uploads")
    void uploadPhoto_finalizedRequest_rejected() {
        CreditRequest cr = openRequest(100L);
        when(creditRequestService.getById(anyLong(), anyLong(), anyBoolean())).thenReturn(cr);
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(statusRow(SystemStatus.APPROVED)));

        assertThatThrownBy(() -> service.uploadPhoto(100L, 500L, jpeg("a.jpg", 100), 7L, false))
                .isInstanceOf(PhotoUploadException.class)
                .extracting("reason")
                .isEqualTo(PhotoUploadException.Reason.REQUEST_FINALIZED);
    }

    @Test
    @DisplayName("uploadPhoto — line at the per-line cap throws TOO_MANY_PER_LINE")
    void uploadPhoto_capExceeded_rejected() {
        when(creditRequestService.getById(anyLong(), anyLong(), anyBoolean())).thenReturn(openRequest(100L));
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));
        // Repo returns 5 existing — adding a 6th must fail.
        when(photoRepository.findByWrongDeviceLineIdOrderById(500L))
                .thenReturn(List.of(stub(), stub(), stub(), stub(), stub()));

        assertThatThrownBy(() -> service.uploadPhoto(100L, 500L, jpeg("a.jpg", 100), 7L, false))
                .isInstanceOf(PhotoUploadException.class)
                .extracting("reason")
                .isEqualTo(PhotoUploadException.Reason.TOO_MANY_PER_LINE);
        verify(photoRepository, never()).save(any());
    }

    @Test
    @DisplayName("uploadPhoto — service does NOT enforce per-line cap for DAMAGE photos (no wrongDeviceLineId)")
    void uploadPhoto_damageBypassesPerLineCap() {
        when(creditRequestService.getById(anyLong(), anyLong(), anyBoolean())).thenReturn(openRequest(100L));
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));
        when(photoRepository.save(any(CreditRequestPhoto.class))).thenAnswer(inv -> inv.getArgument(0));

        // No call to findByWrongDeviceLineIdOrderById expected.
        CreditRequestPhoto saved = service.uploadPhoto(100L, null, jpeg("d.jpg", 100), 7L, false);

        assertThat(saved.getKind()).isEqualTo(PhotoKind.DAMAGE);
        verify(photoRepository, never()).findByWrongDeviceLineIdOrderById(anyLong());
    }

    @Test
    @DisplayName("listPhotos — passes the ownership check to CreditRequestService and returns the repo result")
    void listPhotos_delegates() {
        when(creditRequestService.getById(100L, 7L, false)).thenReturn(openRequest(100L));
        when(photoRepository.findByCreditRequestIdOrderById(100L))
                .thenReturn(List.of(stub(), stub()));

        List<CreditRequestPhoto> photos = service.listPhotos(100L, 7L, false);

        assertThat(photos).hasSize(2);
        verify(creditRequestService).getById(100L, 7L, false);
    }

    @Test
    @DisplayName("downloadPhoto — unknown id throws EntityNotFoundException (→ 404)")
    void downloadPhoto_unknown_throws() {
        when(photoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.downloadPhoto(99L, 7L, false))
                .isInstanceOf(EntityNotFoundException.class);
        verify(creditRequestService, never()).getById(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("deletePhoto — buyer can delete own upload")
    void deletePhoto_buyerOwnUpload_succeeds() {
        CreditRequestPhoto photo = stub();
        photo.setId(10L);
        photo.setCreditRequestId(100L);
        photo.setCreatedById(7L);
        when(photoRepository.findById(10L)).thenReturn(Optional.of(photo));
        when(creditRequestService.getById(100L, 7L, false)).thenReturn(openRequest(100L));
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));

        service.deletePhoto(10L, 7L, false);

        verify(photoRepository).delete(photo);
    }

    @Test
    @DisplayName("deletePhoto — buyer cannot delete another user's upload (SecurityException → 403)")
    void deletePhoto_buyerForeignUpload_throws() {
        CreditRequestPhoto photo = stub();
        photo.setId(10L);
        photo.setCreditRequestId(100L);
        photo.setCreatedById(99L); // uploaded by someone else
        when(photoRepository.findById(10L)).thenReturn(Optional.of(photo));
        when(creditRequestService.getById(100L, 7L, false)).thenReturn(openRequest(100L));
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));

        assertThatThrownBy(() -> service.deletePhoto(10L, 7L, false))
                .isInstanceOf(SecurityException.class);
        verify(photoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deletePhoto — admin can delete any photo, even one uploaded by another user")
    void deletePhoto_adminAnyUpload_succeeds() {
        CreditRequestPhoto photo = stub();
        photo.setId(10L);
        photo.setCreditRequestId(100L);
        photo.setCreatedById(99L);
        when(photoRepository.findById(10L)).thenReturn(Optional.of(photo));
        when(creditRequestService.getById(100L, 3L, true)).thenReturn(openRequest(100L));
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));

        service.deletePhoto(10L, 3L, true);

        verify(photoRepository).delete(photo);
    }

    @Test
    @DisplayName("uploadPhoto — saves a usable byte-array snapshot (regression guard for MultipartFile.getBytes)")
    void uploadPhoto_savesByteSnapshot() throws IOException {
        when(creditRequestService.getById(anyLong(), anyLong(), anyBoolean())).thenReturn(openRequest(100L));
        when(creditRequestService.findStatusRow(anyLong())).thenReturn(Optional.of(pendingStatus()));
        when(photoRepository.save(any(CreditRequestPhoto.class))).thenAnswer(inv -> inv.getArgument(0));
        byte[] payload = new byte[]{0x4F, 0x4B, (byte) 0xC3, 0x28};
        MultipartFile file = new MockMultipartFile("file", "snap.png", "image/png", payload);

        ArgumentCaptor<CreditRequestPhoto> captor = ArgumentCaptor.forClass(CreditRequestPhoto.class);
        service.uploadPhoto(100L, 500L, file, 7L, false);
        verify(photoRepository).save(captor.capture());

        assertThat(captor.getValue().getBlob()).containsExactly(payload);
    }

    // ── helpers ────────────────────────────────────────────────────────

    private static CreditRequest openRequest(Long id) {
        CreditRequest cr = new CreditRequest();
        cr.setId(id);
        cr.setStatusId(2L);
        cr.setBuyerCodeId(500L);
        return cr;
    }

    private static CreditRequestStatus pendingStatus() {
        return statusRow(SystemStatus.PENDING_APPROVAL);
    }

    private static CreditRequestStatus statusRow(SystemStatus status) {
        CreditRequestStatus row = new CreditRequestStatus();
        row.setId(1L);
        row.setSystemStatus(status);
        return row;
    }

    private static MultipartFile jpeg(String filename, int bytes) {
        return new MockMultipartFile("file", filename, "image/jpeg", new byte[bytes]);
    }

    private static CreditRequestPhoto stub() {
        CreditRequestPhoto p = new CreditRequestPhoto();
        p.setBlob(new byte[]{1, 2, 3});
        return p;
    }
}
