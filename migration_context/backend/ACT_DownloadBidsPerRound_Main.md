# Microflow Detailed Specification: ACT_DownloadBidsPerRound_Main

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **DB Retrieve **AuctionUI.AllBidDownload_ScreenHelper**  (Result: **$AllBidDownload_ScreenHelper**)**
3. **Update **$AllBidDownload_ScreenHelper**
      - Set **R1Caption** = `if $SchedulingAuction/Round=1 then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R1Caption`
      - Set **R2Caption** = `if $SchedulingAuction/Round=2 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R2Caption`
      - Set **UpsellCaption** = `if $SchedulingAuction/Round=3 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/UpsellCaption`**
4. **JavaCallAction**
5. **Update **$AllBidDownload_ScreenHelper**
      - Set **R1Caption** = `if $SchedulingAuction/Round=1 then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R1Caption`
      - Set **R2Caption** = `if $SchedulingAuction/Round=2 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R2Caption`
      - Set **UpsellCaption** = `if $SchedulingAuction/Round=3 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/UpsellCaption`**
6. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
7. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
8. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active' ) ]` (Result: **$BuyerCodeList**)**
9. **AggregateList**
10. **CreateList**
11. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='AllBids_by_BuyerCode']` (Result: **$MxTemplate**)**
12. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
13. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week=$Week and Data_Wipe_Quantity > 0 ]` (Result: **$AgregatedInventoryList_DataWipe**)**
14. **Create Variable **$BuyerCodeCounter** = `0`**
15. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
   │ 1. **Update Variable **$BuyerCodeCounter** = `$BuyerCodeCounter + 1`**
   │ 2. **Create Variable **$FinalBuyerCode** = `false`**
   │ 3. 🔀 **DECISION:** `$BuyerCodeCounter = $BuyerCodeTotalCount`
   │    ➔ **If [false]:**
   │       1. **Call Microflow **AuctionUI.ACT_DownloadBidsPerRound_PerBuyerCode** (Result: **$AllBidDownload_ScreenHelper_2**)**
   │    ➔ **If [true]:**
   │       1. **Update Variable **$FinalBuyerCode** = `true`**
   │       2. **Call Microflow **AuctionUI.ACT_DownloadBidsPerRound_PerBuyerCode** (Result: **$AllBidDownload_ScreenHelper_2**)**
   └─ **End Loop**
16. **LogMessage**
17. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.