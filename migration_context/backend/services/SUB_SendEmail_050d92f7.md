# Microflow Analysis: SUB_SendEmail

### Requirements (Inputs):
- **$EmailMessage** (A record of type: Email_Connector.EmailMessage)

### Execution Steps:
1. **Run another process: "Email_Connector.VAL_EmailRecipients"
      - Store the result in a new variable called **$Valid****
2. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Retrieve
      - Store the result in a new variable called **$EmailAccount****
4. **Retrieve
      - Store the result in a new variable called **$AttachmentList****
5. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailMessage.hasAttachments] to: "$AttachmentList != empty"**
6. **Java Action Call
      - Store the result in a new variable called **$isMailSent**** ⚠️ *(This step has a safety catch if it fails)*
7. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailMessage.SentDate] to: "[%CurrentDateTime%]"
      - Change [Email_Connector.EmailMessage.Status] to: "Email_Connector.ENUM_EmailStatus.SENT"
      - Change [Email_Connector.EmailMessage_EmailAccount] to: "$EmailAccount"
      - **Save:** This change will be saved to the database immediately.**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
