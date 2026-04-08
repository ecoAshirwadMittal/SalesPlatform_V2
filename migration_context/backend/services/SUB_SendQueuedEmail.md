# Microflow Detailed Specification: SUB_SendQueuedEmail

### 📥 Inputs (Parameters)
- **$EmailMessage** (Type: Email_Connector.EmailMessage)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **JavaCallAction**
3. **Update **$EmailMessage**
      - Set **SentDate** = `[%CurrentDateTime%]`
      - Set **LastSendError** = `empty`
      - Set **LastSendAttemptAt** = `[%CurrentDateTime%]`
      - Set **Status** = `Email_Connector.ENUM_EmailStatus.SENT`
      - Set **QueuedForSending** = `false`
      - Set **From** = `$EmailAccount/MailAddress`**
4. **LogMessage**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.