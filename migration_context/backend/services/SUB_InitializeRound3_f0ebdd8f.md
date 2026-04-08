# Microflow Analysis: SUB_InitializeRound3

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_ChangeSavedBidsToPreviouslySubmitted"**
2. **Run another process: "AuctionUI.ACT_CalculateTargetPrice"**
3. **Run another process: "AuctionUI.ACT_CalculateHighestBids"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
