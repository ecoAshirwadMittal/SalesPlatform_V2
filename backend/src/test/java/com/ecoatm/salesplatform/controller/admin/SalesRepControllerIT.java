package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.buyermgmt.SalesRepresentative;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.repository.SalesRepresentativeRepository;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-context IT for {@link SalesRepController} against real Flyway'd Postgres
 * — mirrors {@link AdminEmailControllerSmokeIT}'s pattern (extends
 * {@link PostgresIntegrationTest}, {@code @AutoConfigureMockMvc}, auth injected
 * via the {@code authentication(...)} post-processor in the exact shape
 * {@code JwtAuthenticationFilter} installs from a real JWT — the principal is
 * the caller's {@code Long} user id).
 *
 * <p>{@code @Transactional} rolls back all seeded + created rows after each
 * test. Owner/changer stamping uses seeded dev user ids (9001 admin, 9003
 * salesops) so the {@code owner_id -> identity.users(id)} FK is satisfied.
 *
 * <p>Run with {@code -Dspring.flyway.validate-on-migrate=false} (the pg-test
 * profile already sets this) so the shared dev DB's checksum drift does not
 * block context boot.
 */
@AutoConfigureMockMvc
@Transactional
class SalesRepControllerIT extends PostgresIntegrationTest {

    private static final long ADMIN_USER_ID = 9001L;    // admin@test.com (Administrator)
    private static final long SALESOPS_USER_ID = 9003L; // salesops@test.com (SalesOps)

    @Autowired private MockMvc mvc;
    @Autowired private SalesRepresentativeRepository salesRepRepository;
    @Autowired private OfferRepository offerRepository;

    // ── authz matrix ──────────────────────────────────────────────────

    @Test
    @DisplayName("Bidder is forbidden (403) on create / update / delete")
    void bidderForbiddenOnWrites() throws Exception {
        mvc.perform(post("/api/v1/admin/sales-representatives").with(bidder())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"A\",\"lastName\":\"B\"}"))
           .andExpect(status().isForbidden());

        mvc.perform(put("/api/v1/admin/sales-representatives/1").with(bidder())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"A\",\"lastName\":\"B\",\"active\":true}"))
           .andExpect(status().isForbidden());

        mvc.perform(delete("/api/v1/admin/sales-representatives/1").with(bidder()))
           .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Unauthenticated write returns 401")
    void unauthenticatedReturns401() throws Exception {
        mvc.perform(post("/api/v1/admin/sales-representatives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"A\",\"lastName\":\"B\"}"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("SalesOps may create (201) — proves the second allowed role")
    void salesOpsCanCreate() throws Exception {
        mvc.perform(post("/api/v1/admin/sales-representatives").with(salesOps())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Sops\",\"lastName\":\"Createtest\"}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").isNumber())
           .andExpect(jsonPath("$.firstName").value("Sops"))
           .andExpect(jsonPath("$.active").value(true));
    }

    // ── create ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Administrator create returns 201 with a trimmed, stamped rep")
    void adminCreateReturns201() throws Exception {
        mvc.perform(post("/api/v1/admin/sales-representatives").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"  Ztest  \",\"lastName\":\"  Uniquerep  \",\"active\":true}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").isNumber())
           .andExpect(jsonPath("$.firstName").value("Ztest"))
           .andExpect(jsonPath("$.lastName").value("Uniquerep"));
    }

    @Test
    @DisplayName("Create with a case-insensitive duplicate name returns 409")
    void duplicateNameReturns409() throws Exception {
        seedRep("Dupe", "Guardtest");

        mvc.perform(post("/api/v1/admin/sales-representatives").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"dupe\",\"lastName\":\"GUARDTEST\"}"))
           .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Create with a blank name returns 400")
    void blankNameReturns400() throws Exception {
        mvc.perform(post("/api/v1/admin/sales-representatives").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"  \",\"lastName\":\"Nolastblank\"}"))
           .andExpect(status().isBadRequest());
    }

    // ── delete ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Delete a rep that has an associated offer returns 409")
    void deleteWithOffersReturns409() throws Exception {
        Long repId = seedRep("Hasoffer", "Deletetest");
        seedOfferFor(repId);

        mvc.perform(delete("/api/v1/admin/sales-representatives/" + repId).with(admin()))
           .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Delete a rep with no offers returns 204")
    void deleteCleanReturns204() throws Exception {
        Long repId = seedRep("Nooffer", "Deletetest");

        mvc.perform(delete("/api/v1/admin/sales-representatives/" + repId).with(admin()))
           .andExpect(status().isNoContent());
    }

    // ── seeding helpers ───────────────────────────────────────────────

    private Long seedRep(String first, String last) {
        SalesRepresentative rep = new SalesRepresentative();
        rep.setId(salesRepRepository.nextId());
        rep.setFirstName(first);
        rep.setLastName(last);
        rep.setActive(true);
        // created_date / changed_date are NOT NULL in the table (V8); the
        // production create path stamps them, so a raw test seed must too.
        rep.setCreatedDate(java.time.LocalDateTime.now());
        rep.setChangedDate(java.time.LocalDateTime.now());
        return salesRepRepository.saveAndFlush(rep).getId();
    }

    private void seedOfferFor(Long salesRepId) {
        Offer offer = new Offer();
        offer.setOfferType("BUYER");
        offer.setStatus("Sales_Review");
        offer.setSalesRepId(salesRepId);
        offer.setVisibleInHistory(true);
        offerRepository.saveAndFlush(offer);
    }

    // ── auth shapes (Long principal, matching JwtAuthenticationFilter) ──

    private static RequestPostProcessor admin() {
        return principal(ADMIN_USER_ID, "admin@test.com", "ROLE_Administrator");
    }

    private static RequestPostProcessor salesOps() {
        return principal(SALESOPS_USER_ID, "salesops@test.com", "ROLE_SalesOps");
    }

    private static RequestPostProcessor bidder() {
        return principal(9999L, "bidder@buyerco.com", "ROLE_Bidder");
    }

    private static RequestPostProcessor principal(long userId, String email, String role) {
        return authentication(new UsernamePasswordAuthenticationToken(
                userId, email, List.of(new SimpleGrantedAuthority(role))));
    }
}
