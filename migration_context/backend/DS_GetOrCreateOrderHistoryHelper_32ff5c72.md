# Microflow Analysis: DS_GetOrCreateOrderHistoryHelper

### Execution Steps:
1. **Run another process: "EcoATM_PWS.DS_BuyerCodeBySession"
      - Store the result in a new variable called **$BuyerCode****
2. **Retrieve
      - Store the result in a new variable called **$OrderHistoryHelperList****
3. **Decision:** "Order History Helper Unavailable?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create Object
      - Store the result in a new variable called **$NewOrderHistoryHelper****
5. **Run another process: "EcoATM_PWS.SUB_CalculateOrderHistoryTabTotals"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
