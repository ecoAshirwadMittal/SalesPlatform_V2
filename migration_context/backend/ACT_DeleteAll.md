# Microflow Detailed Specification: ACT_DeleteAll

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_EB.ReserveBid**  (Result: **$ReserveBidList**)**
2. **DB Retrieve **EcoATM_EB.ReserveBidSync**  (Result: **$ReserveBidSyncList**)**
3. **Delete**
4. **Delete**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.