-- ==============================================================================
-- Migration: V28__order_submission_columns.sql
-- Description: Add columns needed for full ACT_Offer_SubmitOrder logic:
--   - offer_item: offer_drawer_status, buyer_counter_status
--   - offer: final_offer_submitted_on
--   - order: is_successful, oracle_json_response, json_content, buyer_code_id
-- ==============================================================================

-- OfferItem: drawer status tracks item lifecycle (Sales_Review, Ordered, Accepted, etc.)
ALTER TABLE pws.offer_item ADD COLUMN IF NOT EXISTS offer_drawer_status VARCHAR(50);

-- OfferItem: buyer's response to a counter offer (Accept/Decline)
ALTER TABLE pws.offer_item ADD COLUMN IF NOT EXISTS buyer_counter_status VARCHAR(50);

-- Offer: timestamp when the final offer was submitted to Oracle
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS final_offer_submitted_on TIMESTAMP;

-- Order: whether Oracle returned success (ReturnCode='00')
ALTER TABLE pws."order" ADD COLUMN IF NOT EXISTS is_successful BOOLEAN;

-- Order: raw JSON response from Oracle ERP
ALTER TABLE pws."order" ADD COLUMN IF NOT EXISTS oracle_json_response TEXT;

-- Order: outbound JSON payload sent to Oracle
ALTER TABLE pws."order" ADD COLUMN IF NOT EXISTS json_content TEXT;

-- Order: buyer code linkage (Mendix: Order_BuyerCode association)
ALTER TABLE pws."order" ADD COLUMN IF NOT EXISTS buyer_code_id BIGINT;
