# Microflow Analysis: SUB_BidRound_GetCurrentBIdRound

### Requirements (Inputs):
- **$CurrentSchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$BidRoundList****
3. **Take the list **$BidRoundList**, perform a [Filter] where: { $BuyerCode }, and call the result **$CurrentBidRoundList****
4. **Take the list **$CurrentBidRoundList**, perform a [Head], and call the result **$NewBidRound****
5. **Run another process: "Custom_Logging.SUB_Log_Info"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
