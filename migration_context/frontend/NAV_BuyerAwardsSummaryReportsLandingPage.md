# Nanoflow: NAV_BuyerAwardsSummaryReportsLandingPage

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesOps

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **Call Microflow **EcoATM_DA.SUB_DAHelper_GerOrCreate** (Result: **$DAHelper**)**
4. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$CurrentWeek**)**
5. 🔀 **DECISION:** `$CurrentWeek!=empty`
   ➔ **If [true]:**
      1. **Update **$DAHelper**
      - Set **DAHelper_Week** = `$CurrentWeek`**
      2. **Call Microflow **EcoATM_Reports.SUB_GetBuyerAwardSummaryrReportForWeek****
      3. **Open Page: **EcoATM_Reports.PG_BuyerAwardsSummaryReport****
      4. **Call Microflow **Custom_Logging.SUB_Log_Info****
      5. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `No Report Exists for this Week`**
      2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
