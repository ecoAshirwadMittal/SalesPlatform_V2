# Microflow Analysis: ACT_BidDataDoc_PopulateExcelDoc

### Requirements (Inputs):
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Decision:** "Sched Auct found?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Run another process: "Custom_Logging.SUB_Log_Error"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
