# Microflow Analysis: ACT_UpdateBidSubmitLog

### Requirements (Inputs):
- **$Message** (A record of type: Object)
- **$Status** (A record of type: Object)
- **$BidSubmitLog** (A record of type: AuctionUI.BidSubmitLog)

### Execution Steps:
1. **Decision:** "$UserClickBidSubmitLog Exists?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
