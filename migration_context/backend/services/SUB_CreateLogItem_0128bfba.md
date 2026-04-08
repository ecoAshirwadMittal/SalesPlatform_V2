# Microflow Analysis: SUB_CreateLogItem

### Requirements (Inputs):
- **$TriggeredInMF** (A record of type: Object)
- **$LogType** (A record of type: Object)
- **$Message** (A record of type: Object)
- **$EmailMessage** (A record of type: Email_Connector.EmailMessage)
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)
- **$isUnread** (A record of type: Object)
- **$Error** (A record of type: System.Error)

### Execution Steps:
1. **Log Message**
2. **Create Object
      - Store the result in a new variable called **$NewEmailErrorLog****
3. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailConnectorLog.Logtype] to: "$LogType"
      - Change [Email_Connector.EmailConnectorLog.ErrorMessage] to: "$Error/Message
"
      - Change [Email_Connector.EmailConnectorLog.TriggeredInMF] to: "$TriggeredInMF"
      - Change [Email_Connector.EmailConnectorLog.StackTrace] to: "$Error/Stacktrace"
      - Change [Email_Connector.EmailConnectorLog.Message] to: "$Message"
      - Change [Email_Connector.EmailConnectorLog_EmailMessage] to: "$EmailMessage"
      - Change [Email_Connector.EmailConnectorLog_EmailAccount] to: "$EmailAccount"
      - Change [Email_Connector.EmailConnectorLog.IsUnread] to: "$isUnread"
      - **Save:** This change will be saved to the database immediately.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
