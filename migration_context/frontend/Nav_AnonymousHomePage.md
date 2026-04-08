# Microflow Detailed Specification: Nav_AnonymousHomePage

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **DeepLink.DeepLinkHome** (Result: **$DeeplinkExecuted**)**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. 🔀 **DECISION:** `$DeeplinkExecuted`
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_UserManagement.Login_New****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.