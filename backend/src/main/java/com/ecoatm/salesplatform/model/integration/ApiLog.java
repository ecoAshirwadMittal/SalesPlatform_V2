package com.ecoatm.salesplatform.model.integration;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_log", schema = "integration")
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @Column(name = "system_target")
    private String systemTarget;

    private String method;
    private String url;

    @Column(name = "http_status")
    private String httpStatus;

    @Column(name = "is_successful")
    private Boolean isSuccessful;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() { createdDate = LocalDateTime.now(); }
}
