# Microflow Analysis: ACT_SelectedConfiguration_FetchEmailServerConfigAndShowConfigPage

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Decision:** "isEmailConfigAutoDetect?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Java Action Call
      - Store the result in a new variable called **$EmailProvider**** ⚠️ *(This step has a safety catch if it fails)*
3. **Decision:** "isOAuthUsedAndValid?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailProvider.Username] to: "$EmailAccount/Username"
      - Change [Email_Connector.EmailProvider.MailAddress] to: "$EmailAccount/MailAddress"
      - Change [Email_Connector.EmailProvider.Password] to: "$EmailAccount/Password"
      - Change [Email_Connector.EmailProvider.FromDisplayName] to: "$EmailAccount/FromDisplayName"
      - Change [Email_Connector.EmailProvider.IsSharedMailbox] to: "$EmailAccount/IsSharedMailbox"
      - Change [Email_Connector.EmailProvider.isOAuthUsed] to: "$EmailAccount/isOAuthUsed"
      - Change [Email_Connector.EmailProvider.AuthType] to: "$EmailAccount/Email_Connector.EmailAccount_OAuthProvider/Email_Connector.OAuthProvider/OAuthType"
      - Change [Email_Connector.EmailProvider_OAuthProvider] to: "$EmailAccount/Email_Connector.EmailAccount_OAuthProvider/Email_Connector.OAuthProvider"**
5. **Create Object
      - Store the result in a new variable called **$NewSelectedConfiguration****
6. **Close Form**
7. **Show Page**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
