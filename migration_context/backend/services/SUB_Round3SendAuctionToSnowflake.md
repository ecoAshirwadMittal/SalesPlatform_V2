# Microflow Detailed Specification: SUB_Round3SendAuctionToSnowflake

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
2. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendAuctionDataToSnowflake`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.SUB_SetAuctionStatus****
      2. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
      3. **Call Microflow **AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.