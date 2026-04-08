# Microflow Detailed Specification: SUB_DeviceBuyer_CalculateLastAwardedBid

### 📥 Inputs (Parameters)
- **$NPE_ClearingBid** (Type: EcoATM_DA.NPE_ClearingBid)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Create Variable **$CurrentLowestPrice** = `empty`**
3. **DB Retrieve **EcoATM_EB.ReserveBid** Filter: `[ ProductId = $DeviceAllocation/ProductID and Grade = $DeviceAllocation/Grade ]` (Result: **$ReserveBidList**)**
4. 🔀 **DECISION:** `$ReserveBidList != empty`
   ➔ **If [true]:**
      1. 🔄 **LOOP:** For each **$IteratorReserveBid** in **$ReserveBidList**
         │ 1. 🔀 **DECISION:** `$CurrentLowestPrice != empty`
         │    ➔ **If [true]:**
         │       1. 🔀 **DECISION:** `$IteratorReserveBid/Bid < $CurrentLowestPrice`
         │          ➔ **If [true]:**
         │             1. **Update **$NPE_ClearingBid**
      - Set **LastAwardedMinimumPrice** = `$IteratorReserveBid/Bid`**
         │             2. **Update Variable **$CurrentLowestPrice** = `$IteratorReserveBid/Bid`**
         │          ➔ **If [false]:**
         │    ➔ **If [false]:**
         │       1. **Update **$NPE_ClearingBid**
      - Set **LastAwardedMinimumPrice** = `$IteratorReserveBid/Bid`**
         │       2. **Update Variable **$CurrentLowestPrice** = `$IteratorReserveBid/Bid`**
         └─ **End Loop**
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$NPE_ClearingBid**
      - Set **LastAwardedDate** = `'N/A'`
      - Set **LastAwardedMinimumPrice** = `0`**
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.