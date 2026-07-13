-- V103: Add SLA + counter-offer-reminder flag columns to pws.offer
-- Gap 2.3 prerequisite. These three booleans back:
--   * offer_beyond_sla      — the SLA-tag job (PWSAdminController.setSLATags /
--                             removeSLATags already write this column, so before
--                             this migration those endpoints threw
--                             `column "offer_beyond_sla" does not exist`).
--   * first_reminder_sent   — the counter-offer reminder job's first-reminder gate.
--   * second_reminder_sent  — the counter-offer reminder job's second-reminder gate.
-- All three are one-shot flags: default false, flipped true once the job acts.
-- Idempotent (ADD COLUMN IF NOT EXISTS) so re-running against a partially
-- migrated dev DB is safe.

ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS offer_beyond_sla BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS first_reminder_sent BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS second_reminder_sent BOOLEAN NOT NULL DEFAULT false;
