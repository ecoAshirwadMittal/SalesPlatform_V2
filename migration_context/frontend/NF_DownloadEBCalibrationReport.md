# Nanoflow: NF_DownloadEBCalibrationReport

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesOps

## 📥 Inputs

- **$YearWeekFileName** (String)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$ReturnValueName**)**
2. **Call JS Action **DataWidgets.Export_To_Excel** (Result: **$ExportSuccess**)**
3. 🔀 **DECISION:** `$ExportSuccess = true`
   ➔ **If [true]:**
      1. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `Error Exporting EB Calibration Report - {1}`**
      2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
