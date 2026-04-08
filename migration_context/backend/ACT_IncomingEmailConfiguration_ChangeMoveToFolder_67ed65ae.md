# Microflow Analysis: ACT_IncomingEmailConfiguration_ChangeMoveToFolder

### Requirements (Inputs):
- **$Folder** (A record of type: Email_Connector.Folder)
- **$IncomingEmailConfiguration** (A record of type: Email_Connector.IncomingEmailConfiguration)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [Email_Connector.IncomingEmailConfiguration.MoveFolder] to: "$Folder/Name"**
2. **Close Form**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
