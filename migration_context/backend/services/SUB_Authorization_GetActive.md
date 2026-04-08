# Microflow Detailed Specification: SUB_Authorization_GetActive

### 📥 Inputs (Parameters)
- **$User** (Type: System.User)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MicrosoftGraph.Authorization** Filter: `[MicrosoftGraph.Authorization_Authentication/MicrosoftGraph.Authentication/IsActive] [MicrosoftGraph.Authorization_User = $User] [Successful]` (Result: **$Authorization**)**
2. 🏁 **END:** Return `$Authorization`

**Final Result:** This process concludes by returning a [Object] value.