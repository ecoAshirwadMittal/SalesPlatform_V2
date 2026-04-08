# Microflow Analysis: ACT_LogSendToSharepoint

### Requirements (Inputs):
- **$RetryCount** (A record of type: Object)
- **$UserClickBidSubmitLog** (A record of type: AuctionUI.BidSubmitLog)

### Execution Steps:
1. **Decision:** "$UserClickBidSubmitLog Exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Create Object
      - Store the result in a new variable called **$NewBidSubmitLog****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
