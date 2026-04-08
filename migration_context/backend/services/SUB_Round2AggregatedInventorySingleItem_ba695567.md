# Microflow Analysis: SUB_Round2AggregatedInventorySingleItem

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$BuyerCodeList** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **AuctionUI.BidRoundSelectionFilter** using filter: { [Round=2]
 } (Call this list **$BidRoundSelectionFilter**)**
3. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[
AuctionUI.BidData_BuyerCode = $BuyerCode
and (BidAmount > 0)]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction=$Auction]
 } (Call this list **$AggregatedInventoryList_Round1Bids**)**
4. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BuyerCode = $BuyerCode]
[AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round=1]/AuctionUI.SchedulingAuction_Auction=$Auction]
[BidAmount>0]
 } (Call this list **$BidDataList_Round1**)**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
