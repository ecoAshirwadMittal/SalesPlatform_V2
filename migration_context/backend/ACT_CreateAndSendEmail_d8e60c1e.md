# Microflow Analysis: ACT_CreateAndSendEmail

### Requirements (Inputs):
- **$ForgotPassword** (A record of type: ForgotPassword.ForgotPassword)
- **$IsSignUp** (A record of type: Object)
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Search the Database for **Email_Connector.EmailAccount** using filter: { Show everything } (Call this list **$EmailSettings**)**
2. **Create Object
      - Store the result in a new variable called **$NewEmail****
3. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailMessage.To] to: "$ForgotPassword/EmailAddress"
      - Change [Email_Connector.EmailMessage.From] to: "$EmailTemplate/FromAddress"
      - Change [Email_Connector.EmailMessage.CC] to: "$EmailTemplate/CC"
      - Change [Email_Connector.EmailMessage.BCC] to: "$EmailTemplate/BCC"
      - Change [Email_Connector.EmailMessage.Subject] to: "if $EmailTemplate/Subject != empty then
	$EmailTemplate/Subject
else ''"
      - Change [Email_Connector.EmailMessage.Content] to: "$EmailTemplate/Content"
      - Change [Email_Connector.EmailMessage.SentDate] to: "[%CurrentDateTime%]"
      - Change [Email_Connector.EmailMessage.PlainBody] to: "$EmailTemplate/PlainBody"
      - Change [Email_Connector.EmailMessage.UseOnlyPlainText] to: "$EmailTemplate/UseOnlyPlainText"
      - **Save:** This change will be saved to the database immediately.**
4. **Search the Database for **Email_Connector.Attachment** using filter: { [Email_Connector.Attachment_EmailTemplate = $EmailTemplate] } (Call this list **$AttachmentsFromTemplate**)**
5. **Run another process: "Email_Connector.SUB_EmailMessage_SetAttachments"
      - Store the result in a new variable called **$AttachmentsForEmail****
6. **Create Variable**
7. **Decision:** "is SelectedEmailAccount is not empty ?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
8. **Search the Database for **Email_Connector.EmailAccount** using filter: { [Username = $SelectedEmailAccount and isOutgoingEmailConfigured] } (Call this list **$EmailAccount**)**
9. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailAccount.Username] to: "$EmailAccount/Username"
      - Change [Email_Connector.EmailAccount.MailAddress] to: "$EmailAccount/MailAddress"
      - Change [Email_Connector.EmailAccount.Password] to: "$EmailAccount/Password"
      - Change [Email_Connector.EmailAccount.Timeout] to: "$EmailAccount/Timeout"
      - Change [Email_Connector.EmailAccount.sanitizeEmailBodyForXSSScript] to: "$EmailAccount/sanitizeEmailBodyForXSSScript"
      - Change [Email_Connector.EmailAccount.isP12Configured] to: "$EmailAccount/isP12Configured"
      - Change [Email_Connector.EmailAccount.isLDAPConfigured] to: "$EmailAccount/isLDAPConfigured"
      - Change [Email_Connector.EmailAccount.isIncomingEmailConfigured] to: "$EmailAccount/isIncomingEmailConfigured"
      - Change [Email_Connector.EmailAccount.isOutgoingEmailConfigured] to: "$EmailAccount/isOutgoingEmailConfigured"
      - Change [Email_Connector.EmailAccount.FromDisplayName] to: "$EmailAccount/FromDisplayName"
      - Change [Email_Connector.EmailAccount.UseSSLCheckServerIdentity] to: "$EmailAccount/UseSSLCheckServerIdentity"
      - Change [Email_Connector.EmailAccount.IsSharedMailbox] to: "$EmailAccount/IsSharedMailbox"
      - Change [Email_Connector.EmailAccount.isOAuthUsed] to: "$EmailAccount/isOAuthUsed"
      - Change [Email_Connector.EmailAccount.isEmailConfigAutoDetect] to: "$EmailAccount/isEmailConfigAutoDetect"
      - Change [Email_Connector.EmailAccount.ComposeEmail] to: "$EmailAccount/ComposeEmail"
      - Change [Email_Connector.OutgoingEmailConfiguration_EmailAccount] to: "$EmailAccount/Email_Connector.OutgoingEmailConfiguration_EmailAccount"
      - Change [Email_Connector.IncomingEmailConfiguration_EmailAccount] to: "$EmailAccount/Email_Connector.IncomingEmailConfiguration_EmailAccount"
      - Change [Email_Connector.Pk12Certificate_EmailAccount] to: "$EmailAccount/Email_Connector.Pk12Certificate_EmailAccount"
      - Change [Email_Connector.EmailAccount_LDAPConfiguration] to: "$EmailAccount/Email_Connector.EmailAccount_LDAPConfiguration"
      - Change [Email_Connector.EmailAccount_OAuthProvider] to: "$EmailAccount/Email_Connector.EmailAccount_OAuthProvider"
      - Change [Email_Connector.EmailAccount_OAuthToken] to: "$EmailAccount/Email_Connector.EmailAccount_OAuthToken"**
10. **Retrieve
      - Store the result in a new variable called **$TokenList****
11. **Decision:** "tokens found"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
12. **Run another process: "ForgotPassword.DecryptForgotPasswordURL"
      - Store the result in a new variable called **$DecryptedForgotPassword****
13. **Java Action Call
      - Store the result in a new variable called **$ReplaceTokens**** ⚠️ *(This step has a safety catch if it fails)*
14. **Run another process: "Encryption.Decrypt"
      - Store the result in a new variable called **$DecryptedPassword****
15. **Java Action Call
      - Store the result in a new variable called **$Succesful**** ⚠️ *(This step has a safety catch if it fails)*
16. **Decision:** "Succesful"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
17. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailMessage.SentDate] to: "[%CurrentDateTime%]"
      - Change [Email_Connector.EmailMessage.Status] to: "Email_Connector.ENUM_EmailStatus.SENT"
      - **Save:** This change will be saved to the database immediately.**
18. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
