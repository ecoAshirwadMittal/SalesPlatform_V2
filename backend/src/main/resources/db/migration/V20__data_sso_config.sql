-- =============================================================================
-- V20: Data migration — SSO configuration (SAML, keystores, certs)
-- Generated from QA database on 2026-04-08 10:06
-- =============================================================================

-- SAML Authn Contexts
INSERT INTO sso.saml_authn_contexts (id, description, value, default_priority, provisioned, created_date, changed_date) VALUES
  (1, 'Kerberos', 'urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos', 1000, true, '2024-02-26T23:47:27.193000', '2026-04-06T16:48:17.986000'),
  (2, 'Integrated Windows Authentication', 'urn:federation:authentication:windows', 900, true, '2024-02-26T23:47:27.199000', '2026-04-06T16:48:17.992000'),
  (3, 'Public Key – X.509', 'urn:oasis:names:tc:SAML:2.0:ac:classes:X509', 800, true, '2024-02-26T23:47:27.205000', '2026-04-06T16:48:17.994000'),
  (4, 'SSL/TLS Certificate-Based Client Authentication', 'urn:oasis:names:tc:SAML:2.0:ac:classes:TLSClient', 700, true, '2024-02-26T23:47:27.208000', '2026-04-06T16:48:17.997000'),
  (5, 'PasswordProtectedTransport', 'urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport', 600, true, '2024-02-26T23:47:27.212000', '2026-04-06T16:48:18.001000'),
  (6, 'Password', 'urn:oasis:names:tc:SAML:2.0:ac:classes:Password', 500, true, '2024-02-26T23:47:27.215000', '2026-04-06T16:48:18.003000'),
  (7, 'Internet Protocol', 'urn:oasis:names:tc:SAML:2.0:ac:classes:InternetProtocol', 400, true, '2024-02-26T23:47:27.219000', '2026-04-06T16:48:18.006000'),
  (8, 'InternetProtocolPassword', 'urn:oasis:names:tc:SAML:2.0:ac:classes:InternetProtocolPassword', 400, true, '2024-02-26T23:47:27.222000', '2026-04-06T16:48:18.009000'),
  (9, 'MobileOneFactorContract', 'urn:oasis:names:tc:SAML:2.0:ac:classes:MobileOneFactorContract', 400, true, '2024-02-26T23:47:27.225000', '2026-04-06T16:48:18.011000'),
  (10, 'MobileOneFactorUnregistered', 'urn:oasis:names:tc:SAML:2.0:ac:classes:MobileOneFactorUnregistered', 400, true, '2024-02-26T23:47:27.228000', '2026-04-06T16:48:18.013000'),
  (11, 'MobileTwoFactorContract', 'urn:oasis:names:tc:SAML:2.0:ac:classes:MobileTwoFactorContract', 400, true, '2024-02-26T23:47:27.231000', '2026-04-06T16:48:18.015000'),
  (12, 'MobileTwoFactorUnregistered', 'urn:oasis:names:tc:SAML:2.0:ac:classes:MobileTwoFactorUnregistered', 400, true, '2024-02-26T23:47:27.234000', '2026-04-06T16:48:18.018000'),
  (13, 'PreviousSession', 'urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession', 400, true, '2024-02-26T23:47:27.236000', '2026-04-06T16:48:18.020000'),
  (14, 'Public Key - XML Digital Signature', 'urn:oasis:names:tc:SAML:2.0:ac:classes:XMLDSig', 400, true, '2024-02-26T23:47:27.239000', '2026-04-06T16:48:18.022000'),
  (15, 'Public Key – PGP', 'urn:oasis:names:tc:SAML:2.0:ac:classes:PGP', 400, true, '2024-02-26T23:47:27.242000', '2026-04-06T16:48:18.024000'),
  (16, 'Public Key – SPKI', 'urn:oasis:names:tc:SAML:2.0:ac:classes:SPKI', 400, true, '2024-02-26T23:47:27.245000', '2026-04-06T16:48:18.026000'),
  (17, 'Secure Remote Password', 'urn:oasis:names:tc:SAML:2.0:ac:classes:SecureRemotePassword', 400, true, '2024-02-26T23:47:27.248000', '2026-04-06T16:48:18.029000'),
  (18, 'Smartcard', 'urn:oasis:names:tc:SAML:2.0:ac:classes:Smartcard', 400, true, '2024-02-26T23:47:27.250000', '2026-04-06T16:48:18.032000'),
  (19, 'SmartcardPKI', 'urn:oasis:names:tc:SAML:2.0:ac:classes:SmartcardPKI', 400, true, '2024-02-26T23:47:27.253000', '2026-04-06T16:48:18.034000'),
  (20, 'SoftwarePKI', 'urn:oasis:names:tc:SAML:2.0:ac:classes:SoftwarePKI', 400, true, '2024-02-26T23:47:27.256000', '2026-04-06T16:48:18.036000'),
  (21, 'Telephony ("Nomadic")', 'urn:oasis:names:tc:SAML:2.0:ac:classes:NomadTelephony', 400, true, '2024-02-26T23:47:27.258000', '2026-04-06T16:48:18.039000'),
  (22, 'Telephony (Authenticated)', 'urn:oasis:names:tc:SAML:2.0:ac:classes:AuthenticatedTelephony', 400, true, '2024-02-26T23:47:27.260000', '2026-04-06T16:48:18.041000'),
  (23, 'Telephony (Personalized)', 'urn:oasis:names:tc:SAML:2.0:ac:classes:PersonalTelephony', 400, true, '2024-02-26T23:47:27.263000', '2026-04-06T16:48:18.042000'),
  (24, 'Telephony', 'urn:oasis:names:tc:SAML:2.0:ac:classes:Telephony', 400, true, '2024-02-26T23:47:27.265000', '2026-04-06T16:48:18.044000'),
  (25, 'TimeSyncToken', 'urn:oasis:names:tc:SAML:2.0:ac:classes:TimeSyncToken', 400, true, '2024-02-26T23:47:27.268000', '2026-04-06T16:48:18.046000'),
  (26, 'Unspecified', 'urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified', 400, true, '2024-02-26T23:47:27.270000', '2026-04-06T16:48:18.049000')
ON CONFLICT DO NOTHING;

-- X.509 Certificates
INSERT INTO sso.x509_certificates (id, issuer_name, serial_number, subject, valid_from, valid_until, base64_cert) VALUES
  (1, 'CN=Microsoft Azure Federated SSO Certificate', '554a6665 652e878f 4f600421 73aa7b33', 'CN=Microsoft Azure Federated SSO Certificate', '2024-02-26T21:47:24', '2027-02-26T21:47:24', NULL),
  (2, 'CN=Microsoft Azure Federated SSO Certificate', '554a6665 652e878f 4f600421 73aa7b33', 'CN=Microsoft Azure Federated SSO Certificate', '2024-02-26T21:47:24', '2027-02-26T21:47:24', NULL)
ON CONFLICT DO NOTHING;

-- Keystores (minimal)
INSERT INTO sso.keystores (id, alias, password, rebuild_keystore, last_changed_on) VALUES
  (1, 'default', NULL, false, NULL)
ON CONFLICT DO NOTHING;

-- IDP Metadata
INSERT INTO sso.idp_metadata (id, entity_id, idp_metadata_url, metadata_xml, updated, signing_cert_id) VALUES
  (1, 'https://sts.windows.net/61c95826-2b6a-4da0-92e1-f07725a94cd7/', 'https://login.microsoftonline.com/61c95826-2b6a-4da0-92e1-f07725a94cd7/federationmetadata/2007-06/federationmetadata.xml?appid=67337a42-40a2-47d3-96c0-97114719d945', NULL, false, 1)
ON CONFLICT DO NOTHING;

-- SP Metadata
INSERT INTO sso.sp_metadata (id, entity_id, organization_name, organization_display_name, organization_url, contact_given_name, contact_surname, contact_email_address, application_url, does_entity_id_differ_from_appurl, log_available_days, use_encryption, encryption_method, encryption_key_length, keystore_id) VALUES
  (1, 'https://ecoatm-auctions-accp.mendixcloud.com', 'ecoATM Acceptance', 'AzureAD', 'https://ecoatm-auctions-accp.mendixcloud.com', 'Michale', 'Hale', 'michael.hale@ecoatm.com', 'https://ecoatm-auctions-accp.mendixcloud.com', false, 7, true, 'SHA256WithRSA', '_2048bit_Encryption', 1)
ON CONFLICT DO NOTHING;

-- SSO Configuration
INSERT INTO sso.sso_configurations (id, alias, active, is_saml_logging_enabled, authn_context, read_idp_metadata_from_url, create_users, allow_idp_initiated_authentication, identifying_assertion_type, custom_identifying_assertion_name, use_custom_logic_for_provisioning, use_custom_after_signin_logic, disable_nameid_policy, enable_delegated_authentication, delegated_authentication_url, enable_mobile_auth_token, response_protocol_binding, enable_assertion_consumer_service_index, assertion_consumer_service_index, enable_force_authentication, use_encryption, encryption_method, encryption_key_length, wizard_mode, current_wizard_step, claim_maps, idp_metadata_id, keystore_id, default_role_id) VALUES
  (1, 'AzureAD', true, true, 'EXACT', true, true, true, 'Use_Name_ID', NULL, false, true, true, false, NULL, false, 'POST_BINDING', 'No', 0, false, false, 'SHA256WithRSA', '_2048bit_Encryption', false, 'Step10', '[{"attribute_name": "http://schemas.microsoft.com/identity/claims/displayname", "user_field": "FullName"}, {"attribute_name": "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name", "user_field": "Email"}]', NULL, 1, 1)
ON CONFLICT DO NOTHING;

-- Forgot Password Config (singleton)
INSERT INTO sso.forgot_password_config (id, deeplink_identifier, reset_email_template, signup_email_template) VALUES
  (1, NULL, NULL, NULL)
ON CONFLICT DO NOTHING;
