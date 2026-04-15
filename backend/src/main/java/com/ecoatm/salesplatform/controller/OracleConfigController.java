package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.OracleConfigDto;
import com.ecoatm.salesplatform.model.integration.OracleConfig;
import com.ecoatm.salesplatform.repository.integration.OracleConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Admin endpoint for Oracle ERP integration configuration.
 * Mirrors Mendix PWSConfiguration_Edit page + ACT_PWSConfiguration_TestAuthentication.
 */
@RestController
@RequestMapping("/api/v1/admin/oracle-config")
@RequiredArgsConstructor
public class OracleConfigController {

    private static final Logger log = LoggerFactory.getLogger(OracleConfigController.class);

    private final OracleConfigRepository oracleConfigRepository;
    private final ObjectMapper objectMapper;

    @Value("${oracle.username:}")
    private String oracleUsername;

    @Value("${oracle.password:}")
    private String oraclePassword;

    /**
     * GET — Mendix: DS_PWSConfiguration / SUB_PWSConfiguration_GetOrCreate
     * Returns the singleton config row, creating one with defaults if none exists.
     */
    @GetMapping
    public ResponseEntity<OracleConfigDto> getConfig() {
        OracleConfig config = getOrCreate();
        return ResponseEntity.ok(toDto(config));
    }

    /**
     * PUT — Save changes (Mendix: Save Changes action on PWSConfiguration_Edit)
     */
    @PutMapping
    public ResponseEntity<OracleConfigDto> updateConfig(@RequestBody OracleConfigDto dto) {
        OracleConfig config = getOrCreate();

        config.setAuthPath(dto.authPath());
        config.setCreateOrderPath(dto.createOrderPath());
        config.setCreateRmaPath(dto.createRmaPath());
        config.setTimeoutMs(dto.timeoutMs());
        config.setIsActive(dto.isCreateOrderApiOn());
        config.setUpdatedDate(LocalDateTime.now());

        oracleConfigRepository.save(config);
        return ResponseEntity.ok(toDto(config));
    }

    /**
     * POST /test-auth — Mendix: ACT_PWSConfiguration_TestAuthentication
     * Attempts to fetch an OAuth token from the configured auth endpoint.
     */
    @PostMapping("/test-auth")
    public ResponseEntity<Map<String, Object>> testAuthentication() {
        OracleConfig config = getOrCreate();

        if (config.getAuthPath() == null || config.getAuthPath().isBlank()) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "title", "Unable to authenticate",
                    "message", "Auth path is not configured."
            ));
        }

        if (oracleUsername == null || oracleUsername.isBlank()) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "title", "Unable to authenticate",
                    "message", "Oracle username is not configured (set oracle.username env var)."
            ));
        }

        try {
            int timeout = config.getTimeoutMs() != null ? config.getTimeoutMs() : 5000;
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(timeout))
                    .build();

            String authBody = "grant_type=client_credentials";
            String basicAuth = java.util.Base64.getEncoder().encodeToString(
                    (oracleUsername + ":" + oraclePassword).getBytes());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getAuthPath()))
                    .timeout(Duration.ofMillis(timeout))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + basicAuth)
                    .POST(HttpRequest.BodyPublishers.ofString(authBody))
                    .build();

            log.info("Oracle test-auth → {}", config.getAuthPath());
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Oracle test-auth response HTTP {}", response.statusCode());

            JsonNode json = objectMapper.readTree(response.body());
            String token = json.has("access_token") ? json.get("access_token").asText() : null;

            if (token != null && !token.isBlank()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "title", "Successfully authenticated",
                        "message", "Token received (HTTP " + response.statusCode() + ")."
                ));
            } else {
                String errMsg = json.has("error_description") ? json.get("error_description").asText()
                        : json.has("error") ? json.get("error").asText()
                        : "No access_token in response (HTTP " + response.statusCode() + ")";
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "title", "Unable to authenticate",
                        "message", errMsg
                ));
            }
        } catch (Exception e) {
            log.error("Oracle test-auth failed", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "title", "Unable to authenticate",
                    "message", "Please check the logs for more details: " + e.getMessage()
            ));
        }
    }

    // --- Helpers ---

    private OracleConfig getOrCreate() {
        return oracleConfigRepository.findAll().stream().findFirst()
                .orElseGet(() -> {
                    OracleConfig c = new OracleConfig();
                    c.setIsActive(false);
                    c.setTimeoutMs(5000);
                    c.setUpdatedDate(LocalDateTime.now());
                    return oracleConfigRepository.save(c);
                });
    }

    private OracleConfigDto toDto(OracleConfig config) {
        return new OracleConfigDto(
                config.getId(),
                oracleUsername,
                null, // never expose password
                oraclePassword != null && !oraclePassword.isBlank(),
                config.getAuthPath(),
                config.getCreateOrderPath(),
                config.getCreateRmaPath(),
                config.getTimeoutMs(),
                config.getIsActive(),
                config.getUpdatedDate() != null ? config.getUpdatedDate().toString() : null
        );
    }
}
