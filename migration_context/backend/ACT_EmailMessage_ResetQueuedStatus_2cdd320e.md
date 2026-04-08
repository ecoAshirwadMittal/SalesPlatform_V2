# Microflow Analysis: ACT_EmailMessage_ResetQueuedStatus

### Requirements (Inputs):
- **$EmailMessage** (A record of type: Email_Connector.EmailMessage)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailMessage.Status] to: "Email_Connector.ENUM_EmailStatus.QUEUED"
      - Change [Email_Connector.EmailMessage.ResendAttempts] to: "0"
      - **Save:** This change will be saved to the database immediately.**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
