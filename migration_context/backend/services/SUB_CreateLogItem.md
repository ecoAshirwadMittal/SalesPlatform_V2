# Microflow Detailed Specification: SUB_CreateLogItem

### 📥 Inputs (Parameters)
- **$TriggeredInMF** (Type: Variable)
- **$LogType** (Type: Variable)
- **$Message** (Type: Variable)
- **$EmailMessage** (Type: Email_Connector.EmailMessage)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)
- **$isUnread** (Type: Variable)
- **$Error** (Type: System.Error)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Create **Email_Connector.EmailConnectorLog** (Result: **$NewEmailErrorLog**)**
3. **Update **$NewEmailErrorLog** (and Save to DB)
      - Set **Logtype** = `$LogType`
      - Set **ErrorMessage** = `$Error/Message`
      - Set **TriggeredInMF** = `$TriggeredInMF`
      - Set **StackTrace** = `$Error/Stacktrace`
      - Set **Message** = `$Message`
      - Set **EmailConnectorLog_EmailMessage** = `$EmailMessage`
      - Set **EmailConnectorLog_EmailAccount** = `$EmailAccount`
      - Set **IsUnread** = `$isUnread`**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.