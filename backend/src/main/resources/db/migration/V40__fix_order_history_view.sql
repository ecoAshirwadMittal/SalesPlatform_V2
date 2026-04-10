-- =============================================================================
-- V40: Fix Order History view to match Mendix QA behavior
--
-- Three issues fixed:
--   1. Hide synthetic SYSTEM offers that had no offer_buyercode link in Mendix.
--      Only 108 Mendix offers existed; the remaining SYSTEM offers were
--      fabricated from standalone orders during migration and should not appear.
--   2. Map order_status to Mendix-compatible external status text.
--      Mendix maps through OrderStatus.ExternalStatusText; our view was showing
--      raw Deposco API responses (e.g. 'Success', error messages).
--   3. Fix SKU count: populate offer_item.sku from linked device.sku where null.
-- =============================================================================

-- ── 1. Add flag column to hide synthetic offers ──
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS visible_in_history boolean NOT NULL DEFAULT true;

-- Mark synthetic SYSTEM offers as not visible.
-- Real Mendix offers have these 108 legacy_ids (extracted from qa-0407 ecoatm_pws$offer).
-- BUYER offers are always visible. SYSTEM offers are visible only if they map to real Mendix offers.
UPDATE pws.offer SET visible_in_history = false
WHERE offer_type = 'SYSTEM'
  AND legacy_id IS NOT NULL
  AND legacy_id::bigint NOT IN (
    140456013379974990, 140456013379987522, 140456013379988475, 140456013380106111,
    140456013380118803, 140456013380423910, 140456013380588676, 140456013380665513,
    140456013380665664, 140456013380665782, 140456013380678369, 140456013380717435,
    140456013380749364, 140456013380752784, 140456013380752935, 140456013380793523,
    140456013380793609, 140456013380819144, 140456013380883180, 140456013380959928,
    140456013381343916, 140456013381344116, 140456013381344220, 140456013381407909,
    140456013381408012, 140456013381408187, 140456013381408350, 140456013381408404,
    140456013381933107, 140456013381933254, 140456013382022277, 140456013382060787,
    140456013382061257, 140456013382419344, 140456013382419557, 140456013382419696,
    140456013382764796, 140456013382828770, 140456013382830917, 140456013383020743,
    140456013383251165, 140456013383263887, 140456013383264114, 140456013383264140,
    140456013383264353, 140456013383264455, 140456013383264516, 140456013383264657,
    140456013383264781, 140456013383265018, 140456013383265085, 140456013383265234,
    140456013383265332, 140456013383265492, 140456013383265621, 140456013383265741,
    140456013383265799, 140456013383266005, 140456013383266053, 140456013383266201,
    140456013383266394, 140456013383266500, 140456013383266619, 140456013383266740,
    140456013383266852, 140456013383267043, 140456013383267103, 140456013383267283,
    140456013383267373, 140456013383267564, 140456013383267682, 140456013383267822,
    140456013383267870, 140456013383267985, 140456013383268222, 140456013383268337,
    140456013383268444, 140456013383268553, 140456013383268662, 140456013383268772,
    140456013383268909, 140456013383269050, 140456013383269228, 140456013383269325,
    140456013383269478, 140456013383269513, 140456013383276737, 140456013383276837,
    140456013383289594, 140456013383289687, 140456013383289766, 140456013383289956,
    140456013383290051, 140456013383290157, 140456013383290263, 140456013383290476,
    140456013383290546, 140456013383290717, 140456013383290829, 140456013383290952,
    140456013383291092, 140456013383291160, 140456013383291332, 140456013383291512,
    140456013383291523, 140456013383291742, 140456013383291855, 140456013383292003
  );

-- ── 2. Populate offer_item.sku from linked device ──
UPDATE pws.offer_item oi
SET sku = d.sku
FROM mdm.device d
WHERE oi.device_id = d.id
  AND (oi.sku IS NULL OR oi.sku = '')
  AND d.sku IS NOT NULL;

-- ── 3. Recreate the view with status mapping and visibility filter ──
-- Status mapping: offer.status → Mendix-compatible external text via CASE.
-- This avoids an extra join table and keeps the mapping explicit.
-- The mapped statuses match Mendix OrderStatus.ExternalStatusText exactly.
CREATE OR REPLACE VIEW pws.offer_and_orders_view AS
SELECT
    COALESCE(o.id, -ofe.id)                                AS id,
    COALESCE(o.order_number, ofe.offer_number)              AS order_number,
    ofe.submission_date                                      AS offer_date,
    o.order_date                                             AS order_date,
    -- Map to Mendix-compatible external status text
    CASE
        -- If order has shipped (by Deposco status), show Shipped
        WHEN o.order_status IN ('Ship Complete', 'Partially Shipped') THEN 'Shipped'
        -- If order is awaiting pickup
        WHEN o.order_status IN ('Loaded', 'Partially Loaded', 'Staged') THEN 'Awaiting Carrier Pickup'
        -- If order is cancelled
        WHEN o.order_status = 'Canceled' THEN 'Order Cancelled'
        -- If order exists (any Deposco in-process status or success or error), show In Process
        WHEN o.id IS NOT NULL THEN 'In Process'
        -- No order: map offer status to external text
        WHEN ofe.status = 'Sales_Review' THEN 'Offer Pending'
        WHEN ofe.status = 'Buyer_Acceptance' THEN 'Offer Pending'
        WHEN ofe.status = 'Declined' THEN 'Offer Declined'
        WHEN ofe.status = 'Draft' THEN 'Draft'
        WHEN ofe.status = 'Submitted' THEN 'Submitted'
        WHEN ofe.status = 'Pending_Review' THEN 'Offer Pending'
        WHEN ofe.status = 'Pending_Order' THEN 'In Process'
        WHEN ofe.status = 'Ordered' THEN 'In Process'
        ELSE COALESCE(ofe.status, '')
    END                                                      AS order_status,
    o.ship_date                                              AS ship_date,
    o.ship_method                                            AS ship_method,
    -- SKU count: from offer items (now populated with device SKU)
    (SELECT COUNT(DISTINCT oi.sku)
       FROM pws.offer_item oi
      WHERE oi.offer_id = ofe.id
        AND oi.sku IS NOT NULL AND oi.sku != '')::int       AS sku_count,
    COALESCE(ofe.total_qty, 0)                               AS total_quantity,
    COALESCE(ofe.total_price, 0)                             AS total_price,
    (SELECT b.company_name
       FROM buyer_mgmt.buyer_code_buyers bcb
       JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
      WHERE bcb.buyer_code_id = ofe.buyer_code_id
      LIMIT 1)                                               AS buyer,
    (SELECT b.company_name
       FROM buyer_mgmt.buyer_code_buyers bcb
       JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
      WHERE bcb.buyer_code_id = ofe.buyer_code_id
      LIMIT 1)                                               AS company,
    GREATEST(ofe.updated_date, o.updated_date)               AS last_update_date,
    CASE WHEN o.id IS NOT NULL THEN 'Order' ELSE 'Offer' END AS offer_order_type,
    ofe.buyer_code_id                                        AS buyer_code_id,
    ofe.id                                                   AS offer_id
FROM pws.offer ofe
LEFT JOIN pws."order" o ON o.offer_id = ofe.id
WHERE ofe.visible_in_history = true;
