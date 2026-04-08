-- =============================================================================
-- V15: Seed development roles and sample users
--
-- Purpose: Provide working login credentials for every known user role so the
--          login page (http://localhost:3000/login) is immediately testable.
--
-- Roles seeded (from Mendix prod): Administrator, Co-Admin, SalesOps,
--   SalesRep, Bidder, ecoAtmDirectAdmin
--
-- IMPORTANT: This migration is for LOCAL DEV only.
--            Never deploy this to staging/production environments.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- 1. Seed user roles
-- IDs 1001–1006 to avoid collision with future prod data imports.
-- model_guid values are synthetic dev GUIDs.
-- ---------------------------------------------------------------------------
INSERT INTO identity.user_roles (id, model_guid, name, description) VALUES
  (1001, 'dev-guid-0001-administrator',  'Administrator',     'Full system access — manage users, roles, settings, and all operational features'),
  (1002, 'dev-guid-0002-coadmin',        'Co-Admin',          'Delegated admin with most Administrator privileges except role management'),
  (1003, 'dev-guid-0003-salesops',       'SalesOps',          'Sales Operations — manages auctions, bid data, and buyer qualifications'),
  (1004, 'dev-guid-0004-salesrep',       'SalesRep',          'Sales Representative — views inventory and manages buyer relationships'),
  (1005, 'dev-guid-0005-bidder',         'Bidder',            'External buyer who participates in auctions and submits bids'),
  (1006, 'dev-guid-0006-directadmin',    'ecoAtmDirectAdmin', 'ecoATM Direct program administrator — manages direct-sale buyers and pricing')
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------------------------------------
-- 2. Seed sample users (one per role)
--    IDs 9001–9006, clearly dev-only.
--    Passwords are BCrypt ($2b$10$) hashes — Spring BCryptPasswordEncoder
--    accepts both $2a$ and $2b$ prefixes.
--
--    Use NON-ecoatm.com emails so the frontend doesn't redirect to SSO.
-- ---------------------------------------------------------------------------
--    | Email                      | Password       | Role             |
--    |----------------------------|----------------|------------------|
--    | admin@test.com             | Admin123!      | Administrator    |
--    | coadmin@test.com           | CoAdmin123!    | Co-Admin         |
--    | salesops@test.com          | SalesOps123!   | SalesOps         |
--    | salesrep@test.com          | SalesRep123!   | SalesRep         |
--    | bidder@buyerco.com         | Bidder123!     | Bidder           |
--    | directadmin@test.com       | Direct123!     | ecoAtmDirectAdmin|
-- ---------------------------------------------------------------------------
INSERT INTO identity.users (id, user_type, name, password, active, blocked, failed_logins, web_service_user, is_anonymous) VALUES
  (9001, 'Administration.Account',                  'admin@test.com',       '$2b$10$kvNUvxXhdOPLCgNHK2sGTeHLN6ODQgjfmMjWmE8nm.H0kv9SYjy1K', true, false, 0, false, false),
  (9002, 'Administration.Account',                  'coadmin@test.com',     '$2b$10$Yi7pFG4elgPqEuQDyCrqruRRk84iylC3P.APs4mFW0YuNeytws0yG', true, false, 0, false, false),
  (9003, 'Administration.Account',                  'salesops@test.com',    '$2b$10$OdgxmeCD08Pbq53CU59M8OyjI9JgFVgUiMpLO0CsC.lWK4/0/s0Cu', true, false, 0, false, false),
  (9004, 'Administration.Account',                  'salesrep@test.com',    '$2b$10$K8YsBbMRK8lLmH5Vq.HTPO1KjYMH76/6Mup3LRI7/ZHhrCStq7EbK', true, false, 0, false, false),
  (9005, 'EcoATM_UserManagement.EcoATMDirectUser',  'bidder@buyerco.com',   '$2b$10$pe5V18PUjvfOsJTpvS4kgOfwyu7/bdAlrqs3agUmt7M7tGFLY.D.e',  true, false, 0, false, false),
  (9006, 'Administration.Account',                  'directadmin@test.com', '$2b$10$6XWarAdFAGyV3lPpsUkSEehN16OfScm1D9ckSCOWORc1FU4CAlpMO', true, false, 0, false, false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- 3. Assign each user to their role
-- ---------------------------------------------------------------------------
INSERT INTO identity.user_role_assignments (user_id, role_id) VALUES
  (9001, 1001),  -- admin@test.com        → Administrator
  (9002, 1002),  -- coadmin@test.com       → Co-Admin
  (9003, 1003),  -- salesops@test.com      → SalesOps
  (9004, 1004),  -- salesrep@test.com      → SalesRep
  (9005, 1005),  -- bidder@buyerco.com     → Bidder
  (9006, 1006)   -- directadmin@test.com   → ecoAtmDirectAdmin
ON CONFLICT DO NOTHING;
