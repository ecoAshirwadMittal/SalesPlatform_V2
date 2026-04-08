# Microflow Detailed Specification: ACT_PurgeBidData

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$CurrentAuction**)**
3. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_BidData_BatchDeleteAllBidsByAuction****
4. **DB Retrieve **AuctionUI.BidRound** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidData/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $CurrentAuction]` (Result: **$BidRoundList**)**
5. **Delete**
6. **Retrieve related **AggInventoryTotals_Week** via Association from **$CurrentAuction** (Result: **$AggreegatedInventoryTotals**)**
7. **Delete**
8. **Call Microflow **AuctionUI.ACT_DeleteAggregatedInventoryForWeek****
9. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $CurrentAuction]` (Result: **$BidDataList**)**
10. **Delete**
11. **Show Message (Information): `Purge Complete. All objects of: -BuyerBidSummaryReport -BidData -BidRound -BuyerBidDetailReport For the latest Auction: {1} Have been deleted.`**
12. **Delete**
13. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
14. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.