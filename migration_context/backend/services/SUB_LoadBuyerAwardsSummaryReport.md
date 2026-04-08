# Microflow Detailed Specification: SUB_LoadBuyerAwardsSummaryReport

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Create Variable **$currentWeek** = `$Week/WeekNumber`**
3. **Create Variable **$currentYear** = `$Week/Year`**
4. **Create Variable **$previousYear** = `if ($Week/WeekNumber = 1) then ($Week/Year - 1) else ($Week/Year)`**
5. **Create Variable **$previousWeek** = `if ($Week/WeekNumber = 1) then 52 else ($Week/WeekNumber - 1)`**
6. **Call Microflow **EcoATM_Reports.SUB_DeleteBuyerAwardSummaryReportForWeek****
7. **ExecuteDatabaseQuery**
8. **ExportXml**
9. **ImportXml**
10. **DB Retrieve **EcoATM_DA.BuyerSummary** Filter: `[ ( EcoATM_DA.BuyerSummary_Week = $DAHelper/EcoATM_DA.DAHelper_Week and BuyerCode = 'Total' ) ]` (Result: **$BuyerSummaryTotalsRow**)**
11. **Call Microflow **EcoATM_Reports.SUB_BuyerAwardSummaryTotals_GetOrCreate** (Result: **$BuyerSummaryTotals**)**
12. **Update **$BuyerSummaryTotals** (and Save to DB)
      - Set **SalesQty** = `$BuyerSummaryTotalsRow/SalesQty`
      - Set **Amount** = `$BuyerSummaryTotalsRow/Amount`
      - Set **WeeklyBudget** = `$BuyerSummaryTotalsRow/WeeklyBudget`
      - Set **PreviousWeekSalesQty** = `$BuyerSummaryTotalsRow/PreviousWeekSalesQty`
      - Set **PreviousWeekAmount** = `$BuyerSummaryTotalsRow/PreviousWeekAmount`
      - Set **PreviousWeekWeeklyBudget** = `$BuyerSummaryTotalsRow/PreviousWeekWeeklyBudget`**
13. **Delete**
14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
15. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [String] value.