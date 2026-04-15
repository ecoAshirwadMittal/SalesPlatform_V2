-- Repeatable migration: Apply updated_date / changed_date triggers to all tables.
-- This is the single source of truth for trigger management (supersedes V11 trigger block).
-- Flyway re-runs this whenever its checksum changes.

-- ══════════════════════════════════════════════════════════════════════════════
-- changed_date trigger (identity, user_mgmt, buyer_mgmt, sso schemas)
-- ══════════════════════════════════════════════════════════════════════════════

CREATE OR REPLACE FUNCTION update_changed_date()
RETURNS TRIGGER AS $$
BEGIN
    NEW.changed_date = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE
    t RECORD;
BEGIN
    FOR t IN VALUES
        ('identity',   'users'),
        ('identity',   'accounts'),
        ('identity',   'sessions'),
        ('user_mgmt',  'ecoatm_direct_users'),
        ('buyer_mgmt', 'buyers'),
        ('buyer_mgmt', 'buyer_codes'),
        ('buyer_mgmt', 'qualified_buyer_codes'),
        ('buyer_mgmt', 'sales_representatives'),
        ('sso',        'sso_configurations'),
        ('sso',        'sso_audit_log'),
        ('sso',        'password_reset_tokens')
    LOOP
        EXECUTE format(
            'DROP TRIGGER IF EXISTS trg_update_changed_date ON %I.%I;
             CREATE TRIGGER trg_update_changed_date
             BEFORE UPDATE ON %I.%I
             FOR EACH ROW EXECUTE FUNCTION update_changed_date()',
            t.column1, t.column2, t.column1, t.column2
        );
    END LOOP;
END $$;

-- ══════════════════════════════════════════════════════════════════════════════
-- updated_date trigger (pws, mdm, integration, email schemas)
-- Finding 14: Consolidate trigger logic for all tables with updated_date
-- ══════════════════════════════════════════════════════════════════════════════

CREATE OR REPLACE FUNCTION update_updated_date()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_date = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE
    t RECORD;
BEGIN
    FOR t IN VALUES
        -- pws schema
        ('pws', 'offer'),
        ('pws', 'offer_item'),
        ('pws', 'order'),
        ('pws', 'shipment_detail'),
        ('pws', 'rma'),
        ('pws', 'rma_item'),
        ('pws', 'rma_status'),
        ('pws', 'rma_reason'),
        ('pws', 'rma_template'),
        ('pws', 'case_lot'),
        ('pws', 'imei_detail'),
        ('pws', 'future_price_config'),
        ('pws', 'feature_flag'),
        ('pws', 'pws_constants'),
        ('pws', 'order_status_config'),
        ('pws', 'maintenance_mode'),
        ('pws', 'navigation_menu'),
        -- mdm schema
        ('mdm', 'device'),
        ('mdm', 'price_history'),
        -- integration schema
        ('integration', 'oracle_config'),
        ('integration', 'deposco_config'),
        -- email schema
        ('email', 'smtp_config'),
        ('email', 'email_template')
    LOOP
        EXECUTE format(
            'DROP TRIGGER IF EXISTS trg_update_updated_date ON %I.%I;
             CREATE TRIGGER trg_update_updated_date
             BEFORE UPDATE ON %I.%I
             FOR EACH ROW EXECUTE FUNCTION update_updated_date()',
            t.column1, t.column2, t.column1, t.column2
        );
    END LOOP;
END $$;
