# Microflow Analysis: ACT_CheckForSelectedBuyerCode

### Execution Steps:
1. **Run another process: "EcoATM_PWS.DS_BuyerCodeBySession"
      - Store the result in a new variable called **$BuyerCode****
2. **Decision:** "Check condition"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
