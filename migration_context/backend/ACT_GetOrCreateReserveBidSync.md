# Microflow Detailed Specification: ACT_GetOrCreateReserveBidSync

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_GetTimeOffset** (Result: **$TimeZoneOffset**)**
2. **DB Retrieve **EcoATM_EB.ReserveBidSync**  (Result: **$ExistingReserveBidSync**)**
3. 🔀 **DECISION:** `$ExistingReserveBidSync != empty`
   ➔ **If [true]:**
      1. **Update **$ExistingReserveBidSync** (and Save to DB)
      - Set **LastSyncDateTime** = `subtractHours([%CurrentDateTime%],$TimeZoneOffset)`**
      2. 🏁 **END:** Return `$ExistingReserveBidSync`
   ➔ **If [false]:**
      1. **Create **EcoATM_EB.ReserveBidSync** (Result: **$NewReserveBidSync**)
      - Set **LastSyncDateTime** = `subtractHours([%CurrentDateTime%],$TimeZoneOffset)`**
      2. 🏁 **END:** Return `$NewReserveBidSync`

**Final Result:** This process concludes by returning a [Object] value.