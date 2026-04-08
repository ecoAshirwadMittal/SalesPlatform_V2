# Microflow Detailed Specification: ACT_GetMostRecentReserveBidRefresh

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_EB.ReserveBidSync**  (Result: **$CurrentReserveBidSync**)**
2. 🏁 **END:** Return `$CurrentReserveBidSync`

**Final Result:** This process concludes by returning a [Object] value.