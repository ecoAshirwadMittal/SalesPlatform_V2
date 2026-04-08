# Microflow Detailed Specification: SUB_SendEmailFromJavaAction

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)
- **$EmailMessage** (Type: Email_Connector.EmailMessage)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Email_Connector.Attachment** Filter: `[Email_Connector.Attachment_EmailTemplate = $EmailTemplate]` (Result: **$AttachmentsFromTemplate**)**
2. **Call Microflow **Email_Connector.SUB_EmailMessage_SetAttachments** (Result: **$AttachmentsForEmail**)**
3. **Commit/Save **$AttachmentsForEmail** to Database**
4. 🔀 **DECISION:** `$EmailMessage/QueuedForSending`
   ➔ **If [false]:**
      1. **Update **$EmailMessage** (and Save to DB)
      - Set **EmailMessage_EmailAccount** = `$EmailAccount`
      - Set **From** = `$EmailTemplate/FromAddress`**
      2. **Call Microflow **Email_Connector.SUB_SendEmail** (Result: **$Variable**)**
      3. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Update **$EmailMessage** (and Save to DB)
      - Set **EmailMessage_EmailAccount** = `$EmailAccount`
      - Set **hasAttachments** = `$AttachmentsForEmail!=empty`
      - Set **From** = `$EmailTemplate/FromAddress`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.