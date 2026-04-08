# Microflow Detailed Specification: ACT_DownloadUserGuide

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_MDM.UserHelperGuide** Filter: `[Active] [GuideType='Auctions']` (Result: **$UserHelperGuide**)**
2. 🔀 **DECISION:** `$UserHelperGuide !=empty`
   ➔ **If [true]:**
      1. **DownloadFile**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.