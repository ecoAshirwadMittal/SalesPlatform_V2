package com.ecoatm.salesplatform.model.email;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Singleton SMTP configuration row for the unified email module (V92,
 * {@code email.smtp_config}). The DDL pins {@code id} to {@code 1} via
 * {@code DEFAULT 1 CHECK (id = 1)} and the migration seeds that one row —
 * there is never a second one. No {@code @GeneratedValue}: callers always
 * load and update the existing row via {@code findById(1L)}, never insert
 * a new one.
 */
@Entity
@Table(name = "smtp_config", schema = "email")
public class SmtpConfig {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "server_host", length = 255)
    private String serverHost;

    @Column(name = "server_port", nullable = false)
    private Integer serverPort = 587;

    @Column(name = "protocol", nullable = false, length = 20)
    private String protocol = "SMTP";

    @Column(name = "from_address", length = 255)
    private String fromAddress;

    @Column(name = "from_display_name", length = 255)
    private String fromDisplayName;

    @Column(name = "reply_to", length = 255)
    private String replyTo;

    @Column(name = "use_ssl", nullable = false)
    private Boolean useSsl = Boolean.FALSE;

    @Column(name = "use_tls", nullable = false)
    private Boolean useTls = Boolean.TRUE;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = Boolean.FALSE;

    @Column(name = "max_retry_attempts", nullable = false)
    private Integer maxRetryAttempts = 3;

    @Column(name = "timeout_ms", nullable = false)
    private Integer timeoutMs = 10000;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate = Instant.now();

    @Column(name = "changed_by_id")
    private Long changedById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getServerHost() { return serverHost; }
    public void setServerHost(String serverHost) { this.serverHost = serverHost; }

    public Integer getServerPort() { return serverPort; }
    public void setServerPort(Integer serverPort) { this.serverPort = serverPort; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getFromAddress() { return fromAddress; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }

    public String getFromDisplayName() { return fromDisplayName; }
    public void setFromDisplayName(String fromDisplayName) { this.fromDisplayName = fromDisplayName; }

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    public Boolean getUseSsl() { return useSsl; }
    public void setUseSsl(Boolean useSsl) { this.useSsl = useSsl; }

    public Boolean getUseTls() { return useTls; }
    public void setUseTls(Boolean useTls) { this.useTls = useTls; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Integer getMaxRetryAttempts() { return maxRetryAttempts; }
    public void setMaxRetryAttempts(Integer maxRetryAttempts) { this.maxRetryAttempts = maxRetryAttempts; }

    public Integer getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }

    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }

    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant changedDate) { this.changedDate = changedDate; }

    public Long getChangedById() { return changedById; }
    public void setChangedById(Long changedById) { this.changedById = changedById; }
}
