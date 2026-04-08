-- =============================================================================
-- V4: SSO – Configuration, IDP Metadata, Keystore, Certificates
-- Source: saml20$ssoconfiguration, saml20$idpmetadata, saml20$keystore,
--         saml20$spmetadata, saml20$x509certificate, saml20$endpoint,
--         saml20$attribute, saml20$claimmap, saml20$samlauthncontext
--
-- Design notes:
--   • The full saml20 module has 40+ tables for XML metadata parsing.
--     We collapse the IDP metadata XML into a single TEXT column to avoid
--     a brittle 15-table normalization of an XML document.
--   • The SSO configuration, keystore, and SP metadata are fully modeled.
--   • Claim maps (attribute → user field) are stored as JSONB for flexibility.
--   • samlauthncontext lookup table is kept for the 26 standard contexts.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- SAML Authentication Contexts (26 standard values, provisioned at startup)
-- Source: saml20$samlauthncontext
-- ---------------------------------------------------------------------------
CREATE TABLE sso.saml_authn_contexts (
    id               BIGINT        PRIMARY KEY,
    description      VARCHAR(200),
    value            VARCHAR(200)  NOT NULL UNIQUE,   -- URN value
    default_priority INTEGER,
    provisioned      BOOLEAN       NOT NULL DEFAULT true,
    created_date     TIMESTAMP,
    changed_date     TIMESTAMP
);

COMMENT ON TABLE  sso.saml_authn_contexts IS 'Standard SAML 2.0 authentication context class references (saml20$samlauthncontext)';
COMMENT ON COLUMN sso.saml_authn_contexts.value IS 'Full URN e.g. urn:oasis:names:tc:SAML:2.0:ac:classes:Password';

-- ---------------------------------------------------------------------------
-- Keystore (SP private key for signing/encryption)
-- Source: saml20$keystore
-- ---------------------------------------------------------------------------
CREATE TABLE sso.keystores (
    id                BIGINT        PRIMARY KEY,
    alias             VARCHAR(200)  NOT NULL,        -- e.g. https://buy.ecoatmdirect.com
    password          VARCHAR(200),                  -- AES-encrypted in Mendix, store as-is for migration
    rebuild_keystore  BOOLEAN       NOT NULL DEFAULT false,
    last_changed_on   TIMESTAMP
);

COMMENT ON TABLE  sso.keystores IS 'SAML SP signing/encryption keystore (saml20$keystore)';
COMMENT ON COLUMN sso.keystores.password IS 'Encrypted keystore password; application layer decrypts with AES key';

-- ---------------------------------------------------------------------------
-- X.509 Certificates (IDP signing certificates)
-- Source: saml20$x509certificate
-- ---------------------------------------------------------------------------
CREATE TABLE sso.x509_certificates (
    id           BIGINT        PRIMARY KEY,
    issuer_name  VARCHAR(500),
    serial_number TEXT,
    subject      VARCHAR(500),
    valid_from   TIMESTAMP,
    valid_until  TIMESTAMP,
    base64_cert  TEXT          -- PEM-encoded certificate body
);

COMMENT ON TABLE sso.x509_certificates IS 'X.509 certificates used by the IDP for signing assertions (saml20$x509certificate)';

-- ---------------------------------------------------------------------------
-- IDP Metadata (collapsed from 15-table XML parse tree)
-- Source: saml20$idpmetadata + saml20$entitiesdescriptor + saml20$entitydescriptor
-- ---------------------------------------------------------------------------
CREATE TABLE sso.idp_metadata (
    id                  BIGINT        PRIMARY KEY,
    entity_id           VARCHAR(200),  -- e.g. https://sts.windows.net/{tenant}/
    idp_metadata_url    VARCHAR(500),  -- URL to fetch metadata XML (saml20$ssoconfiguration.idpmetadataurl)
    metadata_xml        TEXT,          -- raw XML for full fidelity; populated by scheduled refresh job
    updated             BOOLEAN       NOT NULL DEFAULT false,
    signing_cert_id     BIGINT        REFERENCES sso.x509_certificates(id)
);

COMMENT ON TABLE  sso.idp_metadata IS 'IDP metadata collapsed from saml20 XML parse tree — entity_id + raw XML stored for refresh';
COMMENT ON COLUMN sso.idp_metadata.metadata_xml IS 'Raw federation metadata XML fetched from idp_metadata_url; replaces 15-table normalization';

-- ---------------------------------------------------------------------------
-- SP Metadata (this application acting as SAML Service Provider)
-- Source: saml20$spmetadata
-- ---------------------------------------------------------------------------
CREATE TABLE sso.sp_metadata (
    id                              BIGINT        PRIMARY KEY,
    entity_id                       VARCHAR(200)  NOT NULL,  -- e.g. https://buy.ecoatmdirect.com
    organization_name               VARCHAR(200),
    organization_display_name       VARCHAR(200),
    organization_url                VARCHAR(200),
    contact_given_name              VARCHAR(200),
    contact_surname                 VARCHAR(200),
    contact_email_address           VARCHAR(200),
    application_url                 VARCHAR(200),
    does_entity_id_differ_from_appurl BOOLEAN     NOT NULL DEFAULT false,
    log_available_days              INTEGER,
    use_encryption                  BOOLEAN       NOT NULL DEFAULT true,
    encryption_method               VARCHAR(13),  -- e.g. SHA256WithRSA
    encryption_key_length           VARCHAR(19),  -- e.g. _2048bit_Encryption
    keystore_id                     BIGINT        REFERENCES sso.keystores(id)
);

COMMENT ON TABLE sso.sp_metadata IS 'SAML SP self-description metadata (saml20$spmetadata)';

-- ---------------------------------------------------------------------------
-- SSO Configuration (one active config = AzureAD in prod)
-- Source: saml20$ssoconfiguration
-- ---------------------------------------------------------------------------
CREATE TABLE sso.sso_configurations (
    id                                  BIGINT        PRIMARY KEY,
    alias                               VARCHAR(300)  NOT NULL,  -- e.g. AzureAD
    active                              BOOLEAN       NOT NULL DEFAULT false,
    is_saml_logging_enabled             BOOLEAN       NOT NULL DEFAULT true,
    authn_context                       VARCHAR(7),   -- EXACT | MINIMUM | MAXIMUM | BETTER
    read_idp_metadata_from_url          BOOLEAN       NOT NULL DEFAULT true,
    create_users                        BOOLEAN       NOT NULL DEFAULT false,
    allow_idp_initiated_authentication  BOOLEAN       NOT NULL DEFAULT true,
    identifying_assertion_type          VARCHAR(19),  -- Use_Name_ID | Use_Attribute
    custom_identifying_assertion_name   TEXT,
    use_custom_logic_for_provisioning   BOOLEAN       NOT NULL DEFAULT false,
    use_custom_after_signin_logic       BOOLEAN       NOT NULL DEFAULT false,
    disable_nameid_policy               BOOLEAN       NOT NULL DEFAULT false,
    enable_delegated_authentication     BOOLEAN       NOT NULL DEFAULT false,
    delegated_authentication_url        VARCHAR(500),
    enable_mobile_auth_token            BOOLEAN       NOT NULL DEFAULT false,
    response_protocol_binding           VARCHAR(16),  -- POST_BINDING | REDIRECT_BINDING
    enable_assertion_consumer_service_index VARCHAR(3),
    assertion_consumer_service_index    INTEGER       NOT NULL DEFAULT 0,
    enable_force_authentication         BOOLEAN       NOT NULL DEFAULT false,
    use_encryption                      BOOLEAN       NOT NULL DEFAULT true,
    encryption_method                   VARCHAR(13),
    encryption_key_length               VARCHAR(19),
    wizard_mode                         BOOLEAN       NOT NULL DEFAULT false,
    current_wizard_step                 VARCHAR(6),
    -- Claim map stored as JSONB: [{ "attribute_name": "...", "user_field": "..." }]
    -- Replaces saml20$claimmap + saml20$claimmap_attribute + saml20$claimmap_mxobjectmember
    claim_maps                          JSONB         NOT NULL DEFAULT '[]',
    -- References
    idp_metadata_id                     BIGINT        REFERENCES sso.idp_metadata(id),
    keystore_id                         BIGINT        REFERENCES sso.keystores(id),
    default_role_id                     BIGINT        REFERENCES identity.user_roles(id)
);

COMMENT ON TABLE  sso.sso_configurations IS 'SAML 2.0 SSO integration configuration — one row per IdP (saml20$ssoconfiguration)';
COMMENT ON COLUMN sso.sso_configurations.claim_maps IS 'JSONB array mapping SAML assertion attributes to user profile fields; replaces saml20$claimmap 3-table join';
COMMENT ON COLUMN sso.sso_configurations.default_role_id IS 'Role assigned to newly provisioned SSO users (maps ssoconfiguration_defaultuserroletoassign)';

-- ---------------------------------------------------------------------------
-- SSO Configuration ↔ Authn Contexts (ordered priority list)
-- Source: saml20$configuredsamlauthncontext (collapsed into ordered array approach)
-- ---------------------------------------------------------------------------
CREATE TABLE sso.sso_config_authn_contexts (
    sso_config_id   BIGINT  NOT NULL REFERENCES sso.sso_configurations(id) ON DELETE CASCADE,
    authn_context_id BIGINT NOT NULL REFERENCES sso.saml_authn_contexts(id),
    priority        INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (sso_config_id, authn_context_id)
);

COMMENT ON TABLE sso.sso_config_authn_contexts IS 'Ordered authentication context classes configured per SSO config';

-- Indexes
CREATE INDEX idx_sso_config_active  ON sso.sso_configurations(active);
CREATE INDEX idx_idp_metadata_entity ON sso.idp_metadata(entity_id);
