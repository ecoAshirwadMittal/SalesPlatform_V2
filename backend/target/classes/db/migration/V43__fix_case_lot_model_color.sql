-- V43: Fix model and color misclassification for SPB10000100+ devices
-- These devices have "iPhone 14 Pro Max" in description but got model_id for "iPhone 14"
-- Also fix "Space Black" devices that got color "Black"

-- Fix model: set to iPhone 14 Pro Max (id 9) for all matching descriptions
UPDATE mdm.device SET model_id = (SELECT id FROM mdm.model WHERE name = 'iPhone 14 Pro Max' AND length(name) = 17 LIMIT 1)
WHERE item_type = 'SPB'
  AND description LIKE 'iPhone 14 Pro Max%'
  AND model_id != (SELECT id FROM mdm.model WHERE name = 'iPhone 14 Pro Max' AND length(name) = 17 LIMIT 1);

-- Fix color: set to Space Black for descriptions containing "Space Black"
UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Space Black')
WHERE item_type = 'SPB'
  AND description LIKE '%Space Black%'
  AND color_id != (SELECT id FROM mdm.color WHERE name = 'Space Black');

-- Fix color: set to Space Blue for descriptions containing "Space Blue"
UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Space Blue')
WHERE item_type = 'SPB'
  AND description LIKE '%Space Blue%'
  AND color_id != (SELECT id FROM mdm.color WHERE name = 'Space Blue');

-- Fix color: set to Space Silver for descriptions containing "Space Silver"
UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Space Silver')
WHERE item_type = 'SPB'
  AND description LIKE '%Space Silver%'
  AND color_id != (SELECT id FROM mdm.color WHERE name = 'Space Silver');

-- Fix color: set to Space Gold for descriptions containing "Space Gold"
UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Space Gold')
WHERE item_type = 'SPB'
  AND description LIKE '%Space Gold%'
  AND color_id != (SELECT id FROM mdm.color WHERE name = 'Space Gold');

-- Fix color: set to Space Gray for descriptions containing "Space Gray"
UPDATE mdm.device SET color_id = (SELECT id FROM mdm.color WHERE name = 'Space Gray')
WHERE item_type = 'SPB'
  AND description LIKE '%Space Gray%'
  AND color_id != (SELECT id FROM mdm.color WHERE name = 'Space Gray');
