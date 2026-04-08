# Microflow Analysis: SUB_ListBuyerCodeAggregatedInventory

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (A record of type: AuctionUI.Auction)
- **$QualifiedBuyerCodes** (A record of type: EcoATM_BuyerManagement.QualifiedBuyerCodes)

### Execution Steps:
1. **Search the Database for **AuctionUI.BidRoundSelectionFilter** using filter: { [Round=2] } (Call this list **$BidRoundSelectionFilter**)**
2. **Decision:** "?Include All Inventory"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Wholesale?**
3. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[
AuctionUI.BidData_BuyerCode = $BuyerCode
and (BidAmount > 0)]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction=$Auction]
 } (Call this list **$AggregatedInventoryList_Round1Bids**)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
