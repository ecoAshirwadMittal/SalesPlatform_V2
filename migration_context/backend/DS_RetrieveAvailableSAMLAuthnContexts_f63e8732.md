# Microflow Analysis: DS_RetrieveAvailableSAMLAuthnContexts

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Search the Database for **SAML20.SAMLAuthnContext** using filter: { [not(SAML20.ConfiguredSAMLAuthnContext_SAMLAuthnContext/SAML20.ConfiguredSAMLAuthnContext/SAML20.ConfiguredSAMLAuthnContext_SSOConfiguration = $SSOConfiguration)] } (Call this list **$AvailableSAMLAuthnContextList**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
