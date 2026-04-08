# Microflow Detailed Specification: ACT_CreateAndSendEmail

### 📥 Inputs (Parameters)
- **$ForgotPassword** (Type: ForgotPassword.ForgotPassword)
- **$IsSignUp** (Type: Variable)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailSettings**)**
2. **Create **Email_Connector.EmailMessage** (Result: **$NewEmail**)**
3. **Update **$NewEmail** (and Save to DB)
      - Set **To** = `$ForgotPassword/EmailAddress`
      - Set **From** = `$EmailTemplate/FromAddress`
      - Set **CC** = `$EmailTemplate/CC`
      - Set **BCC** = `$EmailTemplate/BCC`
      - Set **Subject** = `if $EmailTemplate/Subject != empty then $EmailTemplate/Subject else ''`
      - Set **Content** = `$EmailTemplate/Content`
      - Set **SentDate** = `[%CurrentDateTime%]`
      - Set **PlainBody** = `$EmailTemplate/PlainBody`
      - Set **UseOnlyPlainText** = `$EmailTemplate/UseOnlyPlainText`**
4. **DB Retrieve **Email_Connector.Attachment** Filter: `[Email_Connector.Attachment_EmailTemplate = $EmailTemplate]` (Result: **$AttachmentsFromTemplate**)**
5. **Call Microflow **Email_Connector.SUB_EmailMessage_SetAttachments** (Result: **$AttachmentsForEmail**)**
6. **Create Variable **$SelectedEmailAccount** = `'apikey'`**
7. 🔀 **DECISION:** `$SelectedEmailAccount != empty`
   ➔ **If [true]:**
      1. **DB Retrieve **Email_Connector.EmailAccount** Filter: `[Username = $SelectedEmailAccount and isOutgoingEmailConfigured]` (Result: **$EmailAccount**)**
      2. **Update **$EmailSettings**
      - Set **Username** = `$EmailAccount/Username`
      - Set **MailAddress** = `$EmailAccount/MailAddress`
      - Set **Password** = `$EmailAccount/Password`
      - Set **Timeout** = `$EmailAccount/Timeout`
      - Set **sanitizeEmailBodyForXSSScript** = `$EmailAccount/sanitizeEmailBodyForXSSScript`
      - Set **isP12Configured** = `$EmailAccount/isP12Configured`
      - Set **isLDAPConfigured** = `$EmailAccount/isLDAPConfigured`
      - Set **isIncomingEmailConfigured** = `$EmailAccount/isIncomingEmailConfigured`
      - Set **isOutgoingEmailConfigured** = `$EmailAccount/isOutgoingEmailConfigured`
      - Set **FromDisplayName** = `$EmailAccount/FromDisplayName`
      - Set **UseSSLCheckServerIdentity** = `$EmailAccount/UseSSLCheckServerIdentity`
      - Set **IsSharedMailbox** = `$EmailAccount/IsSharedMailbox`
      - Set **isOAuthUsed** = `$EmailAccount/isOAuthUsed`
      - Set **isEmailConfigAutoDetect** = `$EmailAccount/isEmailConfigAutoDetect`
      - Set **ComposeEmail** = `$EmailAccount/ComposeEmail`
      - Set **OutgoingEmailConfiguration_EmailAccount** = `$EmailAccount/Email_Connector.OutgoingEmailConfiguration_EmailAccount`
      - Set **IncomingEmailConfiguration_EmailAccount** = `$EmailAccount/Email_Connector.IncomingEmailConfiguration_EmailAccount`
      - Set **Pk12Certificate_EmailAccount** = `$EmailAccount/Email_Connector.Pk12Certificate_EmailAccount`
      - Set **EmailAccount_LDAPConfiguration** = `$EmailAccount/Email_Connector.EmailAccount_LDAPConfiguration`
      - Set **EmailAccount_OAuthProvider** = `$EmailAccount/Email_Connector.EmailAccount_OAuthProvider`
      - Set **EmailAccount_OAuthToken** = `$EmailAccount/Email_Connector.EmailAccount_OAuthToken`**
      3. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
      4. 🔀 **DECISION:** `$TokenList != empty`
         ➔ **If [true]:**
            1. **Call Microflow **ForgotPassword.DecryptForgotPasswordURL** (Result: **$DecryptedForgotPassword**)**
            2. **JavaCallAction**
            3. **Call Microflow **Encryption.Decrypt** (Result: **$DecryptedPassword**)**
            4. **JavaCallAction**
            5. 🔀 **DECISION:** `$Succesful`
               ➔ **If [true]:**
                  1. **Update **$NewEmail** (and Save to DB)
      - Set **SentDate** = `[%CurrentDateTime%]`
      - Set **Status** = `Email_Connector.ENUM_EmailStatus.SENT`**
                  2. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Update **$NewEmail** (and Save to DB)
      - Set **LastSendError** = `'Failed to send an email to: ' + $NewEmail/To +' an error occured in the SendEmail java action ' + $latestError/Message + ' ' + $latestError/Stacktrace`
      - Set **LastSendAttemptAt** = `[%CurrentDateTime%]`
      - Set **Status** = `Email_Connector.ENUM_EmailStatus.ERROR`**
                  2. **Call Microflow **Email_Connector.SUB_CreateLogItem** (Result: **$Variable**)**
                  3. 🏁 **END:** Return `false`
         ➔ **If [false]:**
            1. **Call Microflow **Encryption.Decrypt** (Result: **$DecryptedPassword**)**
            2. **JavaCallAction**
            3. 🔀 **DECISION:** `$Succesful`
               ➔ **If [true]:**
                  1. **Update **$NewEmail** (and Save to DB)
      - Set **SentDate** = `[%CurrentDateTime%]`
      - Set **Status** = `Email_Connector.ENUM_EmailStatus.SENT`**
                  2. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Update **$NewEmail** (and Save to DB)
      - Set **LastSendError** = `'Failed to send an email to: ' + $NewEmail/To +' an error occured in the SendEmail java action ' + $latestError/Message + ' ' + $latestError/Stacktrace`
      - Set **LastSendAttemptAt** = `[%CurrentDateTime%]`
      - Set **Status** = `Email_Connector.ENUM_EmailStatus.ERROR`**
                  2. **Call Microflow **Email_Connector.SUB_CreateLogItem** (Result: **$Variable**)**
                  3. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. **DB Retrieve **Email_Connector.EmailAccount** Filter: `[Username=$EmailTemplate/FromAddress and isOutgoingEmailConfigured]` (Result: **$RetrieveSMTPOfTemplate**)**
      2. 🔀 **DECISION:** `$RetrieveSMTPOfTemplate != empty`
         ➔ **If [true]:**
            1. **Create **ForgotPassword.EmailTemplateSMTP** (Result: **$SetSMTPToTemplate**)
      - Set **EmailTemplateSMTP_EmailAccount** = `$RetrieveSMTPOfTemplate`
      - Set **EmailTemplateSMTP_EmailTemplate** = `$EmailTemplate`**
               *(Merging with existing path logic)*
         ➔ **If [false]:**
            1. **LogMessage**
            2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.