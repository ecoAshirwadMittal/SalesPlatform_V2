# Microflow Detailed Specification: ACT_EmailAccount_GetOrRenewTokenJavaAction

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EmailAccount/isOAuthUsed`
   ➔ **If [false]:**
      1. **Call Microflow **Email_Connector.SUB_CreateLogItem****
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **Retrieve related **EmailAccount_OAuthProvider** via Association from **$EmailAccount** (Result: **$OAuthProvider**)**
      2. **Retrieve related **EmailAccount_OAuthToken** via Association from **$EmailAccount** (Result: **$ExistingOAuthToken**)**

**Final Result:** This process concludes by returning a [String] value.