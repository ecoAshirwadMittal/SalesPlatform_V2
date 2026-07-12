package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.OracleResponse;
import com.ecoatm.salesplatform.model.integration.OracleConfig;
import com.ecoatm.salesplatform.repository.integration.OracleConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * HTTP client for Oracle ERP Create Order integration.
 *
 * Mendix parity: SUB_Order_SendOrderToOracle →
 *   1. Read OracleConfig row; if inactive, return simulated success.
 *   2. POST to {@code authPath} for bearer token (CWS_PostToken).
 *   3. POST payload to {@code createOrderPath} with Bearer token (CWS_PostCreateOrder).
 *   4. Parse response into {@link OracleResponse}.
 *
 * Extracted from OfferService during Phase 5 of the simplification plan
 * (docs/tasks/simplification-phase5-plan.md). Keeps OfferService focused
 * on workflow orchestration; makes the HTTP surface independently
 * testable with MockRestServiceServer / wiremock.
 */
@Component
public class OracleOrderClient {

    private static final Logger log = LoggerFactory.getLogger(OracleOrderClient.class);

    private final OracleConfigRepository oracleConfigRepository;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    @Value("${oracle.username:}")
    private String oracleUsername;

    @Value("${oracle.password:}")
    private String oraclePassword;

    public OracleOrderClient(OracleConfigRepository oracleConfigRepository,
                             ObjectMapper objectMapper,
                             Environment environment) {
        this.oracleConfigRepository = oracleConfigRepository;
        this.objectMapper = objectMapper;
        this.environment = environment;
    }

    /**
     * Send a prepared JSON payload to Oracle's Create Order endpoint.
     * Never throws — failures are returned as an {@link OracleResponse} with
     * a populated {@code returnMessage} (and blank {@code returnCode}) so the
     * caller can route to the Pending_Order branch. The toggle-off / missing-
     * config path is handled by {@link #offlineOrErrorResponse()}: a simulated
     * success in local development only, and a fail-closed error response in
     * every deployed environment.
     */
    public OracleResponse submitOrder(String jsonPayload) {
        OracleConfig config = oracleConfigRepository.findAll().stream().findFirst().orElse(null);

        if (config == null || !Boolean.TRUE.equals(config.getIsActive())) {
            return offlineOrErrorResponse();
        }

        int timeout = config.getTimeoutMs() != null ? config.getTimeoutMs() : 30000;
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeout))
                .build();

        String token;
        try {
            token = fetchOracleToken(client, config, timeout);
        } catch (Exception e) {
            log.error("Oracle token request failed", e);
            return errorResponse("No Token Generated: " + e.getMessage());
        }

        if (token == null || token.isBlank()) {
            log.error("Oracle returned empty token");
            return errorResponse("No Token Generated");
        }

        try {
            return postCreateOrder(client, config, token, jsonPayload, timeout);
        } catch (Exception e) {
            log.error("Oracle create order request failed", e);
            return errorResponse("Oracle API call failed: " + e.getMessage());
        }
    }

    /**
     * Toggle-off / missing-config response.
     *
     * <p>In local development ({@link #isSimulatedOracleAllowed()}) this returns
     * a stubbed success (returnCode {@code "00"}, {@code SIM-} order number) so
     * the happy-path workflow stays exercised without a live Oracle. In every
     * deployed environment it fails CLOSED with an error response (blank
     * returnCode), so the caller routes the offer to Pending_Order instead of
     * fabricating an order number.
     *
     * <p>Why fail-closed-by-default (allowlist) rather than {@code production}-only
     * (denylist): QA is a REAL Oracle environment — Oracle is active there, and
     * local will later point at the Oracle QA config too. A fake success in QA
     * would silently drop a real order. This is an intentional divergence from
     * the repo's {@code production}-gated secret validators
     * ({@code JwtSecretValidator} / {@code DbPasswordValidator} /
     * {@code EmailSmtpValidator}): those guard a secret with a benign weak
     * default, whereas Oracle order-creation is a live integration in QA, so the
     * SIM fake-success is scoped to local dev only.
     *
     * <p>Shared helper: a future {@code submitRma} toggle-off path reuses this
     * unchanged (do not change its signature).
     */
    private OracleResponse offlineOrErrorResponse() {
        if (isSimulatedOracleAllowed()) {
            log.warn("Oracle API is toggled off or config missing — "
                    + "simulating success (local development only)");
            OracleResponse r = new OracleResponse();
            r.setReturnCode("00");
            r.setReturnMessage("Oracle API disabled — simulated success (local development only)");
            r.setOrderNumber("SIM-" + System.currentTimeMillis());
            return r;
        }
        log.warn("Oracle API is toggled off or config missing — failing closed "
                + "(deployed environment); routing to Pending_Order");
        return errorResponse("Oracle API is disabled");
    }

    /**
     * True ONLY for local development: no active profile (the default when the
     * app runs via {@code mvn spring-boot:run}), or an explicit {@code local} /
     * {@code dev} profile. Every other profile ({@code production}, {@code qa},
     * {@code staging}, or any named profile) returns false, so the Oracle
     * toggle-off path fails closed.
     */
    private boolean isSimulatedOracleAllowed() {
        List<String> active = Arrays.asList(environment.getActiveProfiles());
        return active.isEmpty() || active.contains("local") || active.contains("dev");
    }

    /**
     * Build an error {@link OracleResponse}: a blank {@code returnCode} is the
     * "not a success" signal the caller keys on to route the offer to
     * Pending_Order. Shared by the fail-closed toggle-off branch and the genuine
     * token-fetch / create-call failure paths (which fail this way in ALL
     * profiles, local dev included — a real failure is never simulated away).
     */
    private OracleResponse errorResponse(String message) {
        OracleResponse r = new OracleResponse();
        r.setReturnMessage(message);
        return r;
    }

    /**
     * POST to Oracle auth endpoint to get a bearer token.
     * Mendix: CWS_PostToken — POST to PWSConfiguration.OracleAPIPathToken (authPath).
     * Body is {@code grant_type=client_credentials}; basic auth header carries
     * client_id/secret sourced from {@code oracle.username}/{@code oracle.password}.
     */
    private String fetchOracleToken(HttpClient client, OracleConfig config, int timeout) throws Exception {
        String authBody = "grant_type=client_credentials";
        String basicAuth = Base64.getEncoder().encodeToString(
                (oracleUsername + ":" + oraclePassword).getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getAuthPath()))
                .timeout(Duration.ofMillis(timeout))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + basicAuth)
                .POST(HttpRequest.BodyPublishers.ofString(authBody))
                .build();

        log.info("Oracle token request → {}", config.getAuthPath());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Oracle token response HTTP {}", response.statusCode());

        JsonNode json = objectMapper.readTree(response.body());
        return json.has("access_token") ? json.get("access_token").asText() : null;
    }

    /**
     * POST order payload to Oracle create order endpoint.
     * Mendix: CWS_PostCreateOrder — POST to PWSConfiguration.OracleAPIPathCreateOrder (createOrderPath)
     * with Bearer token and JSON body. Parses both camelCase and PascalCase
     * response keys to match observed Oracle behavior.
     */
    private OracleResponse postCreateOrder(HttpClient client, OracleConfig config,
                                           String token, String jsonPayload, int timeout) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getCreateOrderPath()))
                .timeout(Duration.ofMillis(timeout))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        log.info("Oracle create order request → {}", config.getCreateOrderPath());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Oracle create order response HTTP {} body={}", response.statusCode(), response.body());

        OracleResponse oracleResponse = new OracleResponse();
        oracleResponse.setHttpCode(response.statusCode());
        oracleResponse.setJsonResponse(response.body());

        try {
            JsonNode json = objectMapper.readTree(response.body());
            JsonNode resp = json.has("response") ? json.get("response") : json;
            oracleResponse.setReturnCode(
                    resp.has("returnCode") ? resp.get("returnCode").asText()
                    : resp.has("ReturnCode") ? resp.get("ReturnCode").asText() : null);
            oracleResponse.setReturnMessage(
                    resp.has("returnMessage") ? resp.get("returnMessage").asText()
                    : resp.has("ReturnMessage") ? resp.get("ReturnMessage").asText() : null);
            oracleResponse.setOrderNumber(
                    resp.has("orderNumber") ? resp.get("orderNumber").asText()
                    : resp.has("OrderNumber") ? resp.get("OrderNumber").asText() : null);
            oracleResponse.setOrderId(
                    resp.has("orderId") ? resp.get("orderId").asText()
                    : resp.has("OrderId") ? resp.get("OrderId").asText() : null);
        } catch (Exception e) {
            log.warn("Failed to parse Oracle response JSON", e);
            oracleResponse.setReturnMessage("Failed to parse Oracle response: " + e.getMessage());
        }

        return oracleResponse;
    }
}
