# Nanoflow: ACT_GetBuyerAwardsSummaryReportByWeek

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesOps

## 📥 Inputs

- **$DAHelper** (EcoATM_DA.DAHelper)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
2. **Retrieve related **DAHelper_Week** via Association from **$DAHelper** (Result: **$Week**)**
3. **Call Microflow **EcoATM_Reports.SUB_GetBuyerAwardSummaryrReportForWeek** (Result: **$BuyerSummaryReportList**)**
4. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
