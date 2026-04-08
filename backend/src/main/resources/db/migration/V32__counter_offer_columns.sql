-- V32: Add counter-offer summary and final-offer tracking columns
-- Supports PWSBuyerCounterOffers page (buyer response to sales rep counters)

-- Offer-level counter/final summary columns (Mendix: Offer entity attributes)
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS counter_offer_total_sku INTEGER DEFAULT 0;
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS counter_offer_total_qty INTEGER DEFAULT 0;
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS counter_offer_total_price NUMERIC(14, 2) DEFAULT 0;
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS final_offer_total_sku INTEGER DEFAULT 0;
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS final_offer_total_qty INTEGER DEFAULT 0;
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS final_offer_total_price NUMERIC(14, 2) DEFAULT 0;
ALTER TABLE pws.offer ADD COLUMN IF NOT EXISTS counter_response_submitted_on TIMESTAMP;

-- Item-level final offer columns (Mendix: OfferItem entity attributes)
ALTER TABLE pws.offer_item ADD COLUMN IF NOT EXISTS final_offer_price NUMERIC(14, 2);
ALTER TABLE pws.offer_item ADD COLUMN IF NOT EXISTS final_offer_quantity INTEGER;
ALTER TABLE pws.offer_item ADD COLUMN IF NOT EXISTS final_offer_total_price NUMERIC(14, 2);
ALTER TABLE pws.offer_item ADD COLUMN IF NOT EXISTS counter_case_price_total NUMERIC(14, 2);
