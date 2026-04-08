# Microflow Analysis: ACT_EmailMessage_ComposeReply

### Requirements (Inputs):
- **$EmailMessage** (A record of type: Email_Connector.EmailMessage)
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Create Object
      - Store the result in a new variable called **$NewEmailMessageToSend****
2. **Decision:** "ReplyTo?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailMessage.To] to: "$EmailMessage/ReplyTo"**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
