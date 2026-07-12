package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.OracleResponse;
import com.ecoatm.salesplatform.model.integration.OracleConfig;
import com.ecoatm.salesplatform.repository.integration.OracleConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link OracleOrderClient}'s profile-gated toggle-off behaviour
 * (RMA-functional plan open-Q3 / Task A,
 * {@code docs/tasks/rma-functional-plan-2026-07-11.md}).
 *
 * <p>Contract under test:
 * <ul>
 *   <li>dev / no profile + toggle-off / missing-config → simulated success
 *       ({@code returnCode="00"}, {@code SIM-…} order number).</li>
 *   <li>{@code production} profile + toggle-off / missing-config → error
 *       ({@code returnCode} null, {@code returnMessage} populated,
 *       {@code orderNumber} null) so the caller routes to Pending, never
 *       fake-creating a real order/RMA.</li>
 *   <li>An active config is never short-circuited to the SIM branch — the real
 *       HTTP path is attempted; a genuine token failure returns a real error in
 *       both profiles (dev does NOT downgrade a genuine failure to a SIM).</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OracleOrderClient — dev-only simulated success (open-Q3 Task A)")
class OracleOrderClientTest {

    @Mock
    private OracleConfigRepository oracleConfigRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private OracleOrderClient client(Environment environment) {
        return new OracleOrderClient(oracleConfigRepository, objectMapper, environment);
    }

    private MockEnvironment envWithProfiles(String... profiles) {
        MockEnvironment env = new MockEnvironment();
        if (profiles.length > 0) {
            env.setActiveProfiles(profiles);
        }
        return env;
    }

    private OracleConfig inactiveConfig() {
        OracleConfig c = new OracleConfig();
        c.setIsActive(false);
        return c;
    }

    private OracleConfig activeConfig(String authPath) {
        OracleConfig c = new OracleConfig();
        c.setIsActive(true);
        c.setAuthPath(authPath);
        c.setCreateOrderPath("http://oracle.example.test/order");
        c.setTimeoutMs(1000);
        return c;
    }

    // ── toggle-off / missing-config: dev → SIM ─────────────────────────────

    @Nested
    @DisplayName("dev / no profile — toggle-off simulates success")
    class DevToggleOff {

        @Test
        @DisplayName("dev profile, toggle OFF → returnCode=00 + SIM- order number")
        void devProfile_toggleOff_returnsSim() {
            when(oracleConfigRepository.findAll()).thenReturn(List.of(inactiveConfig()));

            OracleResponse r = client(envWithProfiles("dev")).submitOrder("{}");

            assertThat(r.getReturnCode()).isEqualTo("00");
            assertThat(r.getOrderNumber()).startsWith("SIM-");
        }

        @Test
        @DisplayName("no active profile, toggle OFF → returnCode=00 + SIM- order number")
        void noProfile_toggleOff_returnsSim() {
            when(oracleConfigRepository.findAll()).thenReturn(List.of(inactiveConfig()));

            OracleResponse r = client(envWithProfiles()).submitOrder("{}");

            assertThat(r.getReturnCode()).isEqualTo("00");
            assertThat(r.getOrderNumber()).startsWith("SIM-");
        }

        @Test
        @DisplayName("dev profile, config missing (null) → simulated success")
        void devProfile_configMissing_returnsSim() {
            when(oracleConfigRepository.findAll()).thenReturn(Collections.emptyList());

            OracleResponse r = client(envWithProfiles("dev")).submitOrder("{}");

            assertThat(r.getReturnCode()).isEqualTo("00");
            assertThat(r.getOrderNumber()).startsWith("SIM-");
        }
    }

    // ── toggle-off / missing-config: production → error (never SIM) ─────────

    @Nested
    @DisplayName("production — toggle-off / missing-config returns error, never SIM")
    class ProductionToggleOff {

        @Test
        @DisplayName("production profile, toggle OFF → error: no returnCode, populated returnMessage, no orderNumber")
        void productionProfile_toggleOff_returnsError() {
            when(oracleConfigRepository.findAll()).thenReturn(List.of(inactiveConfig()));

            OracleResponse r = client(envWithProfiles("production")).submitOrder("{}");

            assertThat(r.getReturnCode()).isNull();
            assertThat(r.getReturnMessage()).isNotNull();
            assertThat(r.getOrderNumber()).isNull();
        }

        @Test
        @DisplayName("production profile, config missing (null) → same error contract")
        void productionProfile_configMissing_returnsError() {
            when(oracleConfigRepository.findAll()).thenReturn(Collections.emptyList());

            OracleResponse r = client(envWithProfiles("production")).submitOrder("{}");

            assertThat(r.getReturnCode()).isNull();
            assertThat(r.getReturnMessage()).isNotNull();
            assertThat(r.getOrderNumber()).isNull();
        }
    }

    // ── real (isActive=true) path untouched; genuine failures stay errors ──

    @Nested
    @DisplayName("active config — real HTTP path attempted (SIM branch bypassed)")
    class ActiveConfigRealPath {

        // An authPath with an illegal character makes URI.create throw
        // synchronously inside fetchOracleToken — no live network call — so the
        // token-fetch branch is provably reached. Only the real path yields a
        // "No Token Generated" message; the config-off branch never would.
        private static final String INVALID_AUTH_PATH = "http://oracle host/token";

        @Test
        @DisplayName("production profile, active config + token failure → error (not SIM)")
        void productionProfile_activeConfig_tokenFailure_returnsError() {
            when(oracleConfigRepository.findAll())
                    .thenReturn(List.of(activeConfig(INVALID_AUTH_PATH)));

            OracleResponse r = client(envWithProfiles("production")).submitOrder("{}");

            assertThat(r.getReturnCode()).isNull();
            assertThat(r.getOrderNumber()).isNull();
            assertThat(r.getReturnMessage()).startsWith("No Token Generated");
        }

        @Test
        @DisplayName("dev profile, active config + genuine token failure → real error, NOT a SIM")
        void devProfile_activeConfig_genuineFailure_returnsErrorNotSim() {
            when(oracleConfigRepository.findAll())
                    .thenReturn(List.of(activeConfig(INVALID_AUTH_PATH)));

            OracleResponse r = client(envWithProfiles("dev")).submitOrder("{}");

            // Proves both: (1) the real path was attempted — an active config is
            // NOT short-circuited to the config-off SIM branch; and (2) under dev
            // a genuine failure surfaces the real error, never a SIM (only the
            // toggle-off/missing-config branch is profile-gated to SIM in dev).
            assertThat(r.getReturnMessage()).startsWith("No Token Generated");
            assertThat(r.getReturnCode()).isNull();
            assertThat(r.getOrderNumber()).isNull();
        }
    }
}
