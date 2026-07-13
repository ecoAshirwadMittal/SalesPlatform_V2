-- V99__seed_manual_qualification_email_template.sql
-- Gap-analysis 2.4 sub-feature 2 (the email half). Seeds the buyer-facing
-- manual-qualification email into the unified email.template store (V92). Sent
-- by listener/buyermgmt/ManualQualificationEmailListener on a
-- QualificationOverriddenEvent whose roundStatus=Started AND included=true, via
-- EmailService.sendTemplated("ManualQualification", ...). Port of the legacy
-- SUB_SendManualQualificationEmail (called from NF_OnIncludedChanged_New).
--
-- from_address / reply_to are left NULL to match the Partial-Credit / RMA
-- templates — EmailService.resolveFrom falls back to the smtp_config
-- from-address at send time, so the "from" is owned by ops config, not the row.
--
-- Bodies use {{varName}} substitution (TemplateRenderer). The listener supplies
-- buyerCode, schedulingAuctionId, and qualifiedAtDisplay (a pre-formatted UTC
-- timestamp string) — none of which contains a dollar-brace sequence that would
-- collide with Flyway's own placeholder syntax (same guard the V90 / V93 seeds
-- used; note this comment deliberately avoids writing that sequence literally,
-- since Flyway resolves placeholders even inside SQL comments).
--
-- Idempotent: ON CONFLICT (template_key) DO NOTHING so a re-run (or a hand-seed
-- in a shared dev DB) is a no-op, matching the repo's seed style.
INSERT INTO email.template (template_key, template_name, subject, content_html, content_plain, enabled, description)
VALUES (
    'ManualQualification',
    'ManualQualification',
    'Buyer code {{buyerCode}} has been qualified for an active auction round',
    '<p>Hello,</p>'
    || '<p>Buyer code <strong>{{buyerCode}}</strong> has been <strong>manually qualified</strong> to participate in an active auction round (auction reference <strong>{{schedulingAuctionId}}</strong>).</p>'
    || '<p>Qualified at: <strong>{{qualifiedAtDisplay}}</strong></p>'
    || '<p>You can now place bids for this round in your buyer portal.</p>'
    || '<p>Thank you,<br/>ecoATM Direct</p>',
    'Hello,' || E'\n\n'
    || 'Buyer code {{buyerCode}} has been manually qualified to participate in an active auction round (auction reference {{schedulingAuctionId}}).' || E'\n\n'
    || 'Qualified at: {{qualifiedAtDisplay}}' || E'\n\n'
    || 'You can now place bids for this round in your buyer portal.' || E'\n\n'
    || 'Thank you,' || E'\n' || 'ecoATM Direct' || E'\n',
    TRUE,
    'Sent to the buyer when an admin manually qualifies their buyer code on a Started auction round (SUB_SendManualQualificationEmail).'
)
ON CONFLICT (template_key) DO NOTHING;
