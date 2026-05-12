package com.ecoatm.salesplatform.model.partialcredit;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * One row per email send attempt — lets us answer "did the buyer get the
 * email?" without mining stdout. Written by
 * {@link com.ecoatm.salesplatform.service.partialcredit.EmailAuditService}
 * from the {@code AFTER_COMMIT} async listener that dispatches the email,
 * so audit rows survive even if the originating business transaction is
 * already committed.
 *
 * <p>{@code credit_request_id} is nullable + {@code ON DELETE SET NULL}
 * so deleting a credit request doesn't lose the audit trail of what was
 * sent for it.
 */
@Entity
@Table(name = "email_audit", schema = "partial_credit")
public class EmailAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_key", nullable = false, length = 80)
    private String templateKey;

    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;

    @Column(name = "credit_request_id")
    private Long creditRequestId;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt = Instant.now();

    @Column(name = "success", nullable = false)
    private Boolean success;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public Long getCreditRequestId() { return creditRequestId; }
    public void setCreditRequestId(Long creditRequestId) { this.creditRequestId = creditRequestId; }
    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
