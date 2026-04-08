-- ============================================================================
-- V27: Add human-readable offer_number to pws.offer + per-buyer-code sequence
--
-- Mendix ACr_UpdateOfferID logic:
--   OfferID = {BuyerCode}{YY}{LeftPad(sequence, 3, '0')}
--   e.g., BC00126001 = buyer code "BC001" + year "26" + sequence "001"
--   Sequence is per buyer code, resets each year.
-- ============================================================================

-- 1. Add offer_number column (the human-readable ID shown to users)
ALTER TABLE pws.offer
    ADD COLUMN IF NOT EXISTS offer_number VARCHAR(50);

CREATE INDEX IF NOT EXISTS idx_pws_offer_number ON pws.offer (offer_number);

-- 2. Per-buyer-code sequence counter (mirrors Mendix OfferID entity)
CREATE TABLE IF NOT EXISTS pws.offer_id_sequence (
    id              BIGSERIAL PRIMARY KEY,
    buyer_code_id   BIGINT    NOT NULL,
    year_prefix     CHAR(2)   NOT NULL,  -- 2-digit year, e.g. '26'
    max_sequence    INT       NOT NULL DEFAULT 0,
    UNIQUE (buyer_code_id, year_prefix)
);
