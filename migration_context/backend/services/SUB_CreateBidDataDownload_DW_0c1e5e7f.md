# Microflow Analysis: SUB_CreateBidDataDownload_DW

### Requirements (Inputs):
- **$AgregatedInventoryList_DataWipe** (A record of type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$AllBidDownloadList** (A record of type: AuctionUI.AllBidDownload)
- **$BidDataList** (A record of type: AuctionUI.BidData)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
