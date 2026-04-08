package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.DeviceResponse;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.repository.pws.CaseLotRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.PwsInventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class InventoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private PwsInventoryService inventoryService;
    @MockBean private CaseLotRepository caseLotRepository;

    private String validToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private DeviceResponse makeDeviceResponse(Long id, String sku) {
        Device d = new Device();
        d.setId(id);
        d.setSku(sku);
        return DeviceResponse.fromEntity(d);
    }

    // --- Auth enforcement ---

    @Test
    void listDevices_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/devices"))
                .andExpect(status().isUnauthorized());
    }

    // --- List devices ---

    @Test
    void listDevices_withToken_returns200() throws Exception {
        when(inventoryService.listActiveDevices()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/inventory/devices")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // --- Get device by ID ---

    @Test
    void getDeviceById_withToken_returns200() throws Exception {
        when(inventoryService.getDeviceById(1L)).thenReturn(makeDeviceResponse(1L, "PWS001"));

        mockMvc.perform(get("/api/v1/inventory/devices/1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("PWS001"));
    }

    @Test
    void getDeviceById_notFound_returns404() throws Exception {
        when(inventoryService.getDeviceById(999L))
                .thenThrow(new IllegalArgumentException("Device not found"));

        mockMvc.perform(get("/api/v1/inventory/devices/999")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Device not found"));
    }

    // --- Get device by SKU ---

    @Test
    void getDeviceBySku_withToken_returns200() throws Exception {
        when(inventoryService.getDeviceBySku("PWS001")).thenReturn(makeDeviceResponse(1L, "PWS001"));

        mockMvc.perform(get("/api/v1/inventory/devices")
                        .param("sku", "PWS001")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("PWS001"));
    }

    // --- Create device ---

    @Test
    void createDevice_withToken_returns201() throws Exception {
        when(inventoryService.createDevice(any())).thenReturn(makeDeviceResponse(1L, "PWS002"));

        mockMvc.perform(post("/api/v1/inventory/devices")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"PWS002\",\"brandId\":1,\"categoryId\":1,\"modelId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("PWS002"));
    }

    @Test
    void createDevice_invalidInput_returns400() throws Exception {
        when(inventoryService.createDevice(any()))
                .thenThrow(new IllegalArgumentException("SKU already exists"));

        mockMvc.perform(post("/api/v1/inventory/devices")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"DUP001\",\"brandId\":1,\"categoryId\":1,\"modelId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("SKU already exists"));
    }

    // --- Case lots ---

    @Test
    void listCaseLots_withToken_returns200() throws Exception {
        when(caseLotRepository.findByIsActiveTrueOrderByIdAsc()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/inventory/case-lots")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
