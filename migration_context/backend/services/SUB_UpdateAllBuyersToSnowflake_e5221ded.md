# Microflow Analysis: SUB_UpdateAllBuyersToSnowflake

### Execution Steps:
1. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
2. **Decision:** "send buyer to snowflake?"
   - If [true] -> Move to: **Submicroflow**
   - If [false] -> Move to: **Finish**
3. **Run another process: "EcoATM_MDM.SUB_SendAllBuyersToSnowflake"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
