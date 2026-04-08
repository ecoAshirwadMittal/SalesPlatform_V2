# Microflow Detailed Specification: DS_FolderList

### 📥 Inputs (Parameters)
- **$IncomingEmailConfiguration** (Type: Email_Connector.IncomingEmailConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **IncomingEmailConfiguration_EmailAccount** via Association from **$IncomingEmailConfiguration** (Result: **$EmailAccount**)**
2. **JavaCallAction**
3. 🏁 **END:** Return `$FolderList`

**Final Result:** This process concludes by returning a [List] value.