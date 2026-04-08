# Microflow Detailed Specification: SUB_ReserveBid_DeleteAll

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_EB.ReserveBid**  (Result: **$ReserveBidList**)**
2. 🔀 **DECISION:** `$ReserveBidList!=empty`
   ➔ **If [true]:**
      1. **Delete**
         *(Merging with existing path logic)*
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.