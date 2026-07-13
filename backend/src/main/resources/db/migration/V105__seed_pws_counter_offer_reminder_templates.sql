-- V105__seed_pws_counter_offer_reminder_templates.sql
-- Gap-analysis 2.3 sub-feature 1 (Chunks C+D). Seeds the two buyer-facing
-- counter-offer reminder emails into the unified email.template store (V92).
-- Sent by service/pws/CounterOfferReminderService's hourly scheduled tick, which
-- calls EmailService.sendTemplated("PwsCounterOfferFirstReminder", ...) /
-- ("PwsCounterOfferSecondReminder", ...) for offers left in Buyer_Acceptance
-- past pws_constants.hours_first_counter_reminder / hours_second_counter_reminder.
-- Port of the legacy ACT_SendCounterOfferReminderEmails +
-- SUB_SendCounterOfferReminderEmail (calling SUB_SendFirstReminderEmail /
-- SUB_SendSecondReminderEmail).
--
-- Email copy is BEST-EFFORT: the bodies of SUB_SendFirstReminderEmail /
-- SUB_SendSecondReminderEmail are NOT present in migration_context, so this
-- reasonable copy must be reviewed by ops before pws.counter-reminder.enabled is
-- flipped true in QA/prod.
--
-- from_address / reply_to are left NULL to match the Partial-Credit / RMA /
-- ManualQualification templates — EmailService.resolveFrom falls back to the
-- smtp_config from-address at send time, so the "from" is owned by ops config,
-- not the row.
--
-- Bodies use {{varName}} substitution (TemplateRenderer). The sender supplies
-- buyerName, companyName, offerNumber, and counterOfferUrl — none of which
-- contains a dollar-brace sequence that would collide with Flyway's own
-- placeholder syntax (same guard the V90 / V93 / V99 / V101 / V102 seeds used;
-- this comment deliberately avoids writing that sequence literally, since Flyway
-- resolves placeholders even inside SQL comments).
--
-- Idempotent: ON CONFLICT (template_key) DO NOTHING so a re-run (or a hand-seed
-- in a shared dev DB) is a no-op, matching the repo's seed style.

INSERT INTO email.template (template_key, template_name, subject, content_html, content_plain, enabled, description)
VALUES (
    'PwsCounterOfferFirstReminder',
    'PwsCounterOfferFirstReminder',
    'Reminder: your counter offer {{offerNumber}} is waiting for your response',
    '<p>Hello {{buyerName}},</p>'
    || '<p>This is a friendly reminder that a counter offer on your submission '
    || '<strong>{{offerNumber}}</strong> for <strong>{{companyName}}</strong> is '
    || 'still awaiting your response.</p>'
    || '<p>Please review the counter offer and accept, counter, or decline it '
    || 'before it expires.</p>'
    || '<p><a href="{{counterOfferUrl}}">Review your counter offer</a></p>'
    || '<p>Thank you,<br/>ecoATM Direct</p>',
    'Hello {{buyerName}},' || E'\n\n'
    || 'This is a friendly reminder that a counter offer on your submission '
    || '{{offerNumber}} for {{companyName}} is still awaiting your response.' || E'\n\n'
    || 'Please review the counter offer and accept, counter, or decline it before '
    || 'it expires.' || E'\n\n'
    || 'Review your counter offer: {{counterOfferUrl}}' || E'\n\n'
    || 'Thank you,' || E'\n' || 'ecoATM Direct' || E'\n',
    TRUE,
    'First counter-offer reminder for a PWS offer sitting in Buyer_Acceptance past hours_first_counter_reminder (SUB_SendFirstReminderEmail). Copy is best-effort pending ops review.'
)
ON CONFLICT (template_key) DO NOTHING;

INSERT INTO email.template (template_key, template_name, subject, content_html, content_plain, enabled, description)
VALUES (
    'PwsCounterOfferSecondReminder',
    'PwsCounterOfferSecondReminder',
    'Final reminder: your counter offer {{offerNumber}} needs your response',
    '<p>Hello {{buyerName}},</p>'
    || '<p>This is a final reminder that a counter offer on your submission '
    || '<strong>{{offerNumber}}</strong> for <strong>{{companyName}}</strong> is '
    || 'still awaiting your response and may soon expire.</p>'
    || '<p>Please act now to accept, counter, or decline the counter offer so you '
    || 'do not miss out.</p>'
    || '<p><a href="{{counterOfferUrl}}">Review your counter offer</a></p>'
    || '<p>Thank you,<br/>ecoATM Direct</p>',
    'Hello {{buyerName}},' || E'\n\n'
    || 'This is a final reminder that a counter offer on your submission '
    || '{{offerNumber}} for {{companyName}} is still awaiting your response and '
    || 'may soon expire.' || E'\n\n'
    || 'Please act now to accept, counter, or decline the counter offer so you do '
    || 'not miss out.' || E'\n\n'
    || 'Review your counter offer: {{counterOfferUrl}}' || E'\n\n'
    || 'Thank you,' || E'\n' || 'ecoATM Direct' || E'\n',
    TRUE,
    'Second/final counter-offer reminder for a PWS offer sitting in Buyer_Acceptance past hours_second_counter_reminder (SUB_SendSecondReminderEmail). Copy is best-effort pending ops review.'
)
ON CONFLICT (template_key) DO NOTHING;
