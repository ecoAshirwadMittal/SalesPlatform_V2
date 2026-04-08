# Microflow Analysis: MB_SelectAttribute

### Requirements (Inputs):
- **$Attribute** (A record of type: SAML20.Attribute)
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [SAML20.SSOConfiguration_Attribute] to: "$Attribute"**
2. **Close Form**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
