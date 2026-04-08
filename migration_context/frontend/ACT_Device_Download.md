# Nanoflow: ACT_Device_Download

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
2. **Call JS Action **DataWidgets.Export_To_Excel** (Result: **$ExportSuccess**)**
3. 🔀 **DECISION:** `$ExportSuccess`
   ➔ **If [true]:**
      1. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      2. **Show Message (Error): `We hit an error downloading the pricing data. Please try again.`**
      3. **Call Microflow **Tracing.ACT_Log_Error****
      4. 🏁 **END:** Return empty

## ⚠️ Error Handling

- On error in **Call JS Action **DataWidgets.Export_To_Excel** (Result: **$ExportSuccess**)** → ExclusiveMerge

## 🏁 Returns
`Void`
