# Microflow Analysis: ACT_LDAPConfiguration_ChangeBaseName

### Requirements (Inputs):
- **$LDAPBaseName** (A record of type: Email_Connector.LDAPBaseDN)
- **$LDAPConfiguration** (A record of type: Email_Connector.LDAPConfiguration)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [Email_Connector.LDAPConfiguration.BaseDN] to: "$LDAPBaseName/Name"**
2. **Close Form**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
