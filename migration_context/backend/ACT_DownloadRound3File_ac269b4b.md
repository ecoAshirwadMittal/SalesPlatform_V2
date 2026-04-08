# Microflow Analysis: ACT_DownloadRound3File

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
2. **Take the list **$SchedulingAuctionList**, perform a [FindByExpression] where: { $currentObject/Round=3 }, and call the result **$Round3SchedulingAuction****
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "EcoATM_BuyerManagement.SUB_BidRound_GetCurrentBIdRound"
      - Store the result in a new variable called **$BidRound****
5. **Decision:** "Bid Round exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Run another process: "Custom_Logging.SUB_Log_Error"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
