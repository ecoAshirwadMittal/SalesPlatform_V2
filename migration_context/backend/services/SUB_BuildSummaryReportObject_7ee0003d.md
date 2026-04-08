# Microflow Analysis: SUB_BuildSummaryReportObject

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$BidDataList** (A record of type: EcoATM_Buyer.BidData)

### Execution Steps:
1. **Search the Database for **AuctionUI.Week** using filter: { [AuctionUI.Auction_Week/AuctionUI.Auction/AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/AuctionUI.BidRound_SchedulingAuction = $BidRound or
AuctionUI.AggregatedInventory_Week/AuctionUI.AggregatedInventory/EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound = $BidRound] } (Call this list **$Week**)**
2. **Retrieve
      - Store the result in a new variable called **$BuyerBidSummaryReportList****
3. **Take the list **$BuyerBidSummaryReportList**, perform a [FindByExpression] where: { $currentObject/BuyerCode = $BidRound/AuctionUI.BidRound_BuyerCode/AuctionUI.BuyerCode/Code }, and call the result **$ExistingMatch****
4. **Decision:** "Is ExistingMatch empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Create Object
      - Store the result in a new variable called **$NewBuyerBidSummaryReport****
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
