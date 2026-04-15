-- =============================================================================
-- V51: Add FK constraints and fix RMA monetary column types
--
-- Finding 12: offer_item.device_id and offer.sales_rep_id lack FK constraints
-- Finding 16: RMA monetary columns are INTEGER but should be NUMERIC(14,2)
-- =============================================================================

-- ══════════════════════════════════════════════════════════════════════════════
-- Finding 12: FK on offer_item.device_id → mdm.device
-- ══════════════════════════════════════════════════════════════════════════════

-- Clean orphan rows that reference non-existent devices
DELETE FROM pws.offer_item
WHERE device_id IS NOT NULL
  AND device_id NOT IN (SELECT id FROM mdm.device);

ALTER TABLE pws.offer_item
  ADD CONSTRAINT fk_offer_item_device
  FOREIGN KEY (device_id) REFERENCES mdm.device(id);

CREATE INDEX IF NOT EXISTS idx_offer_item_device_id ON pws.offer_item(device_id);

-- ══════════════════════════════════════════════════════════════════════════════
-- Finding 12: FK on offer.sales_rep_id → buyer_mgmt.sales_representatives
-- ══════════════════════════════════════════════════════════════════════════════

-- Null out orphan references
UPDATE pws.offer SET sales_rep_id = NULL
WHERE sales_rep_id IS NOT NULL
  AND sales_rep_id NOT IN (SELECT id FROM buyer_mgmt.sales_representatives);

ALTER TABLE pws.offer
  ADD CONSTRAINT fk_offer_sales_rep
  FOREIGN KEY (sales_rep_id) REFERENCES buyer_mgmt.sales_representatives(id);

CREATE INDEX IF NOT EXISTS idx_offer_sales_rep_id ON pws.offer(sales_rep_id);

-- ══════════════════════════════════════════════════════════════════════════════
-- Finding 16: RMA monetary columns INTEGER → NUMERIC(14,2)
-- ══════════════════════════════════════════════════════════════════════════════

ALTER TABLE pws.rma ALTER COLUMN request_sales_total TYPE NUMERIC(14,2);
ALTER TABLE pws.rma ALTER COLUMN approved_sales_total TYPE NUMERIC(14,2);
ALTER TABLE pws.rma_item ALTER COLUMN sale_price TYPE NUMERIC(14,2);
