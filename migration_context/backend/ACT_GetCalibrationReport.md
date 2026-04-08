# Microflow Detailed Specification: ACT_GetCalibrationReport

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)
- **$EBCalibrationQueryHelper** (Type: EcoATM_Reports.EBCalibrationQueryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SendCohortMappingToSnowflake'`**
2. **Create Variable **$Description** = `'Run EB Calibration Report'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. 🔀 **DECISION:** `$Week != empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **ExecuteDatabaseQuery**
      2. **ExecuteDatabaseQuery**
      3. **Retrieve related **AUCTIONS_EB_CALIBRATION_REPORT_OUTPUT_EBCalibrationQueryHelper** via Association from **$EBCalibrationQueryHelper** (Result: **$AUCTIONS_EB_CALIBRATION_REPORT_OUTPUTList_existing**)**
      4. **Delete**
      5. 🔄 **LOOP:** For each **$IteratorAUCTIONS_EB_CALIBRATION_REPORT_OUTPUT** in **$AUCTIONS_EB_CALIBRATION_REPORT_OUTPUT_List**
         │ 1. **Update **$IteratorAUCTIONS_EB_CALIBRATION_REPORT_OUTPUT**
      - Set **AUCTIONS_EB_CALIBRATION_REPORT_OUTPUT_EBCalibrationQueryHelper** = `$EBCalibrationQueryHelper`**
         └─ **End Loop**
      6. **Commit/Save **$AUCTIONS_EB_CALIBRATION_REPORT_OUTPUT_List** to Database**
      7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      8. 🏁 **END:** Return `$AUCTIONS_EB_CALIBRATION_REPORT_OUTPUT_List`

**Final Result:** This process concludes by returning a [List] value.