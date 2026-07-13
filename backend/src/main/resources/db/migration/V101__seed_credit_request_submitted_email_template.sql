-- V101__seed_credit_request_submitted_email_template.sql
-- Gap 2.5 Task 1. Seeds the buyer-facing partial-credit submission-confirmation
-- email into the unified email.template store (V92). Sent by
-- listener/partialcredit/CreditRequestSubmittedEmailListener on a
-- CreditRequestSubmittedEvent (DRAFT -> PENDING_APPROVAL) via
-- EmailService.sendTemplated("CreditRequestSubmitted", ...). Port of the legacy
-- SUB_SendCreditRequestSubmittedEmail (Mendix "CreditRequestSubmitted" template).
--
-- from_address / reply_to are left NULL to match the Partial-Credit + RMA
-- templates: EmailService.resolveFrom falls back to the smtp_config from-address
-- at send time, so the "from" is owned by ops config, not the row.
--
-- Bodies use {{varName}} substitution (TemplateRenderer). No dollar-brace
-- sequence appears anywhere in this file (not even in comments), so nothing
-- collides with Flyway's own placeholder syntax (same guard the V90 / V93 seeds
-- used).
--
-- Idempotent: ON CONFLICT (template_key) DO NOTHING so a re-run (or a hand-seed
-- in a shared dev DB) is a no-op, matching the repo's seed style.
INSERT INTO email.template (template_key, template_name, subject, content_html, content_plain, enabled, description)
VALUES (
    'CreditRequestSubmitted',
    'CreditRequestSubmitted',
    'Your partial credit request {{requestNumber}} has been submitted',
    '<p>Hello {{buyerName}},</p>'
    || '<p>Your partial credit request <strong>{{requestNumber}}</strong> has been <strong>submitted</strong> and is now pending review.</p>'
    || '<p>Reasons: <strong>{{requestReasons}}</strong></p>'
    || '<p>Requested total: <strong>{{totalDevices}}</strong></p>'
    || '<p>Our team will review your request and follow up with the outcome.</p>'
    || '<p>Thank you,<br/>ecoATM Direct</p>',
    'Hello {{buyerName}},' || E'\n\n'
    || 'Your partial credit request {{requestNumber}} has been submitted and is now pending review.' || E'\n\n'
    || 'Reasons: {{requestReasons}}' || E'\n'
    || 'Requested total: {{totalDevices}}' || E'\n\n'
    || 'Our team will review your request and follow up with the outcome.' || E'\n\n'
    || 'Thank you,' || E'\n' || 'ecoATM Direct' || E'\n',
    TRUE,
    'Sent to the buyer when a partial credit request is submitted (SUB_SendCreditRequestSubmittedEmail).'
)
ON CONFLICT (template_key) DO NOTHING;
