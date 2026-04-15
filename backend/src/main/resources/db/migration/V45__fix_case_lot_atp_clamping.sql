-- V45: Fix case lot ATP quantity clamping bug from V42
-- V42 used GREATEST(1, ...) which fabricated atp_qty=1 for devices with 0 ATP.
-- The correct value is FLOOR(device_atp / pack_size), which can be 0.
-- Also fixes avl_qty to match.

UPDATE pws.case_lot cl
SET case_lot_atp_qty = COALESCE(
        FLOOR(COALESCE(d.atp_qty, d.available_qty, 0)::NUMERIC
              / NULLIF(cl.case_lot_size, 0))::INT,
        0),
    case_lot_avl_qty = COALESCE(
        FLOOR(COALESCE(d.atp_qty, d.available_qty, 0)::NUMERIC
              / NULLIF(cl.case_lot_size, 0))::INT,
        0)
FROM mdm.device d
WHERE d.id = cl.device_id
  AND cl.case_lot_atp_qty = 1
  AND COALESCE(d.atp_qty, d.available_qty, 0) <= 0
  -- Exclude manually-set lots from V42 section 10 (SPB10000101, SPB10000102, SPB10000001)
  AND cl.case_lot_id NOT IN (
      'SPB10000101--24', 'SPB10000101--20',
      'SPB10000102--22', 'SPB10000102--24', 'SPB10000102--25',
      'SPB10000001--15'
  );
