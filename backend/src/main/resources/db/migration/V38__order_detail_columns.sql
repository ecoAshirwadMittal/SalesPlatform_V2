-- Phase 3: Order Details support
-- 1. Add shipped tracking columns to offer_item (Mendix: shippedqty, shippedprice)
ALTER TABLE pws.offer_item ADD COLUMN IF NOT EXISTS shipped_qty INTEGER DEFAULT 0;
ALTER TABLE pws.offer_item ADD COLUMN IF NOT EXISTS shipped_price NUMERIC(14, 2) DEFAULT 0;

-- 2. Create imei_detail table (Mendix: ecoatm_pws$imeidetail)
-- Direct FKs instead of Mendix-style junction tables because the relationship is 1:1
-- (confirmed by equal row counts: 155,911 in imeidetail, imeidetail_offeritem, imeidetail_shipmentdetail)
CREATE TABLE IF NOT EXISTS pws.imei_detail (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    imei_number VARCHAR(200),
    serial_number VARCHAR(200),
    box_lpn_number VARCHAR(200),
    offer_item_id BIGINT REFERENCES pws.offer_item(id),
    shipment_detail_id BIGINT REFERENCES pws.shipment_detail(id),
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_imei_detail_offer_item ON pws.imei_detail(offer_item_id);
CREATE INDEX IF NOT EXISTS idx_imei_detail_shipment ON pws.imei_detail(shipment_detail_id);
