# Microflow Analysis: ACT_EmailAccount_SaveManualConfig

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Decision:** "IncomingSet?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **OutgoingSet?**
2. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailAccount.MailAddress] to: "if($EmailAccount/IsSharedMailbox) then
$EmailAccount/MailAddress
else $EmailAccount/Username
"**
3. **Decision:** "isOAuth?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Run another process: "Email_Connector.SUB_EmailAccount_Save"**
5. **Close Form**
6. **Decision:** "isOAuth?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
7. **Show Page**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
