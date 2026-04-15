package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.OracleResponse;
import com.ecoatm.salesplatform.model.integration.OracleConfig;
import com.ecoatm.salesplatform.repository.integration.OracleConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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
 */
@Component
public class OracleOrderClient {

    private static final Logger log = LoggerFactory.getLogger(OracleOrderClient.class);

    private final OracleConfigRepository oracleConfigRepository;
    private final ObjectMapper objectMapper;

    @Value("${oracle.username:}")
    private String oracleUsername;

    @Value("${oracle.password:}")
    private String oraclePassword;

    public OracleOrderClient(OracleConfigRepository oracleConfigRepository,
                             ObjectMapper objectMapper) {
        this.oracleConfigRepository = oracleConfigRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Send a prepared JSON payload to Oracle's Create Order endpoint.
     * Never throws — failures are returned as an {@link OracleResponse} with
     * a populated {@code returnMessage} so the caller can route to the
     * Pending_Order branch. The toggle-off path returns a stubbed success
     * response (returnCode="00") to keep local/dev flows exercising the
     * full happy-path workflow.
     */
    public OracleResponse submitOrder(String jsonPayload) {
        OracleConfig config = oracleConfigRepository.findAll().stream().findFirst().orElse(null);

        if (config == null || !Boolean.TRUE.equals(config.getIsActive())) {
            log.warn("Oracle Create Order API is toggled off or config missing — simulating success");
            OracleResponse r = new OracleResponse();
            r.setReturnCode("00");
            r.setReturnMessage("Toggle Turned Off — simulated success");
            r.setOrderNumber("SIM-" + System.currentTimeMillis());
            return r;
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
            OracleResponse r = new OracleResponse();
            r.setReturnMessage("No Token Generated: " + e.getMessage());
            return r;
        }

        if (token == null || token.isBlank()) {
            log.error("Oracle returned empty token");
            OracleResponse r = new OracleResponse();
            r.setReturnMessage("No Token Generated");
            return r;
        }

        try {
            return postCreateOrder(client, config, token, jsonPayload, timeout);
        } catch (Exception e) {
            log.error("Oracle create order request failed", e);
            OracleResponse r = new OracleResponse();
            r.setReturnMessage("Oracle API call failed: " + e.getMessage());
            return r;
        }
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
