# Microflow Analysis: ACT_EmailMessage_SendComposedEmail

### Requirements (Inputs):
- **$EmailMessage** (A record of type: Email_Connector.EmailMessage)

### Execution Steps:
1. **Increment Counter Meter**
2. **Run another process: "Email_Connector.SUB_SendEmail"
      - Store the result in a new variable called **$Variable****
3. **Decision:** "IsSent?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Retrieve
      - Store the result in a new variable called **$EmailAccount****
5. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailAccount.ComposeEmail] to: "false"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
