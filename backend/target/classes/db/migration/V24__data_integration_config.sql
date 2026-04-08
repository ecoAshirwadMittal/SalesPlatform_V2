-- =============================================================================
-- V24: Data migration — Integration configuration (Deposco, error mappings)
-- Generated from QA database on 2026-04-08 10:06
-- =============================================================================

-- Deposco Config
INSERT INTO integration.deposco_config (id, legacy_id, base_url, username, password_hash, last_sync_time, timeout_ms, is_active, updated_date) VALUES
  (1, 173388585653764303, 'https://ecoatm-ua.deposco.com/', 'api.user', 'ecoatm2025!', '2026-03-27T20:52:23.738000', 5000, true, NULL)
ON CONFLICT DO NOTHING;

-- Error Mappings
INSERT INTO integration.error_mapping (id, source_system, source_error_code, source_error_type, user_error_code, user_error_message, bypass_for_user) VALUES
  (1, 'ORACLE', '00', 'INFO', 'PWS-00', 'Success', true),
  (2, 'ORACLE', '01', 'ERROR', 'PWS-01', 'Origin System Order Id cannot be blank.', true),
  (3, 'ORACLE', '02', 'ERROR', 'PWS-02', 'Duplicate order', false),
  (4, 'ORACLE', '03', 'ERROR', 'PWS-03', 'Order Type cannot be blank', false),
  (5, 'ORACLE', '04', 'ERROR', 'PWS-04', 'Order Type is not valid', false),
  (6, 'ORACLE', '05', 'ERROR', 'PWS-05', 'Order Type not configured correctly (multiple assignments)', false),
  (7, 'ORACLE', '06', 'ERROR', 'PWS-06', 'Order Date cannot be blank', false),
  (8, 'ORACLE', '07', 'ERROR', 'PWS-07', 'Order Date is not valid. Format must be YYYYMMDDHH24MISS', false),
  (9, 'ORACLE', '08', 'ERROR', 'PWS-08', 'Buyer Code cannot be blank', false),
  (10, 'ORACLE', '09', 'ERROR', 'PWS-09', 'Buyer Code is not active at the customer level', false),
  (11, 'ORACLE', '10', 'ERROR', 'PWS-10', 'Buyer Code is not active at the site level 11 Buyer Code is not active at the business purpose level', false),
  (12, 'ORACLE', '11', 'ERROR', 'PWS-11', 'Buyer Code is not active at the business purpose level', false),
  (13, 'ORACLE', '12', 'ERROR', 'PWS-12', 'Buyer Code is not valid', false),
  (14, 'ORACLE', '13', 'ERROR', 'PWS-13', 'At least one line must be entered', false),
  (15, 'ORACLE', '17', 'ERROR', 'PWS-17', 'Order Date cannot be a future date', false),
  (16, 'ORACLE', '40', 'ERROR', 'PWS-40', 'Freight Term is not valid', false),
  (17, 'ORACLE', '43', 'ERROR', 'PWS-43', 'Freight Carrier is not valid', false),
  (18, 'ORACLE', '50', 'ERROR', 'PWS-50', 'Item does not exist', false),
  (19, 'ORACLE', '51', 'ERROR', 'PWS-51', 'Item does not exist in organization', false),
  (20, 'ORACLE', '52', 'ERROR', 'PWS-52', 'Item is not active', false),
  (21, 'ORACLE', '60', 'ERROR', 'PWS-60', 'Quantity/Unit Selling Price cannot be zero', false),
  (22, 'ORACLE', '61', 'ERROR', 'PWS-61', 'Quantity/Unit Selling Price cannot be negative', false),
  (23, 'ORACLE', '62', 'ERROR', 'PWS-62', 'Unit Selling Price is not valid. Precision is 2', false),
  (24, 'ORACLE', '63', 'ERROR', 'PWS-63', 'Quantity/Unit Selling Price is not a valid number.', false),
  (25, 'ORACLE', '80', 'ERROR', 'PWS-80', 'Origin System User cannot be blank nor exceed 2000 characters.', false),
  (26, 'ORACLE', '97', 'ERROR', 'PWS-97', 'Oracle Order creation error', false),
  (27, 'ORACLE', '99', 'ERROR', 'PWS-99', 'Unexpected program error', false)
ON CONFLICT DO NOTHING;


SELECT setval('integration.deposco_config_id_seq', 1, true);
SELECT setval('integration.error_mapping_id_seq', 27, true);