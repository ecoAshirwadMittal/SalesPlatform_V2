# Microflow Detailed Specification: ACT_SetSharepointDownloadStart

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.AllBidDownload_ScreenHelper**  (Result: **$AllBidDownload_ScreenHelper**)**
2. **Update **$AllBidDownload_ScreenHelper**
      - Set **R1Caption** = `if $SchedulingAuction/Round=1 then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R1Caption`
      - Set **R2Caption** = `if $SchedulingAuction/Round=2 and $SchedulingAuction/HasRound then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R2Caption`
      - Set **UpsellCaption** = `if $SchedulingAuction/Round=3 and $SchedulingAuction/HasRound then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/UpsellCaption`**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.