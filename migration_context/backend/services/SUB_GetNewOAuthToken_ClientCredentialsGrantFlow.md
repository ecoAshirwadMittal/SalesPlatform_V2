# Microflow Detailed Specification: SUB_GetNewOAuthToken_ClientCredentialsGrantFlow

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)
- **$OAuthProvider** (Type: Email_Connector.OAuthProvider)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Email_Connector.SUB_Decrypt** (Result: **$clientSecret**)**
2. **RestCall**
3. 🔀 **DECISION:** `$latestHttpResponse/StatusCode = 200`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$newOAuthToken`
   ➔ **If [false]:**
      1. **LogMessage**
      2. **Call Microflow **Email_Connector.SUB_CreateLogItem****
      3. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.