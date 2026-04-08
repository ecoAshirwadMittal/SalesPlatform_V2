-- =============================================================================
-- V25: Add sequence for identity.users PK generation
-- Original Mendix IDs were externally managed; new users created by the
-- modern app need an auto-incrementing source that starts above the max
-- migrated ID.
-- =============================================================================

DO $$
DECLARE
    max_id BIGINT;
BEGIN
    SELECT COALESCE(MAX(id), 0) INTO max_id FROM identity.users;
    EXECUTE format('CREATE SEQUENCE identity.users_id_seq START WITH %s INCREMENT BY 1', max_id + 1);
END $$;
