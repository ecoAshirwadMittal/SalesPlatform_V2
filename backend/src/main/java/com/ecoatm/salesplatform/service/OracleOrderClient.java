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
 *
 * <p><b>Toggle-off is DEV-ONLY simulated success</b> (RMA-functional plan
 * open-Q3, {@code docs/tasks/rma-functional-plan-2026-07-11.md}): when the
 * config is missing / {@code is_active=false}, the legacy behaviour of
 * returning a {@code SIM-…}/{@code returnCode="00"} stub is kept ONLY under
 * the {@code local}/{@code dev} profile so local flows exercise the full
 * happy path. Under the {@code production} profile the same toggle-off
 * returns an <em>error</em> response (empty {@code returnCode}, populated
 * {@code returnMessage}) so the caller routes to {@code Pending_Order}
 * rather than fake-creating a real order/RMA. A genuine token/network
 * failure always returns a real error (both profiles) — only the
 * toggle-off/missing-config branch is profile-gated to SIM. Mirrors the
 * {@code JwtSecretValidator}/{@code EmailSmtpValidator} production-profile
 * check idiom.
 */
@Component
public class OracleOrderClient {

    private static final Logger log = LoggerFactory.getLogger(OracleOrderClient.class);

    /** Reason surfaced (as {@code returnMessage} in prod, SIM note in dev) when Oracle is toggled off. */
    private static final String REASON_DISABLED = "Oracle API is disabled";

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
     * a populated {@code returnMessage} so the caller can route to the
     * Pending_Order branch.
     *
     * <p>Toggle-off / missing-config is profile-gated via
     * {@link #offlineOrErrorResponse(String)}: dev/local returns the
     * {@code SIM-…}/{@code returnCode="00"} stub so the happy path stays
     * exercised; production returns an error so the caller never fake-creates
     * a real order. Genuine token/create failures always return a real error
     * ({@link #errorResponse(String)}) in both profiles.
     */
    public OracleResponse submitOrder(String jsonPayload) {
        OracleConfig config = oracleConfigRepository.findAll().stream().findFirst().orElse(null);

        if (config == null || !Boolean.TRUE.equals(config.getIsActive())) {
            return offlineOrErrorResponse(REASON_DISABLED);
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
     * Response for the toggle-off / missing-config branch.
     *
     * <p>Production ({@code production} profile active) returns an error
     * {@link #errorResponse(String)} so the caller routes to Pending and never
     * fake-creates a real order/RMA. Dev/local returns the legacy simulated
     * success so local flows exercise the full happy path. This is the ONLY
     * path that can produce a SIM stub — routing every branch's error through
     * {@link #errorResponse(String)} keeps the SIM path impossible under
     * production. Mirrors the {@code JwtSecretValidator}/{@code EmailSmtpValidator}
     * {@code getActiveProfiles().contains("production")} idiom.
     */
    private OracleResponse offlineOrErrorResponse(String reason) {
        if (isProduction()) {
            log.warn("Oracle API is toggled off or config missing (production) — returning error so caller routes to Pending");
            return errorResponse(reason);
        }
        log.warn("Oracle API is toggled off or config missing (dev/local) — simulating success");
        OracleResponse r = new OracleResponse();
        r.setReturnCode("00");
        r.setReturnMessage(reason + " — simulated success");
        r.setOrderNumber("SIM-" + System.currentTimeMillis());
        return r;
    }

    /**
     * Build an error {@link OracleResponse}: no {@code returnCode} and no
     * {@code orderNumber}, only a {@code returnMessage}. A null {@code returnCode}
     * routes callers ({@code OfferService.handleOracleResponse}) to Pending_Order,
     * never Ordered — the response is never mistaken for a real create.
     */
    private OracleResponse errorResponse(String reason) {
        OracleResponse r = new OracleResponse();
        r.setReturnMessage(reason);
        return r;
    }

    private boolean isProduction() {
        return Arrays.asList(environment.getActiveProfiles()).contains("production");
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
