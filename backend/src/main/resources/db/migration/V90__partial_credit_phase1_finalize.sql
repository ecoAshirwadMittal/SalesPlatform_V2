-- ---------------------------------------------------------------------
-- V90 — Partial Credit, Phase 1 finalisation (Sprint 4).
--
-- Three changes, all additive — no destructive ops on V89 tables.
--   1. partial_credit.email_templates       — DB-backed email copy with 3 seed
--                                              rows mirroring the hardcoded
--                                              strings currently in
--                                              ReviewCompletedEmailListener,
--                                              plus a new PhotoUploadRequested
--                                              template for Sprint 4.
--   2. partial_credit.email_audit           — one row per send attempt so
--                                              "did the buyer receive the
--                                              email?" is answerable without
--                                              stdout mining.
--   3. credit_requests ON-BEHALF columns    — is_on_behalf, on_behalf_of_id,
--                                              on_behalf_buyer_code_id. Lets a
--                                              sales-rep file on behalf of a
--                                              buyer (SPKB-3659) and keeps the
--                                              request visible on both the
--                                              rep's and the buyer's landing.
--
-- Deliberately NOT included (per Sprint 4 §7 decision 2026-05-11):
--   - role_assignments seed for the V89 PartialCredit_* user_roles rows.
--     Those four rows (1101-1104) stay orphaned. Partial-credit controllers
--     use the existing global roles (Bidder / SalesRep / SalesOps /
--     Administrator) directly. Promoting the role tier is a future migration
--     if/when wholesale-eligibility gating becomes desired.
-- ---------------------------------------------------------------------

-- ---------------------------------------------------------------------
-- 1. email_templates
-- ---------------------------------------------------------------------

CREATE TABLE partial_credit.email_templates (
    id              BIGSERIAL PRIMARY KEY,
    template_key    VARCHAR(80)  NOT NULL UNIQUE,
    subject         VARCHAR(255) NOT NULL,
    body_html       TEXT         NOT NULL,
    body_text       TEXT,
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    description     VARCHAR(500),
    created_date    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    changed_date    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by_id   BIGINT REFERENCES identity.users(id),
    changed_by_id   BIGINT REFERENCES identity.users(id),
    -- template_key is referenced from listener code, so disallow anything
    -- that would force a callsite change just to look it up.
    CONSTRAINT email_templates_key_chk CHECK (template_key ~ '^[A-Za-z0-9_]+$')
);

CREATE INDEX idx_pc_email_templates_key ON partial_credit.email_templates (template_key);

-- Seeds. Bodies use {{varName}} substitution rendered by EmailTemplateService
-- (Chunk 2). The current ReviewCompletedEmailListener (V89-era) hardcodes
-- the exact strings below; seeding them verbatim means the listener flip in
-- Chunk 2 is a no-op for observable behaviour.
--
-- Note on {{approvedTotalDisplay}}: this variable carries the rendered
-- currency string (e.g. "$25.00"). We cannot put a literal dollar sign
-- next to the {{ }} pair in this file because Flyway parses dollar-brace
-- pairs as its own placeholder syntax. Currency formatting belongs in
-- the renderer anyway — the service substitutes the formatted amount
-- (sign included) as a single value.
INSERT INTO partial_credit.email_templates (template_key, subject, body_html, body_text, description) VALUES
(
    'ReviewCompleted_Approved',
    'Your partial credit request {{requestNumber}} has been approved',
    '<p>Hello,</p>'
    || '<p>Your partial credit request <strong>{{requestNumber}}</strong> for order {{orderNumber}} has been <strong>approved</strong>.</p>'
    || '<p>Approved credit total: <strong>{{approvedTotalDisplay}}</strong></p>'
    || '<p>You can view the full review breakdown in your buyer portal.</p>'
    || '<p>Thank you,<br/>ecoATM Direct</p>',
    'Hello,' || E'\n\n'
    || 'Your partial credit request {{requestNumber}} for order {{orderNumber}} has been approved.' || E'\n\n'
    || 'Approved credit total: {{approvedTotalDisplay}}' || E'\n\n'
    || 'You can view the full review breakdown in your buyer portal.' || E'\n\n'
    || 'Thank you,' || E'\n' || 'ecoATM Direct' || E'\n',
    'Sent to the buyer when an admin approves their partial credit request.'
),
(
    'ReviewCompleted_Declined',
    'Your partial credit request {{requestNumber}} has been declined',
    '<p>Hello,</p>'
    || '<p>Your partial credit request <strong>{{requestNumber}}</strong> for order {{orderNumber}} has been <strong>declined</strong>.</p>'
    || '<p>You can view the full review breakdown in your buyer portal.</p>'
    || '<p>Thank you,<br/>ecoATM Direct</p>',
    'Hello,' || E'\n\n'
    || 'Your partial credit request {{requestNumber}} for order {{orderNumber}} has been declined.' || E'\n\n'
    || 'You can view the full review breakdown in your buyer portal.' || E'\n\n'
    || 'Thank you,' || E'\n' || 'ecoATM Direct' || E'\n',
    'Sent to the buyer when an admin declines their partial credit request.'
),
(
    'PhotoUploadRequested',
    'Please upload photos for partial credit request {{requestNumber}}',
    '<p>Hello,</p>'
    || '<p>An admin reviewing your partial credit request <strong>{{requestNumber}}</strong> has asked for photos of the device on the following line:</p>'
    || '<p><em>{{wrongDeviceLineDescription}}</em></p>'
    || '<p>Please upload the requested photos {{photoUploadDeadline}}.</p>'
    || '<p>You can attach photos directly from your buyer portal.</p>'
    || '<p>Thank you,<br/>ecoATM Direct</p>',
    'Hello,' || E'\n\n'
    || 'An admin reviewing your partial credit request {{requestNumber}} has asked for photos of the device on the following line:' || E'\n\n'
    || '  {{wrongDeviceLineDescription}}' || E'\n\n'
    || 'Please upload the requested photos {{photoUploadDeadline}}.' || E'\n\n'
    || 'You can attach photos directly from your buyer portal.' || E'\n\n'
    || 'Thank you,' || E'\n' || 'ecoATM Direct' || E'\n',
    'Sent to the buyer when an admin asks for additional wrong-device photos during review.'
);

-- ---------------------------------------------------------------------
-- 2. email_audit
-- ---------------------------------------------------------------------

CREATE TABLE partial_credit.email_audit (
    id                BIGSERIAL PRIMARY KEY,
    template_key      VARCHAR(80)  NOT NULL,
    recipient_email   VARCHAR(255) NOT NULL,
    credit_request_id BIGINT REFERENCES partial_credit.credit_requests(id) ON DELETE SET NULL,
    sent_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    success           BOOLEAN      NOT NULL,
    error_message     TEXT
);

CREATE INDEX idx_pc_email_audit_request ON partial_credit.email_audit (credit_request_id);
CREATE INDEX idx_pc_email_audit_sent_at ON partial_credit.email_audit (sent_at DESC);

-- ---------------------------------------------------------------------
-- 3. credit_requests — on-behalf columns
-- ---------------------------------------------------------------------

ALTER TABLE partial_credit.credit_requests
    ADD COLUMN is_on_behalf            BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN on_behalf_of_id         BIGINT REFERENCES identity.users(id),
    ADD COLUMN on_behalf_buyer_code_id BIGINT REFERENCES buyer_mgmt.buyer_codes(id);

-- Partial index keeps the row-count small on a table that is almost entirely
-- non-on-behalf rows; "find all requests submitted on behalf of user X" is
-- the only query that hits it.
CREATE INDEX idx_cr_on_behalf_of
    ON partial_credit.credit_requests (on_behalf_of_id)
    WHERE on_behalf_of_id IS NOT NULL;
