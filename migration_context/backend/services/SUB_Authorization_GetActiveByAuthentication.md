# Microflow Detailed Specification: SUB_Authorization_GetActiveByAuthentication

### 📥 Inputs (Parameters)
- **$Authentication** (Type: MicrosoftGraph.Authentication)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MicrosoftGraph.Authorization** Filter: `[MicrosoftGraph.Authorization_Authentication = $Authentication] [MicrosoftGraph.Authorization_User = $currentUser] [Successful]` (Result: **$Authorization**)**
2. 🏁 **END:** Return `$Authorization`

**Final Result:** This process concludes by returning a [Object] value.