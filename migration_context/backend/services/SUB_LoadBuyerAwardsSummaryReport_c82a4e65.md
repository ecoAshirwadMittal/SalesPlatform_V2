# Microflow Analysis: SUB_LoadBuyerAwardsSummaryReport

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create Variable**
3. **Create Variable**
4. **Create Variable**
5. **Create Variable**
6. **Run another process: "EcoATM_Reports.SUB_DeleteBuyerAwardSummaryReportForWeek"**
7. **Execute Database Query
      - Store the result in a new variable called **$BuyerSummaryReport**** ⚠️ *(This step has a safety catch if it fails)*
8. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
9. **Import Xml** ⚠️ *(This step has a safety catch if it fails)*
10. **Search the Database for **EcoATM_DA.BuyerSummary** using filter: { [
  (
    EcoATM_DA.BuyerSummary_Week = $DAHelper/EcoATM_DA.DAHelper_Week
    and BuyerCode = 'Total'
  )
]
 } (Call this list **$BuyerSummaryTotalsRow**)**
11. **Run another process: "EcoATM_Reports.SUB_BuyerAwardSummaryTotals_GetOrCreate"
      - Store the result in a new variable called **$BuyerSummaryTotals****
12. **Update the **$undefined** (Object):
      - Change [EcoATM_Reports.BuyerAwardSummaryTotals.SalesQty] to: "$BuyerSummaryTotalsRow/SalesQty
"
      - Change [EcoATM_Reports.BuyerAwardSummaryTotals.Amount] to: "$BuyerSummaryTotalsRow/Amount
"
      - Change [EcoATM_Reports.BuyerAwardSummaryTotals.WeeklyBudget] to: "$BuyerSummaryTotalsRow/WeeklyBudget
"
      - Change [EcoATM_Reports.BuyerAwardSummaryTotals.PreviousWeekSalesQty] to: "$BuyerSummaryTotalsRow/PreviousWeekSalesQty
"
      - Change [EcoATM_Reports.BuyerAwardSummaryTotals.PreviousWeekAmount] to: "$BuyerSummaryTotalsRow/PreviousWeekAmount
"
      - Change [EcoATM_Reports.BuyerAwardSummaryTotals.PreviousWeekWeeklyBudget] to: "$BuyerSummaryTotalsRow/PreviousWeekWeeklyBudget
"
      - **Save:** This change will be saved to the database immediately.**
13. **Delete**
14. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
