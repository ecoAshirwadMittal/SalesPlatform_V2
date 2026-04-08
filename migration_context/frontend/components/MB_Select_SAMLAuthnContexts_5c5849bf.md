# Microflow Analysis: MB_Select_SAMLAuthnContexts

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)
- **$SAMLAuthnContextList** (A record of type: SAML20.SAMLAuthnContext)

### Execution Steps:
1. **Run another process: "SAML20.MF_CreateConfiguredSAMLAuthnContext_FromList"
      - Store the result in a new variable called **$Variable****
2. **Close Form**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
