# Microflow Detailed Specification: SUB_SendEmail

### 📥 Inputs (Parameters)
- **$EmailMessage** (Type: Email_Connector.EmailMessage)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Email_Connector.VAL_EmailRecipients** (Result: **$Valid**)**
2. 🔀 **DECISION:** `$Valid`
   ➔ **If [true]:**
      1. **Retrieve related **EmailMessage_EmailAccount** via Association from **$EmailMessage** (Result: **$EmailAccount**)**
      2. **Retrieve related **Attachment_EmailMessage** via Association from **$EmailMessage** (Result: **$AttachmentList**)**
      3. **Update **$EmailMessage**
      - Set **hasAttachments** = `$AttachmentList != empty`**
      4. **JavaCallAction**
      5. **Update **$EmailMessage** (and Save to DB)
      - Set **SentDate** = `[%CurrentDateTime%]`
      - Set **Status** = `Email_Connector.ENUM_EmailStatus.SENT`
      - Set **EmailMessage_EmailAccount** = `$EmailAccount`**
      6. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.