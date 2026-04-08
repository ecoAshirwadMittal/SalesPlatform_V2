# Microflow Detailed Specification: ACT_Auction_SendAllBidsToSnowflake_Admin

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
2. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
   ➔ **If [true]:**
      1. **DB Retrieve **AuctionUI.BidRound** Filter: `[AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction=$Auction]` (Result: **$BidRoundListSorted_All**)**
      2. **List Operation: **Filter** on **$undefined** where `true` (Result: **$BidRoundList_submitted**)**
      3. 🔄 **LOOP:** For each **$IteratorBidRound** in **$BidRoundList_submitted**
         │ 1. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
         │    ➔ **If [true]:**
         │       1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
         │    ➔ **If [false]:**
         └─ **End Loop**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.