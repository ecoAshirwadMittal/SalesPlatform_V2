# Microflow Analysis: ACT_BuyerAwardSummaryReport_ShowPage

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)
- **$CurrentObjectBuyerSummary** (A record of type: EcoATM_DA.BuyerSummary)

### Execution Steps:
1. **Decision:** "Has Current Object?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Run another process: "EcoATM_Reports.SUB_LoadBuyerDetails"**
3. **Show Page**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
