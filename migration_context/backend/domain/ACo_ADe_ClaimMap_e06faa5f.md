# Microflow Analysis: ACo_ADe_ClaimMap

### Requirements (Inputs):
- **$ClaimMap** (A record of type: SAML20.ClaimMap)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SSOConfiguration****
2. **Run another process: "SAML20.SSOConfigurationReloadConfig_ADe"
      - Store the result in a new variable called **$Variable****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
