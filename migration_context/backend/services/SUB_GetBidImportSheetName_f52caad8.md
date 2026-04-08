# Microflow Analysis: SUB_GetBidImportSheetName

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Decision:** "Round 1?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
