# Microflow Detailed Specification: ACT_ShowAccountSettingsPage

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **IncomingEmailConfiguration_EmailAccount** via Association from **$EmailAccount** (Result: **$IncomingEmailConfiguration**)**
2. **Retrieve related **OutgoingEmailConfiguration_EmailAccount** via Association from **$EmailAccount** (Result: **$OutgoingEmailConfiguration**)**

**Final Result:** This process concludes by returning a [Void] value.