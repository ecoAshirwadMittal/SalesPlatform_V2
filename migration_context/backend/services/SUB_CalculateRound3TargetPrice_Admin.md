# Microflow Detailed Specification: SUB_CalculateRound3TargetPrice_Admin

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **AggInventoryHelper_Week** via Association from **$AggInventoryHelper** (Result: **$Week**)**
3. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$Auction**)**
4. 🔀 **DECISION:** `$Auction!=empty`
   ➔ **If [false]:**
      1. **Show Message (Information): `Auction does not exist for selected week!`**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
      2. **List Operation: **Find** on **$undefined** where `3` (Result: **$SchedulingAuction_Round3**)**
      3. 🔀 **DECISION:** `$SchedulingAuction_Round3!=empty`
         ➔ **If [false]:**
            1. **Show Message (Information): `Round 3 does not exist for selected week auction!`**
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **AuctionUI.SUB_InitializeRound3****
            2. **Call Microflow **AuctionUI.ACT_CalculateTargetPrice****
            3. **Update **$AggInventoryHelper****
            4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
            5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.