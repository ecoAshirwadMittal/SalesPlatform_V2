# Microflow Analysis: MB_SelectEntityDescriptor

### Requirements (Inputs):
- **$EntityDescriptor** (A record of type: SAML20.EntityDescriptor)
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [SAML20.SSOConfiguration_PreferedEntityDescriptor] to: "$EntityDescriptor"
      - **Save:** This change will be saved to the database immediately.**
2. **Close Form**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
