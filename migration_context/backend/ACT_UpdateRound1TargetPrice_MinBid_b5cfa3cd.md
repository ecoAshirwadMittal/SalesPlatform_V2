# Microflow Analysis: ACT_UpdateRound1TargetPrice_MinBid

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
2. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [
AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week = $Auction
and
AvgTargetPrice < $BuyerCodeSubmitConfig/MinimumAllowedBid
and
TotalQuantity > 0] } (Call this list **$AggregatedInventoryList_NonDW_TargetPriceBelowMinBidConfig**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Permanently save **$undefined** to the database.**
5. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [
AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week = $Auction
and
DWAvgTargetPrice < $BuyerCodeSubmitConfig/MinimumAllowedBid
and
DWTotalQuantity > 0] } (Call this list **$AggregatedInventoryList_DW_TargetPriceBelowMinBidConfig**)**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Permanently save **$undefined** to the database.**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
