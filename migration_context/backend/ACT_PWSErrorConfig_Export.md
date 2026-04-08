# Microflow Detailed Specification: ACT_PWSErrorConfig_Export

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSIntegration.PWSResponseConfig**  (Result: **$PWSResponseConfigList**)**
2. 🔀 **DECISION:** `$PWSResponseConfigList!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. **Create **EcoATM_PWS.ManageFileDocument** (Result: **$MessageFileDocument**)
      - Set **Name** = `'ErrorMessages.json'`
      - Set **DeleteAfterDownload** = `true`**
      3. **ExportXml**
      4. **DownloadFile**
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.