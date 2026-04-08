# Microflow Analysis: SUB_InitializeRound2

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
5. **Take the list **$SchedulingAuctionList**, perform a [Find] where: { 2 }, and call the result **$SchedulingAuction****
6. **Run another process: "AuctionUI.ACT_ChangeSavedBidsToPreviouslySubmitted"**
7. **Run another process: "AuctionUI.ACT_CalculateTargetPrice"**
8. **Run another process: "AuctionUI.SUB_AssignRoundTwoBuyers"**
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
