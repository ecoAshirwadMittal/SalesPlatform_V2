-- V41: Populate grade and carrier reference data, then link devices
-- Root cause: V21 migrated devices with descriptions containing grade/carrier info
-- but never created the reference records or set the FK columns.

-- ============================================================
-- 1. Seed grade reference rows (idempotent)
-- ============================================================
INSERT INTO mdm.grade (name, display_name, is_enabled, sort_rank)
SELECT v.name, v.display_name, v.is_enabled, v.sort_rank
FROM (VALUES
  ('A',         'A',         true, 1),
  ('B',         'B',         true, 2),
  ('C',         'C',         true, 3),
  ('Value',     'Value',     true, 4),
  ('EXCELLENT', 'Excellent', true, 5),
  ('YYY',       'YYY',       true, 6),
  ('A_YYY',     'A_YYY',     true, 7),
  ('Good',      'Good',      true, 8),
  ('PSF',       'PSF',       true, 9),
  ('FMIP',      'FMIP',      true, 10)
) AS v(name, display_name, is_enabled, sort_rank)
WHERE NOT EXISTS (SELECT 1 FROM mdm.grade g WHERE g.name = v.name);

-- ============================================================
-- 2. Seed carrier reference rows (idempotent)
-- ============================================================
INSERT INTO mdm.carrier (name, display_name, is_enabled, sort_rank)
SELECT v.name, v.display_name, v.is_enabled, v.sort_rank
FROM (VALUES
  ('Unlocked',         'Unlocked',         true,  1),
  ('AT&T',             'AT&T',             true,  2),
  ('T-Mobile',         'T-Mobile',         true,  3),
  ('Verizon',          'Verizon',          true,  4),
  ('WIFI',             'WIFI',             true,  5),
  ('Spectrum Mobile',  'Spectrum Mobile',  true,  6),
  ('Google Fi',        'Google Fi',        true,  7),
  ('Boost Mobile',     'Boost Mobile',     true,  8),
  ('Cricket Wireless', 'Cricket Wireless', true,  9),
  ('Metro by T-Mobile','Metro by T-Mobile',true, 10),
  ('Mint Mobile',      'Mint Mobile',      true, 11),
  ('Straight Talk',    'Straight Talk',    true, 12),
  ('Simple Mobile',    'Simple Mobile',    true, 13),
  ('Page Plus',        'Page Plus',        true, 14),
  ('Tello',            'Tello',            true, 15),
  ('Wing Alpha',       'Wing Alpha',       true, 16),
  ('TracFone',         'TracFone',         true, 17),
  ('Xfinity Mobile',   'Xfinity Mobile',   true, 18),
  ('Total Wireless',   'Total Wireless',   true, 19),
  ('TextNow',          'TextNow',          true, 20),
  ('H2O Wireless',     'H2O Wireless',     true, 21),
  ('Republic Wireless', 'Republic Wireless',true, 22),
  ('Red Pocket',       'Red Pocket',       true, 23),
  ('Visible',          'Visible',          true, 24),
  ('Net10',            'Net10',            true, 25),
  ('FreedomPop',       'FreedomPop',       true, 26),
  ('US Mobile',        'US Mobile',        true, 27),
  ('Consumer Cellular','Consumer Cellular', true, 28),
  ('Ultra Mobile',     'Ultra Mobile',     true, 29),
  ('PureTalk',         'PureTalk',         true, 30)
) AS v(name, display_name, is_enabled, sort_rank)
WHERE NOT EXISTS (SELECT 1 FROM mdm.carrier c WHERE c.name = v.name);

-- ============================================================
-- 3. Link grade_id on devices by parsing the description suffix
--    Description format: [model] [cap] ([carrier]).[cap].[color].[GRADE]
-- ============================================================

-- A grades: GradeA, GradeA+, Grade A, bare "A"
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'A')
WHERE grade_id IS NULL
  AND (   description LIKE '%.GradeA'
       OR description LIKE '%.GradeA+'
       OR description LIKE '%.Grade A'
       OR (description ~ E'\\.[A]$' AND description NOT LIKE '%.Grade%'));

-- B grades: GradeB, GradeB+, GradeBPlus, Grade B, bare "B", bare "B+"
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'B')
WHERE grade_id IS NULL
  AND (   description LIKE '%.GradeB'
       OR description LIKE '%.GradeB+'
       OR description LIKE '%.GradeBPlus'
       OR description LIKE '%.Grade B'
       OR (description ~ E'\\.[B][+]?$' AND description NOT LIKE '%.Grade%'));

-- C grades: GradeC, GradeCPlus, bare "C"
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'C')
WHERE grade_id IS NULL
  AND (   description LIKE '%.GradeC'
       OR description LIKE '%.GradeCPlus'
       OR (description ~ E'\\.[C]$' AND description NOT LIKE '%.Grade%'));

-- Value
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'Value')
WHERE grade_id IS NULL
  AND (description LIKE '%.Value' OR description LIKE '%.value');

-- EXCELLENT
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'EXCELLENT')
WHERE grade_id IS NULL AND description LIKE '%.EXCELLENT';

-- YYY / GradeYYY (but not A_YYY)
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'YYY')
WHERE grade_id IS NULL
  AND (description LIKE '%.YYY' OR description LIKE '%.GradeYYY')
  AND description NOT LIKE '%.GradeA\_YYY'
  AND description NOT LIKE '%.A\_YYY';

-- A_YYY / GradeA_YYY
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'A_YYY')
WHERE grade_id IS NULL
  AND (description LIKE '%.GradeA\_YYY' OR description LIKE '%.A\_YYY');

-- Good
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'Good')
WHERE grade_id IS NULL AND description LIKE '%.Good';

-- PSF
UPDATE mdm.device SET grade_id = (SELECT id FROM mdm.grade WHERE name = 'PSF')
WHERE grade_id IS NULL AND description LIKE '%.PSF';

-- ============================================================
-- 4. Link carrier_id on devices by parsing the parenthesized carrier
--    Uses the parenthesized text followed by a dot
--    e.g. "iPhone 16 Pro 128GB (Unlocked).128GB.Black Titanium.GradeA"
-- ============================================================

-- Normalize WiFi/WIFI/Wifi variants
UPDATE mdm.device SET carrier_id = (SELECT id FROM mdm.carrier WHERE name = 'WIFI')
WHERE carrier_id IS NULL
  AND substring(description from E'\\(([^)]+)\\)\\.') IN ('WiFi', 'WIFI', 'Wifi');

-- Normalize Unlocked/UNLOCKED
UPDATE mdm.device SET carrier_id = (SELECT id FROM mdm.carrier WHERE name = 'Unlocked')
WHERE carrier_id IS NULL
  AND substring(description from E'\\(([^)]+)\\)\\.') IN ('Unlocked', 'UNLOCKED');

-- All other carriers: match exact name
UPDATE mdm.device d SET carrier_id = c.id
FROM mdm.carrier c
WHERE d.carrier_id IS NULL
  AND c.name NOT IN ('Unlocked', 'WIFI')
  AND substring(d.description from E'\\(([^)]+)\\)\\.') = c.name;
