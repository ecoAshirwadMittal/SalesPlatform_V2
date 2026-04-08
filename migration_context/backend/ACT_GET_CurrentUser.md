# Microflow Detailed Specification: ACT_GET_CurrentUser

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[Name=$currentUser/Name]` (Result: **$EcoATMDirectUser**)**
2. 🏁 **END:** Return `$EcoATMDirectUser`

**Final Result:** This process concludes by returning a [Object] value.