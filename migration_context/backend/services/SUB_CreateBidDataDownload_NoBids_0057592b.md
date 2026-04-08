# Microflow Analysis: SUB_CreateBidDataDownload_NoBids

### Requirements (Inputs):
- **$AggregatedInventoryList** (A record of type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$AllBidDownloadList** (A record of type: AuctionUI.AllBidDownload)
- **$BidDataList** (A record of type: AuctionUI.BidData)
- **$NewAllBidsDoc** (A record of type: AuctionUI.AllBidsDoc)

### Execution Steps:
1. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
