package com.ecoatm.salesplatform.model.integration;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Maps to integration.oracle_config — singleton row holding Oracle ERP connection settings.
 * Mendix equivalent: EcoATM_PWSIntegration.PWSConfiguration
 */
@Entity
@Table(name = "oracle_config", schema = "integration")
public class OracleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @Column(name = "auth_path")
    private String authPath;

    @Column(name = "create_order_path")
    private String createOrderPath;

    @Column(name = "create_rma_path")
    private String createRmaPath;

    @Column(name = "timeout_ms")
    private Integer timeoutMs;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long legacyId) { this.legacyId = legacyId; }

    public String getAuthPath() { return authPath; }
    public void setAuthPath(String authPath) { this.authPath = authPath; }

    public String getCreateOrderPath() { return createOrderPath; }
    public void setCreateOrderPath(String createOrderPath) { this.createOrderPath = createOrderPath; }

    public String getCreateRmaPath() { return createRmaPath; }
    public void setCreateRmaPath(String createRmaPath) { this.createRmaPath = createRmaPath; }

    public Integer getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}
