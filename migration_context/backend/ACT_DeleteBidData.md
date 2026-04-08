# Microflow Detailed Specification: ACT_DeleteBidData

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
2. 🔄 **LOOP:** For each **$IteratorSchedulingAuction** in **$SchedulingAuctionList**
   │ 1. **Retrieve related **BidRound_SchedulingAuction** via Association from **$IteratorSchedulingAuction** (Result: **$BidRoundList**)**
   │ 2. 🔄 **LOOP:** For each **$IteratorBidRound** in **$BidRoundList**
   │    │ 1. **Retrieve related **BidRound_BuyerCode** via Association from **$IteratorBidRound** (Result: **$BuyerCode**)**
   │    │ 2. **Create **AuctionUI.BidDataDeleteHelper** (Result: **$NewBidDataDeleteHelper**)
      - Set **Year** = `$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/Year`
      - Set **WeekNumber** = `$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekNumber`
      - Set **DryRun** = `false`
      - Set **BatchSize** = `1000`
      - Set **BuyerCode** = `$BuyerCode/Code`**
   │    │ 3. **Call Microflow **AuctionUI.ACT_DeleteBidDataByWeekBuyer****
   │    └─ **End Loop**
   └─ **End Loop**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.