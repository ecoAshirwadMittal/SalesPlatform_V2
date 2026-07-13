-- V104: pws.company_holiday — the holiday calendar consumed by the SLA-tag
-- business-day math (gap 2.3 sub-feature 2). The modern port of the legacy
-- Mendix EcoATM_MDM.CompanyHoliday entity: SUB_CalculateSLADate walks back
-- `pws_constants.sla_days` BUSINESS days from "now", skipping weekends AND any
-- date that appears in this table.
--
-- Placed in the `pws` schema (not `mdm`) deliberately: the only consumer is the
-- PWS SLA-tag service, so the table is co-located with the feature that reads
-- it (the brief specifies pws.company_holiday).
--
-- Seed source: the legacy `ecoatm_mdm$companyholiday` DATA rows were NOT present
-- in migration_context/ (only the schema + three unaligned sample values were
-- extractable). The seed below is therefore a DOCUMENTED BEST-EFFORT: the seven
-- standard US-federal / corporate-observed holidays for 2025–2027. This matches
-- the legacy set exactly where the samples were visible (New Year's Day,
-- Memorial Day, Juneteenth, Independence Day, Thanksgiving) and the legacy row
-- count (7 rows in the source). If the authoritative legacy rows are later
-- recovered, replace/extend this seed.
--
-- Idempotent: CREATE TABLE IF NOT EXISTS + a UNIQUE(holiday_date) constraint
-- with ON CONFLICT DO NOTHING, so re-running against a partially-migrated dev DB
-- is safe.

CREATE TABLE IF NOT EXISTS pws.company_holiday (
    id            BIGSERIAL PRIMARY KEY,
    holiday_date  DATE NOT NULL,
    name          VARCHAR(200),
    created_date  TIMESTAMP DEFAULT NOW(),
    updated_date  TIMESTAMP DEFAULT NOW(),
    CONSTRAINT uq_company_holiday_date UNIQUE (holiday_date)
);

CREATE INDEX IF NOT EXISTS idx_company_holiday_date ON pws.company_holiday (holiday_date);

-- Seven observed holidays × three calendar years (2025–2027). Floating dates
-- (Memorial Day = last Monday of May, Labor Day = first Monday of September,
-- Thanksgiving = fourth Thursday of November) are pre-computed per year.
INSERT INTO pws.company_holiday (holiday_date, name) VALUES
    -- 2025
    (DATE '2025-01-01', 'New Year''s Day'),
    (DATE '2025-05-26', 'Memorial Day'),
    (DATE '2025-06-19', 'Juneteenth'),
    (DATE '2025-07-04', 'Independence Day'),
    (DATE '2025-09-01', 'Labor Day'),
    (DATE '2025-11-27', 'Thanksgiving Day'),
    (DATE '2025-12-25', 'Christmas Day'),
    -- 2026
    (DATE '2026-01-01', 'New Year''s Day'),
    (DATE '2026-05-25', 'Memorial Day'),
    (DATE '2026-06-19', 'Juneteenth'),
    (DATE '2026-07-04', 'Independence Day'),
    (DATE '2026-09-07', 'Labor Day'),
    (DATE '2026-11-26', 'Thanksgiving Day'),
    (DATE '2026-12-25', 'Christmas Day'),
    -- 2027
    (DATE '2027-01-01', 'New Year''s Day'),
    (DATE '2027-05-31', 'Memorial Day'),
    (DATE '2027-06-19', 'Juneteenth'),
    (DATE '2027-07-04', 'Independence Day'),
    (DATE '2027-09-06', 'Labor Day'),
    (DATE '2027-11-25', 'Thanksgiving Day'),
    (DATE '2027-12-25', 'Christmas Day')
ON CONFLICT (holiday_date) DO NOTHING;
