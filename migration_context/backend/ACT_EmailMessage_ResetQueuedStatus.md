# Microflow Detailed Specification: ACT_EmailMessage_ResetQueuedStatus

### 📥 Inputs (Parameters)
- **$EmailMessage** (Type: Email_Connector.EmailMessage)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$EmailMessage** (and Save to DB)
      - Set **Status** = `Email_Connector.ENUM_EmailStatus.QUEUED`
      - Set **ResendAttempts** = `0`**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.