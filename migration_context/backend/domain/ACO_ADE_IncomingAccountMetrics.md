# Microflow Detailed Specification: ACO_ADE_IncomingAccountMetrics

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Email_Connector.EmailAccount** Filter: `[Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.IncomingEmailConfiguration]` (Result: **$EmailAccountList**)**
2. **JavaCallAction**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.