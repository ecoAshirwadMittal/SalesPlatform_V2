-- =============================================================================
-- V5: SSO – SAML Requests, Responses, and Audit Log
-- Source: saml20$samlrequest, saml20$samlresponse, saml20$ssolog
--
-- Design notes:
--   • samlrequest and samlresponse are linked via M:M in Mendix but in practice
--     a request maps to exactly one response. We store response_id on the request.
--   • ssolog is the SSO access audit log — retained in full for compliance.
--   • All tables reference sso_config to allow multi-IdP expansion later.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- SAML Responses (arrive before requests are correlated in some flows)
-- Source: saml20$samlresponse
-- ---------------------------------------------------------------------------
CREATE TABLE sso.saml_responses (
    id              BIGINT  PRIMARY KEY,
    sso_config_id   BIGINT  REFERENCES sso.sso_configurations(id)
);

COMMENT ON TABLE sso.saml_responses IS 'SAML response records (saml20$samlresponse); linked to SSO config';

-- ---------------------------------------------------------------------------
-- SAML Requests (SP-initiated auth requests)
-- Source: saml20$samlrequest + saml20$samlrequest_ssoconfiguration +
--         saml20$samlrequest_samlresponse
-- ---------------------------------------------------------------------------
CREATE TABLE sso.saml_requests (
    id                  BIGINT        PRIMARY KEY,
    request_id          VARCHAR(200),                 -- UUID sent in AuthnRequest
    has_request         VARCHAR(3)    DEFAULT 'Yes',  -- Yes/No (Mendix legacy field)
    has_response        VARCHAR(3)    DEFAULT 'No',   -- Yes/No
    returned_principal  VARCHAR(200),                 -- email of authenticated user
    response_id         VARCHAR(200),                 -- IDP-assigned response ID
    sso_config_id       BIGINT        REFERENCES sso.sso_configurations(id),
    saml_response_id    BIGINT        REFERENCES sso.saml_responses(id)
);

COMMENT ON TABLE  sso.saml_requests IS 'SAML SP-initiated authentication requests (saml20$samlrequest)';
COMMENT ON COLUMN sso.saml_requests.returned_principal IS 'Email/NameID of the user returned by the IdP after successful authentication';

-- ---------------------------------------------------------------------------
-- SSO Access Audit Log (compliance retention required)
-- Source: saml20$ssolog
-- ---------------------------------------------------------------------------
CREATE TABLE sso.sso_audit_log (
    id              BIGINT        PRIMARY KEY,
    message         TEXT,
    logon_result    VARCHAR(7)    NOT NULL DEFAULT 'Success',  -- Success | Failure
    created_date    TIMESTAMP     NOT NULL DEFAULT NOW(),
    changed_date    TIMESTAMP     NOT NULL DEFAULT NOW(),
    -- No owner/changedby in prod data — anonymous SSO flow
    owner_id        BIGINT        REFERENCES identity.users(id),
    changed_by_id   BIGINT        REFERENCES identity.users(id)
);

COMMENT ON TABLE  sso.sso_audit_log IS 'SAML SSO login attempt audit log (saml20$ssolog); retained for compliance';
COMMENT ON COLUMN sso.sso_audit_log.logon_result IS 'Outcome of the SSO flow: Success or Failure';

-- Performance indexes for audit queries
CREATE INDEX idx_sso_audit_log_created    ON sso.sso_audit_log(created_date DESC);
CREATE INDEX idx_sso_audit_log_result     ON sso.sso_audit_log(logon_result);
CREATE INDEX idx_saml_requests_principal  ON sso.saml_requests(returned_principal);
CREATE INDEX idx_saml_requests_request_id ON sso.saml_requests(request_id);
