# Microflow Detailed Specification: ACT_EmailMessage_ComposeReply

### 📥 Inputs (Parameters)
- **$EmailMessage** (Type: Email_Connector.EmailMessage)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **Email_Connector.EmailMessage** (Result: **$NewEmailMessageToSend**)
      - Set **To** = `$EmailMessage/From`
      - Set **Subject** = `'Re: '+ $EmailMessage/Subject`
      - Set **From** = `$EmailAccount/MailAddress`
      - Set **RetrieveDate** = `$EmailMessage/RetrieveDate`
      - Set **Content** = `' '+ ' '+ '------------------------------------------------------------------------------------------------'+' '+ 'On '+toString($EmailMessage/RetrieveDate)+' , '+$EmailMessage/From+' wrote:'+' '+ ' '+ $EmailMessage/Content`
      - Set **UseOnlyPlainText** = `$EmailMessage/UseOnlyPlainText`
      - Set **hasAttachments** = `$EmailMessage/hasAttachments`
      - Set **ResendAttempts** = `$EmailMessage/ResendAttempts`
      - Set **Status** = `$EmailMessage/Status`
      - Set **EmailMessage_EmailAccount** = `$EmailAccount`**
2. 🔀 **DECISION:** `$EmailMessage/ReplyTo!= empty`
   ➔ **If [true]:**
      1. **Update **$NewEmailMessageToSend**
      - Set **To** = `$EmailMessage/ReplyTo`**
      2. **Maps to Page: **Email_Connector.ComposeEmail_NewEdit****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **Email_Connector.ComposeEmail_NewEdit****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.