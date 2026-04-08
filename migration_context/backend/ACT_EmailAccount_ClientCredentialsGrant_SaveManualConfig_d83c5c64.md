# Microflow Analysis: ACT_EmailAccount_ClientCredentialsGrant_SaveManualConfig

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
3. **Retrieve
      - Store the result in a new variable called **$OAuthProvider****
4. **Run another process: "Email_Connector.SUB_GetNewOAuthToken_ClientCredentialsGrantFlow"
      - Store the result in a new variable called **$NewOAuthToken_CC****
5. **Show Message**
6. **Delete**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
