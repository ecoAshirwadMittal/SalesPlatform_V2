-- V42: Fix case lot data quality issues
-- Issue 1: Create missing SPB-DONT-USE-* devices (iPad mini, grades DNA-DNE)
-- Issue 2: Populate reference data (model, capacity, carrier, color, grade) for SPB devices
-- Issue 5: Recreate case lots with realistic multi-lot entries and correct ATP quantities
-- Issue 6: Set independent case lot prices matching QA behavior

-- ============================================================
-- 1. Add new grades (B+, DNA-DNE) — idempotent
-- ============================================================
INSERT INTO mdm.grade (name, display_name, is_enabled, sort_rank)
SELECT v.name, v.display_name, v.is_enabled, v.sort_rank
FROM (VALUES
  ('B+',  'B+',  true, 11),
  ('DNA', 'DNA', true, 12),
  ('DNB', 'DNB', true, 13),
  ('DNC', 'DNC', true, 14),
  ('DND', 'DND', true, 15),
  ('DNE', 'DNE', true, 16)
) AS v(name, display_name, is_enabled, sort_rank)
WHERE NOT EXISTS (SELECT 1 FROM mdm.grade g WHERE g.name = v.name);

-- ============================================================
-- 2. Add missing colors — idempotent
-- ============================================================
INSERT INTO mdm.color (name, display_name, is_enabled, sort_rank)
SELECT v.name, v.display_name, v.is_enabled, v.sort_rank
FROM (VALUES
  ('Space Blue',   'Space Blue',   true, 200),
  ('Space Silver', 'Space Silver', true, 201),
  ('Space Gold',   'Space Gold',   true, 202),
  ('Mixed',        'Mixed',        true, 203)
) AS v(name, display_name, is_enabled, sort_rank)
WHERE NOT EXISTS (SELECT 1 FROM mdm.color c WHERE c.name = v.name);

-- ============================================================
-- 3. Create 5 SPB-DONT-USE-* devices (Issue 1)
--    iPad mini, 64GB, WiFi, Pink, grades DNA-DNE
-- ============================================================
INSERT INTO mdm.device (sku, device_code, description, list_price, min_price, available_qty, reserved_qty, atp_qty, weight, item_type, is_active, brand_id, category_id, model_id, capacity_id, carrier_id, color_id, grade_id)
SELECT v.sku, v.sku, v.description, v.list_price, v.min_price, v.avl, 0, v.atp, 0.5,
       'SPB', true,
       3,  -- Apple
       2,  -- Tablet
       94, -- iPad mini
       43, -- 64GB
       6,  -- WIFI
       91, -- Pink
       (SELECT id FROM mdm.grade WHERE name = v.grade_name)
FROM (VALUES
  ('SPB-DONT-USE-001', 'iPad mini (6th generation) 64GB (WiFi).64GB.Pink.DNA', 1250.00, 1000.00, 20,  18, 'DNA'),
  ('SPB-DONT-USE-002', 'iPad mini (6th generation) 64GB (WiFi).64GB.Pink.DNB', 1250.00, 1000.00, 15,  12, 'DNB'),
  ('SPB-DONT-USE-003', 'iPad mini (6th generation) 64GB (WiFi).64GB.Pink.DNC', 1250.00, 1000.00, 10,   8, 'DNC'),
  ('SPB-DONT-USE-004', 'iPad mini (6th generation) 64GB (WiFi).64GB.Pink.DND', 1250.00, 1000.00, 25,  22, 'DND'),
  ('SPB-DONT-USE-005', 'iPad mini (6th generation) 64GB (WiFi).64GB.Pink.DNE', 1250.00, 1000.00, 12,  10, 'DNE')
) AS v(sku, description, list_price, min_price, avl, atp, grade_name)
WHERE NOT EXISTS (SELECT 1 FROM mdm.device d WHERE d.sku = v.sku);

-- ============================================================
-- 4. Populate model_id on SPB devices by matching model names
--    SPB descriptions: "iPhone 15 Pro Max 256GB ..." or "iPhone 13 128GB ..."
-- ============================================================

-- iPhone 15 Pro Max
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 15 Pro Max' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 15 Pro Max %';

-- iPhone 15 Pro (must come after Pro Max)
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 15 Pro' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 15 Pro %';

-- iPhone 15
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 15' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 15 %';

-- iPhone 14 Pro Max
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 14 Pro Max' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 14 Pro Max %';

-- iPhone 14 Pro
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 14 Pro' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 14 Pro %';

-- iPhone 14
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 14' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 14 %';

-- iPhone 13 Pro Max
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 13 Pro Max' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 13 Pro Max %';

-- iPhone 13 Pro
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 13 Pro' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 13 Pro %';

-- iPhone 13
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 13' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 13 %';

-- iPhone 12 Pro Max
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 12 Pro Max' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 12 Pro Max %';

-- iPhone 12 Pro
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 12 Pro' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 12 Pro %';

-- iPhone 12
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 12' LIMIT 1)
WHERE item_type = 'SPB' AND model_id IS NULL AND description LIKE 'iPhone 12 %';

-- Set brand=Apple, category=CellPhone for all iPhone SPB devices that got a model
UPDATE mdm.device SET brand_id = 3, category_id = 1
WHERE item_type = 'SPB' AND brand_id IS NULL AND model_id IS NOT NULL
  AND description LIKE 'iPhone%';

-- ============================================================
-- 5. Populate capacity_id by parsing first \d+GB from description
-- ============================================================
UPDATE mdm.device d SET capacity_id = cap.id
FROM mdm.capacity cap
WHERE d.item_type = 'SPB' AND d.capacity_id IS NULL
  AND cap.name = substring(d.description from '(\d+GB)');

-- ============================================================
-- 6. Populate carrier_id from description text
-- ============================================================

-- AT&T (includes "AT&T/UNL", "AT&T/Unlocked", "AT&T Unlocked" — take AT&T as primary)
UPDATE mdm.device SET carrier_id = (SELECT id FROM mdm.carrier WHERE name = 'AT&T')
WHERE item_type = 'SPB' AND carrier_id IS NULL
  AND description ~ 'AT&T';

-- Unlocked (only if not already set by AT&T above)
UPDATE mdm.device SET carrier_id = (SELECT id FROM mdm.carrier WHERE name = 'Unlocked')
WHERE item_type = 'SPB' AND carrier_id IS NULL
  AND description ~ 'Unlocked';

-- Any remaining SPB devices without carrier: default to Unlocked
UPDATE mdm.device SET carrier_id = (SELECT id FROM mdm.carrier WHERE name = 'Unlocked')
WHERE item_type = 'SPB' AND carrier_id IS NULL;

-- ============================================================
-- 7. Populate grade_id from description text
-- ============================================================

-- B+ grade (most common in SPB descriptions)
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'B+')
WHERE item_type = 'SPB' AND grade_id IS NULL
  AND description ~ ' B\+ ';

-- B grade (without +, e.g. "Unlocked B Space Black")
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'B')
WHERE item_type = 'SPB' AND grade_id IS NULL
  AND description ~ ' B [A-Z]'
  AND description NOT LIKE '% B+%';

-- Remaining SPB devices without grade: default to B+
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'B+')
WHERE item_type = 'SPB' AND grade_id IS NULL;

-- ============================================================
-- 8. Populate color_id from description text
-- ============================================================

-- Titanium colors (must match before single-word colors)
UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Black Titanium')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Black Titanium%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Blue Titanium')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Blue Titanium%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Natural Titanium')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Natural Titanium%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'White Titanium')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%White Titanium%';

-- Space colors
UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Space Black')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Space Black%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Space Gray')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Space Gray%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Space Blue')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Space Blue%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Space Silver')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Space Silver%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Space Gold')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Space Gold%';

-- Mixed colors
UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Mixed')
WHERE item_type = 'SPB' AND color_id IS NULL
  AND (description LIKE '%MIXED%' OR description LIKE '%Mix%' OR description LIKE '%/%');

-- Single-word colors
UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Graphite')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Graphite%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Green')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Green%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Blue')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Blue%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Red')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Red%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Pink')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Pink%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Midnight')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Midnight%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Starlight')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Starlight%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Silver')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Silver%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Gold')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Gold%';

UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Black')
WHERE item_type = 'SPB' AND color_id IS NULL AND description LIKE '%Black%';

-- ============================================================
-- 9. Drop and recreate case lots with realistic data (Issues 5, 6)
--    - Use description-embedded case pack sizes
--    - Create multiple lots per device for key SKUs
--    - Set independent case lot prices (not just unit × size)
-- ============================================================

-- First, clear offer_item FK references to existing case lots
UPDATE pws.offer_item SET case_lot_id = NULL WHERE case_lot_id IS NOT NULL;

-- Delete all existing case lots (they were artificially generated by V31)
DELETE FROM pws.case_lot;

-- Insert case lots using the pack size from each device's description
-- For devices with "N Count Case Pack" in description, extract N
-- For devices without pack size in description, use a sensible default
INSERT INTO pws.case_lot (device_id, case_lot_id, case_lot_size, case_lot_price, case_lot_avl_qty, case_lot_reserved_qty, case_lot_atp_qty, is_active, created_by)
SELECT
    d.id,
    d.sku || '--' || COALESCE(
        CAST(substring(d.description from '(\d+)\s+[Cc]ount\s+[Cc]ase\s+[Pp]ack') AS INT),
        20
    ),
    COALESCE(
        CAST(substring(d.description from '(\d+)\s+[Cc]ount\s+[Cc]ase\s+[Pp]ack') AS INT),
        20
    ),
    -- Case lot price: use the unit price × size (Mendix sync behavior)
    COALESCE(d.list_price, 0) * COALESCE(
        CAST(substring(d.description from '(\d+)\s+[Cc]ount\s+[Cc]ase\s+[Pp]ack') AS INT),
        20
    ),
    -- ATP qty from device (number of cases available)
    GREATEST(1, COALESCE(d.atp_qty, d.available_qty, 0) / NULLIF(COALESCE(
        CAST(substring(d.description from '(\d+)\s+[Cc]ount\s+[Cc]ase\s+[Pp]ack') AS INT),
        20
    ), 0)),
    0,
    GREATEST(1, COALESCE(d.atp_qty, d.available_qty, 0) / NULLIF(COALESCE(
        CAST(substring(d.description from '(\d+)\s+[Cc]ount\s+[Cc]ase\s+[Pp]ack') AS INT),
        20
    ), 0)),
    TRUE,
    'migration-v42'
FROM mdm.device d
WHERE d.item_type = 'SPB' AND d.is_active = TRUE;

-- ============================================================
-- 10. Create additional case lots for key SKUs to match QA
--     QA shows multiple pack sizes per device
-- ============================================================

-- SPB10000102: QA shows sizes 22, 24, 25 with avl 1, 53, 12
-- Already has size 24 from above. Add sizes 22 and 25.
INSERT INTO pws.case_lot (device_id, case_lot_id, case_lot_size, case_lot_price, case_lot_avl_qty, case_lot_reserved_qty, case_lot_atp_qty, is_active, created_by)
SELECT d.id, d.sku || '--22', 22, COALESCE(d.list_price, 0) * 22, 1, 0, 1, TRUE, 'migration-v42'
FROM mdm.device d WHERE d.sku = 'SPB10000102'
UNION ALL
SELECT d.id, d.sku || '--25', 25, COALESCE(d.list_price, 0) * 25, 12, 0, 12, TRUE, 'migration-v42'
FROM mdm.device d WHERE d.sku = 'SPB10000102';

-- Update the existing SPB10000102--24 lot to have avl=53
UPDATE pws.case_lot SET case_lot_avl_qty = 53, case_lot_atp_qty = 53
WHERE case_lot_id = 'SPB10000102--24';

-- SPB10000101: QA shows avl 1, 13 (two lots)
INSERT INTO pws.case_lot (device_id, case_lot_id, case_lot_size, case_lot_price, case_lot_avl_qty, case_lot_reserved_qty, case_lot_atp_qty, is_active, created_by)
SELECT d.id, d.sku || '--20', 20, COALESCE(d.list_price, 0) * 20, 13, 0, 13, TRUE, 'migration-v42'
FROM mdm.device d WHERE d.sku = 'SPB10000101';

-- Update existing SPB10000101--24 lot to have avl=1
UPDATE pws.case_lot SET case_lot_avl_qty = 1, case_lot_atp_qty = 1
WHERE case_lot_id = 'SPB10000101--24';

-- Add additional lots for other active production SKUs
-- SPB10000001: add lot with different size (iPhone 15 Pro Max, 20-count from description)
INSERT INTO pws.case_lot (device_id, case_lot_id, case_lot_size, case_lot_price, case_lot_avl_qty, case_lot_reserved_qty, case_lot_atp_qty, is_active, created_by)
SELECT d.id, d.sku || '--15', 15, COALESCE(d.list_price, 0) * 15, 2, 0, 2, TRUE, 'migration-v42'
FROM mdm.device d WHERE d.sku = 'SPB10000001';

-- ============================================================
-- 11. Fix case lot prices for SPB-DONT-USE-* devices (Issue 6)
--     QA shows independently set prices, not just unit × size
--     e.g. SPB-DONT-USE-004: unit $1,250, case $7,990
-- ============================================================
UPDATE pws.case_lot cl SET case_lot_price = v.price
FROM (VALUES
  ('SPB-DONT-USE-001', 5990.00),
  ('SPB-DONT-USE-002', 6490.00),
  ('SPB-DONT-USE-003', 7290.00),
  ('SPB-DONT-USE-004', 7990.00),
  ('SPB-DONT-USE-005', 4990.00)
) AS v(sku_prefix, price)
WHERE cl.case_lot_id LIKE v.sku_prefix || '--%';
