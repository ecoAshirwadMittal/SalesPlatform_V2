# Microflow Detailed Specification: SUB_DownloadGradingDetails

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSDownloadGradingDetails'`**
2. **Create Variable **$Description** = `'Download Grading Details'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **EcoATM_MDM.UserHelperGuide** Filter: `[Active] [GuideType='PWS_Grading_Details']` (Result: **$UserHelperGuide**)**
5. 🔀 **DECISION:** `$UserHelperGuide !=empty`
   ➔ **If [true]:**
      1. **DownloadFile**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.