# Microflow Detailed Specification: DS_EmailAccount_CreateDummyAccount

### ⚙️ Execution Flow (Logic Steps)
1. **Create **Email_Connector.IncomingEmailConfiguration** (Result: **$NewIncomingEmailConfiguration**)**
2. **Create **Email_Connector.OutgoingEmailConfiguration** (Result: **$NewOutgoingEmailConfiguration**)**
3. **Create **Email_Connector.EmailAccount** (Result: **$NewEmailAccount**)
      - Set **OutgoingEmailConfiguration_EmailAccount** = `$NewOutgoingEmailConfiguration`
      - Set **IncomingEmailConfiguration_EmailAccount** = `$NewIncomingEmailConfiguration`**
4. 🏁 **END:** Return `$NewEmailAccount`

**Final Result:** This process concludes by returning a [Object] value.