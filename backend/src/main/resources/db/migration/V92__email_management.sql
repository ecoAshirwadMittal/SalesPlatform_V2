-- V92__email_management.sql
-- V35 (abandoned scaffolding for this same feature) created dead, unreferenced
-- email.smtp_config / email.email_template / email.email_log tables. No code
-- reads them; drop-and-recreate approved 2026-07-11. Drop first so the clean
-- design tables below own the smtp_config / template / log names.
DROP TABLE IF EXISTS email.email_log CASCADE;
DROP TABLE IF EXISTS email.email_template CASCADE;
DROP TABLE IF EXISTS email.smtp_config CASCADE;

CREATE TABLE email.smtp_config (
  id                  BIGINT PRIMARY KEY DEFAULT 1 CHECK (id = 1),
  server_host         VARCHAR(255),
  server_port         INT          NOT NULL DEFAULT 587,
  protocol            VARCHAR(20)  NOT NULL DEFAULT 'SMTP',
  from_address        VARCHAR(255),
  from_display_name   VARCHAR(255),
  reply_to            VARCHAR(255),
  use_ssl             BOOLEAN      NOT NULL DEFAULT FALSE,
  use_tls             BOOLEAN      NOT NULL DEFAULT TRUE,
  enabled             BOOLEAN      NOT NULL DEFAULT FALSE,
  max_retry_attempts  INT          NOT NULL DEFAULT 3,
  timeout_ms          INT          NOT NULL DEFAULT 10000,
  created_date        TIMESTAMPTZ  NOT NULL DEFAULT now(),
  changed_date        TIMESTAMPTZ  NOT NULL DEFAULT now(),
  changed_by_id       BIGINT REFERENCES identity.users(id)
);
INSERT INTO email.smtp_config (id) VALUES (1);

CREATE TABLE email.template (
  id                BIGSERIAL PRIMARY KEY,
  template_key      VARCHAR(80)  NOT NULL UNIQUE CHECK (template_key ~ '^[A-Za-z0-9_]+$'),
  template_name     VARCHAR(160) NOT NULL,
  subject           VARCHAR(255) NOT NULL,
  content_html      TEXT         NOT NULL,
  content_plain     TEXT,
  from_address      VARCHAR(255),
  from_display_name VARCHAR(255),
  reply_to          VARCHAR(255),
  to_default        VARCHAR(2000),
  cc_default        VARCHAR(2000),
  bcc_default       VARCHAR(2000),
  has_attachment    BOOLEAN      NOT NULL DEFAULT FALSE,
  enabled           BOOLEAN      NOT NULL DEFAULT TRUE,
  description       VARCHAR(500),
  created_date      TIMESTAMPTZ  NOT NULL DEFAULT now(),
  changed_date      TIMESTAMPTZ  NOT NULL DEFAULT now(),
  created_by_id     BIGINT REFERENCES identity.users(id),
  changed_by_id     BIGINT REFERENCES identity.users(id)
);

CREATE TABLE email.log (
  id              BIGSERIAL PRIMARY KEY,
  template_key    VARCHAR(80),
  from_address    VARCHAR(255),
  to_address      VARCHAR(2000) NOT NULL,
  cc              VARCHAR(2000),
  bcc             VARCHAR(2000),
  subject         VARCHAR(255),
  content_html    TEXT,
  status          VARCHAR(10)  NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','SENT','FAILED')),
  error_message   TEXT,
  retry_count     INT          NOT NULL DEFAULT 0,
  next_attempt_at TIMESTAMPTZ,
  source_module   VARCHAR(60),
  source_id       BIGINT,
  sent_date       TIMESTAMPTZ,
  created_date    TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_email_log_retry ON email.log (status, next_attempt_at);
CREATE INDEX idx_email_log_sent  ON email.log (sent_date DESC);
CREATE INDEX idx_email_log_src   ON email.log (source_module, source_id);
CREATE INDEX idx_email_log_key   ON email.log (template_key);

-- Copy the live Partial-Credit templates into the unified store (D1/D5).
INSERT INTO email.template (template_key, template_name, subject, content_html, content_plain, enabled, description, created_date, changed_date)
SELECT template_key, template_key, subject, body_html, body_text, enabled, description, created_date, changed_date
FROM partial_credit.email_templates;
