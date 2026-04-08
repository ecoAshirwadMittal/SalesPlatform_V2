-- =============================================================================
-- V26: Seed dev user profiles, accounts, and buyer associations
--
-- V15 created identity.users and role assignments but not the profile/buyer
-- links needed for buyer-code authorization and buyer-select page.
-- =============================================================================

-- 1. Create accounts for dev users (needed for name/email display)
INSERT INTO identity.accounts (user_id, full_name, email, is_local_user) VALUES
  (9001, 'Admin User',    'admin@test.com',       true),
  (9002, 'CoAdmin User',  'coadmin@test.com',     true),
  (9003, 'SalesOps User', 'salesops@test.com',    true),
  (9004, 'SalesRep User', 'salesrep@test.com',    true),
  (9005, 'Bidder User',   'bidder@buyerco.com',   true),
  (9006, 'Direct Admin',  'directadmin@test.com', true)
ON CONFLICT (user_id) DO NOTHING;

-- 2. Create ecoatm_direct_users profiles
INSERT INTO user_mgmt.ecoatm_direct_users
  (user_id, first_name, last_name, is_buyer_role, overall_user_status, landing_page_preference)
VALUES
  (9001, 'Admin',    'User',  false, 'Active', NULL),
  (9002, 'CoAdmin',  'User',  false, 'Active', NULL),
  (9003, 'SalesOps', 'User',  false, 'Active', NULL),
  (9004, 'SalesRep', 'User',  false, 'Active', NULL),
  (9005, 'Bidder',   'User',  true,  'Active', 'Premium_Wholesale'),
  (9006, 'Direct',   'Admin', false, 'Active', NULL)
ON CONFLICT (user_id) DO NOTHING;

-- 3. Link bidder user to buyer companies
--    buyer_id=73 (Nadia Boonnayanont) → codes 22379, NB_PWS
--    buyer_id=91 (PWS Buyer)          → codes PWS02, PWS03
INSERT INTO user_mgmt.user_buyers (user_id, buyer_id) VALUES
  (9005, 73),   -- Bidder → Nadia Boonnayanont (22379, NB_PWS)
  (9005, 91)    -- Bidder → PWS Buyer (PWS02, PWS03)
ON CONFLICT DO NOTHING;

-- 4. Also link SalesRep to a buyer for testing
INSERT INTO user_mgmt.user_buyers (user_id, buyer_id) VALUES
  (9004, 73),   -- SalesRep → Nadia Boonnayanont
  (9004, 91)    -- SalesRep → PWS Buyer
ON CONFLICT DO NOTHING;
