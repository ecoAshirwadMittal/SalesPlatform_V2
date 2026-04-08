# Nanoflow: ACT_ShowEBCalibrationReport

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesOps

## ⚙️ Execution Flow

1. **Call Microflow **EcoATM_Reports.DS_EBCalibrationQueryHelper** (Result: **$EBCalibrationQueryHelper**)**
2. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$Week**)**
3. **Update **$EBCalibrationQueryHelper** (and Save to DB)
      - Set **DisplayReport** = `true`
      - Set **EBCalibrationQueryHelper_Week** = `$Week`**
4. **Open Page: **EcoATM_Reports.PG_EB_Calibration_Report****
5. **Call Nanoflow **EcoATM_Reports.ACT_RebuildEBCalibrationReport** (Result: **$AUCTIONS_EB_CALIBRATION_REPORT_OUTPUTList**)**
6. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
