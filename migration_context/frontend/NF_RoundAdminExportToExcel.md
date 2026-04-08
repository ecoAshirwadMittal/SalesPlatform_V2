# Nanoflow: NF_RoundAdminExportToExcel

**Allowed Roles:** AuctionUI.Administrator

## ⚙️ Execution Flow

1. **Call JS Action **DataWidgets.Export_To_Excel** (Result: **$ExportSuccess**)**
2. 🔀 **DECISION:** `$ExportSuccess`
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
