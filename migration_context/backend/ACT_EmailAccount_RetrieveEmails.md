# Microflow Detailed Specification: ACT_EmailAccount_RetrieveEmails

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **IncrementCounterMeter**
2. 🔀 **DECISION:** `$EmailAccount/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.IncomingEmailConfiguration/IncomingProtocol != empty`
   ➔ **If [true]:**
      1. **Call Microflow **Email_Connector.SUB_RetrieveEmails** (Result: **$mxEmailMessages**)**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `Incoming email configuration not found for email '{1}'`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.