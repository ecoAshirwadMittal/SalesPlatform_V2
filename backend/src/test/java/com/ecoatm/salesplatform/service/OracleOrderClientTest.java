package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.OracleResponse;
import com.ecoatm.salesplatform.model.integration.OracleConfig;
import com.ecoatm.salesplatform.repository.integration.OracleConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.StandardEnvironment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Fail-closed profile gate for the Oracle toggle-off / missing-config path.
 *
 * <p>Why: QA is a REAL Oracle environment (Oracle is active there; local will
 * later point at the Oracle QA config too). A simulated "success" response in
 * any deployed environment would fabricate an order number and silently drop a
 * real order, so the SIM stub is scoped to LOCAL DEVELOPMENT ONLY: the default
 * (no active profile — how {@code mvn spring-boot:run} runs locally) plus the
 * {@code local} / {@code dev} profiles. Every other profile
 * ({@code production} / {@code qa} / {@code staging} / any named profile)
 * fails closed — a blank {@code returnCode} routes the caller to Pending_Order
 * instead of fake-creating the order.
 */
class OracleOrderClientTest {

    private static final String DISABLED_MESSAGE = "Oracle API is disabled";

    /** No Oracle config row present → the toggle-off / missing-config branch. */
    private static OracleOrderClient clientWithNoConfig(String... profiles) {
        OracleConfigRepository repo = mock(OracleConfigRepository.class);
        when(repo.findAll()).thenReturn(List.of());
        return new OracleOrderClient(repo, new ObjectMapper(), envWithProfiles(profiles));
    }

    /** Config row present but {@code is_active = false} → same toggle-off branch. */
    private static OracleOrderClient clientWithInactiveConfig(String... profiles) {
        OracleConfig inactive = new OracleConfig();
        inactive.setIsActive(false);
        OracleConfigRepository repo = mock(OracleConfigRepository.class);
        when(repo.findAll()).thenReturn(List.of(inactive));
        return new OracleOrderClient(repo, new ObjectMapper(), envWithProfiles(profiles));
    }

    private static StandardEnvironment envWithProfiles(String... profiles) {
        StandardEnvironment env = new StandardEnvironment();
        env.setActiveProfiles(profiles);
        return env;
    }

    private static void assertFailedClosed(OracleResponse r) {
        // blank returnCode is the "not a success" signal the caller keys on
        assertThat(r.getReturnCode()).isNullOrEmpty();
        assertThat(r.getReturnMessage()).isEqualTo(DISABLED_MESSAGE);
        // no fabricated SIM order number leaks out of a deployed environment
        assertThat(r.getOrderNumber()).isNull();
    }

    private static void assertSimulatedSuccess(OracleResponse r) {
        assertThat(r.getReturnCode()).isEqualTo("00");
        assertThat(r.getOrderNumber()).startsWith("SIM-");
    }

    // ── fail-closed: every deployed environment ──────────────────────────

    @Test
    @DisplayName("qa profile + toggle off → fails closed (blank returnCode)")
    void qaProfile_toggleOff_failsClosed() {
        assertFailedClosed(clientWithNoConfig("qa").submitOrder("{}"));
    }

    @Test
    @DisplayName("qa profile + inactive config → fails closed")
    void qaProfile_inactiveConfig_failsClosed() {
        assertFailedClosed(clientWithInactiveConfig("qa").submitOrder("{}"));
    }

    @Test
    @DisplayName("production profile + toggle off → fails closed")
    void productionProfile_toggleOff_failsClosed() {
        assertFailedClosed(clientWithNoConfig("production").submitOrder("{}"));
    }

    @Test
    @DisplayName("staging profile + toggle off → fails closed")
    void stagingProfile_toggleOff_failsClosed() {
        assertFailedClosed(clientWithNoConfig("staging").submitOrder("{}"));
    }

    @Test
    @DisplayName("unknown named profile + toggle off → fails closed (default deny)")
    void unknownProfile_toggleOff_failsClosed() {
        assertFailedClosed(clientWithNoConfig("some-other-env").submitOrder("{}"));
    }

    // ── SIM allowed: local development only ──────────────────────────────

    @Test
    @DisplayName("no active profile (mvn spring-boot:run default) + toggle off → simulated success")
    void defaultProfile_toggleOff_simulatesSuccess() {
        assertSimulatedSuccess(clientWithNoConfig().submitOrder("{}"));
    }

    @Test
    @DisplayName("local profile + toggle off → simulated success")
    void localProfile_toggleOff_simulatesSuccess() {
        assertSimulatedSuccess(clientWithNoConfig("local").submitOrder("{}"));
    }

    @Test
    @DisplayName("dev profile + toggle off → simulated success")
    void devProfile_toggleOff_simulatesSuccess() {
        assertSimulatedSuccess(clientWithNoConfig("dev").submitOrder("{}"));
    }

    // ── submitRma reuses the SAME offlineOrErrorResponse() gate ──────────
    // Task B0: the RMA create path must fail closed in deployed environments
    // and SIM only in local dev, identically to submitOrder — proving it
    // shares the hardened helper rather than re-implementing the toggle.

    @Test
    @DisplayName("submitRma: qa profile + toggle off → fails closed (blank returnCode)")
    void submitRma_qaProfile_toggleOff_failsClosed() {
        assertFailedClosed(clientWithNoConfig("qa").submitRma("{}"));
    }

    @Test
    @DisplayName("submitRma: production profile + inactive config → fails closed")
    void submitRma_productionProfile_inactiveConfig_failsClosed() {
        assertFailedClosed(clientWithInactiveConfig("production").submitRma("{}"));
    }

    @Test
    @DisplayName("submitRma: no active profile (mvn spring-boot:run default) → simulated success")
    void submitRma_defaultProfile_toggleOff_simulatesSuccess() {
        assertSimulatedSuccess(clientWithNoConfig().submitRma("{}"));
    }

    @Test
    @DisplayName("submitRma: dev profile + toggle off → simulated success")
    void submitRma_devProfile_toggleOff_simulatesSuccess() {
        assertSimulatedSuccess(clientWithNoConfig("dev").submitRma("{}"));
    }
}
