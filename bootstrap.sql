-- =============================================================================
-- Bootstrap Script: Run ONCE as a PostgreSQL superuser (e.g., postgres)
-- to set up the application database user and database.
--
-- Usage (Windows):
--   "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -f bootstrap.sql
--
-- Or paste into pgAdmin's Query Tool connected as postgres superuser.
-- =============================================================================

-- Create application role (idempotent)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'salesplatform') THEN
        CREATE ROLE salesplatform LOGIN PASSWORD 'salesplatform';
        RAISE NOTICE 'Role salesplatform created.';
    ELSE
        RAISE NOTICE 'Role salesplatform already exists — skipped.';
    END IF;
END $$;

-- Create database (cannot be inside a DO block — run separately if DB exists)
SELECT 'CREATE DATABASE salesplatform_dev OWNER salesplatform ENCODING ''UTF8'' LC_COLLATE ''en_US.UTF-8'' LC_CTYPE ''en_US.UTF-8'' TEMPLATE template0'
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'salesplatform_dev') \gexec

-- Grant connection privilege
GRANT CONNECT ON DATABASE salesplatform_dev TO salesplatform;

\echo ''
\echo '✅ Bootstrap complete. Next step:'
\echo '   cd backend && mvn flyway:migrate'
\echo ''
