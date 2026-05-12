package com.ecoatm.salesplatform.model.partialcredit;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * DB-backed email template (Sprint 4 / V90). Replaces the hardcoded
 * subject + body strings that lived inside {@code ReviewCompletedEmailListener}
 * pre-Sprint-4. Three rows are seeded by V90:
 * {@code ReviewCompleted_Approved}, {@code ReviewCompleted_Declined},
 * {@code PhotoUploadRequested}.
 *
 * <p>Bodies use {@code {{varName}}} substitution rendered by
 * {@link com.ecoatm.salesplatform.service.partialcredit.EmailTemplateService}.
 * The {@code template_key} column is referenced from listener code, so
 * the migration enforces an alphanumeric-only CHECK constraint to keep
 * it usable as a Java string literal.
 */
@Entity
@Table(name = "email_templates", schema = "partial_credit")
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_key", nullable = false, unique = true, length = 80)
    private String templateKey;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "body_html", nullable = false, columnDefinition = "TEXT")
    private String bodyHtml;

    @Column(name = "body_text", columnDefinition = "TEXT")
    private String bodyText;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = Boolean.TRUE;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate = Instant.now();

    @Column(name = "created_by_id")
    private Long createdById;

    @Column(name = "changed_by_id")
    private Long changedById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getBodyHtml() { return bodyHtml; }
    public void setBodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; }
    public String getBodyText() { return bodyText; }
    public void setBodyText(String bodyText) { this.bodyText = bodyText; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant changedDate) { this.changedDate = changedDate; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long changedById) { this.changedById = changedById; }
}
