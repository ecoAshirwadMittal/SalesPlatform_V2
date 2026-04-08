# Microflow Analysis: DS_GetSSOMetadataLink

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Run another process: "SAML20.DS_GetSPMetadata"
      - Store the result in a new variable called **$SPMetadata****
2. **Create Object
      - Store the result in a new variable called **$NewSSOMetaDataLink****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
