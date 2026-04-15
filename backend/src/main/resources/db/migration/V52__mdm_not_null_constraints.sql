-- =============================================================================
-- V52: Add NOT NULL constraints to MDM tables
--
-- Finding 22: device.sku should not be nullable
-- Finding 23: MDM lookup table name columns should not be nullable
-- =============================================================================

-- ══════════════════════════════════════════════════════════════════════════════
-- Finding 22: device.sku NOT NULL
-- ══════════════════════════════════════════════════════════════════════════════

UPDATE mdm.device SET sku = 'UNKNOWN-' || id WHERE sku IS NULL;
ALTER TABLE mdm.device ALTER COLUMN sku SET NOT NULL;

-- ══════════════════════════════════════════════════════════════════════════════
-- Finding 23: MDM lookup table name NOT NULL
-- ══════════════════════════════════════════════════════════════════════════════

UPDATE mdm.brand SET name = 'Unknown' WHERE name IS NULL;
ALTER TABLE mdm.brand ALTER COLUMN name SET NOT NULL;

UPDATE mdm.category SET name = 'Unknown' WHERE name IS NULL;
ALTER TABLE mdm.category ALTER COLUMN name SET NOT NULL;

UPDATE mdm.model SET name = 'Unknown' WHERE name IS NULL;
ALTER TABLE mdm.model ALTER COLUMN name SET NOT NULL;

UPDATE mdm.condition SET name = 'Unknown' WHERE name IS NULL;
ALTER TABLE mdm.condition ALTER COLUMN name SET NOT NULL;

UPDATE mdm.capacity SET name = 'Unknown' WHERE name IS NULL;
ALTER TABLE mdm.capacity ALTER COLUMN name SET NOT NULL;

UPDATE mdm.carrier SET name = 'Unknown' WHERE name IS NULL;
ALTER TABLE mdm.carrier ALTER COLUMN name SET NOT NULL;

UPDATE mdm.color SET name = 'Unknown' WHERE name IS NULL;
ALTER TABLE mdm.color ALTER COLUMN name SET NOT NULL;

UPDATE mdm.grade SET name = 'Unknown' WHERE name IS NULL;
ALTER TABLE mdm.grade ALTER COLUMN name SET NOT NULL;
