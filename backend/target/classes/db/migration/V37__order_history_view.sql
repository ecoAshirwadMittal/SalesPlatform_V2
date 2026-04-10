-- Order History VIEW
-- Joins pws.offer with pws."order" (LEFT JOIN so offers without orders appear).
-- buyer and company columns resolve from buyer_mgmt.buyers.company_name
-- because buyers table has no first_name/last_name (those exist on sales_representatives only).
-- buyer_code_id enables row-level security scoping in the service layer.
-- COALESCE(o.id, -ofe.id) provides a unique positive/negative id for JPA @Id:
--   real orders    → positive order id
--   offer-only rows → negative offer id (no collision)
CREATE OR REPLACE VIEW pws.offer_and_orders_view AS
SELECT
    COALESCE(o.id, -ofe.id)                            AS id,
    COALESCE(o.order_number, ofe.offer_number)          AS order_number,
    ofe.submission_date                                  AS offer_date,
    o.order_date                                         AS order_date,
    COALESCE(o.order_status, ofe.status)                 AS order_status,
    o.ship_date                                          AS ship_date,
    o.ship_method                                        AS ship_method,
    (SELECT COUNT(DISTINCT oi.sku)
       FROM pws.offer_item oi
      WHERE oi.offer_id = ofe.id)::int                  AS sku_count,
    COALESCE(ofe.total_qty, 0)                           AS total_quantity,
    COALESCE(ofe.total_price, 0)                         AS total_price,
    (SELECT b.company_name
       FROM buyer_mgmt.buyer_code_buyers bcb
       JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
      WHERE bcb.buyer_code_id = ofe.buyer_code_id
      LIMIT 1)                                           AS buyer,
    (SELECT b.company_name
       FROM buyer_mgmt.buyer_code_buyers bcb
       JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
      WHERE bcb.buyer_code_id = ofe.buyer_code_id
      LIMIT 1)                                           AS company,
    GREATEST(ofe.updated_date, o.updated_date)           AS last_update_date,
    CASE WHEN o.id IS NOT NULL THEN 'Order' ELSE 'Offer' END AS offer_order_type,
    ofe.buyer_code_id                                    AS buyer_code_id,
    ofe.id                                               AS offer_id
FROM pws.offer ofe
LEFT JOIN pws."order" o ON o.offer_id = ofe.id;
