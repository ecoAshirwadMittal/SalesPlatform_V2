# Microflow Detailed Specification: ACT_ShowScheduleAuctionConfirmation

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.VAL_Schedule_Auction** (Result: **$IsValid**)**
2. 🔀 **DECISION:** `$IsValid = empty`
   ➔ **If [false]:**
      1. **Show Message (Information): `{1}`**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Maps to Page: **AuctionUI.ScheduleAuction_Confirm****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.