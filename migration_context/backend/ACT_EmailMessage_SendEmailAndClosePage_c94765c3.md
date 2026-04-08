# Microflow Analysis: ACT_EmailMessage_SendEmailAndClosePage

### Requirements (Inputs):
- **$EmailMessage** (A record of type: Email_Connector.EmailMessage)

### Execution Steps:
1. **Run another process: "Email_Connector.SUB_SendEmail"
      - Store the result in a new variable called **$Variable****
2. **Decision:** "IsSent?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Retrieve
      - Store the result in a new variable called **$EmailAccount****
4. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailAccount.ComposeEmail] to: "false"**
5. **Close Form**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
