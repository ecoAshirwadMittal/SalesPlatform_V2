# Microflow Analysis: MB_MigrateToConfiguredSAMLAuthnContexts

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SAMLAuthnContextList****
2. **Run another process: "SAML20.MF_CreateConfiguredSAMLAuthnContext_FromList"
      - Store the result in a new variable called **$Variable****
3. **Update the **$undefined** (Object):
      - Change [SAML20.SSOConfiguration.MigratedToPrioritizedSAMLAuthnContexts] to: "true"
      - Change [SAML20.SSOConfiguration_SAMLAuthnContext] to: "empty"
      - **Save:** This change will be saved to the database immediately.**
4. **Show Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
