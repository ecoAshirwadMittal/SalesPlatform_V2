-- ============================================================================
-- V35: Email Admin — SMTP config, templates, and email log
-- Mirrors legacy Mendix Email_Connector module (emailaccount, emailtemplate, emailmessage)
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS email;

-- ── SMTP Configuration (singleton row) ──────────────────────────────────────

CREATE TABLE email.smtp_config (
    id              BIGSERIAL PRIMARY KEY,
    server_host     VARCHAR(200),
    server_port     INT         DEFAULT 587,
    protocol        VARCHAR(10) DEFAULT 'SMTP',
    username        VARCHAR(200),
    encrypted_password TEXT,
    from_address    VARCHAR(200),
    from_display_name VARCHAR(200),
    reply_to        VARCHAR(200),
    use_ssl         BOOLEAN     DEFAULT false,
    use_tls         BOOLEAN     DEFAULT true,
    enabled         BOOLEAN     DEFAULT false,
    max_retry_attempts INT      DEFAULT 3,
    timeout_ms      INT         DEFAULT 10000,
    created_date    TIMESTAMP   DEFAULT NOW(),
    updated_date    TIMESTAMP   DEFAULT NOW()
);

-- Seed a default disabled config row
INSERT INTO email.smtp_config (server_host, server_port, from_address, from_display_name, reply_to, enabled)
VALUES ('', 587, 'wholesalebids@ecoatm.com', 'ecoATM Direct', 'wholesalebids@ecoatm.com', false);

-- ── Email Templates ─────────────────────────────────────────────────────────

CREATE TABLE email.email_template (
    id              BIGSERIAL PRIMARY KEY,
    template_name   VARCHAR(200) NOT NULL UNIQUE,
    from_address    VARCHAR(200),
    from_display_name VARCHAR(200),
    reply_to        VARCHAR(200),
    to_default      TEXT,
    cc_default      TEXT,
    bcc_default     TEXT,
    subject         VARCHAR(500),
    content_html    TEXT,
    content_plain   TEXT,
    has_attachment   BOOLEAN DEFAULT false,
    created_date    TIMESTAMP DEFAULT NOW(),
    updated_date    TIMESTAMP DEFAULT NOW()
);

-- Seed the 4 PWS email templates matching PWSEmailService
INSERT INTO email.email_template (template_name, from_display_name, reply_to, subject, content_html, from_address)
VALUES
  ('PWSOfferConfirmation', 'ecoATM Direct', 'wholesalebids@ecoatm.com',
   'PWS Offer Confirmation - {offerNumber}', NULL, 'wholesalebids@ecoatm.com'),
  ('PWSOrderConfirmation', 'ecoATM Direct', 'wholesalebids@ecoatm.com',
   'PWS Order Confirmation - {offerNumber}', NULL, 'wholesalebids@ecoatm.com'),
  ('PWSPendingOrder', 'ecoATM Direct', 'wholesalebids@ecoatm.com',
   'PWS Pending Order - {offerNumber}', NULL, 'wholesalebids@ecoatm.com'),
  ('PWSCounterOffer', 'ecoATM Direct', 'wholesalebids@ecoatm.com',
   'PWS Counter Offer - {offerNumber}', NULL, 'wholesalebids@ecoatm.com');

-- ── Email Log ───────────────────────────────────────────────────────────────

CREATE TABLE email.email_log (
    id              BIGSERIAL PRIMARY KEY,
    template_name   VARCHAR(200),
    from_address    VARCHAR(200),
    to_address      TEXT NOT NULL,
    cc              TEXT,
    bcc             TEXT,
    subject         VARCHAR(500),
    content_html    TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
    sent_date       TIMESTAMP,
    error_message   TEXT,
    retry_count     INT DEFAULT 0,
    created_date    TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_email_log_status CHECK (status IN ('SENT', 'QUEUED', 'FAILED', 'ERROR'))
);

CREATE INDEX idx_email_log_status_created ON email.email_log (status, created_date DESC);
CREATE INDEX idx_email_log_created ON email.email_log (created_date);
