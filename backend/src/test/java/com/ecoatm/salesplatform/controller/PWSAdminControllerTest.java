package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PWSAdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class PWSAdminControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;
    @MockBean private JdbcTemplate jdbc;

    private String adminToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private String bidderToken() {
        return jwtService.generateToken(2L, "bidder@test.com", List.of("Bidder"), false);
    }

    // --- SEC-03: Admin endpoints require ADMIN role ---

    @Test
    @DisplayName("admin endpoints reject unauthenticated requests")
    void noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/feature-flags"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("admin endpoints reject non-ADMIN roles")
    void bidderToken_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/feature-flags")
                        .header("Authorization", "Bearer " + bidderToken()))
                .andExpect(status().isForbidden());
    }

    // --- Feature Flags ---

    @Nested
    @DisplayName("Feature Flags")
    class FeatureFlags {

        @Test
        void list_returns200() throws Exception {
            when(jdbc.queryForList(contains("feature_flag")))
                    .thenReturn(List.of(Map.of("id", 1, "name", "ATP_SYNC", "active", true)));

            mockMvc.perform(get("/api/v1/admin/feature-flags")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("ATP_SYNC"));
        }

        @Test
        void create_returns200() throws Exception {
            when(jdbc.update(contains("INSERT INTO pws.feature_flag"), any(), any(), any()))
                    .thenReturn(1);

            mockMvc.perform(post("/api/v1/admin/feature-flags")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"NEW_FLAG\",\"active\":true,\"description\":\"desc\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void update_returns200() throws Exception {
            when(jdbc.update(contains("UPDATE pws.feature_flag"), any(), any(), any(), any(), eq(1L)))
                    .thenReturn(1);

            mockMvc.perform(put("/api/v1/admin/feature-flags/1")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"UPDATED\",\"active\":false,\"description\":\"updated\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void delete_returns200() throws Exception {
            when(jdbc.update(contains("DELETE FROM pws.feature_flag"), eq(1L))).thenReturn(1);

            mockMvc.perform(delete("/api/v1/admin/feature-flags/1")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    // --- Error Messages ---

    @Nested
    @DisplayName("Error Messages")
    class ErrorMessages {

        @Test
        void list_returns200() throws Exception {
            when(jdbc.queryForList(contains("error_mapping")))
                    .thenReturn(List.of(Map.of("id", 1, "source_system", "ORACLE")));

            mockMvc.perform(get("/api/v1/admin/error-messages")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].source_system").value("ORACLE"));
        }

        @Test
        void create_returns200() throws Exception {
            mockMvc.perform(post("/api/v1/admin/error-messages")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"sourceSystem\":\"ORACLE\",\"sourceErrorCode\":\"E001\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void update_returns200() throws Exception {
            mockMvc.perform(put("/api/v1/admin/error-messages/1")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"sourceSystem\":\"ORACLE\",\"sourceErrorCode\":\"E001\",\"userErrorCode\":\"U001\",\"userErrorMessage\":\"Error\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void delete_returns200() throws Exception {
            mockMvc.perform(delete("/api/v1/admin/error-messages/1")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());
        }
    }

    // --- PWS Constants ---

    @Nested
    @DisplayName("PWS Constants")
    class PWSConstants {

        @Test
        void get_existingConfig_returns200() throws Exception {
            when(jdbc.queryForList(contains("pws_constants")))
                    .thenReturn(List.of(Map.of("sla_days", 2, "send_first_reminder", true)));

            mockMvc.perform(get("/api/v1/admin/pws-constants")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sla_days").value(2));
        }

        @Test
        void get_emptyDb_createsDefault() throws Exception {
            when(jdbc.queryForList(contains("pws_constants")))
                    .thenReturn(List.of())
                    .thenReturn(List.of(Map.of("sla_days", 2)));

            mockMvc.perform(get("/api/v1/admin/pws-constants")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());

            verify(jdbc).queryForList(contains("INSERT INTO pws.pws_constants"));
        }

        @Test
        void update_returns200() throws Exception {
            mockMvc.perform(put("/api/v1/admin/pws-constants")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"slaDays\":3,\"sendFirstReminder\":true}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    // --- Order Status ---

    @Nested
    @DisplayName("Order Status")
    class OrderStatus {

        @Test
        void list_returns200() throws Exception {
            when(jdbc.queryForList(contains("order_status_config")))
                    .thenReturn(List.of(Map.of("id", 1, "system_status", "Ordered")));

            mockMvc.perform(get("/api/v1/admin/order-status")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].system_status").value("Ordered"));
        }

        @Test
        void create_returns200() throws Exception {
            mockMvc.perform(post("/api/v1/admin/order-status")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"systemStatus\":\"New\",\"internalStatusText\":\"New Order\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void update_returns200() throws Exception {
            mockMvc.perform(put("/api/v1/admin/order-status/1")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"systemStatus\":\"Ordered\",\"internalStatusText\":\"Order Placed\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void delete_returns200() throws Exception {
            mockMvc.perform(delete("/api/v1/admin/order-status/1")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());
        }
    }

    // --- Maintenance Mode ---

    @Nested
    @DisplayName("Maintenance Mode")
    class MaintenanceMode {

        @Test
        void get_existingConfig_returns200() throws Exception {
            when(jdbc.queryForList(contains("maintenance_mode")))
                    .thenReturn(List.of(Map.of("is_enabled", false)));

            mockMvc.perform(get("/api/v1/admin/maintenance-mode")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.is_enabled").value(false));
        }

        @Test
        void get_emptyDb_createsDefault() throws Exception {
            when(jdbc.queryForList(contains("maintenance_mode")))
                    .thenReturn(List.of())
                    .thenReturn(List.of(Map.of("is_enabled", false)));

            mockMvc.perform(get("/api/v1/admin/maintenance-mode")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());

            verify(jdbc).queryForList(contains("INSERT INTO pws.maintenance_mode"));
        }

        @Test
        void update_returns200() throws Exception {
            mockMvc.perform(put("/api/v1/admin/maintenance-mode")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"isEnabled\":true,\"bannerTitle\":\"Down for maintenance\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    // --- Ranks Config ---

    @Nested
    @DisplayName("Ranks Config")
    class RanksConfig {

        @Test
        void list_returns200() throws Exception {
            when(jdbc.queryForList(contains("mdm.brand")))
                    .thenReturn(List.of(Map.of("id", 1, "name", "Apple")));

            mockMvc.perform(get("/api/v1/admin/ranks-config")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Apple"));
        }

        @Test
        void update_returns200() throws Exception {
            mockMvc.perform(put("/api/v1/admin/ranks-config/1")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Apple\",\"displayName\":\"Apple Inc.\",\"isEnabled\":true,\"sortRank\":1}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    // --- RMA Status ---

    @Nested
    @DisplayName("RMA Status")
    class RmaStatus {

        @Test
        void list_returns200() throws Exception {
            when(jdbc.queryForList(contains("rma_status")))
                    .thenReturn(List.of(Map.of("id", 1, "system_status", "Open")));

            mockMvc.perform(get("/api/v1/admin/rma-status")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());
        }

        @Test
        void create_returns200() throws Exception {
            mockMvc.perform(post("/api/v1/admin/rma-status")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"systemStatus\":\"Open\",\"internalStatusText\":\"RMA Open\"}"))
                    .andExpect(status().isOk());
        }

        @Test
        void update_returns200() throws Exception {
            mockMvc.perform(put("/api/v1/admin/rma-status/1")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"systemStatus\":\"Open\",\"internalStatusText\":\"RMA Open\",\"statusGroup\":\"active\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void delete_returns200() throws Exception {
            mockMvc.perform(delete("/api/v1/admin/rma-status/1")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());
        }
    }

    // --- RMA Templates ---

    @Nested
    @DisplayName("RMA Templates")
    class RmaTemplates {

        @Test
        void list_returns200() throws Exception {
            when(jdbc.queryForList(contains("rma_template")))
                    .thenReturn(List.of(Map.of("id", 1)));
            when(jdbc.queryForList(contains("rma_reason")))
                    .thenReturn(List.of(Map.of("id", 1)));

            mockMvc.perform(get("/api/v1/admin/rma-templates")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.templates").isArray())
                    .andExpect(jsonPath("$.reasons").isArray());
        }

        @Test
        void createReason_returns200() throws Exception {
            mockMvc.perform(post("/api/v1/admin/rma-reasons")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"validReason\":\"Damaged\",\"isActive\":true}"))
                    .andExpect(status().isOk());
        }

        @Test
        void updateTemplate_returns200() throws Exception {
            mockMvc.perform(put("/api/v1/admin/rma-templates/1")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"templateName\":\"Default\",\"isActive\":true,\"fileName\":\"tmpl.pdf\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void updateReason_returns200() throws Exception {
            mockMvc.perform(put("/api/v1/admin/rma-reasons/1")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"validReason\":\"Damaged\",\"isActive\":true}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void deleteReason_returns200() throws Exception {
            mockMvc.perform(delete("/api/v1/admin/rma-reasons/1")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());
        }
    }

    // --- Navigation Menu ---

    @Nested
    @DisplayName("Navigation Menu")
    class NavigationMenu {

        @Test
        void list_returns200() throws Exception {
            when(jdbc.queryForList(contains("navigation_menu")))
                    .thenReturn(List.of(Map.of("id", 1, "name", "Home")));

            mockMvc.perform(get("/api/v1/admin/navigation-menu")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());
        }

        @Test
        void create_returns200() throws Exception {
            mockMvc.perform(post("/api/v1/admin/navigation-menu")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"New Page\",\"isActive\":true,\"sortOrder\":1}"))
                    .andExpect(status().isOk());
        }

        @Test
        void update_returns200() throws Exception {
            mockMvc.perform(put("/api/v1/admin/navigation-menu/1")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Home\",\"isActive\":true,\"sortOrder\":1,\"userGroup\":\"admin\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void delete_returns200() throws Exception {
            mockMvc.perform(delete("/api/v1/admin/navigation-menu/1")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk());
        }
    }

    // --- Deposco Config ---

    @Nested
    @DisplayName("Deposco Config")
    class DeposcoConfig {

        @Test
        void get_existingConfig_returns200() throws Exception {
            when(jdbc.queryForList(contains("deposco_config")))
                    .thenReturn(List.of(Map.of("id", 1, "base_url", "https://api.deposco.com")));

            mockMvc.perform(get("/api/v1/admin/deposco")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.config.base_url").value("https://api.deposco.com"));
        }

        @Test
        void get_emptyDb_returnsEmptyConfig() throws Exception {
            when(jdbc.queryForList(contains("deposco_config")))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/v1/admin/deposco")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.config").isEmpty());
        }

        @Test
        void update_existingConfig_returns200() throws Exception {
            when(jdbc.queryForList(contains("SELECT id FROM integration.deposco_config")))
                    .thenReturn(List.of(Map.of("id", 1)));

            mockMvc.perform(put("/api/v1/admin/deposco")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"baseUrl\":\"https://new.api.com\",\"username\":\"user\",\"password\":\"secret\",\"timeoutMs\":5000,\"isActive\":true}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void update_noExistingConfig_insertsNew() throws Exception {
            when(jdbc.queryForList(contains("SELECT id FROM integration.deposco_config")))
                    .thenReturn(List.of());

            mockMvc.perform(put("/api/v1/admin/deposco")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"baseUrl\":\"https://api.deposco.com\",\"username\":\"user\",\"password\":\"pw\",\"timeoutMs\":5000,\"isActive\":true}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    // --- SLA Tags ---

    @Nested
    @DisplayName("SLA Tags")
    class SlaTags {

        @Test
        void set_returns200() throws Exception {
            when(jdbc.update(contains("offer_beyond_sla = true"))).thenReturn(3);

            mockMvc.perform(post("/api/v1/admin/sla-tags/set")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("SLA tags set on 3 overdue offer(s)."));
        }

        @Test
        void remove_returns200() throws Exception {
            when(jdbc.update(contains("offer_beyond_sla = false"))).thenReturn(2);

            mockMvc.perform(post("/api/v1/admin/sla-tags/remove")
                            .header("Authorization", "Bearer " + adminToken()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("SLA tags removed from 2 offer(s)."));
        }
    }

    // --- Snowflake Sync ---

    @Test
    @DisplayName("snowflake sync returns stubbed success")
    void snowflakeSync_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/admin/snowflake/sync")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Stubbed")));
    }
}
