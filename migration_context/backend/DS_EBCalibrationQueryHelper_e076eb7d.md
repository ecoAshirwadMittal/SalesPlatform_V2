# Microflow Analysis: DS_EBCalibrationQueryHelper

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BidDataQueryHelperList****
2. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Take the list **$BidDataQueryHelperList**, perform a [Head], and call the result **$EBCalibrationQueryHelper****
4. **Delete**
5. **Create Object
      - Store the result in a new variable called **$NewBidDataQueryHelper****
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
