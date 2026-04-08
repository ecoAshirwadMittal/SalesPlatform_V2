# Nanoflow: NF_DownloadCohortMapping

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesOps

## ⚙️ Execution Flow

1. **Call JS Action **DataWidgets.Export_To_Excel** (Result: **$ExportSuccess**)**
2. 🔀 **DECISION:** `$ExportSuccess = true`
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `Error Exporting Data - {1}`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
