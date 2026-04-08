# Microflow Analysis: SUB_SendQueuedEmail

### Requirements (Inputs):
- **$EmailMessage** (A record of type: Email_Connector.EmailMessage)
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Log Message**
2. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName**** ⚠️ *(This step has a safety catch if it fails)*
3. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailMessage.SentDate] to: "[%CurrentDateTime%]"
      - Change [Email_Connector.EmailMessage.LastSendError] to: "empty"
      - Change [Email_Connector.EmailMessage.LastSendAttemptAt] to: "[%CurrentDateTime%]"
      - Change [Email_Connector.EmailMessage.Status] to: "Email_Connector.ENUM_EmailStatus.SENT"
      - Change [Email_Connector.EmailMessage.QueuedForSending] to: "false"
      - Change [Email_Connector.EmailMessage.From] to: "$EmailAccount/MailAddress"**
4. **Log Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
