# Nanoflow: ACT_ExportPOtoExcel

**Allowed Roles:** EcoATM_PO.Administrator, EcoATM_PO.SalesOps

## 📥 Inputs

- **$POHelper** (EcoATM_PO.POHelper)

## ⚙️ Execution Flow

1. **Retrieve related **POHelper_PurchaseOrder** via Association from **$POHelper** (Result: **$PurchaseOrder**)**
2. **Create Variable **$FileName** = `'PO_'+$PurchaseOrder/EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week/Year + '-' +$PurchaseOrder/EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week/WeekNumber + '-' +$PurchaseOrder/EcoATM_PO.PurchaseOrder_Week_To/EcoATM_MDM.Week/Year + '-' +$PurchaseOrder/EcoATM_PO.PurchaseOrder_Week_To/EcoATM_MDM.Week/WeekNumber`**
3. **Call JS Action **DataWidgets.Export_To_Excel** (Result: **$ExportSuccess**)**
4. 🏁 **END:** Return empty

## ⚠️ Error Handling

- On error in **Call JS Action **DataWidgets.Export_To_Excel** (Result: **$ExportSuccess**)** → Show Message (Error): `Unable to Export . Please contact System Administrator !`

## 🏁 Returns
`Void`
