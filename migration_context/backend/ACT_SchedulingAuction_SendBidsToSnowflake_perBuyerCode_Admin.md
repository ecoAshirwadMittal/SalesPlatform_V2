# Microflow Detailed Specification: ACT_SchedulingAuction_SendBidsToSnowflake_perBuyerCode_Admin

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
2. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
   ➔ **If [true]:**
      1. **Retrieve related **BidRound_BuyerCode** via Association from **$BidRound** (Result: **$BuyerCode**)**
      2. **Retrieve related **BidRound_SchedulingAuction** via Association from **$BidRound** (Result: **$SchedulingAuction**)**
      3. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
      4. **DB Retrieve **AuctionUI.BidRound** Filter: `[AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction=$Auction] [AuctionUI.BidRound_BuyerCode = $BuyerCode]` (Result: **$BidRoundListSorted_All**)**
      5. **List Operation: **Filter** on **$undefined** where `true` (Result: **$BidRoundList_submitted**)**
      6. 🔄 **LOOP:** For each **$IteratorBidRound** in **$BidRoundList_submitted**
         │ 1. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
         │    ➔ **If [true]:**
         │       1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
         │    ➔ **If [false]:**
         └─ **End Loop**
      7. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.