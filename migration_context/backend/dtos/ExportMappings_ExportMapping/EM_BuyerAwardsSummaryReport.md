# Export Mapping: EM_BuyerAwardsSummaryReport

**JSON Structure:** `EcoATM_Reports.JSON_BuyerAwardsSummaryReport`

## Mapping Structure

- **Root** (Array)
  - **JsonObject** (Object) → `EcoATM_Integration.BuyerSummaryReport`
    - **BuyerCode** (Value)
      - Attribute: `EcoATM_Integration.BuyerSummaryReport.BUYER_CODE`
    - **BuyerName** (Value)
      - Attribute: `EcoATM_Integration.BuyerSummaryReport.COMPANY_NAME`
    - **SalesQty** (Value)
      - Attribute: `EcoATM_Integration.BuyerSummaryReport.CURRENTSALESQTY`
    - **Amount** (Value)
      - Attribute: `EcoATM_Integration.BuyerSummaryReport.CURRENTSPEND`
    - **WeeklyBudget** (Value)
      - Attribute: `EcoATM_Integration.BuyerSummaryReport.BUDGET`
    - **PreviousWeekSalesQty** (Value)
      - Attribute: `EcoATM_Integration.BuyerSummaryReport.PREVIOUSSALESQTY`
    - **PreviousWeekAmount** (Value)
      - Attribute: `EcoATM_Integration.BuyerSummaryReport.PREVIOUSSPEND`
    - **PreviousWeekWeeklyBudget** (Value)
      - Attribute: `EcoATM_Integration.BuyerSummaryReport.BUDGET`
    - **CurrentEcoATMGradeDetails** (Value)
      - Attribute: `EcoATM_Integration.BuyerSummaryReport.CURRENTECOATMGRADEDETAILS`
