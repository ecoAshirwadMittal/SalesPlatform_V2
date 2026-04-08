# Nanoflow: ACT_RebuildEBCalibrationReport

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesOps

## 📥 Inputs

- **$Week** (EcoATM_MDM.Week)
- **$EBCalibrationQueryHelper** (EcoATM_Reports.EBCalibrationQueryHelper)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `$EBCalibrationQueryHelper/EcoATM_Reports.EBCalibrationQueryHelper_Week/EcoATM_MDM.Week!=empty`
   ➔ **If [true]:**
      1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$ReturnValueName**)**
      2. **Call Microflow **EcoATM_Reports.ACT_GetCalibrationReport** (Result: **$AUCTIONS_EB_CALIBRATION_REPORT_OUTPUT_List**)**
      3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      4. 🏁 **END:** Return `$AUCTIONS_EB_CALIBRATION_REPORT_OUTPUT_List`
   ➔ **If [false]:**
      1. **Show Message (Information): `Please select a week.`**
      2. **Update **$EBCalibrationQueryHelper** (and Save to DB)
      - Set **EBCalibrationQueryHelper_Week** = `$Week`**
      3. 🏁 **END:** Return `empty`

## 🏁 Returns
`List`
