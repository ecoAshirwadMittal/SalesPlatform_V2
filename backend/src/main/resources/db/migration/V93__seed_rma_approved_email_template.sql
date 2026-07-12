-- V93__seed_rma_approved_email_template.sql
-- Gap-analysis #3 "Make RMA Functional", Task C. Seeds the buyer-facing RMA
-- approval email into the unified email.template store (V92). Sent by
-- listener/rma/RmaApprovedEmailListener on an APPROVED RmaReviewCompletedEvent
-- via EmailService.sendTemplated("RMA_Approved", ...). Port of the legacy
-- SUB_SendEmail_RMAApproved (Mendix "PWSRMAApprovalEmail" template).
--
-- from_address / reply_to are left NULL to match the Partial-Credit templates
-- V92 copied over — EmailService.resolveFrom falls back to the smtp_config
-- from-address at send time, so the "from" is owned by ops config, not the row.
--
-- Bodies use {{varName}} substitution (TemplateRenderer). {{approvedTotalDisplay}}
-- carries the rendered currency string (the listener formats the amount with a
-- 2-decimal grouped pattern and a leading currency sign); that sign is supplied
-- at render time, never written here, so this file has no dollar-brace sequence
-- that would collide with Flyway's own placeholder syntax (same guard the V90
-- partial-credit seed used).
--
-- Idempotent: ON CONFLICT (template_key) DO NOTHING so a re-run (or a hand-seed
-- in a shared dev DB) is a no-op, matching the repo's seed style.
INSERT INTO email.template (template_key, template_name, subject, content_html, content_plain, enabled, description)
VALUES (
    'RMA_Approved',
    'RMA_Approved',
    'Your RMA {{rmaNumber}} has been approved',
    '<p>Hello,</p>'
    || '<p>Your RMA <strong>{{rmaNumber}}</strong> for buyer code <strong>{{buyerCode}}</strong> has been <strong>approved</strong>.</p>'
    || '<p>Approved devices: <strong>{{approvedQty}}</strong> across <strong>{{approvedSkus}}</strong> SKU(s).</p>'
    || '<p>Approved sales total: <strong>{{approvedTotalDisplay}}</strong></p>'
    || '<p>Approved items:</p>'
    || '<pre>{{approvedItemsSummary}}</pre>'
    || '<p>You can view the full approved / declined device breakdown in your buyer portal.</p>'
    || '<p>Thank you,<br/>ecoATM Direct</p>',
    'Hello,' || E'\n\n'
    || 'Your RMA {{rmaNumber}} for buyer code {{buyerCode}} has been approved.' || E'\n\n'
    || 'Approved devices: {{approvedQty}} across {{approvedSkus}} SKU(s).' || E'\n'
    || 'Approved sales total: {{approvedTotalDisplay}}' || E'\n\n'
    || 'Approved items:' || E'\n'
    || '{{approvedItemsSummary}}' || E'\n\n'
    || 'You can view the full approved / declined device breakdown in your buyer portal.' || E'\n\n'
    || 'Thank you,' || E'\n' || 'ecoATM Direct' || E'\n',
    TRUE,
    'Sent to the buyer when an RMA review completes as Approved (SUB_SendEmail_RMAApproved).'
)
ON CONFLICT (template_key) DO NOTHING;
