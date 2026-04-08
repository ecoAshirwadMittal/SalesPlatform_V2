# Microflow Analysis: SUB_BidDownloadHelper_CreateList_Depricated_10_28_24

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$Week** (A record of type: EcoATM_Inventory.Week)
- **$AggregatedInventoryList** (A record of type: EcoATM_Inventory.AggregatedInventory)
- **$NewBidDataDoc** (A record of type: AuctionUI.BidDataDoc)

### Execution Steps:
1. **Log Message**
2. **Create List
      - Store the result in a new variable called **$BidDownload_HelperList****
3. **Decision:** "DW buyer code?"
   - If [true] -> Move to: **Sub microflow**
   - If [false] -> Move to: **Finish**
4. **Run another process: "ECOATM_Buyer.SUB_BidDownloadHelper_DWList"**
5. **Log Message**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
