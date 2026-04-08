# Microflow Analysis: SUB_GenerateRound2QualifiedBuyerCodes

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Run another process: "AuctionUI.ACT_ListRound2BuyerCodesUsingAE"
      - Store the result in a new variable called **$BuyerCodeList_Round2****
5. **Run another process: "AuctionUI.SUB_GenerateQualifiedBuyerCodes"
      - Store the result in a new variable called **$BuyerCodeList****
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"
      - Store the result in a new variable called **$Log****
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
