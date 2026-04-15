-- =============================================================================
-- V50: Convert all TIMESTAMP columns to TIMESTAMPTZ
--
-- Finding 4 from database remediation plan.
-- No production data yet — ideal time to fix timezone safety.
--
-- PostgreSQL ALTER COLUMN TYPE TIMESTAMPTZ is a metadata-only change when
-- converting from TIMESTAMP (no table rewrite), so this is fast even on
-- large tables.
--
-- The offer_and_orders_view must be dropped and recreated because it
-- references columns whose types are changing.
-- =============================================================================

-- ── Drop dependent view ──
DROP VIEW IF EXISTS pws.offer_and_orders_view;

-- ══════════════════════════════════════════════════════════════════════════════
-- pws schema
-- ══════════════════════════════════════════════════════════════════════════════

-- pws.offer
ALTER TABLE pws.offer ALTER COLUMN submission_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN sales_review_completed_on TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN canceled_on TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN final_offer_submitted_on TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN counter_response_submitted_on TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.offer_item
ALTER TABLE pws.offer_item ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.offer_item ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws."order"
ALTER TABLE pws."order" ALTER COLUMN order_date TYPE TIMESTAMPTZ;
ALTER TABLE pws."order" ALTER COLUMN ship_date TYPE TIMESTAMPTZ;
ALTER TABLE pws."order" ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws."order" ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.shipment_detail
ALTER TABLE pws.shipment_detail ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.shipment_detail ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.rma
ALTER TABLE pws.rma ALTER COLUMN submitted_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.rma ALTER COLUMN approval_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.rma ALTER COLUMN review_completed_on TYPE TIMESTAMPTZ;
ALTER TABLE pws.rma ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.rma ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.rma_item
ALTER TABLE pws.rma_item ALTER COLUMN ship_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.rma_item ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.rma_item ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.rma_status
ALTER TABLE pws.rma_status ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.rma_status ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.rma_reason
ALTER TABLE pws.rma_reason ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.rma_reason ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.rma_template
ALTER TABLE pws.rma_template ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.rma_template ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.case_lot
ALTER TABLE pws.case_lot ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.case_lot ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.imei_detail
ALTER TABLE pws.imei_detail ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.imei_detail ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.future_price_config
ALTER TABLE pws.future_price_config ALTER COLUMN future_price_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.future_price_config ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.future_price_config ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.feature_flag
ALTER TABLE pws.feature_flag ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.feature_flag ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.pws_constants
ALTER TABLE pws.pws_constants ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.pws_constants ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.order_status_config
ALTER TABLE pws.order_status_config ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.order_status_config ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.maintenance_mode
ALTER TABLE pws.maintenance_mode ALTER COLUMN banner_start_time TYPE TIMESTAMPTZ;
ALTER TABLE pws.maintenance_mode ALTER COLUMN start_time TYPE TIMESTAMPTZ;
ALTER TABLE pws.maintenance_mode ALTER COLUMN end_time TYPE TIMESTAMPTZ;
ALTER TABLE pws.maintenance_mode ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.maintenance_mode ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- pws.navigation_menu
ALTER TABLE pws.navigation_menu ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE pws.navigation_menu ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- ══════════════════════════════════════════════════════════════════════════════
-- mdm schema
-- ══════════════════════════════════════════════════════════════════════════════

-- mdm.device
ALTER TABLE mdm.device ALTER COLUMN last_sync_time TYPE TIMESTAMPTZ;
ALTER TABLE mdm.device ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE mdm.device ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- mdm.price_history
ALTER TABLE mdm.price_history ALTER COLUMN expiration_date TYPE TIMESTAMPTZ;
ALTER TABLE mdm.price_history ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE mdm.price_history ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- ══════════════════════════════════════════════════════════════════════════════
-- integration schema
-- ══════════════════════════════════════════════════════════════════════════════

-- integration.oracle_config
ALTER TABLE integration.oracle_config ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- integration.deposco_config
ALTER TABLE integration.deposco_config ALTER COLUMN last_sync_time TYPE TIMESTAMPTZ;
ALTER TABLE integration.deposco_config ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- integration.api_token
ALTER TABLE integration.api_token ALTER COLUMN expiration_date TYPE TIMESTAMPTZ;
ALTER TABLE integration.api_token ALTER COLUMN created_date TYPE TIMESTAMPTZ;

-- integration.api_log
ALTER TABLE integration.api_log ALTER COLUMN start_time TYPE TIMESTAMPTZ;
ALTER TABLE integration.api_log ALTER COLUMN end_time TYPE TIMESTAMPTZ;
ALTER TABLE integration.api_log ALTER COLUMN created_date TYPE TIMESTAMPTZ;

-- ══════════════════════════════════════════════════════════════════════════════
-- email schema
-- ══════════════════════════════════════════════════════════════════════════════

-- email.smtp_config
ALTER TABLE email.smtp_config ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE email.smtp_config ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- email.email_template
ALTER TABLE email.email_template ALTER COLUMN created_date TYPE TIMESTAMPTZ;
ALTER TABLE email.email_template ALTER COLUMN updated_date TYPE TIMESTAMPTZ;

-- email.email_log
ALTER TABLE email.email_log ALTER COLUMN sent_date TYPE TIMESTAMPTZ;
ALTER TABLE email.email_log ALTER COLUMN created_date TYPE TIMESTAMPTZ;

-- ══════════════════════════════════════════════════════════════════════════════
-- Recreate the offer_and_orders_view (from V40)
-- ══════════════════════════════════════════════════════════════════════════════

CREATE OR REPLACE VIEW pws.offer_and_orders_view AS
SELECT
    COALESCE(o.id, -ofe.id)                                AS id,
    COALESCE(o.order_number, ofe.offer_number)              AS order_number,
    ofe.submission_date                                      AS offer_date,
    o.order_date                                             AS order_date,
    CASE
        WHEN o.order_status IN ('Ship Complete', 'Partially Shipped') THEN 'Shipped'
        WHEN o.order_status IN ('Loaded', 'Partially Loaded', 'Staged') THEN 'Awaiting Carrier Pickup'
        WHEN o.order_status = 'Canceled' THEN 'Order Cancelled'
        WHEN o.id IS NOT NULL THEN 'In Process'
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
