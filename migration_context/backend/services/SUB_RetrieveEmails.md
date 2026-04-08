# Microflow Detailed Specification: SUB_RetrieveEmails

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **IncomingEmailConfiguration_EmailAccount** via Association from **$EmailAccount** (Result: **$IncomingEmailConfiguration**)**
2. **Update **$EmailAccount**
      - Set **IncomingEmailConfiguration_EmailAccount** = `$IncomingEmailConfiguration`**
3. **JavaCallAction**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.