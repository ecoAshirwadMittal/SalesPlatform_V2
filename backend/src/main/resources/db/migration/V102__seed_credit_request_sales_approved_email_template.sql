-- V102__seed_credit_request_sales_approved_email_template.sql
-- Gap 2.5 Task 4. Seeds the accounting-notification email into the unified
-- email.template store (V92). Sent SYNCHRONOUSLY by
-- service/partialcredit/AccountingEmailService.sendAccountingEmail on the manual
-- admin action POST /api/v1/admin/partial-credit/{id}/send-accounting-email via
-- EmailService.sendTemplated("CreditRequestSalesApproved", ...). Port of the
-- legacy ACT_SendCreditRequestAccountingEmail (Mendix "CreditRequestSalesApproved"
-- template).
--
-- from_address / reply_to are left NULL to match the Partial-Credit + RMA
-- templates: EmailService.resolveFrom falls back to the smtp_config from-address
-- at send time, so the "from" is owned by ops config, not the row.
--
-- Recipients are NOT seeded here. The accounting distribution list is not in any
-- migrated Mendix source, so it comes from the per-environment config
-- partial-credit.accounting-email.recipients and is passed as SendOverrides.to at
-- send time. to_default stays NULL (the send always supplies an override list).
--
-- Bodies use {{varName}} substitution (TemplateRenderer) matching the vars
-- AccountingEmailService builds. No dollar-brace sequence appears anywhere in
-- this file (not even in comments), so nothing collides with Flyway's own
-- placeholder syntax (same guard the V90 / V93 / V101 seeds used). The currency
-- symbol shown to the reader is carried inside the totalAmountApproved variable
-- value (prefixed in Java), not written here.
--
-- Idempotent: ON CONFLICT (template_key) DO NOTHING so a re-run (or a hand-seed
-- in a shared dev DB) is a no-op, matching the repo's seed style.
INSERT INTO email.template (template_key, template_name, subject, content_html, content_plain, enabled, description)
VALUES (
    'CreditRequestSalesApproved',
    'CreditRequestSalesApproved',
    'Partial credit {{requestNumber}} approved for {{buyerCode}}',
    '<p>Accounting team,</p>'
    || '<p>The following partial credit request has been <strong>sales-approved</strong> and is ready for processing.</p>'
    || '<table cellpadding="4" cellspacing="0">'
    || '<tr><td><strong>Request number</strong></td><td>{{requestNumber}}</td></tr>'
    || '<tr><td><strong>Week</strong></td><td>{{weekNumber}}</td></tr>'
    || '<tr><td><strong>Buyer</strong></td><td>{{buyerName}}</td></tr>'
    || '<tr><td><strong>Buyer code</strong></td><td>{{buyerCode}}</td></tr>'
    || '<tr><td><strong>Reasons</strong></td><td>{{requestReasons}}</td></tr>'
    || '<tr><td><strong>Total devices approved</strong></td><td>{{totalDevicesApproved}}</td></tr>'
    || '<tr><td><strong>Total amount approved</strong></td><td>{{totalAmountApproved}}</td></tr>'
    || '</table>'
    || '<p>Thank you,<br/>ecoATM Direct</p>',
    'Accounting team,' || E'\n\n'
    || 'The following partial credit request has been sales-approved and is ready for processing.' || E'\n\n'
    || 'Request number: {{requestNumber}}' || E'\n'
    || 'Week: {{weekNumber}}' || E'\n'
    || 'Buyer: {{buyerName}}' || E'\n'
    || 'Buyer code: {{buyerCode}}' || E'\n'
    || 'Reasons: {{requestReasons}}' || E'\n'
    || 'Total devices approved: {{totalDevicesApproved}}' || E'\n'
    || 'Total amount approved: {{totalAmountApproved}}' || E'\n\n'
    || 'Thank you,' || E'\n' || 'ecoATM Direct' || E'\n',
    TRUE,
    'Manual admin accounting notification for a sales-approved partial credit request (ACT_SendCreditRequestAccountingEmail).'
)
ON CONFLICT (template_key) DO NOTHING;
