package com.ecoatm.salesplatform.model.email;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Unified email template row (V92, {@code email.template}). The migration
 * seeds this table from the live Partial-Credit templates
 * ({@code partial_credit.email_templates}); senders resolve a template by
 * {@code template_key} and render {@code content_html} /
 * {@code content_plain}. Distinct from
 * {@link com.ecoatm.salesplatform.model.partialcredit.EmailTemplate}, the
 * module-local table this unified store supersedes for new call sites.
 *
 * <p>The JPA entity name is pinned to {@code EmailManagementTemplate}
 * because Hibernate defaults an entity's name to its unqualified class
 * name, and {@code partialcredit.EmailTemplate} already claims
 * {@code "EmailTemplate"} — two entities can't share one name in the same
 * persistence unit. This only affects the internal/JPQL entity name; the
 * Java type, table mapping, and derived query methods are unaffected.
 */
@Entity(name = "EmailManagementTemplate")
@Table(name = "template", schema = "email")
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_key", nullable = false, unique = true, length = 80)
    private String templateKey;

    @Column(name = "template_name", nullable = false, length = 160)
    private String templateName;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "content_html", nullable = false, columnDefinition = "TEXT")
    private String contentHtml;

    @Column(name = "content_plain", columnDefinition = "TEXT")
    private String contentPlain;

    @Column(name = "from_address", length = 255)
    private String fromAddress;

    @Column(name = "from_display_name", length = 255)
    private String fromDisplayName;

    @Column(name = "reply_to", length = 255)
    private String replyTo;

    @Column(name = "to_default", length = 2000)
    private String toDefault;

    @Column(name = "cc_default", length = 2000)
    private String ccDefault;

    @Column(name = "bcc_default", length = 2000)
    private String bccDefault;

    @Column(name = "has_attachment", nullable = false)
    private Boolean hasAttachment = Boolean.FALSE;

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

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContentHtml() { return contentHtml; }
    public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }

    public String getContentPlain() { return contentPlain; }
    public void setContentPlain(String contentPlain) { this.contentPlain = contentPlain; }

    public String getFromAddress() { return fromAddress; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }

    public String getFromDisplayName() { return fromDisplayName; }
    public void setFromDisplayName(String fromDisplayName) { this.fromDisplayName = fromDisplayName; }

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    public String getToDefault() { return toDefault; }
    public void setToDefault(String toDefault) { this.toDefault = toDefault; }

    public String getCcDefault() { return ccDefault; }
    public void setCcDefault(String ccDefault) { this.ccDefault = ccDefault; }

    public String getBccDefault() { return bccDefault; }
    public void setBccDefault(String bccDefault) { this.bccDefault = bccDefault; }

    public Boolean getHasAttachment() { return hasAttachment; }
    public void setHasAttachment(Boolean hasAttachment) { this.hasAttachment = hasAttachment; }

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
