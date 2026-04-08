# Microflow Analysis: SUB_SendEmailFromJavaAction

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)
- **$EmailMessage** (A record of type: Email_Connector.EmailMessage)
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Search the Database for **Email_Connector.Attachment** using filter: { [Email_Connector.Attachment_EmailTemplate = $EmailTemplate] } (Call this list **$AttachmentsFromTemplate**)**
2. **Run another process: "Email_Connector.SUB_EmailMessage_SetAttachments"
      - Store the result in a new variable called **$AttachmentsForEmail****
3. **Permanently save **$undefined** to the database.**
4. **Decision:** "QueuedForSending"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailMessage_EmailAccount] to: "$EmailAccount"
      - Change [Email_Connector.EmailMessage.From] to: "$EmailTemplate/FromAddress"
      - **Save:** This change will be saved to the database immediately.**
6. **Run another process: "Email_Connector.SUB_SendEmail"
      - Store the result in a new variable called **$Variable****
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
