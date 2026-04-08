# Microflow Detailed Specification: SUB_DeleteBuyerAwardSummaryReportForWeek

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **BuyerSummary_Week** via Association from **$Week** (Result: **$BuyerSummaryList**)**
3. **Retrieve related **BuyerAwardSummaryTotals_Week** via Association from **$Week** (Result: **$BuyerSummaryTotalsList**)**
4. **Delete**
5. **Delete**
6. **Call Microflow **Custom_Logging.SUB_Log_Info****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.