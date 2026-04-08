# Microflow Detailed Specification: SUB_GetNewOAuthToken_AuthCodeGrantFlow

### 📥 Inputs (Parameters)
- **$OAuthProvider** (Type: Email_Connector.OAuthProvider)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)
- **$OAuthToken** (Type: Email_Connector.OAuthToken)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Email_Connector.SUB_Decrypt** (Result: **$clientSecret**)**
2. **Call Microflow **Email_Connector.SUB_Decrypt** (Result: **$refreshToken**)**
3. **RestCall**
4. 🔀 **DECISION:** `$latestHttpResponse/StatusCode = 200`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$newOAuthToken`
   ➔ **If [false]:**
      1. **LogMessage**
      2. **Call Microflow **Email_Connector.SUB_CreateLogItem****
      3. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.