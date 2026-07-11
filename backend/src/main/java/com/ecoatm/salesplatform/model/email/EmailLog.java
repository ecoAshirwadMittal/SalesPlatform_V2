package com.ecoatm.salesplatform.model.email;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * One row per send attempt in the unified email module (V92,
 * {@code email.log}). Written on every attempt by the sender; the retry
 * finder on {@link com.ecoatm.salesplatform.repository.email.EmailLogRepository}
 * re-queries {@code PENDING}/{@code FAILED} rows whose
 * {@code next_attempt_at} has elapsed and {@code retry_count} is still
 * under the cap (T6 {@code EmailRetryWorker}); the paged
 * {@code findByStatus} finder backs the admin log list (T9).
 */
@Entity
@Table(name = "log", schema = "email")
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_key", length = 80)
    private String templateKey;

    @Column(name = "from_address", length = 255)
    private String fromAddress;

    @Column(name = "to_address", nullable = false, length = 2000)
    private String toAddress;

    @Column(name = "cc", length = 2000)
    private String cc;

    @Column(name = "bcc", length = 2000)
    private String bcc;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "content_html", columnDefinition = "TEXT")
    private String contentHtml;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private EmailStatus status = EmailStatus.PENDING;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    @Column(name = "source_module", length = 60)
    private String sourceModule;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "sent_date")
    private Instant sentDate;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }

    public String getFromAddress() { return fromAddress; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }

    public String getToAddress() { return toAddress; }
    public void setToAddress(String toAddress) { this.toAddress = toAddress; }

    public String getCc() { return cc; }
    public void setCc(String cc) { this.cc = cc; }

    public String getBcc() { return bcc; }
    public void setBcc(String bcc) { this.bcc = bcc; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContentHtml() { return contentHtml; }
    public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }

    public EmailStatus getStatus() { return status; }
    public void setStatus(EmailStatus status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public Instant getNextAttemptAt() { return nextAttemptAt; }
    public void setNextAttemptAt(Instant nextAttemptAt) { this.nextAttemptAt = nextAttemptAt; }

    public String getSourceModule() { return sourceModule; }
    public void setSourceModule(String sourceModule) { this.sourceModule = sourceModule; }

    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }

    public Instant getSentDate() { return sentDate; }
    public void setSentDate(Instant sentDate) { this.sentDate = sentDate; }

    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }
}
