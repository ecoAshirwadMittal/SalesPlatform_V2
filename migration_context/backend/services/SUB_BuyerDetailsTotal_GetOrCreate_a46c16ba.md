# Microflow Analysis: SUB_BuyerDetailsTotal_GetOrCreate

### Requirements (Inputs):
- **$BuyerSummary** (A record of type: EcoATM_DA.BuyerSummary)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerDetailsTotal****
2. **Decision:** "BuyerDetailsTotals Exist?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
