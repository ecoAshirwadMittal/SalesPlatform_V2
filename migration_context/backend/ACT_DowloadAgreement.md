# Microflow Detailed Specification: ACT_DowloadAgreement

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `'Download User Agreenment: user-' + $EcoATMDirectUser/Name`**
2. **Create Variable **$Timername** = `'DownloadUserAgreenment'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **EcoATM_MDM.UserHelperGuide** Filter: `[ ( GuideType = 'Sales_Platform_Terms' and Active = true() ) ]` (Result: **$UserHelperGuide**)**
5. 🔀 **DECISION:** `$UserHelperGuide != empty`
   ➔ **If [true]:**
      1. **DownloadFile**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `User Agreement does not exist.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.