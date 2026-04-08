# Microflow Detailed Specification: SUB_CalculateRound2TargetPrice_Admin

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **AggInventoryHelper_Week** via Association from **$AggInventoryHelper** (Result: **$Week**)**
3. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$Auction**)**
4. 🔀 **DECISION:** `$Auction!=empty`
   ➔ **If [true]:**
      1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
      2. **List Operation: **Find** on **$undefined** where `1` (Result: **$SchedulingAuction_Round1**)**
      3. 🔀 **DECISION:** `$SchedulingAuction_Round1/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Closed`
         ➔ **If [false]:**
            1. **Show Message (Information): `Round 1 is still active. Round 2 targets cannot be set until Round 1 ends.`**
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **List Operation: **Find** on **$undefined** where `2` (Result: **$SchedulingAuction_Round2**)**
            2. 🔀 **DECISION:** `$SchedulingAuction_Round2!=empty`
               ➔ **If [true]:**
                  1. **Call Microflow **AuctionUI.ACT_CalculateTargetPrice****
                  2. **Update **$AggInventoryHelper****
                  3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Show Message (Information): `Round 2 does not exist for selected week auction!`**
                  2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `Auction does not exist for selected week!`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.