# Microflow Analysis: ACT_PurgeBidData

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$CurrentAuction****
3. **Run another process: "EcoATM_Direct_Sharepoint.SUB_BidData_BatchDeleteAllBidsByAuction"**
4. **Search the Database for **AuctionUI.BidRound** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidData/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $CurrentAuction] } (Call this list **$BidRoundList**)**
5. **Delete**
6. **Retrieve
      - Store the result in a new variable called **$AggreegatedInventoryTotals****
7. **Delete**
8. **Run another process: "AuctionUI.ACT_DeleteAggregatedInventoryForWeek"**
9. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $CurrentAuction] } (Call this list **$BidDataList**)**
10. **Delete**
11. **Show Message**
12. **Delete**
13. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
