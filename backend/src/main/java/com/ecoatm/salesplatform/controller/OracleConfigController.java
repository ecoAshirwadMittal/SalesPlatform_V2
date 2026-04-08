package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.model.integration.OracleConfig;
import com.ecoatm.salesplatform.repository.integration.OracleConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OracleConfigRepository oracleConfigRepository;

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

        config.setUsername(dto.username);
        if (dto.password != null && !dto.password.isBlank()) {
            config.setPasswordHash(java.util.Base64.getEncoder().encodeToString(dto.password.getBytes()));
        }
        config.setAuthPath(dto.authPath);
        config.setCreateOrderPath(dto.createOrderPath);
        config.setCreateRmaPath(dto.createRmaPath);
        config.setTimeoutMs(dto.timeoutMs);
        config.setIsActive(dto.isCreateOrderApiOn);
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

        if (config.getUsername() == null || config.getUsername().isBlank()) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "title", "Unable to authenticate",
                    "message", "Username is not configured."
            ));
        }

        try {
            int timeout = config.getTimeoutMs() != null ? config.getTimeoutMs() : 5000;
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(timeout))
                    .build();

            String authBody = "grant_type=client_credentials";
            String rawPassword;
            try {
                rawPassword = new String(java.util.Base64.getDecoder().decode(config.getPasswordHash()));
            } catch (Exception e) {
                rawPassword = config.getPasswordHash(); // fallback for legacy plaintext
            }
            String basicAuth = java.util.Base64.getEncoder().encodeToString(
                    (config.getUsername() + ":" + rawPassword).getBytes());

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
        OracleConfigDto dto = new OracleConfigDto();
        dto.id = config.getId();
        dto.username = config.getUsername();
        dto.password = null; // never expose password
        dto.hasPassword = config.getPasswordHash() != null && !config.getPasswordHash().isBlank();
        dto.authPath = config.getAuthPath();
        dto.createOrderPath = config.getCreateOrderPath();
        dto.createRmaPath = config.getCreateRmaPath();
        dto.timeoutMs = config.getTimeoutMs();
        dto.isCreateOrderApiOn = config.getIsActive();
        dto.updatedDate = config.getUpdatedDate() != null ? config.getUpdatedDate().toString() : null;
        return dto;
    }

    public static class OracleConfigDto {
        public Long id;
        public String username;
        public String password;
        public Boolean hasPassword;
        public String authPath;
        public String createOrderPath;
        public String createRmaPath;
        public Integer timeoutMs;
        public Boolean isCreateOrderApiOn;
        public String updatedDate;
    }
}
