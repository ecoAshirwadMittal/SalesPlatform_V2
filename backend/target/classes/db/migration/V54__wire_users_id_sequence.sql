-- =============================================================================
-- V54: Wire identity.users_id_seq to users.id
--
-- Finding 18: The sequence exists but is not wired as the column default.
-- =============================================================================

ALTER TABLE identity.users ALTER COLUMN id SET DEFAULT nextval('identity.users_id_seq');
ALTER SEQUENCE identity.users_id_seq OWNED BY identity.users.id;

-- Ensure sequence is ahead of existing data
SELECT setval('identity.users_id_seq',
              COALESCE((SELECT MAX(id) FROM identity.users), 0) + 1,
              false);
