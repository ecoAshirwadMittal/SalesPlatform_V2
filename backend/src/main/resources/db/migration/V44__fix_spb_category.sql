-- V44: Set category for SPB devices that have brand but missing category
-- V42 set brand+category together WHERE brand_id IS NULL, but 12 devices
-- already had brand from V21, so category was skipped.

-- All iPhone SPB devices → Cell Phone (id 1)
UPDATE mdm.device SET category_id = 1
WHERE item_type = 'SPB' AND category_id IS NULL AND description LIKE 'iPhone%';

-- All iPad SPB devices → Tablet (id 2)
UPDATE mdm.device SET category_id = 2
WHERE item_type = 'SPB' AND category_id IS NULL AND description LIKE 'iPad%';
