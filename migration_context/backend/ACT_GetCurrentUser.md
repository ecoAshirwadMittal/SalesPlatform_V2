# Microflow Detailed Specification: ACT_GetCurrentUser

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(Name = $currentUser/Name)]` (Result: **$EcoATMDirectUser**)**
3. 🏁 **END:** Return `$EcoATMDirectUser`

**Final Result:** This process concludes by returning a [Object] value.