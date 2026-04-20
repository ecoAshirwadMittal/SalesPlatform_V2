-- =============================================================================
-- V66: Add auto-increment sequences for buyer_mgmt.buyers and buyer_codes
-- Needed for Phase 3.2 (Buyer Create) — migrated data used fixed IDs;
-- new rows need sequences starting after the max existing ID.
-- =============================================================================

DO $$
DECLARE
    max_buyer_id BIGINT;
    max_code_id  BIGINT;
BEGIN
    SELECT COALESCE(MAX(id), 0) INTO max_buyer_id FROM buyer_mgmt.buyers;
    EXECUTE format('CREATE SEQUENCE buyer_mgmt.buyers_id_seq START WITH %s INCREMENT BY 1', max_buyer_id + 1);

    SELECT COALESCE(MAX(id), 0) INTO max_code_id FROM buyer_mgmt.buyer_codes;
    EXECUTE format('CREATE SEQUENCE buyer_mgmt.buyer_codes_id_seq START WITH %s INCREMENT BY 1', max_code_id + 1);
END $$;

ALTER TABLE buyer_mgmt.buyers ALTER COLUMN id SET DEFAULT nextval('buyer_mgmt.buyers_id_seq');
ALTER SEQUENCE buyer_mgmt.buyers_id_seq OWNED BY buyer_mgmt.buyers.id;

ALTER TABLE buyer_mgmt.buyer_codes ALTER COLUMN id SET DEFAULT nextval('buyer_mgmt.buyer_codes_id_seq');
ALTER SEQUENCE buyer_mgmt.buyer_codes_id_seq OWNED BY buyer_mgmt.buyer_codes.id;
