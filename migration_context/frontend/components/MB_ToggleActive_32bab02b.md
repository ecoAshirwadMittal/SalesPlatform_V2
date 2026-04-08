# Microflow Analysis: MB_ToggleActive

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [SAML20.SSOConfiguration.Active] to: "not($SSOConfiguration/Active)"
      - **Save:** This change will be saved to the database immediately.**
2. **Java Action Call
      - Store the result in a new variable called **$Variable_1_1**** ⚠️ *(This step has a safety catch if it fails)*
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
