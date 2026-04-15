-- =============================================================================
-- V48: Add FK constraint and index on pws.order.buyer_code_id
-- Integrity fix: buyer_code_id was added in V28 with no FK or index.
-- =============================================================================

-- Clean up any orphaned buyer_code_id values before adding FK
UPDATE pws."order" SET buyer_code_id = NULL
WHERE buyer_code_id IS NOT NULL
  AND buyer_code_id NOT IN (SELECT id FROM buyer_mgmt.buyer_codes);

-- Add FK constraint
ALTER TABLE pws."order"
  ADD CONSTRAINT fk_order_buyer_code
  FOREIGN KEY (buyer_code_id) REFERENCES buyer_mgmt.buyer_codes(id);

-- Add index for join performance
CREATE INDEX idx_order_buyer_code_id ON pws."order"(buyer_code_id);
