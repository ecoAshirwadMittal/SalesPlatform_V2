# Microflow Detailed Specification: ACT_EmailMessage_ComposeNewEmail

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **Email_Connector.EmailMessage** (Result: **$NewEmailMessage**)
      - Set **EmailMessage_EmailAccount** = `$EmailAccount`
      - Set **Content** = `empty`**
2. 🏁 **END:** Return `$NewEmailMessage`

**Final Result:** This process concludes by returning a [Object] value.