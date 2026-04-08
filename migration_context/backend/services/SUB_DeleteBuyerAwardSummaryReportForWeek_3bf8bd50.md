# Microflow Analysis: SUB_DeleteBuyerAwardSummaryReportForWeek

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$BuyerSummaryList****
3. **Retrieve
      - Store the result in a new variable called **$BuyerSummaryTotalsList****
4. **Delete**
5. **Delete**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
