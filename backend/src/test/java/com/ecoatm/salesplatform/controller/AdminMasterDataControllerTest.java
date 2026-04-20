package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.MasterDataItemDto;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.AdminMasterDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminMasterDataController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class AdminMasterDataControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;
    @MockBean private AdminMasterDataService service;

    private String adminToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private String bidderToken() {
        return jwtService.generateToken(2L, "bidder@test.com", List.of("Bidder"), false);
    }

    @Test
    @DisplayName("list rejects unauthenticated")
    void list_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/master-data/brands"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("list rejects non-admin role")
    void list_bidderToken_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/master-data/brands")
                        .header("Authorization", "Bearer " + bidderToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("list returns page for admin")
    void list_admin_returnsPage() throws Exception {
        MasterDataItemDto dto = new MasterDataItemDto(1L, "Apple", "Apple Inc", true, 1);
        when(service.list(eq("brands"), eq(0), eq(50)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/v1/admin/master-data/brands")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Apple"));
    }

    @Test
    @DisplayName("list returns 400 for unknown type")
    void list_unknownType_returns400() throws Exception {
        when(service.list(eq("widgets"), eq(0), eq(50)))
                .thenThrow(new IllegalArgumentException("Unknown master data type: widgets"));

        mockMvc.perform(get("/api/v1/admin/master-data/widgets")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Unknown")));
    }

    @Test
    @DisplayName("create persists new item")
    void create_admin_returnsCreated() throws Exception {
        MasterDataItemDto dto = new MasterDataItemDto(99L, "Samsung", null, true, null);
        when(service.create(eq("brands"), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/admin/master-data/brands")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Samsung\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.name").value("Samsung"));
    }

    @Test
    @DisplayName("create rejects blank name with 400")
    void create_blankName_returns400() throws Exception {
        when(service.create(eq("brands"), any()))
                .thenThrow(new IllegalArgumentException("name is required"));

        mockMvc.perform(post("/api/v1/admin/master-data/brands")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("update returns updated item")
    void update_admin_returnsItem() throws Exception {
        MasterDataItemDto dto = new MasterDataItemDto(5L, "NewName", null, true, null);
        when(service.update(eq("brands"), eq(5L), any())).thenReturn(dto);

        mockMvc.perform(put("/api/v1/admin/master-data/brands/5")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewName\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));
    }

    @Test
    @DisplayName("softDelete returns 409 when already disabled")
    void softDelete_alreadyDisabled_returns409() throws Exception {
        when(service.softDelete(eq("brands"), eq(3L)))
                .thenThrow(new IllegalStateException("brands 3 is already disabled"));

        mockMvc.perform(delete("/api/v1/admin/master-data/brands/3")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("softDelete succeeds for enabled item")
    void softDelete_enabled_returnsOk() throws Exception {
        MasterDataItemDto dto = new MasterDataItemDto(3L, "Foo", null, false, null);
        when(service.softDelete(eq("brands"), eq(3L))).thenReturn(dto);

        mockMvc.perform(delete("/api/v1/admin/master-data/brands/3")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isEnabled").value(false));
    }
}
