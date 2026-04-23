package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BuyerUserGuideListResponse;
import com.ecoatm.salesplatform.dto.BuyerUserGuideMetadata;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.admin.BuyerUserGuideService;
import com.ecoatm.salesplatform.service.admin.BuyerUserGuideService.DownloadResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuyerUserGuideController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
        "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
        "app.jwt.expiration-ms=3600000",
        "app.jwt.remember-me-expiration-ms=7200000"
})
class BuyerUserGuideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private BuyerUserGuideService service;

    private String adminToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private String bidderToken() {
        return jwtService.generateToken(2L, "bidder@test.com", List.of("Bidder"), false);
    }

    private String salesOpsToken() {
        return jwtService.generateToken(3L, "salesops@test.com", List.of("SalesOps"), false);
    }

    // ---------------------------------------------------------------------------
    // GET /admin/buyer-user-guide — role gating
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("GET admin endpoint rejects unauthenticated")
    void adminList_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/buyer-user-guide"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET admin endpoint rejects Bidder role")
    void adminList_bidderToken_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/buyer-user-guide")
                        .header("Authorization", "Bearer " + bidderToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET admin endpoint rejects SalesOps role")
    void adminList_salesOpsToken_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/buyer-user-guide")
                        .header("Authorization", "Bearer " + salesOpsToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET admin endpoint returns list for Administrator")
    void adminList_admin_returnsOk() throws Exception {
        BuyerUserGuideMetadata meta = makeMeta(1L, "guide.pdf", true);
        when(service.list()).thenReturn(new BuyerUserGuideListResponse(meta, List.of(meta)));

        mockMvc.perform(get("/api/v1/admin/buyer-user-guide")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active.fileName").value("guide.pdf"))
                .andExpect(jsonPath("$.history").isArray());
    }

    @Test
    @DisplayName("GET admin endpoint returns active=null when no guide uploaded")
    void adminList_noGuide_returnsNullActive() throws Exception {
        when(service.list()).thenReturn(new BuyerUserGuideListResponse(null, List.of()));

        mockMvc.perform(get("/api/v1/admin/buyer-user-guide")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").doesNotExist());
    }

    // ---------------------------------------------------------------------------
    // POST /admin/buyer-user-guide — upload
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("POST upload rejects Bidder role")
    void upload_bidderToken_returns403() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "g.pdf", "application/pdf", new byte[8]);
        mockMvc.perform(multipart("/api/v1/admin/buyer-user-guide")
                        .file(file)
                        .header("Authorization", "Bearer " + bidderToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST upload returns 400 when service rejects content-type")
    void upload_wrongType_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "g.pdf", "application/pdf", new byte[8]);
        when(service.upload(any(), anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid content type"));

        mockMvc.perform(multipart("/api/v1/admin/buyer-user-guide")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid content type")));
    }

    @Test
    @DisplayName("POST upload returns 400 when service rejects oversized file")
    void upload_tooLarge_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "big.pdf", "application/pdf", new byte[8]);
        when(service.upload(any(), anyLong()))
                .thenThrow(new IllegalArgumentException("File too large"));

        mockMvc.perform(multipart("/api/v1/admin/buyer-user-guide")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("File too large")));
    }

    @Test
    @DisplayName("POST upload returns metadata for valid upload")
    void upload_valid_returnsMetadata() throws Exception {
        BuyerUserGuideMetadata meta = makeMeta(2L, "new-guide.pdf", true);
        when(service.upload(any(), anyLong())).thenReturn(meta);

        MockMultipartFile file = new MockMultipartFile("file", "new-guide.pdf", "application/pdf", new byte[8]);
        mockMvc.perform(multipart("/api/v1/admin/buyer-user-guide")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.fileName").value("new-guide.pdf"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    // ---------------------------------------------------------------------------
    // DELETE /admin/buyer-user-guide/{id}
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE returns 409 when guide is active")
    void delete_activeGuide_returns409() throws Exception {
        doThrow(new IllegalStateException("Cannot delete the active guide"))
                .when(service).delete(5L);

        mockMvc.perform(delete("/api/v1/admin/buyer-user-guide/5")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE returns 204 for inactive guide")
    void delete_inactive_returns204() throws Exception {
        doNothing().when(service).delete(3L);

        mockMvc.perform(delete("/api/v1/admin/buyer-user-guide/3")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE returns 404 for unknown id")
    void delete_unknownId_returns404() throws Exception {
        doThrow(new EntityNotFoundException("BuyerUserGuide not found: id=999"))
                .when(service).delete(999L);

        mockMvc.perform(delete("/api/v1/admin/buyer-user-guide/999")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------------------------
    // GET /bidder/docs/buyer-guide — streaming download
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("bidder download rejects unauthenticated")
    void bidderDownload_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/bidder/docs/buyer-guide"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("bidder download returns 404 when no guide configured")
    void bidderDownload_noGuide_returns404() throws Exception {
        when(service.download())
                .thenThrow(new EntityNotFoundException("No buyer user guide configured"));

        mockMvc.perform(get("/api/v1/bidder/docs/buyer-guide")
                        .header("Authorization", "Bearer " + bidderToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("bidder download streams PDF with correct headers")
    void bidderDownload_active_streamsPdf() throws Exception {
        byte[] pdfBytes = {0x25, 0x50, 0x44, 0x46, 0x2D};
        DownloadResult result = new DownloadResult(
                "buyer-guide.pdf", "application/pdf", pdfBytes.length,
                new ByteArrayInputStream(pdfBytes));
        when(service.download()).thenReturn(result);

        mockMvc.perform(get("/api/v1/bidder/docs/buyer-guide")
                        .header("Authorization", "Bearer " + bidderToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("inline")))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    @DisplayName("Administrator can also access the bidder download endpoint")
    void bidderDownload_admin_streamsOk() throws Exception {
        byte[] pdfBytes = {0x25, 0x50, 0x44, 0x46, 0x2D};
        DownloadResult result = new DownloadResult(
                "buyer-guide.pdf", "application/pdf", pdfBytes.length,
                new ByteArrayInputStream(pdfBytes));
        when(service.download()).thenReturn(result);

        mockMvc.perform(get("/api/v1/bidder/docs/buyer-guide")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk());
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private BuyerUserGuideMetadata makeMeta(long id, String name, boolean active) {
        return new BuyerUserGuideMetadata(id, name, "application/pdf", 4096L,
                1L, "Admin User", Instant.now(), active);
    }
}
