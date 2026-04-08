# Microflow Detailed Specification: SUB_EmailAccount_SubscribeForEmailNotification

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$EmailAccount/Email_Connector.IncomingEmailConfiguration_EmailAccount/Email_Connector.IncomingEmailConfiguration/NotifyOnNewEmails`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.