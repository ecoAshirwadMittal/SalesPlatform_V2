-- Apply update_changed_date trigger to all high-churn tables
-- Run this after all V1-V11 migrations are applied.

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
