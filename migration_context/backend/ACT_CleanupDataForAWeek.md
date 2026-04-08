# Microflow Detailed Specification: ACT_CleanupDataForAWeek

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Timer** = `'AuctionDataPurge'`**
2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
3. **Retrieve related **AggInventoryTotals_Week** via Association from **$Week** (Result: **$AggreegatedInventoryTotals**)**
4. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$Auction**)**
5. **Retrieve related **BidRound_Week** via Association from **$Week** (Result: **$BidRoundList**)**
6. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
7. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
8. **Create **AuctionUI.BidDataDeleteHelper** (Result: **$NewBidDataDeleteHelper**)
      - Set **StartDate** = `$Week/WeekStartDateTime`
      - Set **EndDate** = `$Week/WeekEndDateTime`**
9. **Call Microflow **AuctionUI.MF_CleanupUsingStoredProcedure****
10. **Delete**
11. **Delete**
12. **Delete**
13. **Delete**
14. **Delete**
15. **Update **$Week** (and Save to DB)
      - Set **AuctionDataPurged** = `true`**
16. **Show Message (Information): `Purging Successful`**
17. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
18. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.