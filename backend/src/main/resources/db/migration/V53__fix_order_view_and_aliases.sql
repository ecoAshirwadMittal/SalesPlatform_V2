-- =============================================================================
-- V53: Deduplicate view subqueries with LEFT JOIN LATERAL
--
-- Finding 13: The offer_and_orders_view has two identical correlated
-- subqueries for buyer and company. Replace with a single lateral join.
-- =============================================================================

DROP VIEW IF EXISTS pws.offer_and_orders_view;

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
    buyer_info.company_name                                  AS buyer,
    buyer_info.company_name                                  AS company,
    GREATEST(ofe.updated_date, o.updated_date)               AS last_update_date,
    CASE WHEN o.id IS NOT NULL THEN 'Order' ELSE 'Offer' END AS offer_order_type,
    ofe.buyer_code_id                                        AS buyer_code_id,
    ofe.id                                                   AS offer_id
FROM pws.offer ofe
LEFT JOIN pws."order" o ON o.offer_id = ofe.id
LEFT JOIN LATERAL (
    SELECT b.company_name
    FROM buyer_mgmt.buyer_code_buyers bcb
    JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
    WHERE bcb.buyer_code_id = ofe.buyer_code_id
    LIMIT 1
) buyer_info ON true
WHERE ofe.visible_in_history = true;
