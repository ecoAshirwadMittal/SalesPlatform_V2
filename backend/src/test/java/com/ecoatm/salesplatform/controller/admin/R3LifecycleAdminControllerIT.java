package com.ecoatm.salesplatform.controller.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for {@link R3LifecycleAdminController}.
 *
 * <p>Runs against the full Spring Boot context + real DB. Each test method resets
 * the R3 SA lifecycle status columns to PENDING after completion so tests are
 * isolated from one another — the seed uses ON CONFLICT DO NOTHING which does
 * not overwrite rows that a previous test committed.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-reset.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class R3LifecycleAdminControllerIT {

    @Autowired MockMvc mvc;

    private static final long R3_SA_ID = 6003L;

    @Test
    @DisplayName("ROLE_ADMIN — POST /preprocess-r3 returns 200 + R3PreProcessResponse")
    @WithMockUser(roles = "Administrator")
    void admin_can_preprocess() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3", R3_SA_ID))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("ROLE_ADMIN — POST /reinit-r3 fails 422 if pre-process not yet SUCCESS")
    @WithMockUser(roles = "Administrator")
    void reinit_predecessor_guard() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reinit-r3", R3_SA_ID))
           .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("ROLE_ADMIN — POST /reinit-r3 succeeds after preprocess")
    @WithMockUser(roles = "Administrator")
    void reinit_succeeds_after_preprocess() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3", R3_SA_ID))
           .andExpect(status().isOk());
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reinit-r3", R3_SA_ID))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("non-admin → 403 on both endpoints")
    @WithMockUser(roles = "BUYER")
    void non_admin_forbidden() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3", R3_SA_ID))
           .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/reinit-r3", R3_SA_ID))
           .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("unknown id → 404")
    @WithMockUser(roles = "Administrator")
    void unknown_id_404() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3", 99999L))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("round != 3 → 422")
    @WithMockUser(roles = "Administrator")
    void wrong_round_422() throws Exception {
        mvc.perform(post("/api/v1/admin/auctions/scheduling-auctions/{id}/preprocess-r3", 6002L))
           .andExpect(status().isUnprocessableEntity());
    }
}
