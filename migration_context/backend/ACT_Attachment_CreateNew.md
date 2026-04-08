# Microflow Detailed Specification: ACT_Attachment_CreateNew

### 📥 Inputs (Parameters)
- **$EmailMessage** (Type: Email_Connector.EmailMessage)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **Email_Connector.Attachment** (Result: **$NewAttachment**)
      - Set **Attachment_EmailMessage** = `$EmailMessage`**
2. **Maps to Page: **Email_Connector.AddAttachment_NewEdit****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.