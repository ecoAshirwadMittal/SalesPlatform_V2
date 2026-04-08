# Microflow Detailed Specification: SUB_GetOrCreateDAWeekCurrentWeek

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.Auction**  (Result: **$Auction**)**
2. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
3. **DB Retrieve **EcoATM_DA.DAWeek** Filter: `[EcoATM_DA.DAWeek_Week = $Week]` (Result: **$DAWeek**)**
4. 🔀 **DECISION:** `$DAWeek != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$DAWeek`
   ➔ **If [false]:**
      1. **Create **EcoATM_DA.DAWeek** (Result: **$NewDAWeek**)
      - Set **DAWeek_Week** = `$Week`**
      2. 🏁 **END:** Return `$NewDAWeek`

**Final Result:** This process concludes by returning a [Object] value.